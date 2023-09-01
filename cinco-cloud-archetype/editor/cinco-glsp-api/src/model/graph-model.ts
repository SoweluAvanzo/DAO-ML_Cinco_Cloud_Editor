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
import {
    AbsolutePosition,
    Appearance,
    Attribute,
    canBeCreated,
    Constraint,
    ContainerShape,
    EdgeStyle,
    EdgeType,
    ElementType,
    Ellipse,
    getAppearanceByNameOf,
    getAttributesOf,
    getDefaultValue,
    getEnum,
    getFallbackDefaultValue,
    getSpecOf,
    getStyleByNameOf,
    getUserDefinedType,
    GraphModelStyle,
    GraphType,
    Image,
    isList,
    isPrimitivePropertyType,
    MultiText,
    NodeStyle,
    NodeType,
    Polygon,
    Polyline,
    Rectangle,
    RoundedRectangle,
    RoutingPoint,
    Shape,
    Size,
    Style,
    Text,
    View
} from '@cinco-glsp/cinco-glsp-common';
import { AnyObject, GEdge, GNode, hasArrayProp, hasObjectProp, hasStringProp, Point } from '@eclipse-glsp/server-node';
import * as uuid from 'uuid';
import { GraphModelIndex } from './graph-model-index';

export interface IdentifiableElement {
    id: string;
}

export namespace IdentifiableElement {
    export function is(object: any): object is IdentifiableElement {
        return AnyObject.is(object) && hasStringProp(object, 'id');
    }
}

export interface ModelElementContainer {
    _containments: Node[];
}

export namespace ModelElementContainer {
    export function is(object: any): object is ModelElementContainer {
        return AnyObject.is(object) && hasArrayProp(object, '_containments');
    }
}

export class ModelElement implements IdentifiableElement {
    protected _index?: GraphModelIndex;
    id: string = uuid.v4();
    type: string;
    protected _attributes: Record<string, any> = {};
    protected _view?: View;

    get index(): GraphModelIndex {
        if (!this._index) {
            throw new Error('Index is not set!');
        }
        return this._index;
    }
    set index(index: GraphModelIndex | undefined) {
        this._index = index;
    }

    getSpec(): ElementType {
        return getSpecOf(this.type)!;
    }

    getGraphModel(): GraphModel {
        if (this._index === undefined) {
            throw new Error('Index is not set!');
        }
        return this._index.getRoot();
    }

    /**
     * Checks if a relation (IncomingEdge, OutgoingEdge, Containment) between this element and a target can be resolved,
     * in terms of its constraint. For this, the function checks if the upperbound of the constraint would be violated.
     * @param targetType elementType that will be checked
     * @param elements all current related elements
     * @param constraints all constraints associated with the target element
     * @returns All constraints that are violated. If there is no violated constraint, the target element can be related with this element.
     */
    checkViolations(targetType: string, elements: ModelElement[], constraints: Constraint[]): Constraint[] {
        // all constraints that include the target type
        const capturedConstraints = constraints.filter(
            (c: Constraint) =>
                // elements type is captured by the constraint
                (c.elements ?? []).indexOf(targetType) >= 0
        );
        // if element is not contained in any constrained, it violates all constraints
        if (capturedConstraints.length <= 0) {
            return constraints;
        }
        // out of all constraints that include the targets type...
        const violatedConstraint = capturedConstraints.filter(c => {
            // ...check if those constraints are already met,...
            // ...i.e. the upperbound is met by the sum of all related and captured types
            let upperBound: number = c.upperBound;
            // if upperBound is initially smaller than 0, it is interpreted as wildcard '*'
            if (upperBound < 0) {
                return false;
            }
            for (const type of c.elements ?? []) {
                const connectedNumberOfCapturedType: number = elements.filter(e => e.type === type).length;
                upperBound = upperBound - connectedNumberOfCapturedType;
                if (upperBound <= 0) {
                    // if it is met, the constraint would be violated
                    return true;
                }
            }
            // if the upperBound is not met, the constraint won't be violated
            return false;
        });
        // the function returns all violated constraints
        return violatedConstraint;
    }

    get view(): View | undefined {
        const _view = this._view ?? getSpecOf(this.type)?.view;
        return _view;
    }

    set view(view: View | undefined) {
        this._view = view;
    }

    get cssClasses(): string[] {
        return this.view?.cssClass ?? [];
    }

    set cssClasses(cssClasses: string[]) {
        if (!this.view) {
            throw new Error('ModelElement [' + this.id + ', ' + this.type + "] has no view. Couldn't set cssClasses!");
        }
        this.view.cssClass = cssClasses;
    }

