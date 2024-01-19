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
import { getPort, getWebSocketPath } from '@eclipse-glsp/theia-integration/lib/node';
import {
    DEFAULT_SERVER_PORT,
    DEFAULT_WEBSOCKET_PATH,
    META_DEV_MODE,
    META_LANGUAGES_FOLDER,
    WORKSPACE_FOLDER,
    ROOT_FOLDER_KEY,
    PORT_KEY,
    LANGUAGES_FOLDER_KEY,
    WORKSPACE_FOLDER_KEY,
    WEBSOCKET_PATH_KEY,
    hasArg,
    getArgs,
    ServerArgs,
    WEB_SERVER_PORT_KEY,
    DEFAULT_WEB_SERVER_PORT
} from '@cinco-glsp/cinco-glsp-common';
import { injectable, inject, postConstruct } from 'inversify';
import { GLSPServerUtilServerNode } from './glsp-server-util-server-node';

// args defined at start of the backend, but passed to the execution of the external server
export const DEFAULT_ROOT_FOLDER = __dirname + '/../../..';
export const DEFAULT_META_LANGUAGES_FOLDER = META_LANGUAGES_FOLDER;
export const DEFAULT_WORKSPACE_FOLDER = WORKSPACE_FOLDER;
export const DEFAULT_META_DEV_MODE = '';

@injectable()
export class CincoGLSPServerArgsSetup {
    @inject(GLSPServerUtilServerNode)
    protected glspServerUtilNode: GLSPServerUtilServerNode;
    private _args: ServerArgs;

    @postConstruct()
    initialize(): void {
        this._args = this.setupGLSPServerArgs(this.glspServerUtilNode);
    }

    getArg(): ServerArgs {
        return this._args!;
    }

    setupGLSPServerArgs(glspServerArgsProvider: GLSPServerUtilServerNode): any {
        // create default - precedence: arg -> envVar -> static default value
        const defaultPort = process.env[PORT_KEY] ? +process.env[PORT_KEY]! : DEFAULT_SERVER_PORT;
        const defaultWebSocketPath = process.env[WEBSOCKET_PATH_KEY] ?? DEFAULT_WEBSOCKET_PATH;
        const defaultMetaDevMode = process.env[META_DEV_MODE] ? true : false;
        const defaultMetaLanguagesFolder = process.env[LANGUAGES_FOLDER_KEY] ?? DEFAULT_META_LANGUAGES_FOLDER;
        const defaultWorkspaceFolder = process.env[WORKSPACE_FOLDER_KEY] ?? DEFAULT_WORKSPACE_FOLDER;
        const defaultRootFolder = process.env[ROOT_FOLDER_KEY] ?? DEFAULT_ROOT_FOLDER;
        const defaultWebServerPort = Number.parseInt(process.env[WEB_SERVER_PORT_KEY] ?? `${DEFAULT_WEB_SERVER_PORT}`, 10);

        // set values by precedence
        const port = getPort(PORT_KEY, defaultPort);
        const websocketPath = getWebSocketPath(WEBSOCKET_PATH_KEY) ?? defaultWebSocketPath;
        const languagesFolder = getArgs(LANGUAGES_FOLDER_KEY) ?? defaultMetaLanguagesFolder;
        const workspaceFolder = getArgs(WORKSPACE_FOLDER_KEY) ?? defaultWorkspaceFolder;
        const rootFolder = getArgs(ROOT_FOLDER_KEY) ?? defaultRootFolder;
        const metaDevMode = hasArg(META_DEV_MODE) ?? defaultMetaDevMode;
        const webServerPort = Number.parseInt(getArgs(WEB_SERVER_PORT_KEY) ?? `${defaultWebServerPort}`, 10);

        // make accesible to frontend
        glspServerArgsProvider.setServerArgs(metaDevMode, rootFolder, languagesFolder, workspaceFolder, port, websocketPath, webServerPort);

        return ServerArgs.create(metaDevMode, rootFolder, languagesFolder, workspaceFolder, port, websocketPath, webServerPort);
    }
}
