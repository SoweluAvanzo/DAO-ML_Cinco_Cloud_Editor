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
import { Args, PaletteItem } from '@eclipse-glsp/protocol';
import {
    ContextBundle,
    GraphGModelFactory,
    GraphModelState,
    GraphModelStorage,
    ModelElement,
    getModelFiles,
    getWorkspaceRootUri
} from '@cinco-glsp/cinco-glsp-api';
import {
    ElementType,
    getEdgePalettes,
    getEdgeSpecOf,
    getGraphSpecOf,
    getIconClass,
    getLabel,
    getNodePalettes,
    getNodeSpecOf,
    getPaletteIconClass,
    getPalettes,
    getPrimeNodePaletteCategoriesOf,
    getSpecOf,
    hasLabelAnnotation,
    hasLabelProvider,
    hasLabelProviderFor,
    hasPalette,
    isCreateable,
    isPrimeReference,
    LabelAnnotationType,
    LabelRequestAction,
    LabelResponseAction,
    NodeType,
    resolveParameter,
    UPDATING_RACE_CONDITION_INDICATOR
} from '@cinco-glsp/cinco-glsp-common';
import {
    ActionDispatcher,
    Logger,
    OperationHandlerRegistry,
    SourceModelStorage,
    ToolPaletteItemProvider,
    TriggerEdgeCreationAction,
    GModelFactory,
    TriggerNodeCreationAction
} from '@eclipse-glsp/server';
import { inject, injectable } from 'inversify';
import { SpecifiedEdgeHandler } from '../handler/specified_edge_handler';
import { CreateOperationHandler, SpecifiedElementHandler } from '../handler/specified_element_handler';
import { SpecifiedNodeHandler } from '../handler/specified_node_handler';
import * as path from 'path';
import { CincoActionDispatcher } from '@cinco-glsp/cinco-glsp-api/lib/api/cinco-action-dispatcher';

@injectable()
export class CustomToolPaletteItemProvider extends ToolPaletteItemProvider {
    @inject(Logger)
    readonly logger: Logger;
    @inject(GraphModelState)
    readonly state: GraphModelState;
    @inject(ActionDispatcher)
    readonly actionDispatcher: ActionDispatcher;
    @inject(SourceModelStorage)
    protected sourceModelStorage: SourceModelStorage;
    @inject(GModelFactory)
    protected frontendModelFactory: GraphGModelFactory;
    @inject(OperationHandlerRegistry) operationHandlerRegistry: OperationHandlerRegistry;
    protected counter: number;
    protected WHITE_LIST = ['Nodes', 'Edges'];

    constructor() {
        super();
    }

    getBundle(): ContextBundle {
        return new ContextBundle(this.state, this.logger, this.actionDispatcher, this.sourceModelStorage, this.frontendModelFactory);
    }

    async getItems(args?: Args): Promise<PaletteItem[]> {
        if (!this.state.graphModel) {
            return [];
        }
        if (!getGraphSpecOf(this.state.graphModel.type)) {
            // TODO: this can occur if the palette is fetched, while the metaSpecification
            // is updated, thus, the current type is not present.
            // If the graphmodel is indeed not present anymore, the window should be closed.
            // This is currently a sufficient workaround. The clean way would be to identify
            // the updating of the meta-specification and block this procedure, until the updating
            // is finished. (The UPDATING_RACE_CONDITION_INDICATOR would not be needed anymore)
            return [UPDATING_RACE_CONDITION_INDICATOR];
        }

        const handlers = this.operationHandlerRegistry
            .getAll()
            .filter(handler => handler instanceof SpecifiedElementHandler) as CreateOperationHandler[];
        this.counter = 0;

        // add default categories
        const paletteItems: PaletteItem[] = [];
        const nodeCategory = await this.createDefaultItem(handlers, 'node-group', 'Nodes', 'nodes');
        const edgeCategory = await this.createDefaultItem(handlers, 'edge-group', 'Edges', 'edges');
        if (nodeCategory !== undefined) {
            paletteItems.push(nodeCategory);
        }
        if (edgeCategory !== undefined) {
            paletteItems.push(edgeCategory);
        }

        // TODO: SAMI - check if Promise.all/this block is deterministic
        // add custom palettes
        const customNodePaletteItems = getNodePalettes();
        await Promise.all(
            customNodePaletteItems
                .filter((e: string) => e !== 'Edges' && e !== 'Nodes')
                .map(async (e: string) => {
                    const p = await this.createCustomItem(handlers, e, e, 'node');
                    if (p) {
                        paletteItems.push(p);
                    }
                })
        );
        const customEdgePaletteItems = getEdgePalettes();
        await Promise.all(
            customEdgePaletteItems
                .filter((e: string) => e !== 'Edges' && e !== 'Nodes')
                .map(async (e: string) => {
                    const p = await this.createCustomItem(handlers, e, e, 'edge');
                    if (p) {
                        paletteItems.push(p);
                    }
                })
        );
        // add prime node label into palettes
        const primePalettes = await this.getPrimePalettes();
        await Promise.all(
            primePalettes.map(async entry => {
                const label = entry.categoryLabelId;
                const p = await this.createCustomItem(handlers, label, label, 'node', entry.referenceableElements);
                if (p) {
                    paletteItems.push(p);
                }
            })
        );

        // return palettes
        return paletteItems;
    }