    get style(): Style | undefined {
        let style: string | Style | undefined = this.view?.style ?? getSpecOf(this.type)?.view?.style;
        if (typeof style === 'string') {
            style = getStyleByNameOf(style);
        }
        return style;
    }

    set style(style: Style | string | undefined) {
        const oldStyle = this.style;
        if (oldStyle === style) {
            return;
        }
        if (!this._view) {
            this._view = {
                style: {}
            } as View;
        }
        this._view.style = style;
    }

    get shape(): Shape | undefined {
        const style = this.style;
        if (!this.style) {
            console.log('ModelElement [' + this.id + ', ' + this.type + "] has no style. Couldn't get shape!");
            return undefined;
        }
        let shape;
        if (NodeStyle.is(style)) {
            shape = style.shape;
        } else if (EdgeStyle.is(style)) {
            shape = undefined;
        } else if (GraphModelStyle.is(style)) {
            shape = undefined;
        }
        return shape;
    }

    set shape(shape: Shape | undefined) {
        if (!this.style) {
            throw new Error('ModelElement [' + this.id + ', ' + this.type + "] has no style. Couldn't set shape!");
        }
        if (NodeStyle.is(this.style) && this.style.shape !== shape) {
            const style = { ...this.style };
            style.shape = { ...shape } as Shape;
            this.style = style;
        }
        if (EdgeStyle.is(this.style)) {
            return; // no shape
        }
    }

    get appearance(): Appearance | undefined {
        const shape = this.shape;
        if (!shape) {
            console.log('ModelElement [' + this.id + ', ' + this.type + "] has no shape. Couldn't get appearance!");
            return undefined;
        }
        if (ContainerShape.is(shape) || Text.is(shape) || MultiText.is(shape) || Polyline.is(shape)) {
            const appearance = shape.appearance;
            if (typeof appearance === 'string') {
                return getAppearanceByNameOf(appearance);
            }
            return appearance;
        }
        return undefined;
    }

    set appearance(appearance: Appearance | string | undefined) {
        if (this instanceof Node) {
            const currentShape = { ...this.shape };
            if (!currentShape) {
                throw new Error('ModelElement [' + this.id + ', ' + this.type + "] has no shape. Couldn't set appearance!");
            }
            if (ContainerShape.is(currentShape) || Text.is(currentShape) || MultiText.is(currentShape) || Polyline.is(currentShape)) {
                const currentAppearance = currentShape.appearance;
                if (currentAppearance !== appearance) {
                    currentShape.appearance = appearance;
                    this.shape = currentShape;
                }
            }
        } else {
            if (this instanceof Edge) {
                this.appearance = appearance;
            }
        }
    }

    get propertyDefinitions(): Attribute[] {
        return getAttributesOf(this.type);
    }

    get properties(): Record<string, any> {
        // fix all properties, that are not present
        const definitions = this.propertyDefinitions;
        for (const definition of definitions) {
            if (this._attributes[definition.name] === undefined) {
                if (isList(definition)) {
                    this._attributes[definition.name] = [];
                } else {
                    this._attributes[definition.name] = getDefaultValue(this.type, definition.name);
                }
            }
        }
        return this._attributes;
    }

    set properties(properties: Record<string, any>) {
        const definitions = this.propertyDefinitions;
        for (const definition of definitions) {
            if (isList(definition)) {
                this.setProperty(definition.name, properties[definition.name] ?? definition.defaultValue ?? []);
            } else {
                this.setProperty(
                    definition.name,
                    properties[definition.name] ?? definition.defaultValue ?? getFallbackDefaultValue(definition.type)
                );
            }
        }
    }

    initializeProperties(): void {
        const definitions = this.propertyDefinitions;
        this._attributes = {};
        for (const definition of definitions) {
            if (!isList(definition)) {
                this._attributes[definition.name] = getDefaultValue(this.type, definition.name);
            } else {
                this._attributes[definition.name] = [];
            }
        }
    }

    getPropertyDefinition(name: string): Attribute | undefined {
        return this.propertyDefinitions.filter(p => p.name === name)[0] ?? undefined;
    }

    getProperty(name: string): any {
        return this._attributes[name];
    }

