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

import {
    ActionMessageHandler,
    JsonrpcGLSPClient,
    ActionMessage,
    Action,
    DisposeClientSessionParameters,
    InitializeClientSessionParameters,
    BaseJsonrpcGLSPClient,
    ClientState,
    ConnectionProvider
} from '@eclipse-glsp/protocol';
import { SYSTEM_ID } from './protocol/cinco-glsp-lifecycle';

/**
 * # Why CincoGLSPClient instead of TheiaJsonrpcGLSPClient?
 *
 * The default GLSPClient does not support multiple connections and handlers. A new one overwrites an old one.
 * Also, if you close a second diagram (after a first and second was opened), the clientSession will be disposed,
 * i.e. the connection closed and the related handler removed. The other diagram will have no connection, since it's
 * handler was overwritten. This is insufficient.
 * Also we need to have an open "SYSTEM"-connection to update the meta-modell inside our GLSPServer.
 * Thus this class is preventing the connection from beiing completly diposed, scales the handling of messages for
 * multiple clienst and has build-in SYSTEM-message support, for broadcast propagation.
 */
export class CincoGLSPClient extends BaseJsonrpcGLSPClient {
    protected localClients: Map<string, InitializeClientSessionParameters> = new Map();
    protected handlers: Map<string, ((m: ActionMessage<Action>) => void)[]> = new Map();
    protected globalHandler: ((m: ActionMessage<Action>) => void)[] = [];
    protected reconnect: ((client: CincoGLSPClient) => Promise<void>) | undefined;
    protected theiaMessageService?: any; // optional theia message service
    protected override connectionProvider: ConnectionProvider;

    constructor(options: any) {
        super(options);
        if (options.messageService) {
            this.theiaMessageService = options.messageService;
        }
        this.reconnect = options.reconnect;
    }

    setConnectionProvider(connectionProvider: any /* MessageConnection */): void {
        this.connectionProvider = connectionProvider;
    }

    protected override handleConnectionError(error: Error, message: any, count: number): void {
        // super.handleConnectionError(error, message, count);
        this.stop();
        this.state = ClientState.ServerError;
        if (this.theiaMessageService) {
            this.theiaMessageService.error(`Connection the ${this.id} glsp server is erroring.`);
        }
        if (this.reconnect) {
            this.reconnect(this);
        }
    }

    protected override handleConnectionClosed(): void {
        if (this.theiaMessageService && this.state !== ClientState.Stopping && this.state !== ClientState.Stopped) {
            this.theiaMessageService.error(
                `Connection to the ${this.id} glsp server got closed.` +
                    (this.reconnect ? ' Reconnecting! Please, close and reopen canvas!' : '')
            );
        }
        this.state = this.state === ClientState.Stopping || this.state === ClientState.Stopped ? this.state : ClientState.ServerError;
        if (this.reconnect) {
            this.connectionPromise = undefined;
            this.resolvedConnection = undefined;
            this.reconnect(this);
            return;
        }
        super.handleConnectionClosed();
    }

    override disposeClientSession(params: DisposeClientSessionParameters): Promise<void> {
        const result = this.checkedConnection.sendRequest(JsonrpcGLSPClient.DisposeClientSessionRequest, params).then(() => {
            this.removeLocalClient(params.clientSessionId);
            this.setDefaultCallback();
        });
        return result;
    }

    protected setDefaultCallback(): any {
        return this.checkedConnection.onNotification(JsonrpcGLSPClient.ActionMessageNotification, (msg: ActionMessage) => {
            this.defaultCallback(msg);
        });
    }

    protected defaultCallback(msg: ActionMessage): void {
        if (msg.clientId) {
            if (msg.clientId === SYSTEM_ID) {
                for (const key of this.handlers.keys()) {
                    const handlersOfKey = this.handlers.get(key) ?? [];
                    for (const h of handlersOfKey) {
                        h(msg);
                    }
                }
            } else {
                for (const h of this.handlers.get(msg.clientId) ?? []) {
                    h(msg);
                }
            }
        } else {
            for (const h of this.globalHandler) {
                h(msg);
            }
        }
    }

    override onActionMessage(handler: ActionMessageHandler, clientId?: string): any {
        if (clientId) {
            this.addMessageHandler(clientId, handler);
        } else {
            this.globalHandler.push(handler);
        }
        return this.setDefaultCallback();
    }

    override initializeClientSession(params: InitializeClientSessionParameters): Promise<void> {
        return this.checkedConnection.sendRequest(JsonrpcGLSPClient.InitializeClientSessionRequest, params).then(() => {
            this.addLocalClient(params);
        });
    }

    override sendActionMessage(message: ActionMessage): void {
        if (message.clientId === SYSTEM_ID) {
            for (const client of this.localClients.keys()) {
                const messageForClient = { ...message };
                messageForClient.clientId = client;
                this.checkedConnection.sendNotification(JsonrpcGLSPClient.ActionMessageNotification, messageForClient);
            }
        } else {
            this.checkedConnection.sendNotification(JsonrpcGLSPClient.ActionMessageNotification, message);
        }
    }

    addMessageHandler(clientId: string, handler: (m: ActionMessage) => void): void {
        const handlers = this.handlers.get(clientId) ?? [];
        handlers.push(handler);
        this.handlers.set(clientId, handlers);
    }

    removeHandler(clientId: string, handler?: (m: ActionMessage) => void): void {
        if (this.handlers.has(clientId)) {
            if (handler) {
                const handlers = this.handlers.get(clientId)!.filter(h => h !== handler);
                this.handlers.set(clientId, handlers);
            } else {
                this.handlers.delete(clientId);
            }
        }
    }

    addLocalClient(params: InitializeClientSessionParameters): void {
        if (!this.localClients.has(params.clientSessionId)) {
            this.localClients.set(params.clientSessionId, params);
        }
    }

    removeLocalClient(clientId: string): void {
        this.localClients.delete(clientId);
    }

    getLocalClients(): InitializeClientSessionParameters[] {
        return Array.from(this.localClients.values());
    }

    isConnected(clientId: string): boolean {
        return this.localClients.has(clientId);
    }

    isConnectingOrRunning(): boolean {
        return this.state === ClientState.Initial || this.state === ClientState.Starting || this.state === ClientState.Running;
    }

    resetInitializeResult(): void {
        this._initializeResult = undefined;
    }

    protected override get checkedConnection(): any /* Message Connection */ {
        if (!this.isConnectionActive()) {
            if (this.reconnect) {
                this.reconnect(this);
            }
            throw new Error(JsonrpcGLSPClient.ClientNotReadyMsg);
        }
        return this.resolvedConnection!;
    }
}
