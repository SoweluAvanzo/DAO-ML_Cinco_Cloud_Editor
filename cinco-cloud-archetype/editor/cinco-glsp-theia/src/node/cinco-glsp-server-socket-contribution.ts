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

export const LOG_DIR = path.join(__dirname, '..', '..', 'logs');
const MODULE_PATH = path.join(__dirname, '..', '..', '..', 'cinco-glsp-server', 'bundle', 'cinco-glsp-server-packed.js');

@injectable()
export class CincoGLSPSocketServerContribution extends GLSPSocketServerContribution {
    @inject(CincoGLSPServerArgsSetup)
    protected glspServerArgsProvider: CincoGLSPServerArgsSetup;

    readonly id = getDiagramConfiguration().contributionId;

    createContributionOptions(): Partial<GLSPSocketServerContributionOptions> {
        // env
        const args = this.glspServerArgsProvider.getArg();
        return {
            executable: MODULE_PATH,
            additionalArgs: [
                '-p',
                `${args.port}`,
                '--no-consoleLog',
                '--fileLog',
                'true',
                '--logDir',
                LOG_DIR,
                // cinco specific arguments
                `--rootFolder='${args.rootFolder}'`,
                args.metaDevMode ? '--metaDevMode' : DEFAULT_META_DEV_MODE,
                `--metaLanguagesFolder='${args.languagePath}'`,
                `--workspaceFolder='${args.workspacePath}'`,
                '--webSocket'
            ],
            socketConnectionOptions: {
                port: args.port,
                path: args.websocketPath
            }
        };
    }
}
