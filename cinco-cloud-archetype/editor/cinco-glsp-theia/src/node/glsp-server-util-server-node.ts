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
import { DEFAULT_SERVER_PORT, ServerArgs, DEFAULT_WEBSOCKET_PATH, DEFAULT_WEB_SERVER_PORT } from '@cinco-glsp/cinco-glsp-common';
import {
    DEFAULT_META_DEV_MODE,
    DEFAULT_META_LANGUAGES_FOLDER,
    DEFAULT_ROOT_FOLDER,
    DEFAULT_WORKSPACE_FOLDER
} from './cinco-glsp-server-args-setup';
import { GLSPServerUtilClient, GLSPServerUtilServer } from '../common/glsp-server-util-protocol';
import * as path from 'path';
import * as childProcess from 'child_process';
import { getFilesFromFolder } from './utils/file-helper';
@injectable()
export class GLSPServerUtilServerNode implements GLSPServerUtilServer {
    protected static SERVER_ARGS: ServerArgs;
    client: GLSPServerUtilClient | undefined;

    static watchModeProcess: childProcess.ChildProcess | undefined;

    constructor() {
        if (!GLSPServerUtilServerNode.SERVER_ARGS) {
            this.setServerArgs(
                DEFAULT_META_DEV_MODE !== '',
                DEFAULT_ROOT_FOLDER,
                DEFAULT_META_LANGUAGES_FOLDER,
                DEFAULT_WORKSPACE_FOLDER,
                DEFAULT_SERVER_PORT,
                DEFAULT_WEBSOCKET_PATH,
                DEFAULT_WEB_SERVER_PORT
            );
        }
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

    setServerArgs(
        metaDevMode: boolean,
        rootFolder: string,
        languagePath: string,
        workspacePath: string,
        port: number,
        websocketPath: string,
        webServerPort: number
    ): void {
        const absoluteRootPath = path.resolve(rootFolder);
        GLSPServerUtilServerNode.SERVER_ARGS = {
            metaDevMode: metaDevMode,
            rootFolder: absoluteRootPath,
            languagePath: languagePath,
            workspacePath: workspacePath,
            port: port,
            websocketPath: websocketPath,
            webServerPort: webServerPort
        };
    }

    getServerArgs(): ServerArgs {
        return GLSPServerUtilServerNode.SERVER_ARGS;
    }

    transpileLanguagesFolder(): Promise<void> | undefined {
        const serverArgs = this.getServerArgs();
        const languagesFolder = path.join(serverArgs.rootFolder, serverArgs.languagePath);
        const files = getFilesFromFolder(languagesFolder, './', ['.ts']);
        const filePaths = files.join(' ');
        const exec = `cd ${languagesFolder} && tsc --module none --target es2015 --strict false ${filePaths}`;
        childProcess.exec(exec, (error, stdout, stderr) => {
            if (error) {
                console.error(`error: ${error.message}`);
            } else if (stderr) {
                console.error(`stderr: ${stderr}`);
            } else {
                console.log(`stdout:\n${stdout}`);
            }
        });
        return undefined;
    }

    async transpileWatchLanguagesFolder(): Promise<boolean | undefined> {
        if (!GLSPServerUtilServerNode.watchModeProcess || GLSPServerUtilServerNode.watchModeProcess.killed) {
            const serverArgs = this.getServerArgs();
            const languagesFolder = path.join(serverArgs.rootFolder, serverArgs.languagePath);
            const files = getFilesFromFolder(languagesFolder, './', ['.ts']);
            const filePaths = files.join(' ');
            const exec = `cd ${languagesFolder} && tsc --module none --target es2015 --strict false --watch ${filePaths}`;
            GLSPServerUtilServerNode.watchModeProcess = childProcess.exec(exec, (error, stdout, stderr) => {
                if (error) {
                    console.error(`error: ${error.message}`);
                } else if (stderr) {
                    console.error(`stderr: ${stderr}`);
                } else {
                    console.log(`stdout:\n${stdout}`);
                }
            });
            console.log('Starting watchmode');
            return true;
        } else {
            return new Promise<boolean | undefined>(resolve => {
                const watchmodeProcess = GLSPServerUtilServerNode.watchModeProcess;
                if (watchmodeProcess) {
                    resolve(watchmodeProcess.kill('SIGINT') === true ? false : undefined);
                } else {
                    resolve(undefined);
                }
            });
        }
    }
}