    async createDefaultItem(handlers: CreateOperationHandler[], id: string, label: string, type: string): Promise<PaletteItem | undefined> {
        let filteredHandlers: CreateOperationHandler[] = this.getSpecifiedHandlers(handlers, type);
        // add all handlers, that are neither specifiedNode- nor specifiedEdge-Handlers
        let unspecifiedHandlers = handlers.filter(h => !(h instanceof SpecifiedElementHandler));
        unspecifiedHandlers = unspecifiedHandlers.filter(h => filteredHandlers.indexOf(h) < 0);
        filteredHandlers = filteredHandlers.concat(unspecifiedHandlers);

        const handlerItemsOfLabel = await this.createSortedPaletteItems(filteredHandlers, label);
        if (handlerItemsOfLabel === undefined || handlerItemsOfLabel.length <= 0) {
            return undefined;
        }
        const p = {
            id: id,
            label: label,
            actions: [],
            children: handlerItemsOfLabel,
            icon: 'symbol-property',
            sortString: 'A'
        };
        return p;
    }

    protected getSpecifiedHandlers(handlers: CreateOperationHandler[], type: string): SpecifiedElementHandler[] {
        if (type === 'nodes') {
            const specifiedNodeHandlers = this.getAllSpecifiedHandler(handlers, SpecifiedNodeHandler);
            return specifiedNodeHandlers.filter(h => {
                const specs = h.elementTypeIds.map(e => getNodeSpecOf(e));
                const palettesPerSpec = specs.map(s => getPalettes(s));
                const isNoPalette = palettesPerSpec.map(
                    palettes =>
                        palettes === undefined || // either no palette-annotation
                        palettes.length <= 0 || // palette-annotation is empty
                        palettes.indexOf('Nodes') >= 0 // or the palette-annotation contains "Nodes"
                );
                // if one type has a palette
                return isNoPalette.indexOf(true) >= 0;
            });
        } else if (type === 'edges') {
            const specifiedEdgeHandler = this.getAllSpecifiedHandler(handlers, SpecifiedEdgeHandler);
            return specifiedEdgeHandler.filter(h => {
                const specs = h.elementTypeIds.map(e => getEdgeSpecOf(e));
                const palettesPerSpec = specs.map(s => getPalettes(s));
                const isNoPalette = palettesPerSpec.map(
                    palettes =>
                        palettes === undefined || // either no palette-annotation
                        palettes.length <= 0 || // palette-annotation is empty
                        palettes.indexOf('Edges') >= 0 // or the palette-annotation contains "Edges"
                );
                // if one type has a palette
                return isNoPalette.indexOf(true) >= 0;
            });
        }
        return [];
    }

    getAllSpecifiedHandler(handlers: CreateOperationHandler[], T: any): SpecifiedElementHandler[] {
        const specifiedHandler = Array.from([] as SpecifiedElementHandler[]);
        for (const handler of handlers) {
            if (handler instanceof T && specifiedHandler.filter(h => handler.constructor.name === h.constructor.name).length <= 0) {
                specifiedHandler.push(handler as SpecifiedElementHandler);
            }
        }
        return specifiedHandler;
    }

