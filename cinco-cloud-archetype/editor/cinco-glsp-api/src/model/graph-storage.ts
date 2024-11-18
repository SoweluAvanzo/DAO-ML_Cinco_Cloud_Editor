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

import {
    CreateGraphModelArgument,
    getFileExtension,
    getGraphModelOfFileType,
    hasFileCodec,
    HookType,
    OpenModelFileArgument,
    SaveModelFileArgument
} from '@cinco-glsp/cinco-glsp-common';
import {
    ActionDispatcher,
    GLSPServerError,
    Logger,
    ModelSubmissionHandler,
    RequestModelAction,
    SaveModelAction,
    SOURCE_URI_ARG
} from '@eclipse-glsp/server';
import { AbstractJsonModelStorage } from '@eclipse-glsp/server/lib/node/abstract-json-model-storage';
import { inject, injectable } from 'inversify';
import { GraphModel } from './graph-model';
import { GraphModelState } from './graph-model-state';
import { HookManager } from '../semantics/hook-manager';
import { existsFile, readFile, readFileSync, readJson, readJsonSync, toPath, toWorkspaceUri, writeFile } from '../utils/file-helper';
import { FileCodecManager } from '../semantics/file-codec-manager';
import { ContextBundle } from '../api/context-bundle';

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

    static RW_LOCK_MAP: Map<string, any[]> = new Map();

    static async lockSemaphore(sourceUri: string): Promise<void> {
        const semaphore = GraphModelStorage.RW_LOCK_MAP.get(sourceUri);
        if (!semaphore) {
            GraphModelStorage.RW_LOCK_MAP.set(sourceUri, []);
        } else {
            await new Promise<void>(resolve => {
                semaphore.push(resolve);
            }).then(_ => {
                this.lockSemaphore(sourceUri);
            });
        }
    }

    static unlockSemaphore(sourceUri: string): void {
        if (!GraphModelStorage.RW_LOCK_MAP.has(sourceUri)) {
            throw new Error('No lock to unlock for: ' + sourceUri);
        }
        const keyList = GraphModelStorage.RW_LOCK_MAP.get(sourceUri)!;
        GraphModelStorage.RW_LOCK_MAP.delete(sourceUri);
        keyList.forEach(unlock => unlock());
    }

    override async loadSourceModel(action: RequestModelAction): Promise<void> {
        let sourceUri = this.getSourceUri(action);
        sourceUri = await this.resolveSourceURI(sourceUri);
        const contextBundle = new ContextBundle(this.modelState, this.logger, this.actionDispatcher, this, this.submissionHandler);
        await GraphModelStorage.loadSourceModel(sourceUri, contextBundle);
        if (contextBundle.modelState.graphModel) {
            // On Open
            const parameters: OpenModelFileArgument = {
                kind: 'OpenModelFile',
                modelElementId: contextBundle.modelState.graphModel.id
            };
            HookManager.executeHook(parameters, HookType.ON_OPEN, contextBundle);
        }
    }

    /**
     * Checks if sourceURI is absolute or relative. If it is relative it resolves it to an absolute path.
     * Eventually it returns the absolute sourceURI.
     */
    async resolveSourceURI(sourceUri: string): Promise<string> {
        if (await existsFile(sourceUri)) {
            return sourceUri;
        }
        const workspaceUri = toWorkspaceUri(sourceUri);
        if (!(await existsFile(workspaceUri))) {
            throw new Error('Modelfile for sourceUri not found: ' + sourceUri);
        }
        return workspaceUri;
    }

    override async saveSourceModel(action: SaveModelAction): Promise<void> {
        if (!action.fileUri) {
            action.fileUri = this.modelState.graphModel?._sourceUri;
        }
        try {
            const fileUri = this.getFileUri(action);
            const contextBundle = new ContextBundle(this.modelState, this.logger, this.actionDispatcher, this, this.submissionHandler);
            await GraphModelStorage.saveSourceModel(fileUri, contextBundle);
        } catch (e) {
            this.logger.error('Could not save!\n' + e);
        }
    }

    static async loadSourceModel(sourceUri: string, contextBundle: ContextBundle): Promise<void> {
        const { graphModel, initialized } = await GraphModelStorage.loadFromFile(sourceUri, contextBundle);
        if (graphModel) {
            if (initialized) {
                const parameters: CreateGraphModelArgument = {
                    kind: 'Create',
                    modelElementId: '<NONE>',
                    elementTypeId: graphModel.type,
                    elementKind: 'GraphModel',
                    path: toPath(sourceUri)
                };
                HookManager.executeHook(parameters, HookType.PRE_CREATE, contextBundle);
                contextBundle.modelState.graphModel = graphModel;
                parameters.modelElementId = graphModel.id;
                HookManager.executeHook(parameters, HookType.POST_CREATE, contextBundle);
                await this.saveSourceModel(sourceUri, contextBundle);
            } else {
                contextBundle.modelState.graphModel = graphModel;
            }
        }
    }

    static async saveSourceModel(sourceUri: string, contextBundle: ContextBundle): Promise<void> {
        const canSave = HookManager.executeHook(
            { kind: 'SaveModelFile', modelElementId: contextBundle.modelState.graphModel.id, path: sourceUri } as SaveModelFileArgument,
            HookType.CAN_SAVE,
            contextBundle
        );
        if (canSave) {
            let saved = false;
            try {
                await this.serializeModelFile(sourceUri, contextBundle.modelState.sourceModel as GraphModel, contextBundle);
                saved = true;
            } catch (error) {
                console.log(`Could not encode model to file: ${sourceUri}`, error);
            }
            if (saved) {
                HookManager.executeHook(
                    {
                        kind: 'SaveModelFile',
                        modelElementId: contextBundle.modelState.graphModel.id,
                        path: sourceUri
                    } as SaveModelFileArgument,
                    HookType.POST_SAVE,
                    contextBundle
                );
            }
        }
    }

    static async readModelFromFile(sourceUri: string, contextBundle: ContextBundle): Promise<GraphModel | undefined> {
        const model = (await this.loadFromFile(sourceUri, contextBundle)).graphModel;
        if (!model) {
            return undefined;
        }
        return GraphModelState.resolveGraphmodel(model, new GraphModel(), undefined); // TODO: merged index?
    }

    static readModelFromFileSync(sourceUri: string, contextBundle: ContextBundle): GraphModel | undefined {
        const model = this.loadFromFileSync(sourceUri, contextBundle);
        if (!model) {
            return undefined;
        }
        return GraphModelState.resolveGraphmodel(model, new GraphModel(), contextBundle.modelState?.index); // TODO: merged index?
    }

    static async loadFromFile(
        sourceUri: string,
        contextBundle: ContextBundle,
        executeCanCreateHook: boolean = true
    ): Promise<{ graphModel: GraphModel | undefined; initialized: boolean }> {
        try {
            const path = toPath(sourceUri);
            let fileContent = await this.parseModelFile(path, contextBundle);
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
                if (executeCanCreateHook) {
                    const canCreate = HookManager.executeHook(parameters, HookType.CAN_CREATE, contextBundle);
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

    loadFromFileSync(sourceUri: string, contextBundle: ContextBundle): GraphModel | undefined {
        return GraphModelStorage.loadFromFileSync(sourceUri, contextBundle);
    }

    static loadFromFileSync(sourceUri: string, contextBundle: ContextBundle): GraphModel | undefined {
        try {
            const path = toPath(sourceUri);
            let fileContent: GraphModel | any = this.parseModelFileSync(path, contextBundle);
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

    /**
     * Parsing/Decoding & Serializing/Encoding
     */

    private static async parseModelFile(sourceUri: string, contextBundle: ContextBundle): Promise<GraphModel | undefined> {
        const fileExtension = getFileExtension(sourceUri);
        const graphModelSpec = getGraphModelOfFileType(fileExtension);
        let fileContent: GraphModel | any = '';
        let error;
        try {
            if (graphModelSpec && hasFileCodec(graphModelSpec.elementTypeId)) {
                // Parsing with custom codec
                const content = await readFile(sourceUri);
                if (content === undefined) {
                    throw new Error('Failed to read file: ' + sourceUri);
                } else if (!content) {
                    return undefined; // new file
                }
                fileContent = FileCodecManager.decode(sourceUri, content, contextBundle);
            } else {
                fileContent = await readJson(sourceUri, { hideError: true });
            }
        } catch (e: any) {
            error = e;
        }
        if (error) {
            throw new Error(error);
        }
        return fileContent;
    }

    static parseModelFileSync(sourceUri: string, contextBundle: ContextBundle): GraphModel | undefined {
        const fileExtension = getFileExtension(sourceUri);
        const graphModelSpec = getGraphModelOfFileType(fileExtension);
        let fileContent: GraphModel | undefined;
        let error;
        try {
            if (graphModelSpec && hasFileCodec(graphModelSpec.elementTypeId)) {
                // Parsing with custom codec
                const content = readFileSync(sourceUri);
                if (!content) {
                    throw new Error('Failed to read file: ' + sourceUri);
                }
                fileContent = FileCodecManager.decode(sourceUri, content, contextBundle);
            } else {
                fileContent = readJsonSync(sourceUri, { hideError: true }) as GraphModel;
            }
        } catch (e: any) {
            error = e;
        }
        if (error) {
            throw new Error(error);
        }
        return fileContent;
    }

    private static async serializeModelFile(sourceUri: string, model: GraphModel | undefined, contextBundle: ContextBundle): Promise<void> {
        if (!model) {
            throw new Error('Could not save model! Model is undefined.');
        }
        const graphModelSpec = model.getSpec();
        let fileContent: string | undefined;

        this.lockSemaphore(sourceUri);
        let error;
        try {
            if (hasFileCodec(graphModelSpec.elementTypeId)) {
                // Serializing with custom codec
                fileContent = FileCodecManager.encode(model, contextBundle);
            } else {
                fileContent = this.stringifyGraphModel(model);
            }
            await writeFile(sourceUri, fileContent ?? '');
        } catch (e: any) {
            error = e;
        }
        this.unlockSemaphore(sourceUri);
        if (error) {
            throw new Error(error);
        }
    }

    static stringifyGraphModel(model: GraphModel): string {
        return JSON.stringify(model, undefined, 4) + '\n';
    }
}
