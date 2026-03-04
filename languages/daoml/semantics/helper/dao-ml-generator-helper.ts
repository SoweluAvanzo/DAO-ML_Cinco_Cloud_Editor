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
    const generatorExecutionCmd = 'python3 translator_cli.py -fn=translate -f='
        + sourceUri + ' -tt=' + (optimizedTranslation ? 'optimized' : ' simple');

    const command = 'cd ' + execFolder + ' && '
        + generatorExecutionCmd;

    log('Checking dependencies...');

    const dependencies = ['lxml', 'xmlschema', 'antlr4-python3-runtime', 'networkx', 'jinja2'];
    for (const dep of dependencies) {
        if (!(await checkInstallPackages(dep, log))) {
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

export async function checkInstallPackages(packageName: string, log: logType): Promise<boolean> {
    const pip = 'python3 -m pip';
    let code = await executeProcess(pip + ' show ' + packageName, false, log, undefined);
    if (code === 1) { // not installed
        log('installing missing package!');
        code = await executeProcess(pip + ' install ' + packageName, false, log, undefined);
        if (code === 1) {
            log('failed to install missing package!');
            return false;
        }
        log('installed missing package!');
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

    // If child_process is not available, return success without executing
    if (!execCmd) {
        if (logging) {
            log('(Skipped process execution in webpack environment)', { show: false, logLevel: 0 });
        }
        return 0;
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