    async createCustomItem(
        handlers: CreateOperationHandler[], // the set of all handlers
        categoryId: string, // the label, specifiable via the value inside @palette-annotation or the name of the prime reference
        label: string, // readable GUI name
        type: string, // 'node' or 'edge'
        primeReferencedEntries?: PrimeReferencedEntry[]
    ): Promise<PaletteItem | undefined> {
        let handlerItemsOfLabel: PaletteItem[] = [];
        if (type === 'node') {
            handlerItemsOfLabel = await this.createCustomNodePaletteItems(handlers, categoryId, primeReferencedEntries);
        } else if (type === 'edge') {
            handlerItemsOfLabel = await this.createCustomEdgePaletteItems(handlers, categoryId);
        }
        if (handlerItemsOfLabel.length <= 0) {
            return undefined;
        }
        const paletteIconClass = getPaletteIconClass(categoryId) ?? 'symbol-property';
        const p = {
            id: categoryId.toLowerCase(),
            label: label,
            actions: [],
            children: handlerItemsOfLabel,
            icon: paletteIconClass,
            sortString: 'A'
        };
        return p;
    }

    async createCustomNodePaletteItems(
        handlers: CreateOperationHandler[],
        categoryId: string,
        primeReferencedEntries?: PrimeReferencedEntry[]
    ): Promise<PaletteItem[]> {
        const filteredHandlers = handlers
            .filter(h => h instanceof SpecifiedNodeHandler)
            .map(h => h as SpecifiedNodeHandler)
            .filter(nh => {
                const relevantElements = nh.elementTypeIds.filter(e => this.state.graphModel.couldContain(e));
                for (const e of relevantElements) {
                    if (getPalettes(getNodeSpecOf(e)).includes(categoryId)) {
                        return true;
                    }
                }
                return false;
            });
        return this.createSortedPaletteItems(filteredHandlers, categoryId, primeReferencedEntries);
    }

    async createCustomEdgePaletteItems(handlers: CreateOperationHandler[], categoryId: string): Promise<PaletteItem[]> {
        const filteredHandlers = handlers
            .filter(h => h instanceof SpecifiedEdgeHandler)
            .map(h => h as SpecifiedEdgeHandler)
            .filter(nh => {
                const specs = nh.elementTypeIds.map(e => getEdgeSpecOf(e));
                const palettesPerSpec = specs.map(s => getPalettes(s));
                const isPaletteOfCategory = palettesPerSpec.map(palettes => palettes !== undefined && palettes.indexOf(categoryId) >= 0);
                return isPaletteOfCategory.indexOf(true) >= 0;
            });
        return this.createSortedPaletteItems(filteredHandlers, categoryId);
    }

    async createSortedPaletteItems(
        handlers: CreateOperationHandler[],
        categoryId: string,
        primeReferencedEntries?: PrimeReferencedEntry[]
    ): Promise<PaletteItem[]> {
        const paletteItems: PaletteItem[] = [];
        await Promise.all(
            handlers.map(async handler => {
                if (handler instanceof SpecifiedElementHandler) {
                    const graphModel = this.state.graphModel;
                    await Promise.all(
                        handler.elementTypeIds
                            .filter(e => graphModel.couldContain(e) && isCreateable(e))
                            .map(async elementTypeId => {
                                const spec = getSpecOf(elementTypeId);
                                const action = NodeType.is(spec)
                                    ? TriggerNodeCreationAction.create(elementTypeId)
                                    : TriggerEdgeCreationAction.create(elementTypeId);
                                if (
                                    hasPalette(elementTypeId, categoryId) ||
                                    (getPalettes(spec).length <= 0 && this.WHITE_LIST.includes(categoryId))
                                ) {
                                    if (isPrimeReference(spec)) {
                                        if (primeReferencedEntries && primeReferencedEntries.length > 0) {
                                            await Promise.all(
                                                primeReferencedEntries.map(async entry => {
                                                    if (entry.primeNodeType === elementTypeId) {
                                                        paletteItems.push(
                                                            await this.createPaletteItem(action, handler, elementTypeId, entry, categoryId)
                                                        );
                                                    }
                                                })
                                            );
                                        }
                                    } else {
                                        paletteItems.push(await this.createPaletteItem(action, handler, elementTypeId));
                                    }
                                }
                            })
                    );
                } else {
                    await Promise.all(
                        handler.getTriggerActions().map(async action => {
                            paletteItems.push(await this.createPaletteItem(action, handler));
                        })
                    );
                }
            })
        );
        return paletteItems.sort((a, b) => a.sortString.localeCompare(b.sortString));
    }

