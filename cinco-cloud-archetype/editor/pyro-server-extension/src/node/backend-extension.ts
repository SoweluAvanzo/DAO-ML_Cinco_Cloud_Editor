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
import { ConnectionHandler, JsonRpcConnectionHandler } from '@theia/core';
import { BackendApplicationContribution } from '@theia/core/lib/node/backend-application';
import { ENDPOINT, LogClient, LogServer, LogServerNode } from '../shared/log-protocol';
import { ServerLauncher } from '../shared/server-launcher';
import { isDebugging } from './debugHandler';
import { cmdArgs, cmdDebugArgs, cmdExec, serverFile, serverPath } from './execVars';

export default new ContainerModule(bind => {
    // setting static values for server
    ServerLauncher.FILE_PATH = path.resolve(serverPath, serverFile);
    ServerLauncher.CMD_EXEC = cmdExec;
    ServerLauncher.ARGS = (isDebugging() ? cmdDebugArgs : cmdArgs).concat(ServerLauncher.FILE_PATH);

    /**
     * binding model-server-launcher
     */
    bind(BackendApplicationContribution).to(ServerLauncher).inSingletonScope();
    /**
     * Initialize logging-server to forward it to the frontend's output-channel
     */
    bind(LogServer).to(LogServerNode).inSingletonScope();
    bind(ConnectionHandler).toDynamicValue(ctx =>
        new JsonRpcConnectionHandler<LogClient>(ENDPOINT + 'pyro', client => {
            const logServer = ctx.container.get<LogServer>(LogServer);
            logServer.setClient(client);
            logServer.info(ServerLauncher.LOG);
            return logServer;
        })
    ).inSingletonScope();
});
