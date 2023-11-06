/********************************************************************************
 * Copyright (c) 2020-2023 Cinco Cloud and others.
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
import { ServerArgs, ServerArgsRequest, ServerArgsResponse } from '@cinco-glsp/cinco-glsp-common';
import { IActionDispatcher, IActionHandler } from '@eclipse-glsp/client';
import { injectable } from 'inversify';

@injectable()
export class ServerArgsProvider implements IActionHandler {
    static _locks: ((serverArgs: ServerArgs) => void)[] = [];
    static _serverArgs: ServerArgs;

    handle(action: ServerArgsResponse): void {
        if (ServerArgsProvider._locks.length > 0) {
            for (const lock of ServerArgsProvider._locks) {
                lock(action.serverArgs);
            }
        }
        ServerArgsProvider._serverArgs = action.serverArgs;
    }

    static getServerArgs(): Promise<ServerArgs> {
        if (this._serverArgs) {
            return new Promise<ServerArgs>(resolve => {
                resolve(this._serverArgs);
            });
        } else {
            return new Promise<ServerArgs>(resolve => {
                ServerArgsProvider._locks.push(resolve);
            });
        }
    }

    static load(actionDispatcher: IActionDispatcher): Promise<void> {
        const request = ServerArgsRequest.create();
        return actionDispatcher.dispatch(request);
    }
}
