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
    DEFAULT_SERVER_PORT,
    META_LANGUAGES_FOLDER,
    ServerArgs,
    ServerArgsRequest,
    ServerArgsResponse,
    WORKSPACE_FOLDER
} from '@cinco-glsp/cinco-glsp-common';
import { Action, ActionHandler, MaybePromise, processPort } from '@eclipse-glsp/server-node';
import { injectable } from 'inversify';
import { LanguageFilesRegistry, getPortArg, getLanguageFolderArg, getRoot, getWorkspaceFolderArg } from '@cinco-glsp/cinco-glsp-api';

@injectable()
export class ServerArgsRequestHandler implements ActionHandler {
    actionKinds: string[] = [ServerArgsRequest.KIND];

    execute(action: ServerArgsRequest, ...args: unknown[]): MaybePromise<Action[]> {
        const serverArgs = ServerArgs.create(
            LanguageFilesRegistry.isMetaDevMode,
            getRoot(),
            getLanguageFolderArg() ?? WORKSPACE_FOLDER,
            getWorkspaceFolderArg() ?? META_LANGUAGES_FOLDER,
            processPort(getPortArg() ?? '' + DEFAULT_SERVER_PORT)
        );
        return [ServerArgsResponse.create(serverArgs)];
    }
}
