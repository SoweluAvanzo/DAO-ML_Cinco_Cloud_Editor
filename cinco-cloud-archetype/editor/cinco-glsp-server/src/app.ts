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

import { createAppModule, ServerModule, SocketServerLauncher } from '@eclipse-glsp/server/node';
import { Container } from 'inversify';
import { CincoDiagramModule } from './diagram/cinco-diagram-module';
import { CincoWebSocketServerLauncher } from './cinco-glsp-websocket-server-launcher';
import { DEFAULT_WEB_SERVER_PORT, DEFAULT_WEBSOCKET_PATH } from '@cinco-glsp/cinco-glsp-common';
import { createCincoCliParser } from './cinco-cli-parser';
import { startWebServer } from './web-server/cinco-web-server';

export async function launch(argv?: string[]): Promise<void> {
    const argParser = createCincoCliParser();

    const options = argParser.parse(argv);
    const appContainer = new Container();
    appContainer.load(createAppModule(options));

    const serverModule = new ServerModule().configureDiagramModule(new CincoDiagramModule());

    // check if webServer should be started
    const webServerPort = Number.parseInt(`${options.webServerPort ?? DEFAULT_WEB_SERVER_PORT}`, 10);
    console.log('Starting WebServer at port: ' + webServerPort);
    startWebServer(webServerPort);

    if (options.webSocket) {
        const launcher = appContainer.resolve(CincoWebSocketServerLauncher);
        launcher.configure(serverModule);
        launcher.start({ port: options.port, host: options.host, path: DEFAULT_WEBSOCKET_PATH });
    } else {
        const launcher = appContainer.resolve(SocketServerLauncher);
        launcher.configure(serverModule);
        launcher.start({ port: options.port, host: options.host });
    }
}
