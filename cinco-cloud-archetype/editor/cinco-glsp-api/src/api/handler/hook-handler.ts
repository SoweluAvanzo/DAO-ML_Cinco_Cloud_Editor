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
/* eslint-disable @typescript-eslint/no-empty-function */
import { hasFunctionProp, Point } from '@eclipse-glsp/server';
import { PropertyEditOperation, AnyObject, UserDefinedType, Cell, LayoutArgument } from '@cinco-glsp/cinco-glsp-common';
import { APIBaseHandler } from './api-base-handler';
import { ResizeBounds } from '../types/resize-bounds';
import { Edge, GraphModel, ModelElement, Node, ModelElementContainer } from '../../model/graph-model';

export abstract class AbstractHook extends APIBaseHandler {}

export abstract class AbstractNodeHook extends AbstractHook implements NodeHook {
    // Create
    canCreate(elementTypeId: string, container: ModelElementContainer, position?: Point): boolean {
        return true;
    }
    preCreate(elementTypeId: string, container: ModelElementContainer, position?: Point): void {}
    postCreate(node: Node): void {}
    // Delete
    canDelete(node: Node): boolean {
        return true;
    }
    preDelete(node: Node): void {}
    postDelete(node: Node): void {}
    // Attribute Change
    canAttributeChange(node: Node, operation: PropertyEditOperation): boolean {
        return true;
    }
    preAttributeChange(node: Node, operation: PropertyEditOperation): void {}
    postAttributeChange(node: Node, attributeName: string, oldValue: any): void {}
    // Select
    canSelect(node: Node, isSelected: boolean): boolean {
        return true;
    }
    postSelect(node: Node, isSelected: boolean): void {}
    // Double Click
    canDoubleClick(node: Node): boolean {
        return true;
    }
    postDoubleClick(node: Node): void {}
    // Move
    canMove(node: Node, newPosition?: Point): boolean {
        return true;
    }
    preMove(node: Node, newPosition?: Point): void {}
    postMove(node: Node, oldPosition?: Point): void {}
    // Resize
    canResize(node: Node, resizeBounds: ResizeBounds): boolean {
        return true;
    }
    preResize(node: Node, resizeBounds: ResizeBounds): void {}
    postResize(node: Node, resizeBounds: ResizeBounds): void {}
    // Layout
    canLayout(modelElement: Node, parameter: LayoutArgument): boolean {
        return true;
    }
    preLayout(modelElement: Node, parameter: LayoutArgument): void {}
    postLayout(modelElement: Node, parameter: LayoutArgument): void {}
}

export abstract class AbstractEdgeHook extends AbstractHook implements EdgeHook {
    // Create
    canCreate(elementTypeId: string, source: Node, target: Node): boolean {
        return true;
    }
    preCreate(elementTypeId: string, source: Node, target: Node): void {}
    postCreate(edge: Edge): void {}
    // Delete
    canDelete(edge: Edge): boolean {
        return true;
    }
    preDelete(edge: Edge): void {}
    postDelete(edge: Edge): void {}
    // Attribute Change
    canAttributeChange(edge: Edge, operation: PropertyEditOperation): boolean {
        return true;
    }
    preAttributeChange(edge: Edge, operation: PropertyEditOperation): void {}
    postAttributeChange(edge: Edge, attributeName: string, oldValue: any): void {}
    // Select
    canSelect(edge: Edge, isSelected: boolean): boolean {
        return true;
    }
    postSelect(edge: Edge, isSelected: boolean): void {}
    // Double Click
    canDoubleClick(edge: Edge): boolean {
        return true;
    }
    postDoubleClick(edge: Edge): void {}
    // Reconnect
    canReconnect(edge: Edge, newSource: Cell<Node>, newTarget: Cell<Node>): boolean {
        return true;
    }
    preReconnect(edge: Edge, newSource: Cell<Node>, newTarget: Cell<Node>): void {}
    postReconnect(edge: Edge, oldSource: Cell<Node>, oldTarget: Cell<Node>): void {}
    // Layout
    canLayout(modelElement: Edge, parameter: LayoutArgument): boolean {
        return true;
    }
    preLayout(modelElement: Edge, parameter: LayoutArgument): void {}
    postLayout(modelElement: Edge, parameter: LayoutArgument): void {}
}

