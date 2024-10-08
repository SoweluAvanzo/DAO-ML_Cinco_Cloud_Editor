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
import { ActionDispatcher, GModelElement, GModelIndex, GModelRoot } from '@eclipse-glsp/server';
import { inject, injectable } from 'inversify';
import { Container, Edge, GraphModel, IdentifiableElement, ModelElement, ModelElementContainer, Node } from './graph-model';
import { CincoActionDispatcher } from '../api/cinco-action-dispatcher';
import { hasValidation, isContainer, ValidationRequestAction, ValidationResponseAction } from '@cinco-glsp/cinco-glsp-common';

@injectable()
export class GraphModelIndex extends GModelIndex {
    @inject(ActionDispatcher)
    protected actionDispatcher: CincoActionDispatcher;

    protected graphmodel: GraphModel;
    protected edgesIndex = new Map<string, Edge>();
    protected nodesIndex = new Map<string, Node>();
    protected reverseContainerIndex = new Map<string, Container | GraphModel>(); // index to get Container-Node by Node

    indexGraphModel(graphModel: GraphModel): void {
        this.graphmodel = graphModel;
        this.edgesIndex.clear();
        this.nodesIndex.clear();
        this.reverseContainerIndex.clear();
        this.idToElement.clear();
        this.typeToElements.clear();
        this.reverseIndexContainers(this.graphmodel);
        this.indexEdges(this.graphmodel);
    }

    indexEdges(graphModel: GraphModel): void {
        graphModel.edgeElements.forEach(e => {
            this.edgesIndex.set(e.id, e);
        });
    }

    reverseIndexContainers(container: GraphModel | Container): void {
        container.containedElements.forEach(node => {
            this.nodesIndex.set(node.id, node);
            this.reverseContainerIndex.set(node.id, container);
            if (isContainer(node.type)) {
                this.reverseIndexContainers(node as Container);
            }
        });
    }

    getAllModelElements(): ModelElement[] {
        if (!this.graphmodel) {
            return [];
        }
        let modelElements: ModelElement[] = [this.getRoot()];
        modelElements = modelElements.concat(Array.from(this.nodesIndex.values())).concat(Array.from(this.edgesIndex.values()));
        return modelElements;
    }

    getAllModelElementIds(): string[] {
        let ids: string[] = [this.getRoot().id];
        ids = ids.concat(Array.from(this.nodesIndex.keys())).concat(Array.from(this.edgesIndex.keys()));
        return ids;
    }

    getModelElements(elementTypeId: string): ModelElement[] {
        return this.getAllModelElements().filter(e => e.type === elementTypeId);
    }

    findModelElement(id: string | undefined): ModelElement | undefined {
        if (id === undefined) {
            return undefined;
        }
        return this.getRoot().id === id ? this.getRoot() : (this.findNode(id) ?? this.findEdge(id) ?? undefined);
    }

    findElement(id: string | undefined): IdentifiableElement | undefined {
        if (id === undefined) {
            return undefined;
        }
        return this.getRoot()?.id === id ? this.getRoot() : (this.findNode(id) ?? this.findEdge(id) ?? undefined);
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

    findContainerOf(id: string): ModelElementContainer | undefined {
        return this.reverseContainerIndex.get(id);
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

    async validate(element: ModelElement): Promise<boolean> {
        if (!hasValidation(element.type)) {
            return Promise.resolve(true);
        }
        const responses = await this.actionDispatcher.request(ValidationRequestAction.create(this.graphmodel.id, element.id));
        const validationResults = responses.filter(r => r.kind === ValidationResponseAction.KIND) as ValidationResponseAction[];
        return !ValidationResponseAction.containsErrors(validationResults);
    }
}
