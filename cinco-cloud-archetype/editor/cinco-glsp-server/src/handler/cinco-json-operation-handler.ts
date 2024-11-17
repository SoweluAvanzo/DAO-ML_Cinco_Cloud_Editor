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

import { GraphGModelFactory, GraphModelState, GraphModelStorage, ModelElement } from '@cinco-glsp/cinco-glsp-api';
import { ContextBundle } from '@cinco-glsp/cinco-glsp-api/lib/api/context-bundle';
import { hasValueProvider, ValueUpdateRequestAction } from '@cinco-glsp/cinco-glsp-common';
import {
    ActionDispatcher,
    Command,
    JsonOperationHandler,
    Logger,
    MaybePromise,
    ModelSubmissionHandler,
    Operation,
    SourceModelStorage,
    UpdateModelAction
} from '@eclipse-glsp/server';
import { injectable, inject } from 'inversify';

@injectable()
export abstract class CincoJsonOperationHandler extends JsonOperationHandler {
    @inject(ActionDispatcher)
    protected readonly actionDispatcher: ActionDispatcher;
    @inject(GraphModelState)
    override readonly modelState: GraphModelState;
    @inject(Logger)
    protected readonly logger: Logger;
    @inject(SourceModelStorage)
    protected sourceModelStorage: SourceModelStorage;
    @inject(ModelSubmissionHandler)
    protected submissionHandler: ModelSubmissionHandler;
    // Example Use-Case: async composition of changeBounds and and changeContainer
    static MODEL_ACTION_LOCK: Map<string /* ModelID */, any[] /* Resovle */> = new Map();

    async lockModelActions(): Promise<void> {
        if (CincoJsonOperationHandler.MODEL_ACTION_LOCK.has(this.modelState.graphModel.id)) {
            const locks = CincoJsonOperationHandler.MODEL_ACTION_LOCK.get(this.modelState.graphModel.id)!;
            await new Promise(lock => {
                locks.push(lock);
            });
            this.lockModelActions();
        } else {
            CincoJsonOperationHandler.MODEL_ACTION_LOCK.set(this.modelState.graphModel.id, []);
        }
    }

    unlockModelActions(): void {
        if (!CincoJsonOperationHandler.MODEL_ACTION_LOCK.has(this.modelState.graphModel.id)) {
            throw new Error('Model "' + this.modelState.graphModel.id + '" was not locked!');
        } else {
            const locks = CincoJsonOperationHandler.MODEL_ACTION_LOCK.get(this.modelState.graphModel.id);
            CincoJsonOperationHandler.MODEL_ACTION_LOCK.delete(this.modelState.graphModel.id);
            locks?.forEach(l => l());
        }
    }

    getBundle(): ContextBundle {
        return new ContextBundle(this.modelState, this.logger, this.actionDispatcher, this.sourceModelStorage, this.submissionHandler);
    }

    createCommand(operation: Operation): MaybePromise<Command | undefined> {
        return this.commandOf(() => {
            this.executeOperation(operation);
        });
    }

    abstract executeOperation(operation: Operation): void;

    async handleStateChange(modelElement: ModelElement | undefined, save = true): Promise<void> {
        if (modelElement && hasValueProvider(modelElement.type)) {
            const valueRequest = ValueUpdateRequestAction.create(this.modelState.graphModel.id, modelElement.id, this.operationType);
            await this.actionDispatcher.dispatch(valueRequest); // implies save (see BaseHandlerManager)
        }
        if (save) {
            return this.saveAndUpdate();
        }
    }

    async saveAndUpdate(): Promise<void> {
        const graphmodel = this.modelState.index.getRoot();
        const fileUri = graphmodel._sourceUri;
        if (!fileUri) {
            throw new Error('Graphmodel has no sourceUri! Could not saveAndUpdate!');
        }
        await GraphModelStorage.saveSourceModel(fileUri, this.getBundle());
        return this.updateFrontendModel();
    }

    async updateFrontendModel(): Promise<void> {
        await this.actionDispatcher.dispatch(
            UpdateModelAction.create(GraphGModelFactory.buildGModel(this.modelState.graphModel), { animate: false })
        );
    }
}
