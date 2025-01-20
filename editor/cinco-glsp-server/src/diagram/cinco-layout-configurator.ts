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
import { GraphModelState } from '@cinco-glsp/cinco-glsp-api';
import { CincoActionDispatcher } from '@cinco-glsp/cinco-glsp-api/lib/api/cinco-action-dispatcher';
import {
    getLayout,
    hasLayoutAnnotation,
    hasLayoutOptionsProvider,
    isLayoutable,
    isMovable,
    LayoutOptionsRequestAction,
    LayoutOptionsResponse,
    PredefinedLayouts
} from '@cinco-glsp/cinco-glsp-common';
import { DefaultElementFilter, LayoutConfigurator, LayoutOptions } from '@eclipse-glsp/layout-elk';
import { ActionDispatcher, GEdge, GGraph, GLabel, GModelElement, GNode, GPort, ModelState } from '@eclipse-glsp/server';
import { inject, injectable } from 'inversify';

@injectable()
export class CincoElementFilter extends DefaultElementFilter {
    override apply(element: GModelElement): boolean {
        if (element instanceof GNode) {
            return this.filterNode(element);
        } else if (element instanceof GEdge) {
            return this.filterEdge(element);
        } else if (element instanceof GLabel) {
            this.filterLabel(element);
        } else if (element instanceof GPort) {
            this.filterPort(element);
        }
        return true;
    }

    protected override filterNode(node: GNode): boolean {
        const modelElement = (this.modelState as GraphModelState).index.findModelElement(node.id);
        if (modelElement) {
            return isMovable(modelElement.type) && isLayoutable(modelElement.type);
        }
        return true;
    }

    protected override filterEdge(edge: GEdge): boolean {
        const source = this.modelState.index.get(edge.sourceId);

        if (!source || (source instanceof GNode && !this.filterNode(source)) || (source instanceof GPort && !this.filterPort(source))) {
            return false;
        }

        const target = this.modelState.index.get(edge.targetId);

        if (!target || (target instanceof GNode && !this.filterNode(target)) || (target instanceof GPort && !this.filterPort(target))) {
            return false;
        }
        return true;
    }

    protected override filterLabel(label: GLabel): boolean {
        return true;
    }

    protected override filterPort(port: GPort): boolean {
        return true;
    }
}

@injectable()
export class CincoLayoutConfigurator implements LayoutConfigurator {
    @inject(ActionDispatcher)
    protected actionDispatcher: CincoActionDispatcher;
    @inject(ModelState)
    protected modelState: GraphModelState;

    apply(element: GModelElement): LayoutOptions | undefined {
        throw new Error('Method not implemented.');
    }

    async applyAsync(element: GModelElement): Promise<LayoutOptions | undefined> {
        if (element instanceof GGraph) {
            return this.graphOptions(element);
        } else if (element instanceof GNode) {
            return this.nodeOptions(element);
        } else if (element instanceof GEdge) {
            return this.edgeOptions(element);
        } else if (element instanceof GLabel) {
            return this.labelOptions(element);
        } else if (element instanceof GPort) {
            return this.portOptions(element);
        }
        return undefined;
    }

    async getLayoutOptions(id: string): Promise<LayoutOptions | undefined> {
        const modelElement = this.modelState.index.findModelElement(id);
        let layoutValue: string | undefined = undefined;
        if (modelElement) {
            const spec = modelElement?.getSpec();
            /**
             * 1. LayoutOptionsProvider
             * 2. Layout-Annotation
             * 3. undefined => default
             */
            if (hasLayoutOptionsProvider(spec.elementTypeId)) {
                const responses = await this.actionDispatcher.request(
                    LayoutOptionsRequestAction.create(modelElement.getGraphModel().id, modelElement.id)
                );
                const response = responses.find(r => LayoutOptionsResponse.is(r)) as LayoutOptionsResponse;
                layoutValue = response?.layoutOptions;
            } else if (hasLayoutAnnotation(spec)) {
                layoutValue = getLayout(spec);
            }
            // post-processing and returning
            if (layoutValue) {
                if (PredefinedLayouts.is(layoutValue)) {
                    return PredefinedLayouts.get(layoutValue);
                } else {
                    try {
                        layoutValue = layoutValue?.replace('\\.', '\\\\.');
                        return JSON.parse(layoutValue);
                    } catch (e) {
                        throw Error('LayoutOptions not parsable!: ' + e);
                    }
                }
            }
        }
        return undefined;
    }

    protected async graphOptions(graph: GGraph): Promise<LayoutOptions | undefined> {
        const layoutOptions = await this.getLayoutOptions(graph.id);
        return (
            layoutOptions ?? {
                // default layouting:
                // 'layered', 'stress', 'mrtree', 'radial', 'force', 'disco', 'sporeOverlap', 'sporeCompaction', 'rectpacking'
                'elk.algorithm': 'layered'
            }
        );
    }

    protected async nodeOptions(node: GNode): Promise<LayoutOptions | undefined> {
        return this.getLayoutOptions(node.id);
    }

    protected async edgeOptions(edge: GEdge): Promise<LayoutOptions | undefined> {
        return this.getLayoutOptions(edge.id);
    }

    protected async labelOptions(label: GLabel): Promise<LayoutOptions | undefined> {
        return undefined;
    }

    protected async portOptions(sport: GPort): Promise<LayoutOptions | undefined> {
        return undefined;
    }
}
