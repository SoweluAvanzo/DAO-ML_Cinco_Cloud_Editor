/********************************************************************************
 * Copyright (c) 2024 The DAO ML Team.
 ********************************************************************************/

import {
    GraphModel, ModelElement, RootPath, getLanguageFolder
} from '@cinco-glsp/cinco-glsp-api';
import { LogLevel } from '@eclipse-glsp/server';

type logType = (
    message: string, // the message to log
    options?: {
        channelName?: string; // custom channel name for the log message in the client console
        show?: boolean; // whether to automatically open the client console to show the log message (default: false)
        logLevel?: LogLevel, // how the message is categorized in the client console (default: LogLevel.info)
        dispatchToClient?: boolean // whether to dispatch the log message to the client console (default: true)
    }
) => Promise<void>;

type CopyDirectoryType = (
    relativeSourcePath: string,
    relativeTargetPath: string,
    deleteExistingDirectories: boolean,
    overwriteExistingFiles: boolean,
    sourceRoot: RootPath
) => void;

/**
 * generate files
 */
export async function generate(model: GraphModel, log: logType, copyDirectory: CopyDirectoryType): Promise<void> {
    const sourceUri = model._sourceUri;
    const optimizedTranslation = model.getProperty('optimizedTranslation');
    const languagesFolder = getLanguageFolder();
    const execFolder = languagesFolder + '/daoml/generator';
    const venvPython = execFolder + '/.venv/bin/python3';

    log('Setting up Python virtual environment...');
    if (!(await ensureVenv(execFolder, venvPython, log))) {
        return;
    }

    const generatorExecutionCmd = venvPython + ' translator_cli.py -fn=translate -f='
        + sourceUri + ' -tt=' + (optimizedTranslation ? 'optimized' : ' simple');

    const command = 'cd ' + execFolder + ' && '
        + generatorExecutionCmd;

    log('Checking dependencies...');

    const dependencies = ['lxml', 'xmlschema', 'antlr4-python3-runtime', 'networkx', 'jinja2'];
    for (const dep of dependencies) {
        if (!(await checkInstallPackages(dep, log, venvPython))) {
            return;
        }
    }

    const code = await executeProcess(command, false, log, (error: Error | null, stdout: string, stderr: string) => {
        if (error) {
            log(`${error.message}`, { show: true, logLevel: 1 });
            return;
        }
        if (stderr) {
            log(`${stderr}`, { show: true, logLevel: 1 });
            return;
        }
        log(`${stdout}`, { show: true });
    }).catch(e => {
        log(`Failed to execute generator: ${e}`, { show: true, logLevel: 1 });
        return 1;
    });

    if (code === undefined) { // could not spawn a process in this environment
        log('Cannot execute shell commands in this environment - generation aborted.', { show: true, logLevel: 1 });
        return;
    }

    if (code === 1) { // not installed
        log('failed to execute generator!');
        return;
    }

    // copy files
    copyDirectory(
        'daoml/generator/translated',
        'output',
        true,
        true,
        RootPath.LANGUAGES
    );
    log('Output Generated!');
}

/**
 * Ensures a working venv exists at execFolder/.venv, creating it (and the
 * python3-venv apt package it depends on for ensurepip) if missing or broken.
 * A venv sidesteps Debian's PEP 668 "externally-managed-environment"
 * restriction entirely, rather than needing --break-system-packages on every
 * pip call.
 */
async function ensureVenv(execFolder: string, venvPython: string, log: logType): Promise<boolean> {
    // Checking that pip actually works (not just that the python3 binary exists) also
    // catches a venv left broken by an earlier failed/interrupted setup attempt.
    const pipWorks = await executeProcess(`"${venvPython}" -m pip --version`, false, log, undefined);
    if (pipWorks === 0) {
        return true;
    }

    if (!(await checkInstallAptPackage('python3-venv', log))) {
        return false;
    }

    let venvError = '';
    const createVenvCode = await executeProcess(`cd ${execFolder} && python3 -m venv --clear .venv`, false, log,
        (error: Error | null, _stdout: string, stderr: string) => {
            venvError = (error?.message || stderr || '').trim();
        });
    if (createVenvCode === undefined) {
        log('Cannot execute shell commands in this environment - generation aborted.', { show: true, logLevel: 1 });
        return false;
    }
    if (createVenvCode !== 0) {
        const suffix = venvError ? ': ' + venvError : '!';
        log(`failed to create Python virtual environment${suffix}`, { show: true, logLevel: 1 });
        return false;
    }
    return true;
}

