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
import { EdgeType, NodeType, getSpecOf, isContainer } from '@cinco-glsp/cinco-glsp-common';
import { DefaultModelState } from '@eclipse-glsp/server-node';
import { inject, injectable } from 'inversify';
import { Container, Edge, GraphModel, ModelElement, ModelElementContainer, Node } from './graph-model';
import { GraphModelIndex } from './graph-model-index';

@injectable()
export class GraphModelState extends DefaultModelState {
    @inject(GraphModelIndex)
    override readonly index: GraphModelIndex;

    protected _graphModel: GraphModel;

    get graphModel(): GraphModel {
        return this._graphModel;
    }

    set graphModel(graphModel: GraphModel) {
        this._graphModel = this.resolveGraphmodel(graphModel, new GraphModel(), this.index);
        this.index.indexGraphModel(this._graphModel);
    }

    refresh(): void {
        this.graphModel = this._graphModel;
    }

    resolveGraphmodel(source: GraphModel, target: GraphModel, index: GraphModelIndex | undefined): GraphModel {
        target = Object.assign(target, source);
        target.index = index;
        target.containments = this.resolveContainments(source, index);
        target.edges = this.resolveEdges(source, index);
        return target;
    }

    resolveEdges(graphmodel: GraphModel, index: GraphModelIndex | undefined): Edge[] {
        return graphmodel._edges.map(el => this.cleanModelElementInstance(el, index) as Edge);
    }

    resolveContainments(el: ModelElementContainer, index: GraphModelIndex | undefined): Node[] {
        let containments = el._containments;
        if (containments === undefined) {
            return [];
        }
        containments = containments
            .map(containment => this.cleanModelElementInstance(containment, index) as Node)
            .filter((node): node is Node => !!node);
        return containments;
    }

    cleanModelElementInstance(el: ModelElement, index: GraphModelIndex | undefined): ModelElement | undefined {
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

    copyModelElementProperties<T extends ModelElement>(source: T, index: GraphModelIndex | undefined, target: T): T {
        target.type = source.type;
        target.initializeProperties();
        target = Object.assign(target, source);
        target.index = index;
        target.id = source.id;
        return target;
    }
}
