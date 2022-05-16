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
import { ConnectionHandler, JsonRpcConnectionHandler } from '@theia/core';
import { BackendApplicationContribution } from '@theia/core/lib/node/backend-application';
import { ContainerModule, injectable } from 'inversify';
import { ENDPOINT, PyroLogClient, PyroLogServer } from '../shared/log-protocol';
import { LOG, ServerLauncher } from './server-launcher';

export default new ContainerModule(bind => {
    /**
     * binding model-server-launcher
     */
    bind(BackendApplicationContribution).to(ServerLauncher).inSingletonScope();
    /**
     * Initialize logging-server to forward it to the frontend's output-channel
     */
    bind(PyroLogServer).to(PyroLogServerNode).inSingletonScope();
    bind(ConnectionHandler).toDynamicValue(ctx =>
        new JsonRpcConnectionHandler<PyroLogClient>(ENDPOINT, client => {
            const logServer = ctx.container.get<PyroLogServer>(PyroLogServer);
            logServer.setClient(client);
            logServer.info(LOG);
            return logServer;
        })
    ).inSingletonScope();
});

@injectable()
class PyroLogServerNode implements PyroLogServer {
    client: PyroLogClient | undefined;

    async getLoggerName(): Promise<string> {
        return 'PyroLogServer';
    }

    info(msg: string): void {
        if (this.client) {
            this.client!.info(msg);
        }
    }

    dispose(): void {
        this.info('---disposing---');
    }

    setClient(client: PyroLogClient | undefined): void {
        this.client = client;
    }
}