export async function checkInstallPackages(packageName: string, log: logType, pythonBin = 'python3'): Promise<boolean> {
    const pip = `${pythonBin} -m pip`;
    let code = await executeProcess(pip + ' show ' + packageName, false, log, undefined);
    if (code === undefined) { // could not spawn a process in this environment
        return false;
    }
    if (code === 1) { // not installed
        log('installing missing package!');
        let installOutput = '';
        code = await executeProcess(pip + ' install ' + packageName, false, log, (error: Error | null, stdout: string, stderr: string) => {
            installOutput = (error?.message || stderr || stdout || '').trim();
        });
        if (code === 1 || code === undefined) {
            const suffix = installOutput ? ': ' + installOutput : '!';
            log(`failed to install missing package '${packageName}'${suffix}`, { show: true, logLevel: 1 });
            return false;
        }
        log('installed missing package!');
    }
    return true;
}

/**
 * Checks whether a system (apt) package is installed via the node user's scoped
 * sudo access, installing it if missing. apt's package index is refreshed first,
 * since it's cleared after the image build to keep the image small.
 */
export async function checkInstallAptPackage(packageName: string, log: logType): Promise<boolean> {
    const code = await executeProcess(`dpkg -s ${packageName}`, false, log, undefined);
    if (code === undefined) { // could not spawn a process in this environment
        return false;
    }
    if (code !== 0) { // not installed
        log(`installing missing system package '${packageName}'...`);
        let installOutput = '';
        const installCode = await executeProcess(`sudo apt-get update && sudo apt-get install -y ${packageName}`, false, log,
            (error: Error | null, _stdout: string, stderr: string) => {
                installOutput = (error?.message || stderr || '').trim();
            });
        // apt-get exits with codes other than 1 on failure (e.g. 100), so any non-zero code is a failure.
        if (installCode === undefined || installCode !== 0) {
            const suffix = installOutput ? ': ' + installOutput : '!';
            log(`failed to install missing system package '${packageName}'${suffix}`, { show: true, logLevel: 1 });
            return false;
        }
        log(`installed missing system package '${packageName}'!`);
    }
    return true;
}

export async function executeProcess(
    command: string,
    logging: boolean = true,
    log: logType,
    callback?: (error: Error | null, stdout: string, stderr: string) => void
): Promise<any> {
    // Node built-in modules must be required lazily because language files
    // are loaded through a webpack custom module loader that cannot resolve
    // them at module top-level.
    let execCmd: any;
    try {
        // eslint-disable-next-line @typescript-eslint/no-var-requires
        const childProcessModule = require('child_process');
        execCmd = childProcessModule?.exec;
    } catch {
        // child_process is not available (e.g., webpack environment)
        execCmd = undefined;
    }

    // If child_process is not available, this environment cannot spawn processes at all.
    // Report this clearly instead of silently pretending the command succeeded.
    if (!execCmd) {
        log(`Cannot execute shell commands in this environment (child_process unavailable): ${command}`, { show: true, logLevel: 1 });
        return undefined;
    }

    let lock: any;
    const childProcess = execCmd(command, (error: any, stdout: string, stderr: string) => {
        if (callback) {
            callback(error, stdout, stderr);
        } else {
            if (logging) {
                if (error) {
                    log(`Error: ${error.message}`, { show: true, logLevel: 1 });
                    return;
                }
                if (stderr) {
                    log(`Stderr: ${stderr}`, { show: true, logLevel: 1 });
                    return;
                }
                log(`Output: ${stdout}`, { show: true });
            }
        }
    });
    childProcess.on('spawn', (args: any) => {
        if (logging) {
            log('Process spawned'
                + (args ? ' with args: ' + args : '!'), { show: true, logLevel: 0 });
        }
    });
    childProcess.on('disconnect', () => {
        if (logging) {
            log('Process disconneced!', { show: true, logLevel: 4 });
        }
    });
    childProcess.on('message', (message: any, _sendHandle: any) => {
        if (logging) {
            log('' + message.toString(), { show: true, logLevel: 4 });
        }
    });
    childProcess.on('error', (arg: any) => {
        if (logging) {
            log('Generator had error: '
                + arg, { show: true, logLevel: 1 });
        }
    });
    childProcess.on('exit', (code: any, _signal: any) => {
        if (logging) {
            log('Generator-Process exited with code: '
                + code, { show: true, logLevel: 4 });
        }
    });
    childProcess.on('close', (code: any, _signal: any) => {
        if (logging) {
            log('Generator-Process closed with code: '
                + code, { show: true, logLevel: 4 });
        }
        lock(code);
    });
    return new Promise<any>(resolve => {
        lock = resolve;
    });
}

/**
 * Describe your file content here !
 */
export function getContent(model: ModelElement): string {
    return model.type + ' generation content';
}
