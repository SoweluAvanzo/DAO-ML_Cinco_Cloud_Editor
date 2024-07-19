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
    SOURCE_URI_ARG,
    TypeGuard
} from '@eclipse-glsp/server';
import { AbstractJsonModelStorage } from '@eclipse-glsp/server/lib/node/abstract-json-model-storage';
import { inject, injectable } from 'inversify';
import { GraphModel } from './graph-model';
import { GraphModelState } from './graph-model-state';
import { HookManager } from '../semantics/hook-manager';
import { readJson, toPath, writeFile } from '../utils/file-helper';

@injectable()
export class GraphModelStorage extends AbstractJsonModelStorage {
    @inject(GraphModelState)
    protected override modelState: GraphModelState;
    @inject(Logger)
    protected logger: Logger;
    @inject(ActionDispatcher)
    protected actionDispatcher: ActionDispatcher;

    override loadSourceModel(action: RequestModelAction): MaybePromise<void> {
        const sourceUri = this.getSourceUri(action);
        GraphModelStorage.loadSourceModel(sourceUri, this.modelState, this.logger, this.actionDispatcher);
    }

    override saveSourceModel(action: SaveModelAction): MaybePromise<void> {
        const fileUri = this.getFileUri(action);
        GraphModelStorage.saveSourceModel(fileUri, this.modelState);
    }

    static loadSourceModel(
        sourceUri: string,
        modelState: GraphModelState,
        logger: Logger,
        actionDispatcher: ActionDispatcher
    ): MaybePromise<void> {
        const { graphModel, initialized } = GraphModelStorage.loadFromFile(sourceUri, modelState, logger, actionDispatcher, GraphModel.is);

        if (graphModel) {
            if (initialized) {
                const parameters: CreateGraphModelArgument = {
                    kind: 'Create',
                    modelElementId: '<NONE>',
                    elementTypeId: graphModel.type,
                    elementKind: 'GraphModel',
                    path: toPath(sourceUri)
                };
                HookManager.executeHook(parameters, HookType.PRE_CREATE, modelState, logger, actionDispatcher);
                modelState.graphModel = graphModel;
                parameters.modelElementId = graphModel.id;
                HookManager.executeHook(parameters, HookType.POST_CREATE, modelState, logger, actionDispatcher);
                this.saveSourceModel(sourceUri, modelState);
            } else {
                modelState.graphModel = graphModel;
            }
        }
    }

    static saveSourceModel(sourceUri: string, modelState: GraphModelState): MaybePromise<void> {
        const serializableModel = modelState.sourceModel;
        writeFile(sourceUri, JSON.stringify(serializableModel));
    }

    protected static loadFromFile(
        sourceUri: string,
        modelState: GraphModelState,
        logger: Logger,
        actionDispatcher: ActionDispatcher,
        guard?: TypeGuard<GraphModel>
    ): { graphModel: GraphModel | undefined; initialized: boolean } {
        try {
            const path = toPath(sourceUri);
            let fileContent: GraphModel | any = readJson(path, { hideError: true });
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
                const canCreate = HookManager.executeHook(parameters, HookType.CAN_CREATE, modelState, logger, actionDispatcher);
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
            fileContent = modelState.fixMissingProperties(fileContent, sourceUri);
            return { graphModel: fileContent, initialized: initialized };
        } catch (error) {
            throw new GLSPServerError(`Could not load model from file: ${sourceUri}`, error);
        }
    }

    protected static createModelForEmptyFile(path: string): GraphModel {
        const fileTypeIndex = path.lastIndexOf('.') + 1;
        const fileType = path.substring(fileTypeIndex);
        const spec = getGraphModelOfFileType(fileType);
        const graphModel = new GraphModel();
        if (spec) {
            graphModel.type = spec?.elementTypeId;
        }
        return graphModel;
    }

    protected static getFileUri(action: SaveModelAction, modelState: GraphModelState): string {
        const uri = action.fileUri ?? modelState.get(SOURCE_URI_ARG);
        if (!uri) {
            throw new GLSPServerError('Could not derive fileUri for saving the current source model');
        }
        return uri;
    }
}
