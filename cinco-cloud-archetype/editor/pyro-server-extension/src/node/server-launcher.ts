/*!
 * Copyright (c) 2019-2020 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
import { MaybePromise } from '@theia/core';
import { BackendApplicationContribution } from '@theia/core/lib/node';
import { IProcessExitEvent, ProcessErrorEvent } from '@theia/process/lib/node/process';
import { RawProcess, RawProcessFactory } from '@theia/process/lib/node/raw-process';
import * as cp from 'child_process';
import * as glob from 'glob';
import * as http from 'http';
import * as https from 'https';
import { inject, injectable } from 'inversify';
import * as path from 'path';
import { PyroLogServer } from '../shared/log-protocol';

import { isDebugging } from './debugHandler';
import { cmdArgs, cmdDebugArgs, cmdExec, serverFile, serverPath } from './execVars';

let rawProcess: RawProcess;
export let LOG = '';

@injectable()
export class ServerLauncher implements BackendApplicationContribution {
    @inject(RawProcessFactory) protected readonly processFactory: RawProcessFactory;
    @inject(PyroLogServer) logger: PyroLogServer;

    onStart?(server: http.Server | https.Server): MaybePromise<void> {
        this.logInfo('*** starting model-server ***');
    }

    initialize(): void {
        const execPaths = glob.sync('**/' + serverFile, { cwd: serverPath });
        if (execPaths.length === 0) {
            const msg = '*** server launcher not found ***';
            this.logError(msg);
            throw new Error(msg);
        }
        const execPath = path.resolve(serverPath, serverFile);
        this.logInfo('*** spawn server process from "' + execPath + '" ***');
        this.spawnProcessAsync(
            cmdExec,
            (isDebugging() ? cmdDebugArgs : cmdArgs).concat(execPath),
            {
                detached: false,
                shell: true,
                stdio: ['inherit', 'pipe']
            }
        );
    }

    protected spawnProcessAsync(command: string, args?: string[], options?: cp.SpawnOptions): Promise<RawProcess> {
        rawProcess = this.processFactory({ command, args, options });
        rawProcess.errorStream.on('data', this.logError.bind(this));
        rawProcess.outputStream.on('data', this.logInfo.bind(this));
        return new Promise<RawProcess>((resolve, reject) => {
            LOG = '';
            rawProcess.onError((error: ProcessErrorEvent) => {
                this.onDidFailSpawnProcess(error);
                if (error.code === 'ENOENT') {
                    const guess = command.split(/\s+/).shift();
                    if (guess) {
                        reject(new Error(`Failed to spawn ${guess}\nPerhaps it is not on the PATH.`));
                        return;
                    }
                }
                reject(error);
            });
            rawProcess.onClose((error: IProcessExitEvent) => {
                this.logError(`${error}`);
            });
            rawProcess.onExit((exit: IProcessExitEvent) => {
                this.logError(`${exit}`);
            });
            process.nextTick(() => resolve(rawProcess));
        });
    }

    protected onDidFailSpawnProcess(error: Error | ProcessErrorEvent): void {
        this.logError(error.message);
    }

    protected logError(data: string | Buffer): void {
        this.appendToLog(data);
        if (data) {
            if (this.logger) {
                this.logger!.info(`${data}`);
            }
        }
    }

    protected logInfo(data: string | Buffer): void {
        this.appendToLog(data);
        if (data) {
            if (this.logger) {
                this.logger!.info(`${data}`);
            }
        }
    }

    protected appendToLog(msg: string | Buffer): void {
        LOG += msg;
    }
}
