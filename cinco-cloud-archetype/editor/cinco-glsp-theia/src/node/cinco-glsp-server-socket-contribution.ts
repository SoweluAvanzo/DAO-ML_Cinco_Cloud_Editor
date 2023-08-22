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
import {
    getPort,
    GLSPSocketServerContribution,
    GLSPSocketServerContributionOptions
} from '@eclipse-glsp/theia-integration/lib/node';
import { DEFAULT_SERVER_PORT, META_DEV_MODE, META_LANGUAGES_FOLDER } from '@cinco-glsp/cinco-glsp-common';
import { injectable } from 'inversify';
import * as path from 'path';
import { getDiagramConfiguration } from '../common/cinco-language';

 // args defined at start of the backend, but passed to the execution of the external server
export const PORT_ARG_KEY = 'CINCO_GLSP';
export const LANGUAGES_FOLDER_ARG_KEY = '--META_LANGUAGES_FOLDER';
export const DEV_MODE_ARG_KEY = `--${META_DEV_MODE}`;

export const LOG_DIR = path.join(__dirname, '..', '..', 'logs');
const MODULE_PATH = path.join(__dirname, '..', '..', '..', 'cinco-glsp-server', 'bundle', 'cinco-glsp-server-packed.js');

@injectable()
export class CincoGLSPSocketServerContribution extends GLSPSocketServerContribution {
    readonly id = getDiagramConfiguration().contributionId;

    createContributionOptions(): Partial<GLSPSocketServerContributionOptions> {
        const port = getPort(PORT_ARG_KEY, DEFAULT_SERVER_PORT);
        const languagesFolder = getArgs(LANGUAGES_FOLDER_ARG_KEY) ?? META_LANGUAGES_FOLDER;
        const metaDevMode = hasArg(DEV_MODE_ARG_KEY) ? '--metaDevMode' : '';
        return {
            executable: MODULE_PATH,
            additionalArgs: [
                '-p', `${port}`, '--no-consoleLog', '--fileLog', 'true', '--logDir', LOG_DIR,
                // cinco specific arguments
                `--rootFolder='${__dirname + '/../../..'}'`, metaDevMode, `--metaLanguagesFolder='${languagesFolder}'`
            ],
            socketConnectionOptions: {
                port: port
            }
        };
    }
}

export function getArgs(argsKey: string): string | undefined {
    const args = process.argv.filter(a => a.startsWith(argsKey));
    if (args.length > 0) {
        const result = args[0].substring(argsKey.length + 1, undefined);
        if(result) {
            return result.replace(/"|'/g, ''); // replace quotes
        }
    }
    return undefined;
}

export function hasArg(argsKey: string): boolean {
    const args = process.argv.filter(a => a.startsWith(argsKey));
    return args.length > 0;
}
