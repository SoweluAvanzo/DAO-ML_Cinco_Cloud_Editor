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
import { CreateEdgeOperation, CreateNodeOperation, CreateOperation, Dimension, hasFunctionProp, Point } from '@eclipse-glsp/server';
import { PropertyEditOperation } from '@cinco-glsp/cinco-glsp-common/src/protocol/property-protocol';
import { APIBaseHandler } from '../api/api-base-handler';
import { Edge, GraphModel, ModelElement, Node, ModelElementContainer } from '../model/graph-model';
import { AnyObject } from '@cinco-glsp/cinco-glsp-common';

export abstract class AbstractHooks extends APIBaseHandler {}

export abstract class AbstractNodeHooks extends AbstractHooks implements NodeHooks {
    // Create
    canCreate(operation: CreateNodeOperation): boolean {
        return true;
    }
    preCreate(container: ModelElementContainer, location: Point | undefined): void {}
    postCreate(node: Node): void {}
    // Delete
    canDelete(node: Node): boolean {
        return true;
    }
    preDelete(node: Node): void {}
    postDelete(node: Node): void {}
    // Attribute Change
    canChangeAttribute(modelElement: Node, operation: PropertyEditOperation): boolean {
        return true;
    }
    preAttributeChange(modelElement: Node, operation: PropertyEditOperation): void {}
    postAttributeChange(node: Node, attributeName: string, oldValue: any): void {}
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
    canResize(node: Node, newSize?: Dimension): boolean {
        return true;
    }
    preResize(node: Node, newSize?: Dimension): void {}
    postResize(node: Node, oldSize?: Dimension): void {}
    // Select
    canSelect(node: Node): boolean {
        return true;
    }
    postSelect(node: Node): void {}
}

export abstract class AbstractEdgeHooks extends AbstractHooks implements EdgeHooks {
    // Attribute Change
    canChangeAttribute(modelElement: Edge, operation: PropertyEditOperation): boolean {
        return true;
    }
    preAttributeChange(modelElement: Edge, operation: PropertyEditOperation): void {}
    postAttributeChange(edge: Edge, attributeName: string, oldValue: any): void {}
    // Create
    canCreate(operation: CreateEdgeOperation): boolean {
        return true;
    }
    preCreate(source: Node, target: Node): void {}
    postCreate(edge: Edge): void {}
    // Double Click
    canDoubleClick(edge: Edge): boolean {
        return true;
    }
    postDoubleClick(edge: Edge): void {}
    // Delete
    canDelete(edge: Edge): boolean {
        return true;
    }
    preDelete(edge: Edge): void {}
    postDelete(edge: Edge): void {}
    // Reconnect
    canReconnect(edge: Edge, newSource: Node, newTarget: Node): boolean {
        return true;
    }
    preReconnect(edge: Edge, newSource: Node, newTarget: Node): void {}
    postReconnect(edge: Edge, oldSource: Node, oldTarget: Node): void {}
    // Select
    canSelect(edge: Edge): boolean {
        return true;
    }
    postSelect(edge: Edge): void {}
}

export abstract class AbstractGraphModelHooks extends AbstractHooks implements GraphModelHooks {
    // Attribute Change
    canChangeAttribute(modelElement: GraphModel, operation: PropertyEditOperation): boolean {
        return true;
    }
    preAttributeChange(modelElement: GraphModel, operation: PropertyEditOperation): void {}
    postAttributeChange(graphModel: GraphModel, attributeName: string, oldValue: any): void {}
    // Create
    preCreate(path: string): void {}
    postCreate(graphModel: GraphModel): void {}
    canCreate(operation: CreateOperation): boolean {
        return true;
    }
    // Delete
    canDelete(modelElement: GraphModel): boolean {
        return true;
    }
    preDelete(modelElement: GraphModel): void {}
    postDelete(modelElement: GraphModel): void {}
    // Double Click
    canDoubleClick(graphModel: GraphModel): boolean {
        return true;
    }
    postDoubleClick(graphModel: GraphModel): void {}
    // Select
    canSelect(modelElement: GraphModel): boolean {
        return true;
    }
    postSelect(modelElement: GraphModel): void {}
    // Save
    preSave(graphModel: GraphModel): void {}
    postSave(graphModel: GraphModel): void {}
}

// TODO-SAMI
export abstract class AbstractUserDefinedTypeHooks extends AbstractHooks implements UserdefinedTypeHooks {
    // Attribute Change
    canChangeAttribute(modelElement: Edge, operation: PropertyEditOperation): boolean {
        return true;
    }
    preAttributeChange(modelElement: Edge, operation: PropertyEditOperation): void {}
    postAttributeChange(edge: Edge, attributeName: string, oldValue: any): void {}
    // Create
    canCreate(operation: CreateEdgeOperation): boolean {
        return true;
    }
    preCreate(args: any): void {}
    postCreate(edge: Edge): void {}
    // Delete
    canDelete(edge: Edge): boolean {
        return true;
    }
    preDelete(edge: Edge): void {}
    postDelete(edge: Edge): void {}
}

