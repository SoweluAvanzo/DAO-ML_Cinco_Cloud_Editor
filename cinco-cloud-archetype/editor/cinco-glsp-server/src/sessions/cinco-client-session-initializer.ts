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

import {
    Action,
    ActionDispatcher,
    Args,
    ClientSessionInitializer,
    ClientSessionManager,
    InjectionContainer,
    Logger
} from '@eclipse-glsp/server';
import { Container, inject, injectable } from 'inversify';
import { MetaSpecificationLoader } from '../meta/meta-specification-loader';
import {
    MetaSpecificationResponseAction,
    MetaSpecification,
    HookType,
    getFileExtension,
    getGraphModelOfFileType,
    DeleteArgument
} from '@cinco-glsp/cinco-glsp-common';
import {
    existsFile,
    GraphModelState,
    GraphModelStorage,
    GraphModelWatcher,
    HookManager,
    isMetaDevMode,
    readJson
} from '@cinco-glsp/cinco-glsp-api';
import { CincoClientSessionListener } from './cinco-client-session-listener';

@injectable()
export class CincoClientSessionInitializer implements ClientSessionInitializer {
    static clientSessionsActionDispatcher: Map<number, ActionDispatcher> = new Map();

    @inject(InjectionContainer) protected serverContainer: Container;
    @inject(ClientSessionManager) protected sessions: ClientSessionManager;
    @inject(ActionDispatcher) protected actionDispatcher: ActionDispatcher;
    @inject(Logger) protected logger: Logger;

    protected graphModelWatcherCallback: string;

    initialize(_args?: Args): void {
        CincoClientSessionInitializer.addClient(this.serverContainer.id, this.actionDispatcher);
        if (!CincoClientSessionListener.initialized) {
            const createdCallback = async (
                clientId: string,
                modelState: GraphModelState,
                actionDispatcher: ActionDispatcher
            ): Promise<void> => {
                this.updateGraphModelWatcher(clientId, modelState, actionDispatcher);
                MetaSpecificationLoader.addReloadCallback(async () => {
                    this.updateGraphModelWatcher(clientId, modelState, actionDispatcher);
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

    updateGraphModelWatcher(clientId: string, modelState: GraphModelState, actionDispatcher: ActionDispatcher): void {
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
                    const fileExtension = getFileExtension(dirtyFile.path);
                    const deleteArgument = { kind: 'Delete', modelElementId: '<NONE>', deleted: dirtyFile.path } as DeleteArgument;
                    const graphModelType = getGraphModelOfFileType(fileExtension);
                    if (!graphModelType) {
                        throw new Error('File extension has no graphmodeltype: ' + fileExtension);
                    }
                    deleteArgument.elementTypeId = graphModelType?.elementTypeId;
                    HookManager.executeHook(deleteArgument, HookType.POST_DELETE, modelState, this.logger, actionDispatcher);
                } else {
                    const model = readJson(dirtyFile.path, { hideError: true }) as any | undefined;
                    if (!model || !model.id) {
                        // Modelfile could not be read from path. It is either just intiialized (empty file) or moved.
                        return;
                    }
                    if (!modelState.root) {
                        await GraphModelStorage.loadSourceModel(dirtyFile.path, modelState, this.logger, actionDispatcher);
                    }
                    if (!modelState.index.getRoot()) {
                        throw new Error('Model could not been loaded: ' + dirtyFile.path);
                    }
                    if (wasMovedRenamedOrCreated) {
                        // trigger changedPath (moved or renamed) Hook
                        HookManager.executeHook(
                            { kind: 'ModelFileChange', modelElementId: model.id },
                            HookType.POST_PATH_CHANGE,
                            modelState,
                            this.logger,
                            actionDispatcher
                        );
                    } else if (wasChanged) {
                        // trigger changedContent Hook
                        HookManager.executeHook(
                            { kind: 'ModelFileChange', modelElementId: model.id },
                            HookType.POST_CONTENT_CHANGE,
                            modelState,
                            this.logger,
                            actionDispatcher
                        );
                    }
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
