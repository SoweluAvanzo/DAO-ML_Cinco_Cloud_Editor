/********************************************************************************
 * Copyright (c) 2022 Cinco Cloud.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
import { GLSPSocketServerContribution, GLSPSocketServerContributionOptions } from '@eclipse-glsp/theia-integration/lib/node';
import { injectable, inject } from 'inversify';
import * as path from 'path';
import { getDiagramConfiguration } from '../common/cinco-language';
import { CincoGLSPServerArgsSetup, DEFAULT_META_DEV_MODE } from './cinco-glsp-server-args-setup';
import { Disposable, LogLevel } from '@theia/core';
import * as fs from 'fs';
import { RawProcess } from '@theia/process/lib/node/raw-process';
import { CincoLogger } from './cinco-theia-logger';

export const LOG_DIR = path.join(__dirname, '..', '..', 'logs');
const MODULE_PATH = path.join(__dirname, '..', '..', '..', 'cinco-glsp-server', 'bundle', 'cinco-glsp-server-packed.js');

@injectable()
export class CincoGLSPSocketServerContribution extends GLSPSocketServerContribution {
    @inject(CincoGLSPServerArgsSetup)
    protected glspServerArgsProvider: CincoGLSPServerArgsSetup;
    @inject(CincoLogger)
    protected cincoLogger: CincoLogger;

    readonly id = getDiagramConfiguration().contributionId;

    createContributionOptions(): Partial<GLSPSocketServerContributionOptions> {
        // env
        const args = this.glspServerArgsProvider.getArg();
        const launchServerExternal = process.env['SERVER_EXTERNAL'] === 'true';
        return {
            launchOnDemand: false,
            launchedExternally: launchServerExternal,
            executable: MODULE_PATH,
            additionalArgs: [
                '-p',
                `${args.port}`,
                '--fileLog',
                'true',
                '--logDir',
                LOG_DIR,
                // cinco specific arguments
                `--rootFolder='${args.rootFolder}'`,
                args.metaDevMode ? '--metaDevMode' : DEFAULT_META_DEV_MODE,
                `--metaLanguagesFolder='${args.languagePath}'`,
                `--workspaceFolder='${args.workspacePath}'`,
                '--webSocket',
                `--webServerPort=${args.webServerPort}`
            ],
            socketConnectionOptions: {
                port: args.port,
                path: args.websocketPath
            }
        };
    }

    override async launch(): Promise<void> {
        try {
            if (!this.options.executable) {
                throw new Error('Could not launch GLSP server. No executable path is provided via the contribution options');
            }
            if (!fs.existsSync(this.options.executable)) {
                throw new Error(`Could not launch GLSP server. The given server executable path is not valid: ${this.options.executable}`);
            }
            if (isNaN(this.options.socketConnectionOptions.port)) {
                throw new Error(
                    `Could not launch GLSP Server. The given server port is not a number: ${this.options.socketConnectionOptions.port}`
                );
            }
            let process: RawProcess;
            if (this.options.executable.endsWith('.jar')) {
                process = await this.launchJavaProcess();
            } else if (this.options.executable.endsWith('.js')) {
                process = await this.launchNodeProcess();
            } else {
                throw new Error(`Could not launch GLSP Server. Invalid executable path ${this.options.executable}`);
            }
            process.outputStream.addListener('data', chunk => {
                this.cincoLogger.logGLSPServer(LogLevel.INFO, chunk.toString());
            });
            process.outputStream.addListener('error', err => {
                this.cincoLogger.logGLSPServer(LogLevel.ERROR, err.name + ': ' + err.message + '\n' + err.stack);
            });
            this.toDispose.push(Disposable.create(() => process.kill()));
        } catch (error) {
            this.onReadyDeferred.reject(error);
        }

        return this.onReadyDeferred.promise;
    }
}
