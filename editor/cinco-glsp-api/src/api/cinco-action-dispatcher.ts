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

import { Action, ClientId, DefaultActionDispatcher, Logger, UpdateModelAction } from '@eclipse-glsp/server';
import { inject, injectable } from 'inversify';

@injectable()
export class CincoActionDispatcher extends DefaultActionDispatcher {
    @inject(Logger)
    _logger: Logger;
    @inject(ClientId)
    override readonly clientId: string;

    async request(action: Action): Promise<Action[]> {
        this._logger.debug('Dispatch action:', action.kind);
        const handledOnClient = this.clientActionForwarder.handle(action);

        const actionHandlers = this.actionHandlerRegistry.get(action.kind);
        if (!handledOnClient && actionHandlers.length === 0) {
            this._logger.error(`No handler registered for action kind: ${action.kind}`);
            return Promise.resolve([]);
        }

        const responses: Action[] = [];
        for (const handler of actionHandlers) {
            const response = await this.executeHandler(handler, action);
            responses.push(...response);
        }

        if (UpdateModelAction.is(action) && this.postUpdateQueue.length > 0) {
            responses.push(...this.postUpdateQueue);
            this.postUpdateQueue = [];
        }

        return responses;
    }

    protected override async doDispatch(action: Action): Promise<void> {
        this._logger.debug('Dispatch action:', action.kind);
        const handledOnClient = this.clientActionForwarder.handle(action);

        const actionHandlers = this.actionHandlerRegistry.get(action.kind);
        if (!handledOnClient && actionHandlers.length === 0) {
            this._logger.error(`No handler registered for action kind: ${action.kind}`);
            return Promise.resolve();
        }

        const responses: Action[] = [];
        for (const handler of actionHandlers) {
            const response = await this.executeHandler(handler, action);
            responses.push(...response);
        }

        if (UpdateModelAction.is(action) && this.postUpdateQueue.length > 0) {
            responses.push(...this.postUpdateQueue);
            this.postUpdateQueue = [];
        }

        await this.dispatchResponses(responses);
    }
}
