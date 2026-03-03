/********************************************************************************
 * Copyright (c) 2024 The DAO ML Team.
 ********************************************************************************/
import {
    GraphModel, ModelElement, RootPath, getLanguageFolder
} from '@cinco-glsp/cinco-glsp-api';
import { LogLevel } from '@eclipse-glsp/server';
import { exec, ExecException } from 'child_process';
import * as path from 'path';

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
    const execFolder = path.join(languagesFolder, 'daoml', 'generator');
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

    const code = await executeProcess(command, false, log, (error: ExecException | null, stdout: string, stderr: string) => {
        if (error) {
            log(`${error.message}`, { show: true, logLevel: 1 });
            return;
        }
        if (stderr) {
            log(`${stderr}`, { show: true, logLevel: 1 });
            return;
        }
        log(`${stdout}`, { show: true });
    });
    if (code === 1) { // not installed
        log('failed to execute generator!');
        return;
    }

    // copy files
    copyDirectory(
        path.join('daoml', 'generator', 'translated'),
        path.join('output'),
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
    callback?: (error: ExecException | null, stdout: string, stderr: string) => void
): Promise<any> {
    let lock: any;
    const childProcess = exec(command, (error, stdout, stderr) => {
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
    childProcess.on('message', (message, _sendHandle) => {
        if (logging) {
            log('' + message.toString(), { show: true, logLevel: 4 });
        }
    });
    childProcess.on('error', arg => {
        if (logging) {
            log('Generator had error: '
                + arg, { show: true, logLevel: 1 });
        }
    });
    childProcess.on('exit', (code, _signal) => {
        if (logging) {
            log('Generator-Process exited with code: '
                + code, { show: true, logLevel: 4 });
        }
    });
    childProcess.on('close', (code, _signal) => {
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
