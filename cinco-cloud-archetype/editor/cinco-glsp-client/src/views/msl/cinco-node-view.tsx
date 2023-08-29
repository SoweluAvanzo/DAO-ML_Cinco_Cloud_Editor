/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud.
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
import { ContainerShape, Image, NodeStyle, Polyline } from '@cinco-glsp/cinco-glsp-common';
import { Hoverable, IViewArgs, RenderingContext, SNode, SPort, SShapeElement, Selectable, ShapeView, svg } from '@eclipse-glsp/client';
import { CommandService } from '@theia/core';
import { inject, injectable, optional } from 'inversify';
import { VNode } from 'snabbdom';
import { CincoEdge, CincoNode } from '../../model/model';
import { WorkspaceFileService } from '../../utils/workspace-file-service';
import { CSS_STYLE_PREFIX, buildShape, resolveChildrenRecursivly } from './cinco-view-helper';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const JSX = { createElement: svg };

@injectable()
export class CincoNodeView extends ShapeView {
    @inject(WorkspaceFileService) workspaceFileService: WorkspaceFileService;
    @inject(CommandService) @optional() commandService: CommandService;
    ITEM_COUNTER = -1;
    USE_MARGIN_FIX = true;

    /**
     * Takes a CincoNode and renders it using it's style object
     * @param node the CincoNode that contains the style object
     * @param context
     * @param args
     * @returns
     */
    render(node: Readonly<SShapeElement & Hoverable & Selectable>, context: RenderingContext, args?: IViewArgs): VNode | undefined {
        if (!(node instanceof CincoNode) || !this.isVisible(node, context)) {
            return undefined;
        }
        const style = node.style as NodeStyle | undefined;
        const artificialCSSClass = `${CSS_STYLE_PREFIX}${style?.name ?? 'default'}`;
        const mainContainer = (children: VNode[]): VNode => {
            // discriminate between nodes, containers, edges and rest to fix clipping
            const nodes = node.children.filter(e => e instanceof CincoNode && !e.isContainer);
            const containers = node.children.filter(e => e instanceof CincoNode && e.isContainer);
            const edges = node.children.filter(e => e instanceof CincoEdge);
            const gNodes = children.filter(gElement => nodes.find(n => n.id === gElement.key));
            const gContainers = children.filter(gElement => containers.find(n => n.id === gElement.key));
            const gEdges = children.filter(gElement => edges.find(n => n.id === gElement.key));
            const rest = children.filter(
                gElement => !gEdges.includes(gElement) && !gContainers.includes(gElement) && !gNodes.includes(gElement)
            );
            return (
                <g className={artificialCSSClass}>
                    {gContainers as Iterable<React.ReactNode>}
                    {gNodes as Iterable<React.ReactNode>}
                    {gEdges as Iterable<React.ReactNode>}
                    {rest as Iterable<React.ReactNode>}
                </g>
            ) as unknown as VNode;
        };

        const parameterCount = style?.parameterCount ?? 0;

        // Shape
        this.ITEM_COUNTER = 0;
        const parentScale = { x: 1, y: 1 };
        if (style?.shape) {
            const shape = style.shape;
            if (ContainerShape.is(shape) || Polyline.is(shape) || Image.is(shape)) {
                const shapeSize = shape.size;
                if (shapeSize) {
                    parentScale.x = node.size.width / (shapeSize.width ?? node.size.width);
                    parentScale.y = node.size.height / (shapeSize.height ?? node.size.height);
                }
            }
        }
        const vnode = buildShape(node, style?.shape, node.size, parentScale, undefined, false, parameterCount, this.workspaceFileService)!;

        // Selector
        const borderSize = (vnode?.data?.style?.['strokeWidth'] ?? 0) as number;
        const selector = this.createSelector(node, borderSize);

        // resolve children
        const vNodeChildren = resolveChildrenRecursivly(vnode);
        const children = [vnode].concat(vNodeChildren).concat([selector]);
        const container = mainContainer(children);

        // context-elements (and fix for false located children, e.g. resizeHandles)
        const oldBounds = node.bounds;
        node.bounds = {
            x: node.bounds.x,
            y: node.bounds.y,
            // temporarily add bordersizes to nodes bounds
            width: Math.max(node.bounds.width + 2 * borderSize, 0),
            height: Math.max(node.bounds.height + 2 * borderSize, 0)
        };
        const contextChildren = context.renderChildren(node);
        container.children = container.children?.concat(contextChildren);
        contextChildren.forEach(child => {
            child.data = child.data ?? {};
            child.data.attrs = child.data.attrs ?? {};
            // add negative offset to children, because of the unrespected stroke/border
            child.data.attrs.cx = (child.data.attrs.cx as number) - borderSize;
            child.data.attrs.cy = (child.data.attrs.cy as number) - borderSize;
        });
        node.bounds = oldBounds;

        return container;
    }

    /**
     * MANDATORY-HELPER-FUNCTIONS
     */

    protected createSelector(node: CincoNode, borderSize: number): VNode {
        const result = (
            <rect
                class-sprotty-node={node instanceof SNode}
                class-sprotty-port={node instanceof SPort}
                class-mouseover={node?.hoverFeedback}
                class-selected={node?.selected}
                width={Math.max(node.size.width + 2 * borderSize, 0)}
                height={Math.max(node.size.height + 2 * borderSize, 0)}
                style={{ fill: '#a41d1d00', opacity: '1.0', pointerEvents: 'none' }}
                x={0 - borderSize}
                y={0 - borderSize}
            ></rect>
        ) as unknown as VNode;
        result.children = [];
        return result;
    }
}