export abstract class AbstractGraphModelHook extends AbstractHook implements GraphModelHook {
    // Create
    canCreate(elementTypeId: string, path: string): boolean {
        return true;
    }
    preCreate(elementTypeId: string, path: string): void {}
    postCreate(graphModel: GraphModel): void {}
    // Modelfile Change
    postDelete(path: string): void {
        // THIS IS A SYSTEM TRACKED HOOK. YOU CAN NOT USE GUI-RELATED FEEDBACK.
    }
    postPathChange(graphModel: GraphModel): void {
        // THIS IS A SYSTEM TRACKED HOOK. YOU CAN NOT USE GUI-RELATED FEEDBACK.
    }
    postContentChange(graphModel: GraphModel): void {
        // THIS IS A SYSTEM TRACKED HOOK. YOU CAN NOT USE GUI-RELATED FEEDBACK.
    }
    // Attribute Change
    canAttributeChange(graphModel: GraphModel, operation: PropertyEditOperation): boolean {
        return true;
    }
    preAttributeChange(graphModel: GraphModel, operation: PropertyEditOperation): void {}
    postAttributeChange(graphModel: GraphModel, attributeName: string, oldValue: any): void {}
    // Select
    canSelect(graphModel: GraphModel, isSelected: boolean): boolean {
        return true;
    }
    postSelect(graphModel: GraphModel, isSelected: boolean): void {}
    // Double Click
    canDoubleClick(graphModel: GraphModel): boolean {
        return true;
    }
    postDoubleClick(graphModel: GraphModel): void {}
    // Save
    canSave(graphModel: GraphModel, path: string): boolean {
        return true;
    }
    postSave(graphModel: GraphModel, path: string): void {}
    onOpen(graphModel: GraphModel): void {}
    // Layout
    canLayout(modelElement: GraphModel, parameter: LayoutArgument): boolean {
        return true;
    }
    preLayout(modelElement: GraphModel, parameter: LayoutArgument): void {}
    postLayout(modelElement: GraphModel, parameter: LayoutArgument): void {}
}

// TODO-SAMI: This is not yet further implemented
export abstract class AbstractUserDefinedTypeHook extends AbstractHook implements UserdefinedTypeHook {
    // Create
    canCreate(host: ModelElement): boolean {
        return true;
    }
    preCreate(host: ModelElement): void {}
    postCreate(modelElement: UserDefinedType): void {}
    // Delete
    canDelete(edge: Edge): boolean {
        return true;
    }
    preDelete(edge: Edge): void {}
    postDelete(edge: Edge): void {}
    // Attribute Change
    canAttributeChange(modelElement: Edge, operation: PropertyEditOperation): boolean {
        return true;
    }
    preAttributeChange(modelElement: Edge, operation: PropertyEditOperation): void {}
    postAttributeChange(edge: Edge, attributeName: string, oldValue: any): void {}
}

/**
 * INTERFACES
 */

interface NodeElementHook<T extends Node> extends GraphicalElementHook<T>, ModelElementHook<T>, AttributeHook<T> {
    canCreate(elementTypeId: string, container: ModelElementContainer, position?: Point): boolean;
    preCreate(elementTypeId: string, container: ModelElementContainer, position?: Point): void;
    canMove(node: T, newPosition?: Point): boolean;
    preMove(node: T, newPosition?: Point): void;
    postMove(node: T, oldPosition?: Point): void;
    canResize(node: T, resizeBounds: ResizeBounds): boolean;
    preResize(node: T, resizeBounds: ResizeBounds): void;
    postResize(node: T, resizeBounds: ResizeBounds): void;
}

export namespace NodeElementHook {
    export function is(object: any): object is NodeElementHook<any> {
        return (
            GraphicalElementHook.is(object) &&
            AttributeHook.is(object) &&
            (hasFunctionProp(object, 'preCreate') ||
                hasFunctionProp(object, 'canMove') ||
                hasFunctionProp(object, 'preMove') ||
                hasFunctionProp(object, 'postMove') ||
                hasFunctionProp(object, 'canResize') ||
                hasFunctionProp(object, 'preResize') ||
                hasFunctionProp(object, 'postResize'))
        );
    }
}

interface EdgeElementHook<T extends Edge> extends GraphicalElementHook<T>, ModelElementHook<T>, AttributeHook<T> {
    canCreate(elementTypeId: string, source: Node, target: Node): boolean;
    preCreate(elementTypeId: string, source: Node, target: Node): void;
    canReconnect(edge: T, newSource: Node, newTarget: Node): boolean;
    preReconnect(edge: T, newSource: Node, newTarget: Node): void;
    postReconnect(edge: T, oldSource: Node, oldTarget: Node): void;
}

export namespace EdgeElementHook {
    export function is(object: any): object is EdgeElementHook<any> {
        return (
            GraphicalElementHook.is(object) &&
            AttributeHook.is(object) &&
            (hasFunctionProp(object, 'preCreate') ||
                hasFunctionProp(object, 'canReconnect') ||
                hasFunctionProp(object, 'preReconnect') ||
                hasFunctionProp(object, 'postReconnect'))
        );
    }
}

export interface GraphModelElementHook<T extends GraphModel> extends GraphicalElementHook<T>, AttributeHook<T>, ModelFileHook<T> {
    preCreate(elementTypeId: string, path: string): void; // Use-case, prepare related files before creation of model
    canSave(graphModel: T, path: string): boolean;
    postSave(graphModel: T, path: string): void;
    onOpen(graphModel: T): void;
}

