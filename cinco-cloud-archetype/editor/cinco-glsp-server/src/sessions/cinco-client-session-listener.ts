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
import { ClientSession, ClientSessionListener } from '@eclipse-glsp/server';

export class CincoClientSessionListener implements ClientSessionListener {
    static disposedCallback: Map<string, () => void> = new Map();
    static createdCallback: (clientId: string) => void;
    static initialized = false;

    constructor(createdCallback: (clientId: string) => void) {
        CincoClientSessionListener.initialized = true;
        CincoClientSessionListener.createdCallback = createdCallback;
    }

    static addDisposeCallback(id: string, cb: () => void): void {
        this.disposedCallback.set(id, cb);
    }

    static removeDisposeCallback(clientId: string): void {
        if (this.disposedCallback.has(clientId)) {
            this.disposedCallback.delete(clientId);
        }
    }

    sessionCreated(clientSession: ClientSession): void {
        CincoClientSessionListener.createdCallback(clientSession.id);
    }

    sessionDisposed(client: ClientSession): void {
        for (const entry of CincoClientSessionListener.disposedCallback.entries()) {
            const clientId = entry[0];
            const cb = entry[1];
            if (client.id === clientId) {
                cb();
            }
        }
    }
}
