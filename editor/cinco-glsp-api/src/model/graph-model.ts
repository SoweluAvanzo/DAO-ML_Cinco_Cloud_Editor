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
import * as crypto from 'crypto';
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
    View,
    WebView,
    isInstanceOf,
    UserDefinedType,
    Cell,
    cellValues,
    isConflictFree,
    mapCell,
    Deletable,
    deletableValue
} from '@cinco-glsp/cinco-glsp-common';
import { AnyObject, hasArrayProp, hasStringProp, Point } from '@eclipse-glsp/server';
import { GraphModelIndex } from './graph-model-index';
import { GraphModelStorage } from './graph-storage';
import { getModelFilesSync, getWorkspaceRootUri } from '../utils/file-helper';
import * as path from 'path';
import { ContextBundle } from '../api/types/context-bundle';

export interface IdentifiableElement {
    id: string;
}

export namespace IdentifiableElement {
    export function is(object: any): object is IdentifiableElement {
        return AnyObject.is(object) && hasStringProp(object, 'id');
    }
}

export interface ModelElementContainer extends ModelElement {
    get containments(): Deletable<Node>[];
    set containments(containments: Deletable<Node>[]);
    get containedElements(): Node[];
}

export interface PrimeReference {
    instanceId: string;
    instanceType: string;
    modelId: string;
    modelType: string;
    filePath: string;
}

export namespace PrimeReference {
    export function is(object: any): object is PrimeReference {
        return (
            AnyObject.is(object) &&
            hasStringProp(object, 'instanceId') &&
            hasStringProp(object, 'instanceType') &&
            hasStringProp(object, 'modelId') &&
            hasStringProp(object, 'modelType') &&
            hasStringProp(object, 'filePath')
        );
    }
}

export namespace ModelElementContainer {
    export function is(object: any): object is ModelElementContainer {
        return AnyObject.is(object) && (hasArrayProp(object, '_containments') || 'containments' in object);
    }

    export function getContainedElements(host: ModelElementContainer): Node[] {
        return host.containments.map(deletableValue);
    }

    // TODO: UserdefinedType currently not included
    export function getAllContainedElements(host: ModelElementContainer): Node[] {
        let allElements: Node[] = [];
        allElements = allElements.concat(host.containedElements);
        for (const e of allElements) {
            if (ModelElementContainer.is(e)) {
                allElements = allElements.concat(ModelElementContainer.getAllContainedElements(e));
            }
        }
        return allElements;
    }
}

export class ModelElement implements IdentifiableElement {
    protected _index?: GraphModelIndex;
    id: string = crypto.randomUUID();
    type: string;
    _position: Point;
    _size?: Size;
    protected _attributes: Record<string, any>;
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

    get size(): Size {
        if (this._size) {
            return this._size;
        }
        return { width: 0, height: 0 };
    }

    set size(size: Size) {
        this._size = size;
    }

    get position(): Point {
        if (this._position) {
            return this._position;
        }
        return { x: 0, y: 0 };
    }

