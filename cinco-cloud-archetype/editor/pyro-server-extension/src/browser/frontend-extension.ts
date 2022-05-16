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
import { ContainerModule, injectable, inject } from 'inversify';
import { ENDPOINT, PyroLogClient, PyroLogServer } from '../shared/log-protocol';
import { FrontendApplicationContribution, WebSocketConnectionProvider } from '@theia/core/lib/browser';
import { OutputChannelManager, OutputChannel } from '@theia/output/lib/browser/output-channel';

export let output: OutputChannel;
const CHANNEL_NAME = 'PYRO';

export default new ContainerModule(bind => {
    /**
     * Initialize logging from backend and forward it to the output-channel
     */
    bind(PyroLogServer).toDynamicValue(ctx => {
        const client: PyroLogClient = {
            info: (msg: string) => {
                output.appendLine(msg);
            }
        };
        const connection = ctx.container.get(WebSocketConnectionProvider);
        return connection.createProxy<PyroLogServer>(ENDPOINT, client);
    }).inSingletonScope();
    bind(FrontendApplicationContribution).to(PyroFrontendLoggerContribution);
});

@injectable()
export class PyroFrontendLoggerContribution implements FrontendApplicationContribution {
    @inject(PyroLogServer) logServer: PyroLogServer;
    @inject(OutputChannelManager) outputManager: OutputChannelManager;

    initialize(): void {
        output = this.outputManager.getChannel(CHANNEL_NAME);
        this.logServer.getLoggerName()
            .then(result => console.log('*** Started: "' + result + '" ***'))
            .catch(error => output.appendLine('*** Failed to start: "' + error + '" ***'));
    }
}
