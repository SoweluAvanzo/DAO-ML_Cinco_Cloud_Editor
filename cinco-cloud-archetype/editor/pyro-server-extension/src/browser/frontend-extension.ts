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
import { FrontendApplicationContribution, WebSocketConnectionProvider } from '@theia/core/lib/browser';
import { OutputChannel, OutputChannelManager } from '@theia/output/lib/browser/output-channel';
import { ContainerModule, inject, injectable } from 'inversify';

import { ENDPOINT, LogClient, LogServer } from '../shared/log-protocol';
import { registerEventHandler } from '../shared/drag-and-drop-handler';

export let output: OutputChannel;
const CHANNEL_NAME = 'PYRO';

export default new ContainerModule(bind => {
    registerEventHandler();
    /**
     * Initialize logging from backend and forward it to the output-channel
     */
    bind(LogServer).toDynamicValue(ctx => {
        const client: LogClient = {
            info: (msg: string) => {
                output.appendLine(msg);
            },
            error: (msg: string) => {
                output.appendLine(msg);
            }
        };
        const connection = ctx.container.get(WebSocketConnectionProvider);
        return connection.createProxy<LogServer>(ENDPOINT + 'pyro', client);
    }).inSingletonScope();
    bind(FrontendApplicationContribution).to(FrontendLoggerContribution);
});

@injectable()
export class FrontendLoggerContribution implements FrontendApplicationContribution {
    @inject(LogServer) logServer: LogServer;
    @inject(OutputChannelManager) outputManager: OutputChannelManager;

    initialize(): void {
        output = this.outputManager.getChannel(CHANNEL_NAME);
        this.logServer.getLoggerName()
            .then(result => console.log('*** Started: "' + result + '" ***'))
            .catch(error => output.appendLine('*** Failed to start: "' + error + '" ***'));
    }
}