    set position(pos: Point) {
        this._position = pos;
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

    get valid(): Promise<boolean> {
        return this.index.validate(this);
    }

    get view(): View {
        const _view = this._view ?? { ...getSpecOf(this.type)?.view };
        return _view ?? ({} as View);
    }

    set view(view: View) {
        this._view = view;
    }

    get cssClasses(): string[] {
        return this.view.cssClass ?? ([] as string[]).concat(getSpecOf(this.type)?.view?.cssClass ?? []);
    }

    set cssClasses(cssClasses: string[]) {
        this.view.cssClass = cssClasses;
    }

    get style(): Style | undefined {
        const metaSpecStyle = getSpecOf(this.type)?.view?.style;
        let style: string | Style | undefined =
            this.view.style ?? (typeof metaSpecStyle == 'string' ? metaSpecStyle : { ...(metaSpecStyle ?? ({} as Style)) });
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
            this._view = this.view;
        }
        this.view.style = style;
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
        if (NodeStyle.is(this.style)) {
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
            const currentShape = this.shape;
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

    initializeProperties(): void {
        this._attributes = {};
        // fix all properties, that are not present
        const definitions = this.propertyDefinitions;
        for (const definition of definitions) {
            if (this._attributes[definition.name] === undefined) {
                if (isList(definition)) {
                    this._attributes[definition.name] = [];
                } else {
                    this._attributes[definition.name] = getDefaultValue(this.type, definition.name, definition.annotations ?? []);
                }
            }
        }
    }

    get propertyDefinitions(): Attribute[] {
        return getAttributesOf(this.type);
    }

    get properties(): Record<string, any> {
        if (this._attributes) {
            return this._attributes;
        }
        this.initializeProperties();
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
                    properties[definition.name] ??
                        definition.defaultValue ??
                        getFallbackDefaultValue(definition.type, definition.annotations ?? [])
                );
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
                const userDefinedType = getUserDefinedType(propertyType); // TODO Abstract types are not found in meta specification
                const enumerator = getEnum(propertyType);
                const modelElementReference = getSpecOf(propertyType);
                // enums can be checked against literal-domain | userDefinedTypes can be checked against signature TODO:
                if (userDefinedType || enumerator || modelElementReference) {
                    if (enumerator && !(enumerator.literals.indexOf(value) >= 0)) {
                        throw new Error('Type-Error: Value is not inside the domain of the enumerator: ' + value);
                    }
                    this._attributes[name] = value;
                } else {
                    throw new Error('Type-Error: Type of value does not match type of property!');
                }
            }
        }
    }

    instanceOf(superType: string | ModelElement | ElementType): boolean {
        try {
            if (typeof superType == 'string') {
                return (
                    (superType === 'graphmodel' && GraphModel.is(this)) ||
                    (superType === 'container' && ModelElementContainer.is(this) && Node.is(this)) ||
                    (superType === 'node' && Node.is(this)) ||
                    (superType === 'edge' && Edge.is(this)) ||
                    (superType === 'userdefinedtype' && UserDefinedType.is(this)) ||
                    (superType === 'modelelementcontainer' && ModelElementContainer.is(this)) ||
                    (superType === 'modelelement' && ModelElement.is(this)) ||
                    isInstanceOf(this.getSpec().elementTypeId, superType)
                );
            } else if (ElementType.is(superType)) {
                return isInstanceOf(this.getSpec().elementTypeId, superType.elementTypeId);
            } else if (ModelElement.is(superType)) {
                return isInstanceOf(this.getSpec().elementTypeId, superType.type);
            }
        } catch (e) {
            console.log(e);
        }
        return false;
    }

    toJSON(): any {
        const serialization = { ...this };
        delete serialization._index;
        return serialization;
    }

    isConflictFree(): boolean {
        return isConflictFree(this.toJSON());
    }
}

export namespace ModelElement {
    export function is(object: any): object is ModelElement {
        return AnyObject.is(object) && hasStringProp(object, 'type') && IdentifiableElement.is(object);
    }
}

export class Node extends ModelElement {
    _primeReference?: PrimeReference;

    get parent(): ModelElementContainer | undefined {
        return this.index!.findContainerOf(this.id);
    }

    get successors(): Node[] {
        const edges = this.outgoingEdges;
        return [...new Set(edges.flatMap(edge => edge.targets))];
    }

    get predecessors(): Node[] {
        const edges = this.incomingEdges;
        return [...new Set(edges.flatMap(edge => edge.sources))];
    }

    get outgoingEdges(): Edge[] {
        const node = this.index!.findNode(this.id);
        if (node !== undefined) {
            return this.index!.getOutgoingEdgeElements(node);
        }
        return [];
    }

    get incomingEdges(): Edge[] {
        const node = this.index!.findNode(this.id);
        if (node !== undefined) {
            return this.index!.getIncomingEdgeElements(node);
        }
        return [];
    }

    canBeEdgeTarget(edgeType: string, filter?: (e: Edge) => boolean): boolean {
        const spec = getSpecOf(this.type) as NodeType;
        if (!spec.incomingEdges) {
            return false;
        }
        const constraints: Constraint[] = spec.incomingEdges;
        if (constraints.length <= 0) {
            // cannot contain elements, if no relating constraints are defined
            return false;
        }
        let elements = this.incomingEdges;
        if (filter) {
            elements = elements.filter(e => filter(e));
        }
        return this.checkViolations(edgeType, elements, constraints).length <= 0;
    }

    canBeEdgeSource(edgeType: string, filter?: (e: Edge) => boolean): boolean {
        const spec = getSpecOf(this.type) as NodeType;
        if (!spec.outgoingEdges) {
            return false;
        }
        const constraints: Constraint[] = spec.outgoingEdges;
        if (constraints.length <= 0) {
            // cannot contain elements, if no relating constraints are defined
            return false;
        }
        let elements = this.outgoingEdges;
        if (filter) {
            elements = elements.filter(e => filter(e));
        }
        return this.checkViolations(edgeType, elements, constraints).length <= 0;
    }

