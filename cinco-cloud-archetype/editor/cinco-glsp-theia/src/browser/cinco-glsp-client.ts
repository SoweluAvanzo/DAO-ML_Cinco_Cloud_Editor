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

import { ActionMessageHandler, JsonrpcGLSPClient, ActionMessage, Action } from '@eclipse-glsp/protocol';
import { TheiaJsonrpcGLSPClient } from '@eclipse-glsp/theia-integration';
import { Disposable } from 'vscode-jsonrpc';
import { CincoGLSPClientContribution } from './cinco-glsp-client-contribution';

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
export class CincoGLSPClient extends TheiaJsonrpcGLSPClient {
    protected handlers: Map<string, ((m: ActionMessage<Action>) => void)[]> = new Map();
    protected globalHandler: ((m: ActionMessage<Action>) => void)[] = [];

    override disposeClientSession(params: any): Promise<void> {
        const result = this.checkedConnection.sendRequest(JsonrpcGLSPClient.DisposeClientSessionRequest, params).then(v => {
            this.setDefaultCallback();
        });
        return result;
    }

    protected setDefaultCallback(): Disposable {
        return this.checkedConnection.onNotification(JsonrpcGLSPClient.ActionMessageNotification, msg => {
            this.defaultCallback(msg);
        });
    }

    protected defaultCallback(msg: ActionMessage): void {
        if (msg.clientId) {
            if (msg.clientId === CincoGLSPClientContribution.SYSTEM_ID) {
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

    override onActionMessage(handler: ActionMessageHandler, clientId?: string): Disposable {
        if (clientId) {
            this.addMessageHandler(clientId, handler);
        } else {
            this.globalHandler.push(handler);
        }
        return this.setDefaultCallback();
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
}
