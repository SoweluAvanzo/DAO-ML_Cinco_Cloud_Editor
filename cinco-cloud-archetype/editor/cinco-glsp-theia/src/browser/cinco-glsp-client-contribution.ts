/********************************************************************************
 * Copyright (c) 2019-2023 EclipseSource and others.
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
import { BaseGLSPClientContribution, WebSocketConnectionOptions } from '@eclipse-glsp/theia-integration/lib/browser';
import { EnvVariablesServer } from '@theia/core/lib/common/env-variables';
import { inject, injectable, multiInject } from '@theia/core/shared/inversify';
import { getDiagramConfiguration } from '../common/cinco-language';
import { ActionMessage } from '@eclipse-glsp/sprotty';
import {
    DEFAULT_SERVER_PORT,
    DEFAULT_WEBSOCKET_PATH,
    DIAGRAM_TYPE,
    LANGUAGE_UPDATE_COMMAND,
    LanguageUpdateMessage,
    MetaSpecificationReloadCommand,
    MetaSpecificationResponseAction,
    WEBSOCKET_PORT_KEY,
    CincoGLSPClient,
    SYSTEM_ID,
    WEBSOCKET_HOST_MAPPING,
    META_SPEC_PROVIDER_COMMAND,
    MetaSpecification
} from '@cinco-glsp/cinco-glsp-common';
import { CommandRegistry } from '@theia/core';
import { InitializeClientSessionParameters } from '@eclipse-glsp/protocol';
import { MetaSpecificationReloadCommandHandler } from './meta/meta-specification-reload-command-handler';
import { WebSocketConnectionInfo, isValidWebSocketAddress } from '@eclipse-glsp/theia-integration/lib/common';
import { CincoGLSPDiagramWidget } from './diagram/cinco-glsp-diagram-widget';

@injectable()
export class CincoGLSPClientContribution extends BaseGLSPClientContribution {
    @inject(EnvVariablesServer)
    protected readonly envVariablesServer: EnvVariablesServer;
    @inject(CommandRegistry)
    protected readonly commandRegistry: CommandRegistry;
    @multiInject(CincoGLSPDiagramWidget)
    protected readonly diagramWidgets: CincoGLSPDiagramWidget[];

    readonly id = getDiagramConfiguration().contributionId;
    readonly fileExtensions = getDiagramConfiguration().fileExtensions;

    constructor() {
        super();
        this.initializeSession(SYSTEM_ID);
    }

    protected override async getWebSocketConnectionOptions(): Promise<WebSocketConnectionOptions | undefined> {
        const webSocketPort = await this.getWebSocketPortFromEnv();
        const ssl = await this.envVariablesServer.getValue('USE_SSL');
        const websocketHostMapping = await this.envVariablesServer.getValue(WEBSOCKET_HOST_MAPPING);
        const host = window.location.hostname ?? '0.0.0.0';
        return {
            path: DEFAULT_WEBSOCKET_PATH,
            port: webSocketPort ?? DEFAULT_SERVER_PORT,
            protocol: ssl && ssl.value === 'true' ? 'wss' : 'ws',
            host:
                websocketHostMapping && websocketHostMapping.value && websocketHostMapping.value?.length > 0
                    ? `${host}/${websocketHostMapping.value}`
                    : undefined
        };
    }

    protected async getWebSocketPortFromEnv(): Promise<number | undefined> {
        const envVar = await this.envVariablesServer.getValue(WEBSOCKET_PORT_KEY);
        if (envVar && envVar.value) {
            const webSocketPort = Number.parseInt(envVar.value, 10);
            if (isNaN(webSocketPort) || webSocketPort < 0 || webSocketPort > 65535) {
                throw new Error('Value of environment variable ' + WEBSOCKET_PORT_KEY + ' is not a valid port');
            }
            return webSocketPort;
        }
        return DEFAULT_SERVER_PORT;
    }

    override getWebsocketAddress(opts: WebSocketConnectionOptions): string {
        const address = typeof opts === 'string' ? opts : this.getWebSocketAddress(opts);
        if (!address) {
            throw new Error(`Could not derive server websocket address from options: ${JSON.stringify(opts, undefined, 2)}`);
        }
        if (!isValidWebSocketAddress(address)) {
            throw new Error(`The given websocket server address is not valid: ${address}`);
        }

        return address;
    }

    getWebSocketAddress(info: Partial<WebSocketConnectionInfo>): string | undefined {
        if ('path' in info && info.path !== undefined) {
            const protocol = info.protocol ?? 'ws';
            if (info.host) {
                return `${protocol}://${info.host}/${info.path}`;
            }
            const host = window.location.hostname ?? '0.0.0.0';
            return `${protocol}://${host}:${info.port}/${info.path}`;
        }
        return undefined;
    }

    initializeSession(id: string, reconnect?: boolean, params?: InitializeClientSessionParameters): Promise<void> {
        console.log('Initializing GLSP Client Connection: ' + this.id);
        return this.glspClient.then(client => {
            if (!(client instanceof CincoGLSPClient)) {
                throw Error('Client is no CincoGLSPClient. Maybe the API has changed, please review.');
            }
            return this.initialize(client).then(_v => {
                client
                    .initializeClientSession(
                        params ??
                            ({
                                clientSessionId: id,
                                diagramType: DIAGRAM_TYPE,
                                clientActionKinds: id === SYSTEM_ID ? [MetaSpecificationResponseAction.KIND] : []
                            } as InitializeClientSessionParameters)
                    )
                    .then(() => {
                        console.log('CincoGLSPClient "' + id + '" ' + (reconnect ? 'reconnected!' : 'connected!'));
                        if (id === SYSTEM_ID) {
                            client.onActionMessage((m: ActionMessage) => {
                                const action = m.action;
                                if (MetaSpecificationResponseAction.KIND === action.kind) {
                                    this.commandRegistry.executeCommand(LANGUAGE_UPDATE_COMMAND.id, {
                                        metaSpecification: (m.action as MetaSpecificationResponseAction).metaSpecification
                                    } as LanguageUpdateMessage);
                                    // register command to fetch current meta-spec by other vscode/theia extensions
                                    if (this.commandRegistry.commands.filter(c => c.id === META_SPEC_PROVIDER_COMMAND.id).length <= 0) {
                                        this.commandRegistry.registerCommand(META_SPEC_PROVIDER_COMMAND, {
                                            execute: _ => MetaSpecification.get()
                                        });
                                    }
                                }
                            }, SYSTEM_ID);
                            if (!reconnect) {
                                this.commandRegistry.registerCommand(
                                    { id: MetaSpecificationReloadCommand.ID, label: 'Reload Meta-Specification', category: 'Cinco Cloud' },
                                    new MetaSpecificationReloadCommandHandler(client)
                                );
                                this.commandRegistry.executeCommand(MetaSpecificationReloadCommand.ID);
                            }
                        }
                    });
            });
        });
    }

    protected override async createGLSPClient(connectionProvider: any): Promise<CincoGLSPClient> {
        return new CincoGLSPClient({
            id: this.id,
            connectionProvider,
            messageService: this.messageService,
            reconnect: (client: CincoGLSPClient) => this.doReconnect(client, connectionProvider)
        });
    }

    protected override async activateClient(): Promise<void> {
        const connection = await this.createConnection();
        const client = await this.createGLSPClient(connection);
        await this.addReconnect(client, connection);
        return this.start(client);
    }

    protected async addReconnect(client: CincoGLSPClient, connection: any): Promise<void> {
        return connection.onDispose(() => {
            client.stop().then(_ => {
                this.doReconnect(client, connection);
            });
        });
    }

    protected async doReconnect(client: CincoGLSPClient, connection: any): Promise<void> {
        setTimeout(async () => {
            const reConnection = await this.createConnection();
            client.setConnectionProvider(reConnection);
            await this.addReconnect(client, connection);
            client.resetInitializeResult();
            await client.start();
            await Promise.all(
                client.getLocalClients().map(async params => {
                    await this.initializeSession(params.clientSessionId, true, params);
                })
            );
        }, 3000);
        // refresh canvas here (currently not working)
        // Canvas looses DIContainer for some reason (?)
    }
}