    canBeContainmentOf(c: Container): boolean {
        return c.canContain(this.type);
    }

    override initializeProperties(primeReference?: PrimeReference): void {
        super.initializeProperties();
        this._primeReference = primeReference;
    }

    get isPrime(): boolean {
        return this._primeReference !== undefined;
    }

    get primeReferenceInfo(): PrimeReference | undefined {
        return this._primeReference;
    }

    get primeReference(): ModelElement | undefined {
        if (!this.isPrime) {
            return undefined;
        }
        const filePath = this.primeReferenceInfo!.filePath;
        const workspace = path.join(getWorkspaceRootUri(), filePath);
        let model = GraphModelStorage.readModelFromFileSync(workspace, {} as ContextBundle);
        const primeReference = this.primeReferenceInfo!;
        if (!model || model.id !== primeReference.modelId) {
            // if model is not readable as it is gone or corrupted,
            // or the id is not correct => find correct model
            console.log('Model was not found. Path or Id does not match. Searching in workspace...');
            const modelFiles = getModelFilesSync();
            for (const modelPath of modelFiles) {
                const absPath = path.join(getWorkspaceRootUri(), modelPath);
                const potentialModel = GraphModelStorage.readModelFromFileSync(absPath, {} as ContextBundle);
                if (potentialModel && potentialModel.id === primeReference.modelId) {
                    // model found => update modelPath
                    this.primeReferenceInfo!.filePath = modelPath;
                    model = potentialModel;
                    console.log('Model was found. Updated path!');
                    break;
                }
            }
        }
        if (model) {
            if (model.id === primeReference.instanceId) {
                // model is referenced instance
                return model;
            } else {
                // find element in model
                const potentialRefs = model
                    .getAllContainedElements()
                    .filter(i => ModelElement.is(i))
                    .filter(e => e.id === primeReference.instanceId) as ModelElement[];
                return potentialRefs.length > 0 ? potentialRefs[0] : undefined;
            }
        }
        console.log('Model of referenced element not found. Make sure it is in the workspace.');
        return undefined;
    }

    override get size(): Size {
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
        } else if (WebView.is(shape)) {
            return shape.size ?? { width: 10, height: 10 };
        }
        return { width: 10, height: 10 };
    }

    override set size(size: Size) {
        this._size = size;
    }

    override get position(): Point {
        if (this._position) {
            return this._position;
        }
        const pos = this.getPosition();
        return { x: pos.xPos, y: pos.yPos };
    }

    override set position(pos: Point) {
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
        return (
            AnyObject.is(object) &&
            ((hasStringProp(object, 'type') && NodeType.is(getSpecOf((object as any)['type']))) ||
                (!Edge.is(object) && !GraphModel.is(object) && ModelElement.is(object)))
        );
    }
}

export class Container extends Node implements ModelElementContainer {
    _containments: Node[] = [];

    get containments(): Node[] {
        if (!this._containments) {
            this._containments = [];
        }
        return this._containments!;
    }
    set containments(elements: Node[]) {
        this._containments = elements;
    }
    get containedElements(): Node[] {
        return ModelElementContainer.getContainedElements(this);
    }

    canContain(type: string): boolean {
        const spec = getSpecOf(this.type) as NodeType;
        if (!spec.containments) {
            return false;
        }
        const constraints: Constraint[] = spec.containments;
        const elements = this.containedElements;
        return this.checkViolations(type, elements, constraints).length <= 0;
    }

    getAllContainedElements(): IdentifiableElement[] {
        return ModelElementContainer.getAllContainedElements(this);
    }

    override get valid(): Promise<boolean> {
        const elements = [this as ModelElement].concat(this.containments);
        return Promise.all(
            elements.map(async element => {
                const result = await this.index.validate(element);
                if (!result) {
                    return false;
                }
                return true;
            })
        ).then(results => results.indexOf(false) < 0);
    }
}

export namespace Container {
    export function is(object: any): object is Container {
        return AnyObject.is(object) && Node.is(object) && ModelElementContainer.is(object);
    }
}

export class Edge extends ModelElement {
    sourceID: Cell<string>;
    targetID: Cell<string>;
    _routingPoints: RoutingPoint[];

