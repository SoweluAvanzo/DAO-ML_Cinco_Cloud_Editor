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
import * as cp from 'child_process';
import * as http from 'http';
import * as https from 'https';
import { inject, injectable } from 'inversify';
import { ILogger, MaybePromise } from '@theia/core';
import { BackendApplicationContribution } from '@theia/core/lib/node';
import { IProcessExitEvent, ProcessErrorEvent } from '@theia/process/lib/node/process';
import { RawProcess, RawProcessFactory } from '@theia/process/lib/node/raw-process';
import { LogServer } from './log-protocol';

let rawProcess: RawProcess;

@injectable()
export class ServerLauncher implements BackendApplicationContribution {
    @inject(RawProcessFactory) protected readonly processFactory: RawProcessFactory;
    @inject(ILogger) private readonly consoleLogger: ILogger;
    @inject(LogServer) logger: LogServer;

    static FILE_PATH: string;
    static CMD_EXEC: string;
    static ARGS: string[];
    static LOG = '';
    static CALLBACK: Promise<void> | undefined = undefined;
    static readonly MAX_RETRIES = 10;
    static readonly RETRY_TIMEOUT = 3000;

    onStart?(server: http.Server | https.Server): MaybePromise<void> {
        const msg = '*** starting server "' + ServerLauncher.FILE_PATH + '" ***';
        this.logInfo(msg);
    }

    initialize(): void {
        if (!ServerLauncher.FILE_PATH) {
            const msg = '*** server launcher "' + ServerLauncher.FILE_PATH + '" not found ***';
            this.logError(msg);
            throw new Error(msg);
        }
        if(ServerLauncher.CALLBACK) {
            ServerLauncher.CALLBACK.then( () => this.execute());
        } else {
            this.execute();
        }
    }

    execute(): void {
        this.spawnProcessAsync(
            ServerLauncher.CMD_EXEC,
            ServerLauncher.ARGS,
            {
                detached: false,
                shell: true,
                stdio: ['inherit', 'pipe']
            }
        );
    }

    protected spawnProcessAsync(command: string, args?: string[], options?: cp.SpawnOptions): Promise<RawProcess> {
        return new Promise<RawProcess>((resolve, reject) => {
            let retries = ServerLauncher.MAX_RETRIES;

            const launch = (): void => {
                rawProcess = this.processFactory({ command, args, options });
                rawProcess.errorStream.on('data', this.logError.bind(this));
                rawProcess.outputStream.on('data', this.logInfo.bind(this));
                ServerLauncher.LOG = '';
                rawProcess.onError((error: ProcessErrorEvent) => {
                    this.onDidFailSpawnProcess(error);
                    if (error.code === 'ENOENT') {
                        const guess = command.split(/\s+/).shift();
                        if (guess) {
                            this.logError(`Failed to spawn ${guess}\nPerhaps it is not on the PATH.`);
                        }
                    }
                });
                rawProcess.onClose((error: IProcessExitEvent) => {
                    this.logError(`${error}`);
                    if (error.code !== undefined && error.code > 0) {
                        retries--;
                        this.logError('Failed to start pyro server.');
                        if (retries > 0) {
                            this.logError(`Retry starting pyro server in ${ServerLauncher.RETRY_TIMEOUT}ms...`);
                            setTimeout(() => launch(), ServerLauncher.RETRY_TIMEOUT);
                        }
                    }
                });
                rawProcess.onExit((exit: IProcessExitEvent) => {
                    this.logInfo(`${exit}`);
                });
            };

            launch();

            process.nextTick(() => resolve(rawProcess));
        });
    }

    protected onDidFailSpawnProcess(error: Error | ProcessErrorEvent): void {
        this.logError(error.message);
    }

    protected logError(data: string | Buffer): void {
        this.consoleLogger.error(data);
        this.appendToLog(data);
        if (data) {
            if (this.logger) {
                this.logger!.error(`${data}`);
            }
        }
    }

    protected logInfo(data: string | Buffer): void {
        this.consoleLogger.info(data);
        this.appendToLog(data);
        if (data) {
            if (this.logger) {
                this.logger!.info(`${data}`);
            }
        }
    }

    protected appendToLog(msg: string | Buffer): void {
        ServerLauncher.LOG += msg + '\n';
    }
}
