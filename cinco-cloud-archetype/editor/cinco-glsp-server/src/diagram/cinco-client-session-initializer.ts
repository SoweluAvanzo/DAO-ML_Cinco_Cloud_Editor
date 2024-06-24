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

import { ActionDispatcher, Args, ClientSessionInitializer, ClientSessionManager, InjectionContainer } from '@eclipse-glsp/server';
import { Container, inject, injectable } from 'inversify';
import { MetaSpecificationLoader } from '../meta/meta-specification-loader';
import { MetaSpecificationReloadAction } from '@cinco-glsp/cinco-glsp-common';
import { isMetaDevMode } from '@cinco-glsp/cinco-glsp-api';

@injectable()
export class CincoClientSessionInitializer implements ClientSessionInitializer {
    static clientSessionsActionDispatcher: Map<number, ActionDispatcher> = new Map();

    @inject(InjectionContainer)
    protected serverContainer: Container;
    @inject(ClientSessionManager) protected sessions: ClientSessionManager;
    actionKinds: string[] = [MetaSpecificationReloadAction.KIND];
    @inject(ActionDispatcher)
    protected actionDispatcher: ActionDispatcher;

    initialize(_args?: Args): void {
        CincoClientSessionInitializer.addClient(this.serverContainer.id, this.actionDispatcher);
        if (isMetaDevMode()) {
            MetaSpecificationLoader.watch(async () => {
                await this.actionDispatcher.dispatch(MetaSpecificationReloadAction.create([], true));
            });
        }
    }

    static addClient(id: number, actionDispatcher: ActionDispatcher): void {
        CincoClientSessionInitializer.clientSessionsActionDispatcher.set(id, actionDispatcher);
    }

    static removeClient(id: number): void {
        CincoClientSessionInitializer.clientSessionsActionDispatcher.delete(id);
    }
}