    initialize({ type, sourceID, targetID }: { type: string; sourceID: string; targetID: string }): void {
        this.type = type;
        this.sourceID = sourceID;
        this.targetID = targetID;
        this.initializeProperties();
    }

    get sourceIDs(): ReadonlyArray<string> {
        return cellValues(this.sourceID);
    }

    get source(): Cell<Node> {
        return mapCell(this.sourceID, sourceID => {
            const node = this.index!.findNode(sourceID);
            if (!node) {
                throw new Error(`Edge with id ${this.id} has an undefined sourceID ${sourceID}.`);
            }
            return node;
        });
    }

    get sources(): ReadonlyArray<Node> {
        return cellValues(this.source);
    }

    get targetIDs(): ReadonlyArray<string> {
        return cellValues(this.targetID);
    }

    get target(): Cell<Node> {
        return mapCell(this.targetID, targetID => {
            const node = this.index!.findNode(targetID);
            if (!node) {
                throw new Error(`Edge with id ${this.id} has an undefined targetID ${targetID}.`);
            }
            return node;
        });
    }

    get targets(): ReadonlyArray<Node> {
        return cellValues(this.target);
    }

    canConnectToTarget(node: Node, filter?: (e: Edge) => boolean): boolean {
        return node.canBeEdgeTarget(this.type, filter);
    }

    canConnectToSource(node: Node, filter?: (e: Edge) => boolean): boolean {
        return node.canBeEdgeSource(this.type, filter);
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
        return (
            AnyObject.is(object) &&
            ((hasStringProp(object, 'type') && EdgeType.is(getSpecOf((object as any)['type']))) ||
                (('sourceID' in object || 'source' in object) && ('targetID' in object || 'target' in object) && ModelElement.is(object)))
        );
    }
}

export class GraphModel extends ModelElement implements ModelElementContainer {
    _sourceUri?: string;
    _containments: Deletable<Node>[] = [];
    _edges: Deletable<Edge>[] = [];

    override get index(): GraphModelIndex {
        if (!this._index) {
            throw new Error('Index is not set!');
        }
        return this._index;
    }

    override set index(index: GraphModelIndex | undefined) {
        this._index = index;
    }
    get containments(): Deletable<Node>[] {
        if (!this._containments) {
            this._containments = [];
        }
        return this._containments!;
    }
    get containedElements(): Node[] {
        return ModelElementContainer.getContainedElements(this);
    }
    set containments(elements: Deletable<Node>[]) {
        this._containments = elements;
    }
    get edges(): Deletable<Edge>[] {
        if (!this._edges!) {
            this._edges = [];
        }
        return this._edges!;
    }
    set edges(elements: Deletable<Edge>[]) {
        this._edges = elements;
    }
    get edgeElements(): Edge[] {
        return this.edges.map(deletableValue);
    }

    canContain(type: string): boolean {
        const spec = getSpecOf(this.type) as NodeType;
        if (!spec.containments) {
            return false;
        }
        const constraints: Constraint[] = spec.containments;
        return this.checkViolations(type, this.containedElements, constraints).length <= 0;
    }

    couldContain(type: string): boolean {
        return canBeCreated(this.type, type);
    }

    override getSpec(): GraphType {
        return super.getSpec() as GraphType;
    }

    getAllContainedElements(): ModelElement[] {
        return (ModelElementContainer.getAllContainedElements(this) as ModelElement[])
            .concat(this.edgeElements)
            .map(e => (ModelElement.is(e) && !(e instanceof ModelElement) ? Object.assign(new ModelElement(), e) : e)) as ModelElement[];
    }

    override get valid(): Promise<boolean> {
        const elements = [this as ModelElement].concat(this.getAllContainedElements()).concat();
        return Promise.all(
            elements.map(async element => {
                const result = await this.index.validate(element);
                if (!result) {
                    return false;
                }
                return true;
            })
        ).then(results => results.indexOf(false) < 0);
    }

    override toJSON(): any {
        const serialization = { ...super.toJSON() };
        delete serialization._sourceUri;
        return serialization;
    }
}

export namespace GraphModel {
    export function is(object: any): object is GraphModel {
        return (
            ModelElement.is(object) &&
            ((hasStringProp(object, 'type') && GraphType.is(getSpecOf((object as any)['type']))) ||
                (ModelElementContainer.is(object) && hasArrayProp(object, '_edges')))
        );
    }
}
