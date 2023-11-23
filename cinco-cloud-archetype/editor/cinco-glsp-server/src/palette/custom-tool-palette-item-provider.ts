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
import { GraphModelState, getFilesFromFolder, getWorkspaceRootUri } from '@cinco-glsp/cinco-glsp-api';
import {
    ElementType,
    getEdgePalettes,
    getEdgeSpecOf,
    getGraphTypes,
    getNodePalettes,
    getNodeSpecOf,
    getPalettes,
    getPrimeNodePalettes,
    getSpecOf,
    hasPalette,
    hasPrimeReference,
    isCreateable
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

@injectable()
export class CustomToolPaletteItemProvider extends ToolPaletteItemProvider {
    @inject(GraphModelState) state: GraphModelState;
    @inject(OperationHandlerRegistry) operationHandlerRegistry: OperationHandlerRegistry;
    protected counter: number;
    protected WHITE_LIST = ['Nodes', 'Edges'];

    getItems(args?: Args): Promise<PaletteItem[]> | PaletteItem[] {
        if (!this.state.graphModel) {
            return [];
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
                const p = this.createCustomItem(handlers, e, e.toLowerCase(), e, 'node');
                if (p) {
                    paletteItems.push(p);
                }
            });
        const customEdgePaletteItems = getEdgePalettes();
        customEdgePaletteItems
            .filter((e: string) => e !== 'Edges' && e !== 'Nodes')
            .forEach((e: string) => {
                const p = this.createCustomItem(handlers, e, e.toLowerCase(), e, 'edge');
                if (p) {
                    paletteItems.push(p);
                }
            });

        // add prime node label into palettes
        const workspacePath = getWorkspaceRootUri();
        const modelFileExtensions = getGraphTypes().map(gT => '.' + gT.diagramExtension);
        const modelFiles = getFilesFromFolder(workspacePath, './', modelFileExtensions);
        const primeNodePaletteItems = getPrimeNodePalettes();
        primeNodePaletteItems
            .filter((e: string) => e !== 'Edges' && e !== 'Nodes')
            .forEach((e: string) => {
                const p = this.createCustomItem(handlers, e, e.toLowerCase(), e, 'node', modelFiles);
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
            const specifiedNodeHandlers = Array.from(
                new Set(
                    handlers
                        .filter(h => h instanceof SpecifiedNodeHandler) // all specified handlers
                        .map(h => h as SpecifiedNodeHandler)
                )
            );
            return specifiedNodeHandlers.filter(h => {
                const specs = h.elementTypeIds.map(e => getNodeSpecOf(e));
                const palettesPerSpec = specs.map(s => getPalettes(s?.elementTypeId));
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
            const specifiedEdgeHandler = Array.from(
                new Set(
                    handlers
                        .filter(h => h instanceof SpecifiedEdgeHandler) // all specified handlers
                        .map(h => h as SpecifiedEdgeHandler)
                )
            );
            return specifiedEdgeHandler.filter(h => {
                const specs = h.elementTypeIds.map(e => getEdgeSpecOf(e));
                const palettesPerSpec = specs.map(s => getPalettes(s?.elementTypeId));
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

    createCustomItem(
        handlers: CreateOperationHandler[], // the set of all handlers
        categoryId: string, // value inside @palette-annotation, usally the label
        id: string, // usually just lowercase and without whitespaces
        label: string, // readable GUI name
        type: string, // 'node' or 'edge'
        fileList?: string[] // is prime node ?
    ): PaletteItem | undefined {
        let handlerItemsOfLabel: PaletteItem[] = [];
        if (type === 'node') {
            handlerItemsOfLabel = this.createCustomNodePaletteItems(handlers, categoryId, fileList);
        } else if (type === 'edge') {
            handlerItemsOfLabel = this.createCustomEdgePaletteItems(handlers, categoryId);
        }
        if (handlerItemsOfLabel.length <= 0) {
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

    createCustomNodePaletteItems(handlers: CreateOperationHandler[], categoryId: string, fileList?: string[]): PaletteItem[] {
        const filteredHandlers = handlers
            .filter(h => h instanceof SpecifiedNodeHandler)
            .map(h => h as SpecifiedNodeHandler)
            .filter(nh => {
                const specs = nh.elementTypeIds.map(e => getNodeSpecOf(e));
                specs.forEach(s => {
                    if (hasPrimeReference(s!.elementTypeId) && s!.palettes === undefined) {
                        const labelName = getNodeSpecOf(s!.elementTypeId)!.primeReference!.name;
                        s!.palettes = [labelName];
                    }
                });
                const palettesPerSpec = specs.map(s => getPalettes(s?.elementTypeId));
                const isPaletteOfCategory = palettesPerSpec.map(palettes => palettes !== undefined && palettes.indexOf(categoryId) >= 0);
                return isPaletteOfCategory.indexOf(true) >= 0;
            });
        return this.createSortedPaletteItems(filteredHandlers, categoryId, fileList);
    }

    createCustomEdgePaletteItems(handlers: CreateOperationHandler[], categoryId: string): PaletteItem[] {
        const filteredHandlers = handlers
            .filter(h => h instanceof SpecifiedEdgeHandler)
            .map(h => h as SpecifiedEdgeHandler)
            .filter(nh => {
                const specs = nh.elementTypeIds.map(e => getEdgeSpecOf(e));
                const palettesPerSpec = specs.map(s => getPalettes(s?.elementTypeId));
                const isPaletteOfCategory = palettesPerSpec.map(palettes => palettes !== undefined && palettes.indexOf(categoryId) >= 0);
                return isPaletteOfCategory.indexOf(true) >= 0;
            });
        return this.createSortedPaletteItems(filteredHandlers, categoryId);
    }

    createSortedPaletteItems(handlers: CreateOperationHandler[], categoryId: string, fileList?: string[]): PaletteItem[] {
        const paletteItems: PaletteItem[] = [];
        handlers.forEach(handler => {
            if (handler instanceof SpecifiedElementHandler) {
                const graphModel = this.state.graphModel;
                handler.elementTypeIds
                    .filter(e => isCreateable(e))
                    .forEach(elementTypeId => {
                        const isPartOfPalette = graphModel.couldContain(elementTypeId);
                        const action = getNodeSpecOf(elementTypeId)
                            ? TriggerNodeCreationAction.create(elementTypeId)
                            : TriggerEdgeCreationAction.create(elementTypeId);
                        if (
                            isPartOfPalette && // filter out only creatable elements
                            (hasPalette(elementTypeId, categoryId) ||
                                (getPalettes(elementTypeId).length <= 0 && this.WHITE_LIST.includes(categoryId)))
                        ) {
                            if (hasPrimeReference(elementTypeId)) {
                                if (fileList && fileList.length > 0) {
                                    fileList.forEach(file => {
                                        paletteItems.push(this.createPaletteItem(action, handler, elementTypeId, file));
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
        fileName?: string
    ): PaletteItem {
        if (elementTypeId && hasPrimeReference(elementTypeId) && fileName) {
            return this.createPrimePaletteIten(action, handler, elementTypeId, fileName);
        }
        const label = elementTypeId && handler instanceof SpecifiedElementHandler ? handler.getLabelFor(elementTypeId) : handler.label;
        let icon: string | undefined = undefined;
        if (handler instanceof SpecifiedElementHandler) {
            if (elementTypeId !== undefined) {
                const spec = getSpecOf(elementTypeId);
                icon = spec?.icon;
                if (!icon && spec) {
                    icon = `${spec.elementTypeId.replace(':', '_')}`;
                }
            } else {
                icon = handler.specification?.icon;
            }
        }
        return {
            id: `palette-item-${this.counter}`,
            label,
            sortString: label.charAt(0),
            actions: [action],
            icon: icon ?? 'circle-filled'
        };
    }

    createPrimePaletteIten(
        action: PaletteItem.TriggerElementCreationAction,
        handler: CreateOperationHandler,
        elementTypeId: string,
        fileName: string
    ): PaletteItem {
        const primeType = getNodeSpecOf(elementTypeId)?.primeReference?.type;
        const elementSpecOfPrimedType = getSpecOf(primeType!) as ElementType;
        const label = elementSpecOfPrimedType.label + '(' + fileName + ')';

        let icon: string | undefined = undefined;
        if (handler instanceof SpecifiedElementHandler) {
            icon = handler.specification?.icon;
        }
        return {
            id: `palette-item-${this.counter}`,
            label,
            sortString: label.charAt(0),
            actions: [action],
            icon: icon ?? 'codicon-circle-filled'
        };
    }
}
