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
    Logger,
    ModelSubmissionHandler,
    RequestContextActions,
    SourceModelStorage
} from '@eclipse-glsp/server';
import { Container, inject, injectable } from 'inversify';
import { MetaSpecificationLoader } from '../meta/meta-specification-loader';
import {
    MetaSpecificationResponseAction,
    MetaSpecification,
    HookType,
    getFileExtension,
    getGraphModelOfFileType,
    DeleteArgument,
    ValidationRequestAction,
    AppearanceUpdateRequestAction,
    ValueUpdateRequestAction,
    hasValidation,
    hasAppearanceProvider,
    hasValueProvider,
    SYSTEM_ID
} from '@cinco-glsp/cinco-glsp-common';
import {
    existsFile,
    GraphModelIndex,
    GraphModelState,
    GraphModelStorage,
    GraphModelWatcher,
    HookManager,
    isMetaDevMode,
    readJson
} from '@cinco-glsp/cinco-glsp-api';
import { CincoClientSessionListener } from './cinco-client-session-listener';
import { CincoActionDispatcher } from '@cinco-glsp/cinco-glsp-api/lib/api/cinco-action-dispatcher';

@injectable()
export class CincoClientSessionInitializer implements ClientSessionInitializer {
    static clientSessionsActionDispatcher: Map<number, ActionDispatcher> = new Map();

