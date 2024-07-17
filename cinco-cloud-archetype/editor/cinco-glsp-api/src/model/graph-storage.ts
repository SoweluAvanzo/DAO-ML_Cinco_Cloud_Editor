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

import { CreateGraphModelArgument, getGraphModelOfFileType, HookType } from '@cinco-glsp/cinco-glsp-common';
import {
    ActionDispatcher,
    GLSPServerError,
    Logger,
    MaybePromise,
    RequestModelAction,
    SaveModelAction,
    TypeGuard
} from '@eclipse-glsp/server';
import { AbstractJsonModelStorage } from '@eclipse-glsp/server/lib/node/abstract-json-model-storage';
import { inject, injectable } from 'inversify';
import { GraphModel } from './graph-model';
import { GraphModelState } from './graph-model-state';
import { HookManager } from '../semantics/hook-manager';

@injectable()
export class GraphModelStorage extends AbstractJsonModelStorage {
    @inject(GraphModelState)
    protected override modelState: GraphModelState;
    @inject(Logger)
    protected logger: Logger;
    @inject(ActionDispatcher)
    protected actionDispatcher: ActionDispatcher;

    loadSourceModel(action: RequestModelAction): MaybePromise<void> {
        const sourceUri = this.getSourceUri(action);
        const { graphModel, initialized } = this.loadFromFile(sourceUri, GraphModel.is);

        if (graphModel) {
            if (initialized) {
                const parameters: CreateGraphModelArgument = {
                    kind: 'Create',
                    modelElementId: '<NONE>',
                    elementTypeId: graphModel.type,
                    elementKind: 'GraphModel',
                    path: this.toPath(sourceUri)
                };
                HookManager.executeHook(parameters, HookType.PRE_CREATE, this.modelState, this.logger, this.actionDispatcher);
                this.modelState.graphModel = graphModel;
                parameters.modelElementId = graphModel.id;
                HookManager.executeHook(parameters, HookType.POST_CREATE, this.modelState, this.logger, this.actionDispatcher);
                this.saveSourceModel({ fileUri: sourceUri, kind: 'saveModel' });
            } else {
                this.modelState.graphModel = graphModel;
            }
        }
    }

    saveSourceModel(action: SaveModelAction): MaybePromise<void> {
        const sourceUri = this.getFileUri(action);
        const serializableModel = this.modelState.sourceModel;
        this.writeFile(sourceUri, serializableModel);
    }

    protected override loadFromFile(
        sourceUri: string,
        guard?: TypeGuard<GraphModel>
    ): { graphModel: GraphModel | undefined; initialized: boolean } {
        try {
            const path = this.toPath(sourceUri);
            let fileContent: GraphModel | any = this.readFile(path);
            let initialized = false;
            if (!fileContent) {
                fileContent = this.createModelForEmptyFile(path); // initialized
                const parameters: CreateGraphModelArgument = {
                    kind: 'Create',
                    modelElementId: '<NONE>',
                    elementTypeId: fileContent.type,
                    elementKind: 'GraphModel',
                    path: path
                };
                const canCreate = HookManager.executeHook(
                    parameters,
                    HookType.CAN_CREATE,
                    this.modelState,
                    this.logger,
                    this.actionDispatcher
                );
                if (!canCreate) {
                    return { graphModel: undefined, initialized: false };
                }
                if (!fileContent) {
                    throw new GLSPServerError(`Could not load the source model. The file '${path}' is empty!.`);
                }
                initialized = true;
            }
            if (guard && !guard(fileContent)) {
                throw new Error('The loaded root object is not of the expected type!');
            }
            fileContent = this.modelState.fixMissingProperties(fileContent, sourceUri);
            return { graphModel: fileContent, initialized: initialized };
        } catch (error) {
            throw new GLSPServerError(`Could not load model from file: ${sourceUri}`, error);
        }
    }

    protected override createModelForEmptyFile(path: string): GraphModel {
        const fileTypeIndex = path.lastIndexOf('.') + 1;
        const fileType = path.substring(fileTypeIndex);
        const spec = getGraphModelOfFileType(fileType);
        const graphModel = new GraphModel();
        if (spec) {
            graphModel.type = spec?.elementTypeId;
        }
        return graphModel;
    }
}
