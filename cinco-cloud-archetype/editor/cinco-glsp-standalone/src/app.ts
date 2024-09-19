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

import {
    DiagramLoader,
    GLSPActionDispatcher,
    GLSPClient,
    GLSPWebSocketProvider,
    MaybePromise,
    MessageAction,
    StatusAction
} from '@eclipse-glsp/client';
import { Container } from 'inversify';
import { join, resolve } from 'path';
import createContainer from './di.config';
import { DIAGRAM_TYPE, DEFAULT_WEBSOCKET_PATH, DEFAULT_SERVER_PORT, CincoGLSPClient } from '@cinco-glsp/cinco-glsp-common';
import { getParameters } from './url-parameters';
import * as uuid from 'uuid';

const protocol = window.location.protocol && window.location.protocol === 'https' ? 'wss' : 'ws';
const host = window.location.hostname && window.location.hostname.length > 0 ? window.location.hostname : 'localhost';
const port = DEFAULT_SERVER_PORT;
const endpoint_id = DEFAULT_WEBSOCKET_PATH;
const diagramType = DIAGRAM_TYPE;
const loc = window.location.pathname;
const currentDir = loc.substring(0, loc.lastIndexOf('/'));
const relativeModelPath = getParameters()['model'] ?? 'main.flowgraph';
const filePath = resolve(join(currentDir, '../../workspace/' + relativeModelPath));
const clientId = DIAGRAM_TYPE + '_' + uuid.v4();
const htmlContainerId = 'cinco-diagram';
let glspClient: GLSPClient;
let container: Container;

const webSocketUrl = `${protocol}://${host}${port ? ':' + port : ''}/${endpoint_id}`;
const wsProvider = new GLSPWebSocketProvider(webSocketUrl);
wsProvider.listen({ onConnection: initialize, onReconnect: reconnect, logger: console });

function initialize(connectionProvider: any, isReconnecting = false): MaybePromise<void> {
    glspClient = new CincoGLSPClient({ id: endpoint_id, connectionProvider });
    container = createContainer({ clientId, diagramType, glspClientProvider: async () => glspClient, sourceUri: filePath });

    // create canvas
    const htmlContainer = document.getElementById(htmlContainerId);
    if (!htmlContainer) {
        throw Error('No html container found!');
    }
    const canvas = document.createElement('div');
    canvas.id = clientId;
    canvas.className = 'cinco-canvas';
    htmlContainer.appendChild(canvas);

    const actionDispatcher = container.get(GLSPActionDispatcher);
    const diagramLoader = container.get(DiagramLoader);
    return diagramLoader.load({ requestModelOptions: { isReconnecting } }).then(_ => {
        if (isReconnecting) {
            const message = `Connection to the ${endpoint_id} glsp server got closed. Connection was successfully re-established.`;
            const timeout = 5000;
            const severity = 'WARNING';
            actionDispatcher.dispatchAll([
                StatusAction.create(message, { severity, timeout }),
                MessageAction.create(message, { severity })
            ]);
            return;
        }
    });
}

async function reconnect(connectionProvider: any): Promise<void> {
    glspClient.stop();
    return initialize(connectionProvider, true /* isReconnecting */);
}
