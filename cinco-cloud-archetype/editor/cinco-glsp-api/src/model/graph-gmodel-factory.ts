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
import { EdgeType, NodeType, getSpecOf } from '@cinco-glsp/cinco-glsp-common';
import { GEdge, GEdgeBuilder, GGraph, GModelElement, GModelFactory, GNode, GNodeBuilder } from '@eclipse-glsp/server';
import { inject, injectable } from 'inversify';
import { Container, Edge, GraphModel, Node } from './graph-model';
import { GraphModelState } from './graph-model-state';

@injectable()
export class GraphGModelFactory implements GModelFactory {
    @inject(GraphModelState)
    readonly modelState: GraphModelState;

    createModel(): void {
        const graphmodel = this.modelState.graphModel;
        this.modelState.index.indexGraphModel(graphmodel);
        const children = this.collectChildren(graphmodel);
        const newRoot = GGraph.builder().type(graphmodel.type).id(graphmodel.id).addChildren(children).build();
        this.modelState.updateRoot(newRoot);
    }

    protected collectChildren(container: GraphModel | Container): GModelElement[] {
        const children: GModelElement[] = [];
        container._containments.forEach(n => {
            if (Container.is(n)) {
                const containerChildren = this.collectChildren(n as Container);
                const containerNode = this.createNode(n);
                containerNode.children = containerChildren;
                children.push(containerNode);
            } else if (Node.is(n)) {
                children.push(this.createNode(n));
            }
        });
        if (container instanceof GraphModel) {
            container._edges.forEach(e => children.push(this.createEdge(e)));
        }
        return children;
    }

    protected createNode<T extends Node>(node: T, add?: (preBuild: GNodeBuilder, t: T) => GNodeBuilder): GNode {
        const spec = getSpecOf(node.type) as NodeType | undefined;

        // css
        const cssClasses: string[] = node.cssClasses ?? [];

        // layout is specified
        const layout = 'hbox';
        const layoutOptions = spec?.view?.layoutOptions !== undefined ? spec.view.layoutOptions : [];
        const builder = GNode.builder().type(node.type).id(node.id).position(node.position);
        if (cssClasses !== undefined) {
            cssClasses?.forEach(css => builder.addCssClass(css));
        }
        if (add !== undefined) {
            add(builder, node);
        }
        if (layout !== undefined) {
            builder.layout(layout);
        }
        if (node.size !== undefined) {
            builder.addLayoutOptions({
                prefWidth: node.size.width,
                prefHeight: node.size.height
            });
        }
        if (layoutOptions.length > 0) {
            layoutOptions.forEach((option: Map<string, string>) => {
                builder.addLayoutOptions(option);
            });
        }
        // add view- and property-information
        builder.addArg('persistedView', JSON.stringify(node.view));
        builder.addArg('properties', JSON.stringify(node.properties));
        return builder.build();
    }

    protected createEdge<T extends Edge>(edge: T, add?: (preBuild: GEdgeBuilder, t: T) => GEdgeBuilder): GEdge {
        const spec = getSpecOf(edge.type) as EdgeType | undefined;

        // css
        const cssClasses: string[] = edge.cssClasses ?? [];

        const routerKind = spec?.view?.routerKind;

        if (edge.sourceIDs().length === 1) {
            const sourceID = edge.sourceIDs()[0];
            const builder = GEdge.builder() //
                .type(edge.type)
                .id(edge.id)
                .sourceId(sourceID)
                .targetId(edge.targetID);
            if (cssClasses !== undefined) {
                cssClasses?.forEach((css: string) => builder.addCssClass(css));
            }
            if (routerKind !== undefined) {
                builder.routerKind(routerKind);
            }
            if (add !== undefined) {
                add(builder, edge);
            }

            // add routingPoints-, view- and property-information
            builder.addArg('routingPoints', JSON.stringify(edge.routingPoints));
            builder.addArg('persistedView', JSON.stringify(edge.view));
            builder.addArg('properties', JSON.stringify(edge.properties));
            return builder.build();
        } else {
            throw new Error('TODO: Render conflict marker');
        }
    }
}
