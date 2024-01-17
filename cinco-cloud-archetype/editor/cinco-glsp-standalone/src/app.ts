/********************************************************************************
 * Copyright (c) 2024 Cinco Cloud.
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
import 'reflect-metadata';

import { DiagramLoader, GLSPActionDispatcher, GLSPClient, GLSPWebSocketProvider, MessageAction, StatusAction } from '@eclipse-glsp/client';
import { Container } from 'inversify';
import { join, resolve } from 'path';
import { MessageConnection } from 'vscode-jsonrpc';
import createContainer from './di.config';
import { DIAGRAM_TYPE, DEFAULT_WEBSOCKET_PATH, DEFAULT_SERVER_PORT, CincoGLSPClient } from '@cinco-glsp/cinco-glsp-common';
import { getParameters } from './url-parameters';

const port = DEFAULT_SERVER_PORT;
const id = DEFAULT_WEBSOCKET_PATH;
const diagramType = DIAGRAM_TYPE;
const loc = window.location.pathname;
const currentDir = loc.substring(0, loc.lastIndexOf('/'));
const relativeModelPath = getParameters()['model'] ?? 'main.flowgraph';
const filePath = resolve(join(currentDir, '../../workspace/' + relativeModelPath));
const clientId = DIAGRAM_TYPE;
let glspClient: GLSPClient;
let container: Container;

const webSocketUrl = `ws://localhost:${port}/${id}`;
const wsProvider = new GLSPWebSocketProvider(webSocketUrl);
wsProvider.listen({ onConnection: initialize, onReconnect: reconnect, logger: console });

async function initialize(connectionProvider: MessageConnection, isReconnecting = false): Promise<void> {
    glspClient = new CincoGLSPClient({ id, connectionProvider });
    container = createContainer({ clientId, diagramType, glspClientProvider: async () => glspClient, sourceUri: filePath });
    const actionDispatcher = container.get(GLSPActionDispatcher);
    const diagramLoader = container.get(DiagramLoader);
    await diagramLoader.load({ requestModelOptions: { isReconnecting } });
    if (isReconnecting) {
        const message = `Connection to the ${id} glsp server got closed. Connection was successfully re-established.`;
        const timeout = 5000;
        const severity = 'WARNING';
        actionDispatcher.dispatchAll([StatusAction.create(message, { severity, timeout }), MessageAction.create(message, { severity })]);
        return;
    }
}

async function reconnect(connectionProvider: MessageConnection): Promise<void> {
    glspClient.stop();
    initialize(connectionProvider, true /* isReconnecting */);
}