    @inject(InjectionContainer) protected serverContainer: Container;
    @inject(ClientSessionManager) protected sessions: ClientSessionManager;
    @inject(ActionDispatcher) protected actionDispatcher: ActionDispatcher;
    @inject(Logger) protected logger: Logger;
    @inject(SourceModelStorage)
    protected sourceModelStorage: SourceModelStorage;
    @inject(ModelSubmissionHandler)
    protected submissionHandler: ModelSubmissionHandler;

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
                        CincoClientSessionInitializer.sendToAllClients(
                            response,
                            clientId,
                            CincoClientSessionInitializer.clientSessionsActionDispatcher
                        );
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
        if (clientId !== SYSTEM_ID) {
            return;
        }
        // add graphmodel Watcher
        GraphModelWatcher.removeCallback(clientId);
        GraphModelWatcher.addCallback(clientId, async dirtyFiles => {
            for (const dirtyFile of dirtyFiles) {
                const wasMovedOrDeleted = dirtyFile.eventType === 'rename' && !(await existsFile(dirtyFile.path));
                const wasMovedRenamedOrCreated = dirtyFile.eventType === 'rename' && (await existsFile(dirtyFile.path));
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
                    HookManager.executeHook(
                        deleteArgument,
                        HookType.POST_DELETE,
                        modelState,
                        this.logger,
                        actionDispatcher,
                        this.sourceModelStorage,
                        this.submissionHandler
                    );
                } else {
                    const model = (await readJson(dirtyFile.path, { hideError: true })) as any | undefined;
                    if (!model || !model.id) {
                        // Modelfile could not be read from path. It is either just intiialized (empty file) or moved.
                        return;
                    }
                    if (!modelState.root) {
                        await GraphModelStorage.loadSourceModel(
                            dirtyFile.path,
                            modelState,
                            this.logger,
                            actionDispatcher,
                            this.sourceModelStorage,
                            this.submissionHandler
                        );
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
                            actionDispatcher,
                            this.sourceModelStorage,
                            this.submissionHandler
                        );
                    } else if (wasChanged) {
                        // trigger changedContent Hook
                        HookManager.executeHook(
                            { kind: 'ModelFileChange', modelElementId: model.id },
                            HookType.POST_CONTENT_CHANGE,
                            modelState,
                            this.logger,
                            actionDispatcher,
                            this.sourceModelStorage,
                            this.submissionHandler
                        );
                    }
                    if (modelState.graphModel.id === model.id && modelState instanceof GraphModelState) {
                        this.onGraphModelChange(modelState);
                    }
                }
            }
        });
        GraphModelWatcher.watch(clientId);
    }

    onGraphModelChange(modelState: GraphModelState): void {
        /**
         * PUT ALL ON CHANGE EVENTS HERE
         */
        let requests: Action[] = [];

        // update palettes as PrimeReferences could have
        const paletteResponse = RequestContextActions.create({
            contextId: 'tool-palette',
            editorContext: {
                selectedElementIds: []
            }
        });
        requests.push(paletteResponse);

        requests = requests.concat(this.updateGraphModelHandler(modelState));
        this.sendGraphModelHandlerRequests(requests);
    }

    updateGraphModelHandler(modelState: GraphModelState): Action[] {
        const index: GraphModelIndex = modelState.index as GraphModelIndex;
        const model = index.getRoot();
        const requests: Action[] = [];

        /**
         * Per ModelElement
         */
        const allModelElements = index.getAllModelElements();

        // Validation Request
        for (const modelElement of allModelElements) {
            if (hasValidation(modelElement.type)) {
                const validationRequest = ValidationRequestAction.create(model.id, modelElement.id);
                requests.push(validationRequest);
            }
        }

        // Appearance Provider Request
        for (const modelElement of allModelElements) {
            if (hasAppearanceProvider(modelElement.type)) {
                const appearanceRequest = AppearanceUpdateRequestAction.create(modelElement.id);
                requests.push(appearanceRequest);
            }
        }

        // Value Provider
        for (const modelElement of allModelElements) {
            if (hasValueProvider(modelElement.type)) {
                const valueRequest = ValueUpdateRequestAction.create(modelElement.id);
                requests.push(valueRequest);
            }
        }

        return requests;
    }

    sendGraphModelHandlerRequests(requests: Action[]): void {
        // propagate actions
        for (const request of requests) {
            this.actionDispatcher.dispatch(request);
            CincoClientSessionInitializer.sendToAllOtherClients(
                request,
                this.serverContainer.id,
                CincoClientSessionInitializer.clientSessionsActionDispatcher
            );
        }
    }

    static addClient(id: number, actionDispatcher: ActionDispatcher): void {
        CincoClientSessionInitializer.clientSessionsActionDispatcher.set(id, actionDispatcher);
    }

    static removeClient(id: number): void {
        CincoClientSessionInitializer.clientSessionsActionDispatcher.delete(id);
    }

    static sendToAllOtherClients(message: Action, serverContainerId: number, actionDispatcherMap: Map<number, ActionDispatcher>): void {
        if (actionDispatcherMap) {
            for (const entry of actionDispatcherMap.entries()) {
                if (entry[0] !== serverContainerId) {
                    entry[1].dispatch(message).catch(e => {
                        console.log('An error occured, maybe the client is not connected anymore:\n' + e);
                        CincoClientSessionInitializer.removeClient(entry[0]);
                    });
                }
            }
        }
    }

    static sendToAllClients(message: Action, clientId: string, actionDispatcherMap: Map<number, ActionDispatcher>): void {
        if (clientId !== SYSTEM_ID) {
            return;
        }
        if (actionDispatcherMap) {
            for (const entry of actionDispatcherMap.entries()) {
                entry[1].dispatch(message).catch(e => {
                    console.log('An error occured, maybe the client is not connected anymore:\n' + e);
                    CincoClientSessionInitializer.removeClient(entry[0]);
                });
            }
        }
    }

    static sendToClient(message: Action, clientId: string, actionDispatcherMap: Map<number, ActionDispatcher>): void {
        if (actionDispatcherMap) {
            for (const entry of actionDispatcherMap.entries()) {
                if (entry[1] instanceof CincoActionDispatcher && entry[1].clientId === clientId) {
                    entry[1].dispatch(message).catch(e => {
                        console.log('An error occured, maybe the client is not connected anymore:\n' + e);
                        CincoClientSessionInitializer.removeClient(entry[0]);
                    });
                    return;
                }
            }
        }
    }
}
