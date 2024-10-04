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
import { GraphModelState, GraphModelStorage, ModelElement, getModelFiles, getWorkspaceRootUri } from '@cinco-glsp/cinco-glsp-api';
import {
    ElementType,
    getEdgePalettes,
    getEdgeSpecOf,
    getGraphSpecOf,
    getIconClass,
    getNodePalettes,
    getNodeSpecOf,
    getPaletteIconClass,
    getPalettes,
    getPrimeNodePaletteCategoriesOf,
    getSpecOf,
    hasPalette,
    isCreateable,
    isPrimeReference,
    NodeType,
    UPDATING_RACE_CONDITION_INDICATOR
} from '@cinco-glsp/cinco-glsp-common';
import {
    OperationHandlerRegistry,
    ToolPaletteItemProvider,
    TriggerEdgeCreationAction,
    TriggerNodeCreationAction
} from '@eclipse-glsp/server';
import { inject, injectable } from 'inversify';
import { SpecifiedEdgeHandler } from '../handler/specified_edge_handler';
import { CreateOperationHandler, SpecifiedElementHandler } from '../handler/specified_element_handler';
import { SpecifiedNodeHandler } from '../handler/specified_node_handler';
import * as path from 'path';

@injectable()
export class CustomToolPaletteItemProvider extends ToolPaletteItemProvider {
    @inject(GraphModelState) state: GraphModelState;
    @inject(OperationHandlerRegistry) operationHandlerRegistry: OperationHandlerRegistry;
    protected counter: number;
    protected WHITE_LIST = ['Nodes', 'Edges'];

    async getItems(args?: Args): Promise<PaletteItem[]> {
        if (!this.state.graphModel) {
            return [];
        }
        if(!getGraphSpecOf(this.state.graphModel.type)) {
            // TODO: this can occur if the palette is fetched, while the metaSpecification
            // is updated, thus, the current type is not present.
            // If the graphmodel is indeed not present anymore, the window should be closed.
            // This is currently a sufficient workaround. The clean way would be to identify
            // the updating of the meta-specification and block this procedure, until the updating
            // is finished. (The UPDATING_RACE_CONDITION_INDICATOR would not be needed anymore)
            return [
                UPDATING_RACE_CONDITION_INDICATOR
            ];
        }

        const handlers = this.operationHandlerRegistry
            .getAll()
            .filter(handler => handler instanceof SpecifiedElementHandler) as CreateOperationHandler[];
        this.counter = 0;

        // add default categories
        const paletteItems: PaletteItem[] = [];
        const nodeCategory = this.createDefaultItem(handlers, 'node-group', 'Nodes', 'nodes');
        const edgeCategory = this.createDefaultItem(handlers, 'edge-group', 'Edges', 'edges');
        if (nodeCategory !== undefined) {
            paletteItems.push(nodeCategory);
        }
        if (edgeCategory !== undefined) {
            paletteItems.push(edgeCategory);
        }

        // add custom palettes
        const customNodePaletteItems = getNodePalettes();
        customNodePaletteItems
            .filter((e: string) => e !== 'Edges' && e !== 'Nodes')
            .forEach((e: string) => {
                const p = this.createCustomItem(handlers, e, e, 'node');
                if (p) {
                    paletteItems.push(p);
                }
            });
        const customEdgePaletteItems = getEdgePalettes();
        customEdgePaletteItems
            .filter((e: string) => e !== 'Edges' && e !== 'Nodes')
            .forEach((e: string) => {
                const p = this.createCustomItem(handlers, e, e, 'edge');
                if (p) {
                    paletteItems.push(p);
                }
            });

        // add prime node label into palettes
        const primePalettes = await this.getPrimePalettes();
        primePalettes.forEach(entry => {
            const label = entry.categoryLabelId;
            const p = this.createCustomItem(handlers, label, label, 'node', entry.referenceableElements);
            if (p) {
                paletteItems.push(p);
            }
        });

        // return palettes
        return paletteItems;
    }