    createPaletteItem(
        action: PaletteItem.TriggerElementCreationAction,
        handler: CreateOperationHandler,
        elementTypeId?: string,
        primeArgs?: PrimeReferencedEntry,
        categoryId?: string
    ): Promise<PaletteItem> | PaletteItem {
        const spec = getSpecOf(elementTypeId);
        if (elementTypeId && spec && isPrimeReference(spec) && primeArgs) {
            return this.createPrimePaletteIten(action, handler, elementTypeId, primeArgs, categoryId);
        }
        const label = elementTypeId && handler instanceof SpecifiedElementHandler ? handler.getLabelFor(elementTypeId) : handler.label;
        let iconClass: string | undefined = undefined;
        if (handler instanceof SpecifiedElementHandler) {
            if (elementTypeId !== undefined) {
                iconClass = getIconClass(spec?.elementTypeId);
                if (!iconClass && spec) {
                    iconClass = `${spec.elementTypeId.replace(':', '_')}`;
                }
            } else {
                iconClass = getIconClass(handler.specification?.elementTypeId);
            }
        }
        const id = `palette-item-${this.counter}`;
        const result = {
            id: id,
            label,
            sortString: label.charAt(0),
            actions: [action],
            icon: iconClass ?? 'circle-filled'
        };
        this.counter++;
        return result;
    }

    async createPrimePaletteIten(
        action: PaletteItem.TriggerElementCreationAction,
        handler: CreateOperationHandler,
        elementTypeId: string,
        primeArgs: PrimeReferencedEntry,
        categoryId?: string
    ): Promise<PaletteItem> {
        let iconClass: string | undefined = undefined;
        if (handler instanceof SpecifiedElementHandler) {
            iconClass = getIconClass(elementTypeId);
        }
        const label: string = await this.getPaletteLabel(elementTypeId, primeArgs, categoryId);
        // add prime information to action of palette element
        const primeAction = { ...action };
        primeAction.args = {};
        (primeAction.args as any)['instanceId'] = primeArgs.instanceId;
        (primeAction.args as any)['instanceType'] = primeArgs.instanceType;
        (primeAction.args as any)['modelId'] = primeArgs.modelId;
        (primeAction.args as any)['modelType'] = primeArgs.modelType;
        (primeAction.args as any)['filePath'] = primeArgs.filePath;

        const id = `palette-item-${primeArgs.primeNodeType}-${this.counter}`;
        const result = {
            id: id,
            label,
            sortString: label.charAt(0),
            actions: [primeAction],
            icon: iconClass ?? 'codicon-circle-filled'
        };
        this.counter++;
        return result;
    }

    private async getPaletteLabel(elementTypeId: string, primeArgs: PrimeReferencedEntry, categoryId?: string): Promise<string> {
        let label: string | undefined;
        const primeRefSpec = getSpecOf(elementTypeId) as NodeType;
        const elementSpecOfPrimedType = getSpecOf(primeArgs.instanceType!) as ElementType;
        /**
         * Label is resolved by annotations with paradigm "pointer-first"
         */
        // 1. Label-Provider on pointer
        if (primeRefSpec.primeReference && hasLabelProviderFor(primeRefSpec.primeReference)) {
            const response = await (this.actionDispatcher as CincoActionDispatcher).request(
                LabelRequestAction.create(
                    this.getBundle().modelState.graphModel.id,
                    primeArgs.instanceId,
                    elementTypeId,
                    LabelAnnotationType.POINTER
                )
            );
            const labelResponse = response.find(r => LabelResponseAction.is(r)) as LabelResponseAction | undefined;
            const labelValue = labelResponse?.label;
            if (labelValue) {
                label = resolveParameter(primeArgs.element, labelValue);
            }
        }
        // 2. Label-Annotation on pointer
        if (!label && primeRefSpec.primeReference && hasLabelAnnotation(primeRefSpec.primeReference)) {
            label = getLabel(primeRefSpec.primeReference, categoryId);
            if (label) {
                label = resolveParameter(primeArgs.element, label);
            }
        }
        // 3. Label-Provider on reference
        if (!label && primeRefSpec.primeReference && hasLabelProvider(primeRefSpec.primeReference.type)) {
            const response = await (this.actionDispatcher as CincoActionDispatcher).request(
                LabelRequestAction.create(this.getBundle().modelState.graphModel.id, primeArgs.instanceId, primeArgs.instanceType)
            );
            const labelResponse = response.find(r => LabelResponseAction.is(r)) as LabelResponseAction | undefined;
            const labelValue = labelResponse?.label;
            if (labelValue) {
                label = resolveParameter(primeArgs.element, labelValue);
            }
        }
        // 4. Label-Annotation on reference
        if ((!label || label.length <= 0) && hasLabelAnnotation(elementSpecOfPrimedType)) {
            label = getLabel(elementSpecOfPrimedType, categoryId);
            if (label) {
                label = resolveParameter(primeArgs.element, label);
            }
        }
        // 5. Default-Fallback:
        if (!label || label.length <= 0) {
            label = elementSpecOfPrimedType.label + '\n[' + primeArgs.instanceId + ']\n' + ' (' + primeArgs.filePath + ')';
        }
        return label;
    }

