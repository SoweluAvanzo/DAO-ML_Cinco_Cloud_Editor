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
import * as path from 'path';
import { Client } from 'minio';
import { inject, injectable } from 'inversify';
import { ILogger, MaybePromise } from '@theia/core';
import { BackendApplicationContribution } from '@theia/core/lib/node';
import { IProcessExitEvent, ProcessErrorEvent } from '@theia/process/lib/node/process';
import { RawProcess, RawProcessFactory } from '@theia/process/lib/node/raw-process';
import { PYRO_SERVER_BINARIES_FILE, PYRO_SUBPATH } from '../node/environment-vars';
import { LogServer } from './log-protocol';
import { createClient } from './minio-client';

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

    onStart?(server: http.Server | https.Server): MaybePromise<void> {
        const msg = '*** starting server "' + ServerLauncher.FILE_PATH + '" ***';
        this.logInfo(msg);
    }

    initialize(): void {
        if (PYRO_SERVER_BINARIES_FILE !== '') {
            const minioClient: Client = createClient();
            const file = path.resolve(__dirname, '..', '..', 'pyro-server-binaries.zip');
            minioClient.fGetObject('projects', PYRO_SERVER_BINARIES_FILE, file.toString(), e => {
                if (e) {
                    const msg = 'Failed to fetch pyro server binary';
                    this.logError(msg);
                    throw new Error(`${msg}` + e);
                }

                this.logInfo('Fetched pyro-server binaries');
                const unpackScriptPath = path.resolve(__dirname, '..', '..', 'unpack-pyro-server.sh');
                cp.spawnSync('chmod', ['+X', unpackScriptPath.toString()]);
                const buffer = cp.spawnSync('sh', [unpackScriptPath.toString(), PYRO_SUBPATH]);
                this.logInfo(String(buffer.stdout));
                this.logError(String(buffer.stderr));

                this.execute();
            });
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
        rawProcess = this.processFactory({ command, args, options });
        rawProcess.errorStream.on('data', this.logError.bind(this));
        rawProcess.outputStream.on('data', this.logInfo.bind(this));
        return new Promise<RawProcess>((resolve, reject) => {
            ServerLauncher.LOG = '';
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
                this.logInfo(`${exit}`);
            });
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
