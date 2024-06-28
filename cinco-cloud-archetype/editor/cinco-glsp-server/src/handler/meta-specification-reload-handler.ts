/********************************************************************************
 * Copyright (c) 2022 Cinco Cloud.
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
import { MetaSpecification, MetaSpecificationReloadAction, MetaSpecificationResponseAction } from '@cinco-glsp/cinco-glsp-common';
import { Action, ActionDispatcher, ActionHandler, InjectionContainer } from '@eclipse-glsp/server';
import { injectable, inject, Container } from 'inversify';
import { MetaSpecificationLoader } from '../meta/meta-specification-loader';
import { getLanguageFolder, isMetaDevMode } from '@cinco-glsp/cinco-glsp-api';
import { CincoClientSessionInitializer } from '../diagram/cinco-client-session-initializer';

@injectable()
export class MetaSpecificationReloadHandler implements ActionHandler {
    @inject(InjectionContainer)
    protected serverContainer: Container;
    @inject(ActionDispatcher)
    protected actionDispatcher: ActionDispatcher;
    actionKinds: string[] = [MetaSpecificationReloadAction.KIND];

    async execute(action: MetaSpecificationReloadAction, ...args: unknown[]): Promise<Action[]> {
        if (isMetaDevMode()) {
            const items = action.items;
            const clear = action.clear ?? false;
            if (clear) {
                MetaSpecification.clear();
            }
            if (items === undefined || items.length <= 0) {
                // default behaviour
                const folderPath = getLanguageFolder();
                await MetaSpecificationLoader.load(folderPath);
            } else {
                for (const item of items) {
                    const folderPaths = item.folderPaths;
                    for (const folderPath of folderPaths) {
                        await MetaSpecificationLoader.load(folderPath);
                    }
                }
            }
            // forward the update to the clients, after reload
            const response = MetaSpecificationResponseAction.create(MetaSpecification.get());
            this.sendToAllOtherClients(response);
            return [response];
        }
        const response = MetaSpecificationResponseAction.create(MetaSpecification.get());
        return [response];
    }

    sendToAllOtherClients(message: Action): void {
        const actionDispatcherMap = CincoClientSessionInitializer.clientSessionsActionDispatcher;
        for (const entry of actionDispatcherMap.entries()) {
            if (entry[0] !== this.serverContainer.id) {
                entry[1].dispatch(message).catch(e => {
                    console.log('An error occured, maybe the client is not connected anymore:\n' + e);
                    CincoClientSessionInitializer.removeClient(entry[0]);
                });
            }
        }
    }
}