/**
 * INTERFACES
 */

interface NodeElementHooks<T extends Node> extends GraphicalElementHooks<T>, AttributeHooks<T> {
    preCreate(container: ModelElementContainer, location: Point | undefined): void;
    canMove(node: T, newPosition?: Point): boolean;
    preMove(node: T, newPosition?: Point): void;
    postMove(node: T, oldPosition?: Point): void;
    canResize(node: T, newSize?: Dimension): boolean;
    preResize(node: T, newSize?: Dimension): void;
    postResize(node: T, oldSize?: Dimension): void;
}

export namespace NodeElementHooks {
    export function is(object: any): object is NodeElementHooks<any> {
        return (
            GraphicalElementHooks.is(object) &&
            AttributeHooks.is(object) &&
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

interface EdgeElementHooks<T extends Edge> extends GraphicalElementHooks<T>, AttributeHooks<T> {
    preCreate(sourceElement: Node, targetElement: Node): void;
    canReconnect(edge: T, newSource: Node, newTarget: Node): boolean;
    preReconnect(edge: T, newSource: Node, newTarget: Node): void;
    postReconnect(edge: T, oldSource: Node, oldTarget: Node): void;
}

export namespace EdgeElementHooks {
    export function is(object: any): object is EdgeElementHooks<any> {
        return (
            GraphicalElementHooks.is(object) &&
            AttributeHooks.is(object) &&
            (hasFunctionProp(object, 'preCreate') ||
                hasFunctionProp(object, 'canReconnect') ||
                hasFunctionProp(object, 'preReconnect') ||
                hasFunctionProp(object, 'postReconnect'))
        );
    }
}

interface GraphModelElementHooks<T extends GraphModel> extends GraphicalElementHooks<T>, AttributeHooks<T> {
    preCreate(path: string): void; // Use-case, prepare related files before creation of model
    preSave(graphModel: T): void;
    postSave(graphModel: T): void;
}

export namespace GraphModelElementHooks {
    export function is(object: any): object is GraphModelElementHooks<any> {
        return (
            GraphicalElementHooks.is(object) &&
            AttributeHooks.is(object) &&
            (hasFunctionProp(object, 'preCreate') || hasFunctionProp(object, 'preSave') || hasFunctionProp(object, 'postSave'))
        );
    }
}

interface UserdefinedTypeElementHooks<T extends ModelElement> extends ModelElementHooks<T> {
    preCreate(args: any): void;
}

export namespace UserdefinedTypeElementHooks {
    export function is(object: any): object is UserdefinedTypeElementHooks<any> {
        return ModelElementHooks.is(object) && hasFunctionProp(object, 'preCreate');
    }
}

export interface AttributeHooks<T extends ModelElement> {
    canChangeAttribute(modelElement: T, operation: PropertyEditOperation): boolean;
    preAttributeChange(modelElement: T, operation: PropertyEditOperation): void;
    postAttributeChange(modelElement: T, attributeName: string, oldValue: any): void;
}

export namespace AttributeHooks {
    export function is(object: any): object is AttributeHooks<any> {
        return (
            hasFunctionProp(object, 'canSelect') ||
            hasFunctionProp(object, 'postSelect') ||
            hasFunctionProp(object, 'canDoubleClick') ||
            hasFunctionProp(object, 'postDoubleClick')
        );
    }
}

export interface GraphicalElementHooks<T extends ModelElement> extends ModelElementHooks<T> {
    canSelect(modelElement: T): boolean;
    postSelect(modelElement: T): void;
    canDoubleClick(modelElement: T): boolean;
    postDoubleClick(modelElement: T): void;
}

export namespace GraphicalElementHooks {
    export function is(object: any): object is GraphicalElementHooks<any> {
        return (
            ModelElementHooks.is(object) &&
            (hasFunctionProp(object, 'canSelect') ||
                hasFunctionProp(object, 'postSelect') ||
                hasFunctionProp(object, 'canDoubleClick') ||
                hasFunctionProp(object, 'postDoubleClick'))
        );
    }
}

export interface ModelElementHooks<T extends ModelElement> {
    preCreate(...args: any): void;
    canCreate(operation: CreateOperation): boolean;
    postCreate(modelElement: T): void;
    canDelete(modelElement: T): boolean;
    preDelete(modelElement: T): void;
    postDelete(modelElement: T): void;
}

export namespace ModelElementHooks {
    export function is(object: any): object is ModelElementHooks<any> {
        return (
            AnyObject.is(object) &&
            (hasFunctionProp(object, 'canCreate') ||
                hasFunctionProp(object, 'postCreate') ||
                hasFunctionProp(object, 'canDelete') ||
                hasFunctionProp(object, 'preDelete') ||
                hasFunctionProp(object, 'postDelete'))
        );
    }
}

interface NodeHooks extends NodeElementHooks<Node> {}
interface EdgeHooks extends EdgeElementHooks<Edge> {}
interface GraphModelHooks extends GraphModelElementHooks<GraphModel> {}

// TODO:
interface UserdefinedTypeHooks extends UserdefinedTypeElementHooks<any> {}
