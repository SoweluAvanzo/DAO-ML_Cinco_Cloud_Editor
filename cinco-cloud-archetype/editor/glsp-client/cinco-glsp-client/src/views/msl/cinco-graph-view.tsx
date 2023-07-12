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
import { IActionDispatcher, RenderingContext, SGraph, SGraphView, TYPES, svg } from '@eclipse-glsp/client';
import { inject, injectable } from 'inversify';
import { VNode } from 'snabbdom';
import { CincoGraphModel } from '../../model/model';
import { updatePalette } from './cinco-view-helper';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const JSX = { createElement: svg };
/**
 * IView component that turns an SGraph element and its children into a tree of virtual DOM elements.
 */
@injectable()
export class CincoGraphView<IRenderingArgs> extends SGraphView<IRenderingArgs> {
    @inject(TYPES.IActionDispatcherProvider) protected actionDispatcherProvider: () => Promise<IActionDispatcher>;

    override render(model: Readonly<SGraph>, context: RenderingContext, args?: IRenderingArgs): VNode {
        // update palette
        this.actionDispatcherProvider().then(actionDispatcher => {
            updatePalette(actionDispatcher);
        });
        // render
        const scroll = model.scroll ?? { x: 0, y: 0 };
        const zoom = model.zoom ?? 1.0;
        const transform = `scale(${zoom}) translate(${-scroll.x},${-scroll.y})`;
        if (!(model instanceof CincoGraphModel)) {
            return (
                <svg class-sprotty-graph={true}>
                    <g transform={transform}></g>
                </svg>
            ) as unknown as VNode;
        }
        const edgeRouting = this.edgeRouterRegistry.routeAllChildren(model);
        const elements = context.renderChildren(model, { edgeRouting });
        return (
            <svg class-sprotty-graph={true}>
                <g transform={transform}>{elements as Iterable<React.ReactNode>}</g>
            </svg>
        ) as unknown as VNode;
    }
}
