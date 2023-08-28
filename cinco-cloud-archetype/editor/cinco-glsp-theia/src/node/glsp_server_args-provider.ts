/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud.
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
import { injectable } from 'inversify';
import { DEFAULT_SERVER_PORT, ServerArgs } from '@cinco-glsp/cinco-glsp-common';
import {
    DEFAULT_META_DEV_MODE,
    DEFAULT_META_LANGUAGES_FOLDER,
    DEFAULT_ROOT_FOLDER,
    DEFAULT_WORKSPACE_FOLDER
} from './cinco-glsp-server-socket-contribution';
import { GLSPServerUtilClient, GLSPServerUtilServer } from '../common/glsp-server-util-protocol';
import * as path from 'path';

@injectable()
export class GLSPServerUtilServerNode implements GLSPServerUtilServer {
    protected static SERVER_ARGS: ServerArgs;
    client: GLSPServerUtilClient | undefined;

    constructor() {
        this.setServerArgs(
            DEFAULT_META_DEV_MODE !== '', DEFAULT_ROOT_FOLDER,
            DEFAULT_META_LANGUAGES_FOLDER, DEFAULT_WORKSPACE_FOLDER, DEFAULT_SERVER_PORT)
        ;
    }

    async connect(): Promise<boolean> {
        return true;
    }

    getArgs(): Promise<ServerArgs> {
        if (!this.client) {
            throw new Error('No client connected!');
        }
        return new Promise<ServerArgs>((resolve, reject) => resolve(GLSPServerUtilServerNode.SERVER_ARGS));
    }

    dispose(): void {
        console.log('GLSPServerUtilServerNode - disposed!');
    }

    getClient(): GLSPServerUtilClient | undefined {
        return this.client;
    }

    setClient(client: GLSPServerUtilClient | undefined): void {
        this.client = client;
    }

    setServerArgs(metaDevMode: boolean, rootFolder: string, languagePath: string, workspacePath: string, port: number): void {
        const absoluteRootPath = path.resolve(rootFolder);
        GLSPServerUtilServerNode.SERVER_ARGS = {
            metaDevMode: metaDevMode,
            rootFolder: absoluteRootPath,
            languagePath: languagePath,
            workspacePath: workspacePath,
            port: port
        };
    }

    getServerArgs(): ServerArgs {
        return GLSPServerUtilServerNode.SERVER_ARGS;
    }
}
