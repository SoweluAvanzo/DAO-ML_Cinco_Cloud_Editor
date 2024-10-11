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
import { RenderingContext, GGraph, GGraphView, svg, Bounds, GChildElement, EdgeRouterRegistry } from '@eclipse-glsp/client';
import { injectable, inject, optional } from 'inversify';
import { VNode } from 'snabbdom';
import { CincoEdge, CincoGraphModel, CincoMarker, CincoNode } from '../../model/model';
import { isEdgeType, isNodeType, isUnknownEdgeType, isUnknownNodeType } from './cinco-view-helper';
import { CincoNodeView } from './cinco-node-view';
import { CincoEdgeView } from './cinco-edge-view';
import { UNKNOWN_HEIGHT, UNKNOWN_WIDTH } from './unknown-definitions';
import { FeedbackEdgeEnd } from '@eclipse-glsp/client/lib/features/tools/edge-creation/dangling-edge-feedback';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const JSX = { createElement: svg };

/**
 * IView component that turns an SGraph element and its children into a tree of virtual DOM elements.
 */
@injectable()
export class CincoGraphView<IRenderingArgs> extends GGraphView {
    @inject(EdgeRouterRegistry) @optional() override edgeRouterRegistry: EdgeRouterRegistry;

    override render(model: Readonly<GGraph>, context: RenderingContext, args?: IRenderingArgs): VNode {
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

        // identify nodes, containers, edges and rest to fix clipping
        const feedbackEdges = model.children.filter(e => e instanceof FeedbackEdgeEnd);
        let nodes = model.children.filter(e => e instanceof CincoNode && !isUnknownNodeType(e) && !e.isContainer);
        let containers = model.children.filter(e => e instanceof CincoNode && !isUnknownNodeType(e) && e.isContainer);
        let edges = model.children.filter(e => e instanceof CincoEdge && !isUnknownEdgeType(e));
        const markers = model.children.filter(e => e instanceof CincoMarker) as GChildElement[];

        // identify unknowns
        let unknownNodes = model.children.filter(e => isUnknownNodeType(e));
        const unknownEdges = model.children.filter(e => isUnknownEdgeType(e));
        // get knownNodes that are no CincoNodes/CincoEdges
        const knownUnidentifiedNodes = model.children.filter(
            e => !isUnknownNodeType(e) && isNodeType(e) && !nodes.includes(e) && !containers.includes(e) && !markers.includes(e)
        );
        const mappedKnownNodes = knownUnidentifiedNodes.map(kn => Object.assign(new CincoNode(), kn));
        const knownUnidentifiedEdges = model.children.filter(e => !isUnknownEdgeType(e) && isEdgeType(e) && !edges.includes(e));
        containers = containers.concat(mappedKnownNodes.filter(mkn => mkn.isContainer));
        nodes = nodes.concat(mappedKnownNodes.filter(mkn => !mkn.isContainer));
        edges = edges.concat(knownUnidentifiedEdges.map(ke => Object.assign(new CincoEdge(), ke)));

        // correcting missing values with defaults
        unknownNodes = unknownNodes.map(un => {
            const anyUn = un as any;
            if (!anyUn['bounds']) {
                anyUn['bounds'] = {
                    x: anyUn['position'] ? anyUn['position'].x : 0,
                    y: anyUn['position'] ? anyUn['position'].y : 0,
                    width: anyUn['layoutOptions'] ? anyUn['layoutOptions'].prefWidth : UNKNOWN_WIDTH,
                    height: anyUn['layoutOptions'] ? anyUn['layoutOptions'].prefHeight : UNKNOWN_HEIGHT
                } as Bounds;
            }
            if (!anyUn['strokeWidth']) {
                anyUn['strokeWidth'] = 0;
            }
            return anyUn;
        });
        Object.assign(
            model.children,
            unknownNodes.concat(unknownEdges).concat(containers).concat(edges).concat(nodes).concat(feedbackEdges).concat(markers)
        );

        const edgeRouting = this.edgeRouterRegistry?.routeAllChildren(model);
        const elements = context.renderChildren(model, { edgeRouting, edgeRouterRegistry: this.edgeRouterRegistry });
        const gNodes = elements.filter(gElement => nodes.find(n => n.id === gElement.key));
        const gContainers = elements.filter(gElement => containers.find(n => n.id === gElement.key));
        const gEdges = elements.filter(gElement => edges.find(n => n.id === gElement.key));
        const gMarkers = elements.filter(gElement => markers.find(n => n.id === gElement.key));

        // handle unknowns
        const unknownNodeTypes = Array.from(new Set(unknownNodes.map(e => e.type)));
        const unknownEdgeTypes = Array.from(new Set(unknownEdges.map(e => e.type)));
        for (const unknown of unknownNodeTypes) {
            if (!context.viewRegistry.hasKey(unknown)) {
                context.viewRegistry.register(unknown, new CincoNodeView());
            }
        }
        for (const unknown of unknownEdgeTypes) {
            if (!context.viewRegistry.hasKey(unknown)) {
                context.viewRegistry.register(unknown, new CincoEdgeView());
            }
        }
        const unknownGNodes = elements.filter(gElement => unknownNodes.find(n => n.id === gElement.key));
        const unknownGEdges = elements.filter(gElement => unknownEdges.find(n => n.id === gElement.key));

        const rest = elements.filter(
            gElement =>
                !gEdges.includes(gElement) &&
                !gContainers.includes(gElement) &&
                !gNodes.includes(gElement) &&
                !unknownGNodes.includes(gElement) &&
                !unknownGEdges.includes(gElement) &&
                !gMarkers.includes(gElement)
        );

        return (
            <svg class-sprotty-graph={true}>
                <g transform={transform}>
                    {gContainers as Iterable<React.ReactNode>}
                    {gNodes as Iterable<React.ReactNode>}
                    {gEdges as Iterable<React.ReactNode>}
                    {unknownGNodes as Iterable<React.ReactNode>}
                    {unknownGEdges as Iterable<React.ReactNode>}
                    {gMarkers as Iterable<React.ReactNode>}
                    {rest as Iterable<React.ReactNode>}
                </g>
            </svg>
        ) as unknown as VNode;
    }
}