    setProperty(name: string, value: any): void {
        const propertyDefinition = this.getPropertyDefinition(name);
        const final = propertyDefinition?.final ?? false;
        if (final) {
            throw new Error('Final property can not be changed!');
        } else if (propertyDefinition) {
            const propertyType = propertyDefinition.type;
            // primitive check
            if (
                isPrimitivePropertyType(propertyType) &&
                (isList(propertyDefinition) || !(typeof value == 'object' || typeof value == 'function'))
            ) {
                this._attributes[name] = value;
            } else {
                // check if new complex value has same complex type (UserDefinedType, ModelElementReferences and Enums)
                const userDefinedType = getUserDefinedType(propertyType);
                const enumerator = getEnum(propertyType);
                const modelElementReference = getSpecOf(propertyType);
                // enums can be checked against literal-domain | userDefinedTypes can be checked against signature TODO:
                if (userDefinedType || enumerator || modelElementReference) {
                    if (enumerator && !(enumerator.literals.indexOf(value) >= 0)) {
                        throw new Error('Type-Error: Value is not inside the domain of the enumerator!');
                    }
                    this._attributes[name] = value;
                } else {
                    throw new Error('Type-Error: Type of value does not match type of property!');
                }
            }
        }
    }
}

export namespace ModelElement {
    export function is(object: any): object is ModelElement {
        return AnyObject.is(object) && hasStringProp(object, 'type') && IdentifiableElement.is(object);
    }
}

export class Node extends ModelElement {
    _position: Point;
    _size?: Size;

    get parent(): ModelElementContainer | undefined {
        return this.index!.findContainment(this);
    }

    get successors(): Node[] {
        const edges = this.outgoingEdges;
        return edges.map(e => e.target);
    }

    get predecessors(): Node[] {
        const edges = this.incomingEdges;
        return edges.map(e => e.source);
    }

    get outgoingEdges(): Edge[] {
        const gNode = this.index!.findGElement(this.id) as GNode | undefined;
        if (gNode !== undefined) {
            const gEdges: GEdge[] | undefined = this.index!.getOutgoingEdges(gNode);
            if (gEdges) {
                return gEdges.map(e => this.index!.findElement(e.id)).filter(e => Edge.is(e)) as Edge[];
            }
        }
        return [];
    }

    get incomingEdges(): Edge[] {
        const gNode = this.index!.findGElement(this.id) as GNode | undefined;
        if (gNode !== undefined) {
            const gEdges: GEdge[] | undefined = this.index!.getIncomingEdges(gNode);
            if (gEdges) {
                return gEdges.map(e => this.index!.findElement(e.id)).filter(e => Edge.is(e)) as Edge[];
            }
        }
        return [];
    }

    canBeEdgeTarget(edgeType: string): boolean {
        const spec = getSpecOf(this.type) as NodeType;
        if (!spec.incomingEdges) {
            return false;
        }
        const constraints: Constraint[] = spec.incomingEdges;
        const elements = this.incomingEdges;
        return this.checkViolations(edgeType, elements, constraints).length <= 0;
    }

    canBeEdgeSource(edgeType: string): boolean {
        const spec = getSpecOf(this.type) as NodeType;
        if (!spec.outgoingEdges) {
            return false;
        }
        const constraints: Constraint[] = spec.outgoingEdges;
        const elements = this.outgoingEdges;
        return this.checkViolations(edgeType, elements, constraints).length <= 0;
    }

    canBeContainmentOf(c: Container): boolean {
        return c.canContain(this.type);
    }

    get size(): Size {
        if (this._size) {
            return this._size;
        }
        const shape = this.shape;
        if (Rectangle.is(shape)) {
            return shape.size ?? { width: 10, height: 10 };
        } else if (RoundedRectangle.is(shape)) {
            return shape.size ?? { width: 10, height: 10 };
        } else if (Ellipse.is(shape)) {
            return shape.size ?? { width: 10, height: 10 };
        } else if (Polygon.is(shape)) {
            return shape.size ?? { width: 10, height: 10 };
        } else if (Polyline.is(shape)) {
            return shape.size ?? { width: 10, height: 10 };
        } else if (Image.is(shape)) {
            return shape.size ?? { width: 10, height: 10 };
        }
        return { width: 10, height: 10 };
    }

    set size(size: Size) {
        this._size = size;
    }

    get position(): Point {
        if (this._position) {
            return this._position;
        }
        const pos = this.getPosition();
        return { x: pos.xPos, y: pos.yPos };
    }

    set position(pos: Point) {
        this._position = pos;
    }

    private getPosition(): AbsolutePosition {
        const shape = this.shape;
        if (Rectangle.is(shape)) {
            return (shape.position as AbsolutePosition) ?? { xPos: 0, yPos: 0 };
        } else if (RoundedRectangle.is(shape)) {
            return (shape.position as AbsolutePosition) ?? { xPos: 0, yPos: 0 };
        } else if (Ellipse.is(shape)) {
            return (shape.position as AbsolutePosition) ?? { xPos: 0, yPos: 0 };
        } else if (Polygon.is(shape)) {
            return (shape.position as AbsolutePosition) ?? { xPos: 0, yPos: 0 };
        } else if (Polyline.is(shape)) {
            const points = shape.points;
            return points.length > 0 ? { xPos: points[0].x, yPos: points[0].y } : { xPos: 0, yPos: 0 };
        } else if (Image.is(shape)) {
            return (shape.position as AbsolutePosition) ?? { xPos: 0, yPos: 0 };
        } else if (Text.is(shape)) {
            return (shape.position as AbsolutePosition) ?? { xPos: 0, yPos: 0 };
        } else if (MultiText.is(shape)) {
            return (shape.position as AbsolutePosition) ?? { xPos: 0, yPos: 0 };
        }
        return { xPos: 0, yPos: 0 };
    }
}