    createDefaultItem(handlers: CreateOperationHandler[], id: string, label: string, type: string): PaletteItem | undefined {
        let filteredHandlers: CreateOperationHandler[] = this.getSpecifiedHandlers(handlers, type);
        // add all handlers, that are neither specifiedNode- nor specifiedEdge-Handlers
        let unspecifiedHandlers = handlers.filter(h => !(h instanceof SpecifiedElementHandler));
        unspecifiedHandlers = unspecifiedHandlers.filter(h => filteredHandlers.indexOf(h) < 0);
        filteredHandlers = filteredHandlers.concat(unspecifiedHandlers);

        const handlerItemsOfLabel = this.createSortedPaletteItems(filteredHandlers, label);
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

    createCustomItem(
        handlers: CreateOperationHandler[], // the set of all handlers
        categoryId: string, // the label, specifiable via the value inside @palette-annotation or the name of the prime reference
        label: string, // readable GUI name
        type: string, // 'node' or 'edge'
        primeReferencedEntries?: PrimeReferencedEntry[]
    ): PaletteItem | undefined {
        let handlerItemsOfLabel: PaletteItem[] = [];
        if (type === 'node') {
            handlerItemsOfLabel = this.createCustomNodePaletteItems(handlers, categoryId, primeReferencedEntries);
        } else if (type === 'edge') {
            handlerItemsOfLabel = this.createCustomEdgePaletteItems(handlers, categoryId);
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

    createCustomNodePaletteItems(
        handlers: CreateOperationHandler[],
        categoryId: string,
        primeReferencedEntries?: PrimeReferencedEntry[]
    ): PaletteItem[] {
        const filteredHandlers = handlers
            .filter(h => h instanceof SpecifiedNodeHandler)
            .map(h => h as SpecifiedNodeHandler)
            .filter(nh => {
                for(const e of nh.elementTypeIds) {
                    const s = getNodeSpecOf(e);
                    const palettes = getPalettes(s);
                    if(palettes && palettes.includes(categoryId)) {
                        return true;
                    }
                }
                return false;
            });
        return this.createSortedPaletteItems(filteredHandlers, categoryId, primeReferencedEntries);
    }

    createCustomEdgePaletteItems(handlers: CreateOperationHandler[], categoryId: string): PaletteItem[] {
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

    createSortedPaletteItems(
        handlers: CreateOperationHandler[],
        categoryId: string,
        primeReferencedEntries?: PrimeReferencedEntry[]
    ): PaletteItem[] {
        const paletteItems: PaletteItem[] = [];
        handlers.forEach(handler => {
            if (handler instanceof SpecifiedElementHandler) {
                const graphModel = this.state.graphModel;
                handler.elementTypeIds
                    .filter(e => isCreateable(e))
                    .forEach(elementTypeId => {
                        const isPartOfPalette = graphModel.couldContain(elementTypeId);
                        const spec = getSpecOf(elementTypeId);
                        const action = NodeType.is(spec)
                            ? TriggerNodeCreationAction.create(elementTypeId)
                            : TriggerEdgeCreationAction.create(elementTypeId);
                        if (
                            isPartOfPalette && // filter out only creatable elements
                            (hasPalette(elementTypeId, categoryId) ||
                                (getPalettes(spec).length <= 0 && this.WHITE_LIST.includes(categoryId)))
                        ) {
                            if (isPrimeReference(spec)) {
                                if (primeReferencedEntries && primeReferencedEntries.length > 0) {
                                    primeReferencedEntries.forEach(entry => {
                                        if (entry.primeNodeType === elementTypeId) {
                                            paletteItems.push(this.createPaletteItem(action, handler, elementTypeId, entry));
                                        }
                                    });
                                }
                            } else {
                                paletteItems.push(this.createPaletteItem(action, handler, elementTypeId));
                            }
                        }
                    });
            } else {
                handler.getTriggerActions().forEach(action => {
                    paletteItems.push(this.createPaletteItem(action, handler));
                });
            }
        });
        return paletteItems.sort((a, b) => a.sortString.localeCompare(b.sortString));
    }

    createPaletteItem(
        action: PaletteItem.TriggerElementCreationAction,
        handler: CreateOperationHandler,
        elementTypeId?: string,
        primeArgs?: PrimeReferencedEntry
    ): PaletteItem {
        const spec = getSpecOf(elementTypeId);
        if (elementTypeId && spec && isPrimeReference(spec) && primeArgs) {
            return this.createPrimePaletteIten(action, handler, elementTypeId, primeArgs);
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

    createPrimePaletteIten(
        action: PaletteItem.TriggerElementCreationAction,
        handler: CreateOperationHandler,
        elementTypeId: string,
        primeArgs: PrimeReferencedEntry
    ): PaletteItem {
        const elementSpecOfPrimedType = getSpecOf(primeArgs.instanceType!) as ElementType;
        const label = elementSpecOfPrimedType.label + '\n[' + primeArgs.instanceId + ']\n' + ' (' + primeArgs.filePath + ')';

        let iconClass: string | undefined = undefined;
        if (handler instanceof SpecifiedElementHandler) {
            iconClass = getIconClass(elementTypeId);
        }
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

    private async getPrimePalettes(): Promise<PrimePaletteCategoryEntry[]> {
        const modelFiles = await getModelFiles();
        const primeNodePaletteCategories = getPrimeNodePaletteCategoriesOf(this.state.graphModel.type);
        const primePalettes: PrimePaletteCategoryEntry[] = await Promise.all(primeNodePaletteCategories
            .filter(e => e.label !== 'Edges' && e.label !== 'Nodes')
            .map(async e => {
                const primeReferencableElements = (await Promise.all(modelFiles
                        .map(async file => {
                            const filePath = path.join(getWorkspaceRootUri(), file);
                            const model = await GraphModelStorage.readModelFromFile(filePath);
                            if (model) {
                                const allSupportedModelElementsOfModel = model
                                    ?.getAllContainments()
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
                                            filePath: file
                                        }) as PrimeReferencedEntry
                                );
                            }
                            return undefined;
                        })
                    ))
                    .flat()
                    .filter(entry => entry !== undefined) as PrimeReferencedEntry[];
                return {
                    categoryLabelId: e.label,
                    referenceableElements: primeReferencableElements
                } as PrimePaletteCategoryEntry;
            }));
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