export namespace GraphModelElementHook {
    export function is(object: any): object is GraphModelElementHook<any> {
        return (
            GraphicalElementHook.is(object) &&
            AttributeHook.is(object) &&
            (hasFunctionProp(object, 'preCreate') ||
                hasFunctionProp(object, 'canSave') ||
                hasFunctionProp(object, 'postSave') ||
                hasFunctionProp(object, 'onOpen'))
        );
    }
}

interface UserdefinedTypeElementHook<T extends ModelElement> extends ModelElementHook<T> {
    preCreate(args: any): void;
}

export namespace UserdefinedTypeElementHook {
    export function is(object: any): object is UserdefinedTypeElementHook<any> {
        return ModelElementHook.is(object) && hasFunctionProp(object, 'preCreate');
    }
}

export interface AttributeHook<T extends ModelElement> {
    canAttributeChange(modelElement: T, operation: PropertyEditOperation): boolean;
    preAttributeChange(modelElement: T, operation: PropertyEditOperation): void;
    postAttributeChange(modelElement: T, attributeName: string, oldValue: any): void;
}

export namespace AttributeHook {
    export function is(object: any): object is AttributeHook<any> {
        return (
            hasFunctionProp(object, 'canSelect') ||
            hasFunctionProp(object, 'postSelect') ||
            hasFunctionProp(object, 'canDoubleClick') ||
            hasFunctionProp(object, 'postDoubleClick')
        );
    }
}

export interface GraphicalElementHook<T extends ModelElement> {
    canSelect(modelElement: T, isSelected: boolean): boolean;
    postSelect(modelElement: T, isSelected: boolean): void;
    canDoubleClick(modelElement: T): boolean;
    postDoubleClick(modelElement: T): void;
    canLayout(modelElement: T, parameter: LayoutArgument): boolean;
    preLayout(modelElement: T, parameter: LayoutArgument): void;
    postLayout(modelElement: T, parameter: LayoutArgument): void;
}

export namespace GraphicalElementHook {
    export function is(object: any): object is GraphicalElementHook<any> {
        return (
            ModelElementHook.is(object) &&
            (hasFunctionProp(object, 'canSelect') ||
                hasFunctionProp(object, 'postSelect') ||
                hasFunctionProp(object, 'canDoubleClick') ||
                hasFunctionProp(object, 'postDoubleClick') ||
                hasFunctionProp(object, 'canLayout') ||
                hasFunctionProp(object, 'preLayout') ||
                hasFunctionProp(object, 'postLayout'))
        );
    }
}

export interface ModelElementHook<T extends ModelElement> {
    // Create
    canCreate(...args: any): boolean;
    preCreate(...args: any): void;
    postCreate(modelElement: T): void;
    // Delete
    canDelete(modelElement: T): boolean;
    preDelete(modelElement: T): void;
    postDelete(modelElement: T): void;
}

export namespace ModelElementHook {
    export function is(object: any): object is ModelElementHook<any> {
        return (
            AnyObject.is(object) &&
            (hasFunctionProp(object, 'canCreate') ||
                hasFunctionProp(object, 'preCreate') ||
                hasFunctionProp(object, 'postCreate') ||
                hasFunctionProp(object, 'canDelete') ||
                hasFunctionProp(object, 'preDelete') ||
                hasFunctionProp(object, 'postDelete'))
        );
    }
}

export interface ModelFileHook<T extends ModelElement> {
    // Create
    canCreate(...args: any): boolean;
    preCreate(...args: any): void;
    postCreate(modelElement: T): void;
    // Delete
    postDelete(path: string): void;
    // Change
    postPathChange(modelElement: T): void;
    postContentChange(modelElement: T): void;
}

export namespace ModelFileHook {
    export function is(object: any): object is ModelFileHook<any> {
        return (
            AnyObject.is(object) &&
            (hasFunctionProp(object, 'canCreate') ||
                hasFunctionProp(object, 'preCreate') ||
                hasFunctionProp(object, 'postCreate') ||
                hasFunctionProp(object, 'postDelete') ||
                hasFunctionProp(object, 'postPathChange') ||
                hasFunctionProp(object, 'postContentChange'))
        );
    }
}

interface NodeHook extends NodeElementHook<Node> {}
interface EdgeHook extends EdgeElementHook<Edge> {}
interface GraphModelHook extends GraphModelElementHook<GraphModel> {}

// TODO-SAMI: This is not yet further implemented
interface UserdefinedTypeHook extends ModelElementHook<any> {
    canCreate(host: ModelElement): boolean;
    preCreate(host: ModelElement): void;
    postCreate(modelElement: UserDefinedType): void;
}
