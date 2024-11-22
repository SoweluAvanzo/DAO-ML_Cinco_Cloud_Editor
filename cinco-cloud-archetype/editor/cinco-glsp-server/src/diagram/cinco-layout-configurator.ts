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
import { getLayout, hasLayoutAnnotation, isLayoutable, isMovable, PredefinedLayouts } from '@cinco-glsp/cinco-glsp-common';
import { AbstractLayoutConfigurator, DefaultElementFilter, LayoutOptions } from '@eclipse-glsp/layout-elk';
import { GEdge, GGraph, GLabel, GModelElement, GNode, GPort } from '@eclipse-glsp/server';
import { injectable } from 'inversify';

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
export class CincoLayoutConfigurator extends AbstractLayoutConfigurator {
    getLayoutOptions(id: string): LayoutOptions | undefined {
        const modelElement = (this.modelState as GraphModelState).index.findModelElement(id);
        if (modelElement) {
            const spec = modelElement?.getSpec();
            if (hasLayoutAnnotation(spec)) {
                let layoutValue = getLayout(spec);
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
        }
        return undefined;
    }

    protected override graphOptions(graph: GGraph): LayoutOptions | undefined {
        const layoutOptions = this.getLayoutOptions(graph.id);
        return (
            layoutOptions ?? {
                // default layouting:
                // 'layered', 'stress', 'mrtree', 'radial', 'force', 'disco', 'sporeOverlap', 'sporeCompaction', 'rectpacking'
                'elk.algorithm': 'layered'
            }
        );
    }

    protected override nodeOptions(node: GNode): LayoutOptions | undefined {
        return this.getLayoutOptions(node.id);
    }

    protected override edgeOptions(edge: GEdge): LayoutOptions | undefined {
        return this.getLayoutOptions(edge.id);
    }

    protected override labelOptions(label: GLabel): LayoutOptions | undefined {
        return undefined;
    }

    protected override portOptions(sport: GPort): LayoutOptions | undefined {
        return undefined;
    }
}
