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
import { GModelElement, GModelIndex, GModelRoot } from '@eclipse-glsp/server';
import { injectable } from 'inversify';
import { Container, Edge, GraphModel, IdentifiableElement, ModelElement, ModelElementContainer, Node } from './graph-model';

@injectable()
export class GraphModelIndex extends GModelIndex {
    protected graphmodel: GraphModel;
    protected edgesIndex = new Map<string, Edge>();
    protected nodesIndex = new Map<string, Node>();
    protected containmentsIndex = new Map<Node, Container | GraphModel>(); // index to get Container-Node by Node

    indexGraphModel(graphModel: GraphModel): void {
        this.graphmodel = graphModel;
        this.indexContainments(this.graphmodel);
        this.indexEdges(this.graphmodel);
    }

    indexEdges(container: GraphModel): void {
        container.edges.forEach(e => {
            this.edgesIndex.set(e.id, e);
        });
    }

    indexContainments(container: GraphModel | Container): void {
        container._containments.forEach(n => {
            this.nodesIndex.set(n.id, n);
            this.containmentsIndex.set(n, container);
            if (Container.is(n)) {
                this.indexContainments(n as Container);
            }
        });
    }

    getModelElements(elementTypeId: string): ModelElement[] {
        const allEdges = Array.from(this.edgesIndex.values());
        const allNodes = Array.from(this.nodesIndex.values());
        const allElements = [this.graphmodel as ModelElement].concat(allEdges).concat(allNodes);
        return allElements.filter(e => e.type === elementTypeId);
    }

    findModelElement(id: string | undefined): ModelElement | undefined {
        if (id === undefined) {
            return undefined;
        }
        return this.getRoot().id === id ? this.getRoot() : this.findNode(id) ?? this.findEdge(id) ?? undefined;
    }

    findElement(id: string | undefined): IdentifiableElement | undefined {
        if (id === undefined) {
            return undefined;
        }
        return this.getRoot().id === id ? this.getRoot() : this.findNode(id) ?? this.findEdge(id) ?? undefined;
    }

    findEdge(id: string): Edge | undefined {
        return this.edgesIndex.get(id);
    }

    findNode(id: string): Node | undefined {
        return this.nodesIndex.get(id);
    }

    getRoot(): GraphModel {
        return this.graphmodel;
    }

    findContainment(node: Node): ModelElementContainer | undefined {
        return this.containmentsIndex.get(node);
    }

    findGElement(id: string): GModelElement | undefined {
        const result = this.idToElement.get(id);
        return result !== undefined ? (result as GModelElement) : undefined;
    }

    getGRoot(): GModelRoot {
        return this.findGElement(this.graphmodel.id)! as GModelRoot;
    }

    getAllEdgeElements(): Edge[] {
        return Array.from(this.edgesIndex.values());
    }

    /**
     * Returns all incoming edges for a node.
     *
     * @param node The node where the edges are connected.
     *
     * @returns All incoming edges.
     */
    getIncomingEdgeElements(node: Node): Edge[] {
        return this.getAllEdgeElements().filter(edge => edge.targetIDs.includes(node.id));
    }

    /**
     * Returns all outgoing edges for a node.
     *
     * @param node The node where the edges are connected.
     *
     * @returns All outgoing edges.
     */
    getOutgoingEdgeElements(node: ModelElement): Edge[] {
        return this.getAllEdgeElements().filter(edge => edge.sourceIDs.includes(node.id));
    }
}
