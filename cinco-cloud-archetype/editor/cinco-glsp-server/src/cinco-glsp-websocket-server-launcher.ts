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
import { WebSocketServerLauncher, WebSocketServerOptions } from '@eclipse-glsp/server/node';
import { injectable } from 'inversify';
import { Server } from 'ws';

@injectable()
export class CincoWebSocketServerLauncher extends WebSocketServerLauncher {
    protected override async run(options: WebSocketServerOptions): Promise<void> {
        const resolvedOptions = await this.resolveOptions(options);
        this.server = new Server({ server: resolvedOptions.server, path: resolvedOptions.path });
        const endpoint = `ws://${resolvedOptions.host}:${resolvedOptions.port}${resolvedOptions.path}`;
        this.logger.info(`The Cinco GLSP Websocket launcher is ready to accept new client requests on endpoint '${endpoint}'`);
        console.log(this.startupCompleteMessage.concat(resolvedOptions.port.toString()));

        this.server.on('connection', (ws, req) => {
            const connection = this.createConnection(ws);
            this.createServerInstance(connection);
        });

        return new Promise((resolve, reject) => {
            this.server.on('close', () => resolve(undefined));
            this.server.on('error', error => reject(error));
        });
    }
}
