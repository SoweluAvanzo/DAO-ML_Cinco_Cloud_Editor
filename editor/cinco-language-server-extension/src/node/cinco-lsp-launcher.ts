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
import { ILogger, MaybePromise } from '@theia/core';
import { BackendApplicationContribution } from '@theia/core/lib/node';
import { IProcessExitEvent, ProcessErrorEvent } from '@theia/process/lib/node/process';
import { RawProcess, RawProcessFactory } from '@theia/process/lib/node/raw-process';
import * as cp from 'child_process';
import * as glob from 'glob';
import { inject, injectable } from 'inversify';
import * as path from 'path';
import * as https from 'https';
import * as http from 'http';

const languageServerPath = path.resolve(__dirname, '..', '..', 'language-server', 'bin');
const languageServerName = 'cinco-language-server';
const LOG_NAME = '[CINCO] ';
let rawProcess: RawProcess;

@injectable()
export class CincoLSPServerLauncher implements BackendApplicationContribution {
    @inject(RawProcessFactory) protected readonly processFactory: RawProcessFactory;
    @inject(ILogger) private readonly logger: ILogger;

    onStart?(server: http.Server | https.Server): MaybePromise<void> {
        this.logger.info(LOG_NAME + 'starting CINCO-Language-Server!');
    }

    initialize(): void {
        const execPaths = glob.sync('**/' + languageServerName, { cwd: languageServerPath });
        if (execPaths.length === 0) {
            throw new Error(LOG_NAME + 'Server launcher not found.');
        }
        const execPath = path.resolve(languageServerPath, languageServerName);

        this.logger.info(LOG_NAME + 'Spawn Server Process from ' + execPath);
        this.spawnProcessAsync(execPath, [], {
            detached: false,
            shell: true,
            stdio: ['inherit', 'pipe']
        });
    }

    protected spawnProcessAsync(command: string, args?: string[], options?: cp.SpawnOptions): Promise<RawProcess> {
        rawProcess = this.processFactory({ command, args, options });
        rawProcess.errorStream.on('data', this.logError.bind(this));
        rawProcess.outputStream.on('data', this.logInfo.bind(this));
        return new Promise<RawProcess>((resolve, reject) => {
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
                this.logger.info(LOG_NAME + `: ${error}`);
            });
            rawProcess.onExit((exit: IProcessExitEvent) => {
                this.logger.info(LOG_NAME + `: ${exit}`);
            });
            process.nextTick(() => resolve(rawProcess));
        });
    }

    protected onDidFailSpawnProcess(error: Error | ProcessErrorEvent): void {
        this.logError(error.message);
    }

    protected logError(data: string | Buffer): void {
        if (data) {
            this.logger.error(LOG_NAME + `: ${data}`);
        }
    }

    protected logInfo(data: string | Buffer): void {
        if (data) {
            this.logger.info(LOG_NAME + `: ${data}`);
        }
    }
}
