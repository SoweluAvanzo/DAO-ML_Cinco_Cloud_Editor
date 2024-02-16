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

import {
    DEFAULT_WEB_SERVER_PORT,
    DEFAULT_SERVER_PORT,
    DEFAULT_WEBSOCKET_PATH,
    META_LANGUAGES_FOLDER,
    ServerArgs,
    ServerArgsRequest,
    ServerArgsResponse,
    WORKSPACE_FOLDER,
    WEBSOCKET_HOST_MAPPING,
    WEBSERVER_HOST_MAPPING
} from '@cinco-glsp/cinco-glsp-common';
import { Action, ActionHandler, MaybePromise } from '@eclipse-glsp/server';
import { processPort } from '@eclipse-glsp/server/lib/node/launch/socket-cli-parser';
import { injectable } from 'inversify';
import {
    LanguageFilesRegistry,
    getPortArg,
    getLanguageFolderArg,
    getRoot,
    getWorkspaceFolderArg,
    getWebsocketPathArg,
    getWebServerPortArg
} from '@cinco-glsp/cinco-glsp-api';
import { USE_SSL } from '@cinco-glsp/cinco-glsp-common';

@injectable()
export class ServerArgsRequestHandler implements ActionHandler {
    actionKinds: string[] = [ServerArgsRequest.KIND];

    execute(action: ServerArgsRequest, ...args: unknown[]): MaybePromise<Action[]> {
        const serverArgs = ServerArgs.create(
            LanguageFilesRegistry.isMetaDevMode,
            getRoot(),
            getLanguageFolderArg() ?? WORKSPACE_FOLDER,
            getWorkspaceFolderArg() ?? META_LANGUAGES_FOLDER,
            processPort(getPortArg() ?? '' + DEFAULT_SERVER_PORT),
            getWebsocketPathArg() ?? DEFAULT_WEBSOCKET_PATH,
            getWebServerPortArg() ?? DEFAULT_WEB_SERVER_PORT,
            process.env[USE_SSL] === 'true',
            process.env[WEBSERVER_HOST_MAPPING],
            process.env[WEBSOCKET_HOST_MAPPING]
        );
        return [ServerArgsResponse.create(serverArgs)];
    }
}
