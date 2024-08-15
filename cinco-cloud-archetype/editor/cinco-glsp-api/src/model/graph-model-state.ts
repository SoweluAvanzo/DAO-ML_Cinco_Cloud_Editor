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
import { EdgeType, NodeType, getFileExtension, getGraphModelOfFileType, getSpecOf, isContainer } from '@cinco-glsp/cinco-glsp-common';
import { DefaultModelState, JsonModelState } from '@eclipse-glsp/server';
import { inject, injectable } from 'inversify';
import { Container, Edge, GraphModel, ModelElement, ModelElementContainer, Node } from './graph-model';
import { GraphModelIndex } from './graph-model-index';

@injectable()
export class GraphModelState extends DefaultModelState implements JsonModelState {
    @inject(GraphModelIndex)
    override readonly index: GraphModelIndex;

    protected _graphModel: GraphModel;

    get sourceModel(): object {
        return GraphModelState.resolveGraphmodel(this.graphModel, new GraphModel(), undefined);
    }

    get graphModel(): GraphModel {
        return this._graphModel;
    }

    set graphModel(graphModel: GraphModel) {
        this._graphModel = GraphModelState.resolveGraphmodel(graphModel, new GraphModel(), this.index);
        this.index.indexGraphModel(this._graphModel);
    }

    refresh(): void {
        this.graphModel = this._graphModel;
    }

    static resolveGraphmodel(source: GraphModel, target: GraphModel, index: GraphModelIndex | undefined): GraphModel {
        target = Object.assign(target, source);
        target.index = index;
        target.containments = this.resolveContainments(source, index);
        target.edges = this.resolveEdges(source, index);
        return target;
    }

    static resolveEdges(graphmodel: GraphModel, index: GraphModelIndex | undefined): Edge[] {
        return graphmodel._edges.map(el => this.cleanModelElementInstance(el, index) as Edge);
    }

    static resolveContainments(el: ModelElementContainer, index: GraphModelIndex | undefined): Node[] {
        let containments = el._containments;
        if (containments === undefined) {
            return [];
        }
        containments = containments
            .map(containment => this.cleanModelElementInstance(containment, index) as Node)
            .filter((node): node is Node => !!node);
        return containments;
    }

    static cleanModelElementInstance(el: ModelElement, index: GraphModelIndex | undefined): ModelElement | undefined {
        let element: ModelElement | undefined;
        const spec = getSpecOf(el.type);
        if (NodeType.is(spec)) {
            if (isContainer(spec.elementTypeId)) {
                element = new Container();
            } else {
                element = new Node();
            }
        } else if (EdgeType.is(spec)) {
            element = new Edge();
        } else {
            element = new ModelElement();
        }
        element = this.copyModelElementProperties(el, index, element);
        if (ModelElementContainer.is(element)) {
            element._containments = this.resolveContainments(element, index);
        }
        return element;
    }

    static copyModelElementProperties<T extends ModelElement>(source: T, index: GraphModelIndex | undefined, target: T): T {
        target.type = source.type;
        target.initializeProperties();
        target = Object.assign(target, source);
        target.index = index;
        target.id = source.id;
        return target;
    }

    updateSourceModel(sourceModel: object): void {
        if (GraphModel.is(sourceModel)) {
            if (!this.graphModel._sourceUri) {
                throw new Error('SourceModel has no sourceUri.');
            }
            const graphModel = GraphModelState.fixMissingProperties(sourceModel, this.graphModel._sourceUri);
            this.graphModel = graphModel;
        } else {
            throw new Error('SourceModel update is no valid GraphModel!');
        }
    }

    static fixMissingProperties(graphModel: GraphModel, sourceUri: string): GraphModel {
        if (!graphModel.type) {
            graphModel.type = getGraphModelOfFileType(getFileExtension(sourceUri))?.elementTypeId ?? 'graphmodel';
        }
        if (!graphModel._containments) {
            graphModel._containments = [];
        }
        if (!graphModel._edges) {
            graphModel._edges = [];
        }
        graphModel._sourceUri = sourceUri;
        return graphModel;
    }
}
