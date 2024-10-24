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

import { CreateGraphModelArgument, getGraphModelOfFileType, HookType, SaveModelFileArgument } from '@cinco-glsp/cinco-glsp-common';
import {
    ActionDispatcher,
    GLSPServerError,
    Logger,
    ModelSubmissionHandler,
    RequestModelAction,
    SaveModelAction,
    SOURCE_URI_ARG,
    SourceModelStorage
} from '@eclipse-glsp/server';
import { AbstractJsonModelStorage } from '@eclipse-glsp/server/lib/node/abstract-json-model-storage';
import { inject, injectable } from 'inversify';
import { GraphModel } from './graph-model';
import { GraphModelState } from './graph-model-state';
import { HookManager } from '../semantics/hook-manager';
import { readJson, readJsonSync, toPath, writeFileSync } from '../utils/file-helper';

@injectable()
export class GraphModelStorage extends AbstractJsonModelStorage {
    @inject(GraphModelState)
    protected override modelState: GraphModelState;
    @inject(Logger)
    protected logger: Logger;
    @inject(ActionDispatcher)
    protected actionDispatcher: ActionDispatcher;
    @inject(ModelSubmissionHandler)
    protected submissionHandler: ModelSubmissionHandler;

    override async loadSourceModel(action: RequestModelAction): Promise<void> {
        const sourceUri = this.getSourceUri(action);
        await GraphModelStorage.loadSourceModel(
            sourceUri,
            this.modelState,
            this.logger,
            this.actionDispatcher,
            this,
            this.submissionHandler
        );
    }

    override async saveSourceModel(action: SaveModelAction): Promise<void> {
        if (!action.fileUri) {
            action.fileUri = this.modelState.graphModel?._sourceUri;
        }
        try {
            const fileUri = this.getFileUri(action);
            GraphModelStorage.saveSourceModel(fileUri, this.modelState, this.logger, this.actionDispatcher, this, this.submissionHandler);
        } catch (e) {
            this.logger.error('Could not save!\n' + e);
        }
    }

    static async loadSourceModel(
        sourceUri: string,
        modelState: GraphModelState,
        logger: Logger,
        actionDispatcher: ActionDispatcher,
        sourceModelStorage: SourceModelStorage,
        submissionHandler: ModelSubmissionHandler
    ): Promise<void> {
        const { graphModel, initialized } = await GraphModelStorage.loadFromFile(sourceUri, modelState, logger, actionDispatcher);
        if (graphModel) {
            if (initialized) {
                const parameters: CreateGraphModelArgument = {
                    kind: 'Create',
                    modelElementId: '<NONE>',
                    elementTypeId: graphModel.type,
                    elementKind: 'GraphModel',
                    path: toPath(sourceUri)
                };
                HookManager.executeHook(
                    parameters,
                    HookType.PRE_CREATE,
                    modelState,
                    logger,
                    actionDispatcher,
                    sourceModelStorage,
                    submissionHandler
                );
                modelState.graphModel = graphModel;
                parameters.modelElementId = graphModel.id;
                HookManager.executeHook(
                    parameters,
                    HookType.POST_CREATE,
                    modelState,
                    logger,
                    actionDispatcher,
                    sourceModelStorage,
                    submissionHandler
                );
                this.saveSourceModel(sourceUri, modelState, logger, actionDispatcher, sourceModelStorage, submissionHandler);
            } else {
                modelState.graphModel = graphModel;
            }
        }
    }

    static saveSourceModel(
        sourceUri: string,
        modelState: GraphModelState,
        logger: Logger,
        actionDispatcher: ActionDispatcher,
        sourceModelStorage: SourceModelStorage,
        submissionHandler: ModelSubmissionHandler
    ): void {
        const canSave = HookManager.executeHook(
            { kind: 'SaveModelFile', modelElementId: modelState.graphModel.id, path: sourceUri } as SaveModelFileArgument,
            HookType.CAN_SAVE,
            modelState,
            logger,
            actionDispatcher,
            sourceModelStorage,
            submissionHandler
        );
        if (canSave) {
            writeFileSync(sourceUri, JSON.stringify(modelState.sourceModel));
            HookManager.executeHook(
                { kind: 'SaveModelFile', modelElementId: modelState.graphModel.id, path: sourceUri } as SaveModelFileArgument,
                HookType.POST_SAVE,
                modelState,
                logger,
                actionDispatcher,
                sourceModelStorage,
                submissionHandler
            );
        }
    }

    readModelFromURI(sourceUri: string): GraphModel | undefined {
        return GraphModelStorage.loadFromFileSync(sourceUri);
    }

    static async readModelFromFile(sourceUri: string): Promise<GraphModel | undefined> {
        const model = (await this.loadFromFile(sourceUri)).graphModel;
        if (!model) {
            return undefined;
        }
        return GraphModelState.resolveGraphmodel(model, new GraphModel(), undefined); // TODO: merged index?
    }

    static readModelFromFileSync(sourceUri: string): GraphModel | undefined {
        const model = this.loadFromFileSync(sourceUri);
        if (!model) {
            return undefined;
        }
        return GraphModelState.resolveGraphmodel(model, new GraphModel(), undefined); // TODO: merged index?
    }

    static async loadFromFile(
        sourceUri: string,
        modelState?: GraphModelState,
        logger?: Logger,
        actionDispatcher?: ActionDispatcher,
        sourceModelStorage?: SourceModelStorage,
        submissionHandler?: ModelSubmissionHandler,
        executeCanCreateHook: boolean = true
    ): Promise<{ graphModel: GraphModel | undefined; initialized: boolean }> {
        try {
            const path = toPath(sourceUri);
            let fileContent: GraphModel | any = await readJson(path, { hideError: true });
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
                if (executeCanCreateHook && modelState && logger && actionDispatcher && submissionHandler && sourceModelStorage) {
                    const canCreate = HookManager.executeHook(
                        parameters,
                        HookType.CAN_CREATE,
                        modelState,
                        logger,
                        actionDispatcher,
                        sourceModelStorage,
                        submissionHandler
                    );
                    if (!canCreate) {
                        return { graphModel: undefined, initialized: false };
                    }
                }
                if (!fileContent) {
                    throw new GLSPServerError(`Could not load the source model. The file '${path}' is empty!.`);
                }
                initialized = true;
            }
            if (!GraphModel.is(fileContent)) {
                throw new Error('The loaded root object is not of the expected type!');
            }
            fileContent = GraphModelState.fixMissingProperties(fileContent, sourceUri);
            return { graphModel: Object.assign(new GraphModel(), fileContent), initialized: initialized };
        } catch (error) {
            console.log(`Could not load model from file: ${sourceUri}`, error);
        }
        return { graphModel: undefined, initialized: false };
    }

    protected static loadFromFileSync(sourceUri: string): GraphModel | undefined {
        try {
            const path = toPath(sourceUri);
            let fileContent: GraphModel | any = readJsonSync(path, { hideError: true });
            if (!GraphModel.is(fileContent)) {
                throw new Error('The loaded root object is not of the expected type!');
            }
            fileContent = GraphModelState.fixMissingProperties(fileContent, sourceUri);
            return Object.assign(new GraphModel(), fileContent);
        } catch (error) {
            console.log(`Could not load model from file: ${sourceUri}`, error);
        }
        return undefined;
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