export namespace Node {
    export function is(object: any): object is Node {
        return AnyObject.is(object) && hasObjectProp(object, '_position') && hasObjectProp(object, '_size') && ModelElement.is(object);
    }
}

export class Container extends Node implements ModelElementContainer {
    _containments: Node[];

    get containments(): Node[] {
        if (!this._containments) {
            this._containments = [];
        }
        return this._containments!;
    }
    set containments(elements: Node[]) {
        this._containments = elements;
    }

    canContain(type: string): boolean {
        const spec = getSpecOf(this.type) as NodeType;
        if (!spec.containments) {
            return false;
        }
        const constraints: Constraint[] = spec.containments;
        const elements = this.containments;
        return this.checkViolations(type, elements, constraints).length <= 0;
    }
}

export namespace Container {
    export function is(object: any): object is Container {
        return AnyObject.is(object) && Node.is(object) && ModelElementContainer.is(object);
    }
}

export class Edge extends ModelElement {
    sourceID: string;
    targetID: string;
    _routingPoints: RoutingPoint[];

    get source(): Node {
        const id = this.sourceID;
        const node = this.index!.findNode(id);
        if (!node) {
            throw new Error("Edge with id '" + this.id + "' has an undefined source!");
        }
        return node;
    }

    get target(): Node {
        const id = this.targetID;
        const node = this.index!.findNode(id);
        if (!node) {
            throw new Error("Edge with id '" + this.id + "' has an undefined target!");
        }
        return node;
    }

    canConnectToTarget(node: Node): boolean {
        return node.canBeEdgeTarget(this.type);
    }

    canConnectToSource(node: Node): boolean {
        return node.canBeEdgeSource(this.type);
    }

    get routingPoints(): RoutingPoint[] {
        if (!this._routingPoints) {
            this._routingPoints = [];
        }
        return this._routingPoints;
    }

    set routingPoints(routingPoints: RoutingPoint[]) {
        this._routingPoints = routingPoints;
    }

    /**
     * for legacy, please use @routingPoints
     * @deprecated
     */
    get bendPoints(): RoutingPoint[] {
        if (!this._routingPoints) {
            this._routingPoints = [];
        }
        return this._routingPoints;
    }

    /**
     * for legacy, please use @routingPoints
     * @deprecated
     */
    set bendPoints(bendPoints: RoutingPoint[]) {
        this._routingPoints = bendPoints;
    }

    override getSpec(): EdgeType {
        return super.getSpec() as EdgeType;
    }
}

export namespace Edge {
    export function is(object: any): object is Edge {
        return AnyObject.is(object) && hasStringProp(object, 'sourceID') && hasStringProp(object, 'targetID') && ModelElement.is(object);
    }
}

export class GraphModel extends ModelElement implements ModelElementContainer {
    _sourceUri?: string;
    _containments: Node[] = [];
    _edges: Edge[] = [];

    override get index(): GraphModelIndex {
        if (!this._index) {
            throw new Error('Index is not set!');
        }
        return this._index;
    }
    override set index(index: GraphModelIndex | undefined) {
        this._index = index;
    }
    get containments(): Node[] {
        if (!this._containments) {
            this._containments = [];
        }
        return this._containments!;
    }
    set containments(elements: Node[]) {
        this._containments = elements;
    }
    get edges(): Edge[] {
        if (!this._edges!) {
            this._edges = [];
        }
        return this._edges!;
    }
    set edges(elements: Edge[]) {
        this._edges = elements;
    }

    canContain(type: string): boolean {
        const spec = getSpecOf(this.type) as NodeType;
        if (!spec.containments) {
            return false;
        }
        const constraints: Constraint[] = spec.containments;
        const elements = this.containments;
        return this.checkViolations(type, elements, constraints).length <= 0;
    }

    couldContain(type: string): boolean {
        return canBeCreated(this.type, type);
    }

    override getSpec(): GraphType {
        return super.getSpec() as GraphType;
    }
}

export namespace GraphModel {
    export function is(object: any): object is GraphModel {
        return AnyObject.is(object) || ModelElement.is(object);
    }
}