    private async getPrimePalettes(): Promise<PrimePaletteCategoryEntry[]> {
        const modelFiles = await getModelFiles();
        const primeNodePaletteCategories = getPrimeNodePaletteCategoriesOf(this.state.graphModel.type);
        const primePalettes: PrimePaletteCategoryEntry[] = await Promise.all(
            primeNodePaletteCategories
                .filter(e => e.label !== 'Edges' && e.label !== 'Nodes')
                .map(async e => {
                    const primeReferencableElements = (
                        await Promise.all(
                            modelFiles.map(async file => {
                                const filePath = path.join(getWorkspaceRootUri(), file);
                                const model = await GraphModelStorage.readModelFromFile(filePath, this.getBundle());
                                if (model) {
                                    const allSupportedModelElementsOfModel = model
                                        ?.getAllContainedElements()
                                        .concat(model)
                                        .filter(
                                            element =>
                                                ModelElement.is(element) &&
                                                // polymorphic check
                                                e.elementTypeIds.find(primeType => element.instanceOf(primeType))
                                        ) as ModelElement[];
                                    return allSupportedModelElementsOfModel.map(
                                        element =>
                                            ({
                                                primeNodeType: e.primeElementTypeId,
                                                instanceId: element.id,
                                                instanceType: element.type,
                                                modelId: model.id,
                                                modelType: model.type,
                                                filePath: file,
                                                element
                                            }) as PrimeReferencedEntry
                                    );
                                }
                                return undefined;
                            })
                        )
                    )
                        .flat()
                        .filter(entry => entry !== undefined) as PrimeReferencedEntry[];
                    return {
                        categoryLabelId: e.label,
                        referenceableElements: primeReferencableElements
                    } as PrimePaletteCategoryEntry;
                })
        );
        // merge palettes
        const mergedPrimePallettes = new Map<string, PrimePaletteCategoryEntry>();
        for (const newEntry of primePalettes) {
            const categoryLabelId = newEntry.categoryLabelId;
            if (!mergedPrimePallettes.has(categoryLabelId)) {
                // set
                mergedPrimePallettes.set(categoryLabelId, newEntry);
            } else {
                // merge
                const oldEntry = mergedPrimePallettes.get(categoryLabelId)!;
                mergedPrimePallettes.set(categoryLabelId, PrimePaletteCategoryEntry.merge(oldEntry, newEntry));
            }
        }
        return Array.from(mergedPrimePallettes.values());
    }
}

/** PrimeReference helper-interfaces */

interface PrimeReferencedEntry {
    primeNodeType: string;
    instanceId: string;
    instanceType: string;
    modelId: string;
    modelType: string;
    filePath: string;
    element: ModelElement;
}

namespace PrimeReferencedEntry {
    export function equals(object1: PrimeReferencedEntry, object2: PrimeReferencedEntry): boolean {
        return (
            object1.filePath === object2.filePath &&
            object1.primeNodeType === object2.primeNodeType &&
            object1.instanceId === object2.instanceId &&
            object1.instanceType === object2.instanceType
        );
    }
}

interface PrimePaletteCategoryEntry {
    categoryLabelId: string;
    referenceableElements: PrimeReferencedEntry[];
}

namespace PrimePaletteCategoryEntry {
    export function merge(object1: PrimePaletteCategoryEntry, object2: PrimePaletteCategoryEntry): PrimePaletteCategoryEntry {
        if (object1.categoryLabelId !== object2.categoryLabelId) {
            return object1;
        }
        let currentElements = object1.referenceableElements;
        const incomingElements = object2.referenceableElements;

        // update all new
        const newElements: PrimeReferencedEntry[] = [];
        incomingElements.forEach(n => {
            let exists = false;
            for (const c of currentElements) {
                if (PrimeReferencedEntry.equals(c, n)) {
                    // if exists
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                // if not exists
                newElements.push(n);
            }
        });
        currentElements = newElements.concat(currentElements);
        return {
            categoryLabelId: object1.categoryLabelId,
            referenceableElements: currentElements
        };
    }
}
