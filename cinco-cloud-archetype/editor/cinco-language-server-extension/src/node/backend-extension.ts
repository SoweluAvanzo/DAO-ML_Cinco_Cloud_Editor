/*!
 * Copyright (c) 2019-2020 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */

import * as path from 'path';
import { ContainerModule } from 'inversify';
import { BackendApplicationContribution } from '@theia/core/lib/node/backend-application';
import { ConnectionHandler, JsonRpcConnectionHandler } from '@theia/core';
import { ENDPOINT, LogClient, LogServer, LogServerNode } from '../shared/log-protocol';
import { ServerLauncher } from '../shared/server-launcher';

export default new ContainerModule(bind => {
    const languageServerPath = path.resolve(__dirname, '..', '..', 'language-server', 'bin', 'cinco-language-server');

    // setting static values for server
    ServerLauncher.FILE_PATH = languageServerPath;
    ServerLauncher.CMD_EXEC = languageServerPath;
    ServerLauncher.ARGS = [];

    /**
     * binding language-server-launcher
     */
    bind(BackendApplicationContribution).to(ServerLauncher).inSingletonScope();
    /**
     * Initialize logging-server to forward it to the frontend's output-channel
     */
    bind(LogServer).to(LogServerNode).inSingletonScope();
    bind(ConnectionHandler).toDynamicValue(ctx =>
        new JsonRpcConnectionHandler<LogClient>(ENDPOINT + 'cinco', client => {
            const logServer = ctx.container.get<LogServer>(LogServer);
            logServer.setClient(client);
            logServer.info(ServerLauncher.LOG);
            return logServer;
        })
    ).inSingletonScope();
});
