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
import { Action, ActionHandler, Logger } from '@eclipse-glsp/server-node';
import { inject, injectable } from 'inversify';
import * as uuid from 'uuid';
import { ServerDialogResponse, TypedServerResponseAction } from '../shared/protocol/server-message-protocol';

@injectable()
export class ServerResponseHandler implements ActionHandler {
    @inject(Logger)
    protected readonly logger: Logger;

    actionKinds: string[] = [ServerDialogResponse.KIND];

    static _responseMap: Map<string, (response: TypedServerResponseAction) => Promise<any>> = new Map();

    execute(action: ServerDialogResponse, ...args: unknown[]): Action[] {
        const id = action.messageId;

        if (ServerResponseHandler._responseMap.has(id)) {
            const callback = ServerResponseHandler._responseMap.get(id);
            ServerResponseHandler._responseMap.delete(id);
            if (callback) {
                callback(action);
            }
        }
        return [];
    }

    static getId(): string {
        const newId = uuid.v4();
        return this._responseMap.has(newId) ? this.getId() : newId;
    }

    static registerResponseHandling(callback: (response: TypedServerResponseAction) => any): string {
        const newId = this.getId();
        this._responseMap.set(newId, callback);
        return newId;
    }
}
