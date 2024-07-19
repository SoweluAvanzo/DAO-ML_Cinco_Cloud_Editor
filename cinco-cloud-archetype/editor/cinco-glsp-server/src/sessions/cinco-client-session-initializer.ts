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

import { Action, ActionDispatcher, Args, ClientSessionInitializer, ClientSessionManager, InjectionContainer } from '@eclipse-glsp/server';
import { Container, inject, injectable } from 'inversify';
import { MetaSpecificationLoader } from '../meta/meta-specification-loader';
import { MetaSpecificationResponseAction, MetaSpecification } from '@cinco-glsp/cinco-glsp-common';
import { existsFile, GraphModelWatcher, isMetaDevMode } from '@cinco-glsp/cinco-glsp-api';
import { CincoClientSessionListener } from './cinco-client-session-listener';

@injectable()
export class CincoClientSessionInitializer implements ClientSessionInitializer {
    static clientSessionsActionDispatcher: Map<number, ActionDispatcher> = new Map();

    @inject(InjectionContainer)
    protected serverContainer: Container;
    @inject(ClientSessionManager) protected sessions: ClientSessionManager;
    @inject(ActionDispatcher)
    protected actionDispatcher: ActionDispatcher;
    protected graphModelWatcherCallback: string;

    initialize(_args?: Args): void {
        CincoClientSessionInitializer.addClient(this.serverContainer.id, this.actionDispatcher);
        if (!CincoClientSessionListener.initialized) {
            const createdCallback = async (clientId: string): Promise<void> => {
                this.updateGraphModelWatcher(clientId);
                MetaSpecificationLoader.addReloadCallback(async () => {
                    this.updateGraphModelWatcher(clientId);
                });
                if (isMetaDevMode()) {
                    const watchInfo = await MetaSpecificationLoader.watch(async () => {
                        const response = MetaSpecificationResponseAction.create(MetaSpecification.get());
                        this.actionDispatcher.dispatch(response);
                        this.sendToAllOtherClients(response);
                    }, 'metaspecWatcher_' + clientId);
                    CincoClientSessionListener.addDisposeCallback(clientId, () => {
                        MetaSpecificationLoader.unwatch(watchInfo);
                        GraphModelWatcher.removeCallback(clientId);
                    });
                }
            };
            this.sessions.addListener(new CincoClientSessionListener(createdCallback));
        }
    }

    updateGraphModelWatcher(clientId: string): void {
        if (clientId !== 'SYSTEM') {
            return;
        }
        // add graphmodel Watcher
        GraphModelWatcher.removeCallback(clientId);
        GraphModelWatcher.addCallback(clientId, async dirtyFiles => {
            for (const dirtyFile of dirtyFiles) {
                const wasMovedOrDeleted = dirtyFile.eventType === 'rename' && !existsFile(dirtyFile.path);
                const wasMovedRenamedOrCreated = dirtyFile.eventType === 'rename' && existsFile(dirtyFile.path);
                const wasChanged = dirtyFile.eventType === 'change';
                if (wasMovedOrDeleted) {
                    // trigger delete Hook
                    console.log('File was deleted: ' + dirtyFile.path);
                } else if (wasMovedRenamedOrCreated) {
                    // trigger changedPath (moved or renamed) Hook
                    console.log('File was Moved/Renamed/Created: ' + dirtyFile.path);
                } else if (wasChanged) {
                    // console.log('Graphmodel-File was changed: ' + dirtyFile.path);
                }
            }
        });
        GraphModelWatcher.watch(clientId);
    }

    static addClient(id: number, actionDispatcher: ActionDispatcher): void {
        CincoClientSessionInitializer.clientSessionsActionDispatcher.set(id, actionDispatcher);
    }

    static removeClient(id: number): void {
        CincoClientSessionInitializer.clientSessionsActionDispatcher.delete(id);
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
