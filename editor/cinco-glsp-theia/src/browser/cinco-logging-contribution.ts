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

import { FrontendApplicationContribution } from '@theia/core/lib/browser';
import { inject, injectable } from 'inversify';
import { CincoLoggingClient, CincoLoggingServer } from '../common/cinco-logging-protocol';
import { OutputChannel, OutputChannelManager } from '@theia/output/lib/browser/output-channel';

const CINCO_BACKEND_CHANNEL = 'Cinco Backend';
const GLSP_SERVER_CHANNEL = 'Cinco GLSP Server';

@injectable()
export class CincoLoggingContribution implements FrontendApplicationContribution {
    @inject(CincoLoggingServer) server: CincoLoggingServer;
    @inject(OutputChannelManager) outputChannelManager: OutputChannelManager;
    protected backendChannel: OutputChannel;
    protected serverChannel: OutputChannel;

    initialize(): void {
        // intialize connection
        this.server
            .connect()
            .then(_ => console.log('*** cinco logging connected ***'))
            .then(_ => {
                const client = this.server.getClient!() as CincoLoggingClientNode;
                this.backendChannel = this.outputChannelManager.getChannel(CINCO_BACKEND_CHANNEL);
                this.serverChannel = this.outputChannelManager.getChannel(GLSP_SERVER_CHANNEL);

                client.backendChannel = this.backendChannel;
                client.serverChannel = this.serverChannel;

                this.server.getLog()?.then(fullLog => {
                    fullLog.get('BACKEND')?.forEach(msg => client.forward(msg, 'BACKEND'));
                    fullLog.get('SERVER')?.forEach(msg => client.forward(msg, 'SERVER'));
                    this.server.registerLogging();
                });
            })
            .catch(error => console.log('*** Failed to start cinco logging: "' + error + '" ***'));
    }
}

export class CincoLoggingClientNode implements CincoLoggingClient {
    protected _backendChannel: OutputChannel;
    protected _serverChannel: OutputChannel;

    forward(msg: string, type: 'BACKEND' | 'SERVER'): void {
        switch (type) {
            case 'BACKEND':
                if (this.backendChannel) {
                    this.backendChannel.appendLine(msg);
                }
                break;
            case 'SERVER':
                if (this.serverChannel) {
                    this.serverChannel.append(msg);
                }
                break;
            default:
                return;
        }
    }

    get backendChannel(): OutputChannel {
        return this._backendChannel;
    }

    get serverChannel(): OutputChannel {
        return this._serverChannel;
    }

    set serverChannel(outputChannel: OutputChannel) {
        this._serverChannel = outputChannel;
    }

    set backendChannel(outputChannel: OutputChannel) {
        this._backendChannel = outputChannel;
    }
}
