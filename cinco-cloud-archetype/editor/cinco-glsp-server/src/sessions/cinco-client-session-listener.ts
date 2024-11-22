/********************************************************************************
 * Copyright (c) 2024 Cinco Cloud.
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
import { GraphGModelFactory, GraphModelState } from '@cinco-glsp/cinco-glsp-api';
import { ContextBundle } from '@cinco-glsp/cinco-glsp-api/lib/api/context-bundle';
import { ActionDispatcher, ClientSession, ClientSessionListener, GModelFactory, Logger, SourceModelStorage } from '@eclipse-glsp/server';

export class CincoClientSessionListener implements ClientSessionListener {
    static disposedCallback: Map<string, (() => void)[]> = new Map();
    static createdCallback: (clientId: string, contextBundle: ContextBundle) => void;
    static initialized = false;

    constructor(createdCallback: (clientId: string, contextBundle: ContextBundle) => void) {
        CincoClientSessionListener.initialized = true;
        CincoClientSessionListener.createdCallback = createdCallback;
    }

    static addDisposeCallback(id: string, cb: () => void): void {
        if (!this.disposedCallback.has(id)) {
            this.disposedCallback.set(id, []);
        }
        this.disposedCallback.get(id)?.push(cb);
    }

    static removeDisposeCallback(clientId: string): void {
        if (this.disposedCallback.has(clientId)) {
            this.disposedCallback.delete(clientId);
        }
    }

    sessionCreated(clientSession: ClientSession): void {
        const graphModelState = clientSession.container.get(GraphModelState);
        const actionDispatcher = clientSession.container.get(ActionDispatcher) as ActionDispatcher;
        const logger = clientSession.container.get(Logger) as Logger;
        const sourceModelStorage = clientSession.container.get(SourceModelStorage) as SourceModelStorage;
        const frontendModelFactory = clientSession.container.get(GModelFactory) as GraphGModelFactory;
        const contextBundle = new ContextBundle(graphModelState, logger, actionDispatcher, sourceModelStorage, frontendModelFactory);
        CincoClientSessionListener.createdCallback(clientSession.id, contextBundle);
    }

    sessionDisposed(client: ClientSession): void {
        for (const entry of CincoClientSessionListener.disposedCallback.entries()) {
            const clientId = entry[0];
            const cbs = entry[1];
            if (client.id === clientId) {
                for (const cb of cbs) {
                    try {
                        cb();
                    } catch (e) {
                        console.log(e);
                    }
                }
            }
        }
    }
}
