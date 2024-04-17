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
/* eslint-disable @typescript-eslint/no-empty-function */
import { CreateEdgeOperation, CreateNodeOperation, CreateOperation, Dimension, Point } from '@eclipse-glsp/server';
import { PropertyEditOperation } from '@cinco-glsp/cinco-glsp-common/src/protocol/property-protocol';
import { APIBaseHandler } from '../api/api-base-handler';
import { Edge, GraphModel, ModelElement, Node } from '../model/graph-model';
import { HookRegistry } from '../hook-registry';

export abstract class AbstractHooks extends APIBaseHandler {

    static typeId = 'none';
    static hookName = 'AbstractHooks';
    static hookTypes: string[] = [];
    static readonly actionKinds: string[] = [];
    static register(): void {
        HookRegistry.registerHooks(this);
    }
}

export abstract class AbstractNodeHooks extends AbstractHooks implements NodeHooks {
    canChangeAttribute(operation: PropertyEditOperation): boolean {
        return true;
    }
    preAttributeChange(operation: PropertyEditOperation): void { }
    postAttributeChange(node: Node, attributeName: string, oldValue: any ): void { }
    canCreate(operation: CreateNodeOperation): boolean {
        return true;
    }
    preCreate(containerId: string, locaction: Point | undefined): void { }
    postCreate(node: Node): void { }
    canDelete(node: Node): boolean {
        return true;
    }
    preDelete(node: Node): void { }
    postDelete(node: Node): void { }
    canDoubleClick(node: Node): boolean {
        return true;
    }
    postDoubleClick(node: Node): void { }
    canMove(node: Node, newPosition?: Point): boolean {
        return true;
    }
    preMove(node: Node, newPosition?: Point): void { }
    postMove(node: Node, oldPosition?: Point): void { }
    canResize(node: Node, newSize?: Dimension): boolean {
        return true;
    }
    preResize(node: Node, newSize?: Dimension): void { }
    postResize(node: Node, oldSize?: Dimension): void { }
    canSelect(node: Node): boolean {
        return true;
    }
    postSelect(node: Node): void { }
}

export abstract class AbstractEdgeHooks extends AbstractHooks implements EdgeHooks {
    canChangeAttribute(operation: PropertyEditOperation): boolean {
        return true;
    }
    preAttributeChange(operation: PropertyEditOperation): void { }
    postAttributeChange(edge: Edge, attributeName: string, oldValue: any): void { }
    canCreate(operation: CreateEdgeOperation): boolean {
        return true;
    }
    preCreate(sourceElementId: string, targetElementId: string): void { }
    postCreate(edge: Edge): void { }
    canDoubleClick(edge: Edge): boolean {
        return true;
    }
    postDoubleClick(edge: Edge): void { }
    canDelete(edge: Edge): boolean {
        return true;
    }
    preDelete(edge: Edge): void { }
    postDelete(edge: Edge): void { }
    canReconnect(edge: Edge): boolean {
        return true;
    }
    preReconnect(edge: Edge): void { }
    postReconnect(edge: Edge): void { }
}

export abstract class AbstractGraphModelHooks extends AbstractHooks implements GraphModelHooks {
    canChangeAttribute(operation: PropertyEditOperation): boolean {
        return true;
    }
    preAttributeChange(operation: PropertyEditOperation): void { }
    postAttributeChange(graphModel: GraphModel, attributeName: string, oldValue: any): void { }
    preCreate(graphModel: GraphModel): void { }
    postCreate(graphModel: GraphModel): void { }
    canDoubleClick(graphModel: GraphModel): boolean {
        return true;
    }
    postDoubleClick(graphModel: GraphModel): void { }
    preSave(graphModel: GraphModel): void { }
    postSave(graphModel: GraphModel): void { }
}

export interface Hooks<T extends ModelElement> {
    canChangeAttribute(operation: PropertyEditOperation): boolean;
    preAttributeChange(operation: PropertyEditOperation): void;
    postAttributeChange(modelElement: T, attributeName: string, oldValue: any): void;
    postCreate(modelElement: T): void;
    canDoubleClick(modelElement: T): boolean;
    postDoubleClick(modelElement: T): void;
}

interface GraphicalElements<T extends ModelElement> {
    canCreate(operation: CreateOperation): boolean;
    canDelete(modelElement: T): boolean;
    preDelete(modelElement: T): void;
    postDelete(modelElement: T): void;
}

interface NodeElements<T extends Node> {
    canMove(node: T, newPosition?: Point): boolean;
    preMove(node: T, newPosition?: Point): void;
    postMove(node: T, oldPosition?: Point): void;
    canResize(node: T, newSize?: Dimension): boolean;
    preResize(node: T, newSize?: Dimension): void;
    postResize(node: T, oldSize?: Dimension): void;
    canSelect(node: T): boolean;
    postSelect(node: T): void;
    preCreate(containerId: string, locaction: Point | undefined): void;
}

interface EdgeElements<T extends Edge> {
    canReconnect(edge: T): boolean;
    preReconnect(edge: T): void;
    postReconnect(edge: T): void;
    preCreate(sourceElementId: string, targetElementId: string): void;
}

interface GraphModelElements<T extends GraphModel> {
    preSave(graphModel: T): void;
    postSave(graphModel: T): void;
}

interface NodeHooks extends Hooks<Node>, GraphicalElements<Node>, NodeElements<Node> { }
interface EdgeHooks extends Hooks<Edge>, GraphicalElements<Edge>, EdgeElements<Edge> { }
interface GraphModelHooks extends Hooks<GraphModel>, GraphModelElements<GraphModel> { }
