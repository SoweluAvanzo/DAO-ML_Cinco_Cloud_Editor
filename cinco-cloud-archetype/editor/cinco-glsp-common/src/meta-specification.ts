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

import { HookType } from './protocol/hooks/hook-type';
import { hasArrayProp, hasBooleanProp, hasNumberProp, hasObjectProp, hasStringProp } from './protocol/type-utils';

/**
 * Data model
 */

export namespace MetaSpecification {
    let META_SPECIFICATION: CompositionSpecification = {};

    export function get(): CompositionSpecification {
        return META_SPECIFICATION;
    }

    export function merge(metaSpecification: CompositionSpecification): void {
        addGraphTypes(metaSpecification.graphTypes ?? []);
        addNodeTypes(metaSpecification.nodeTypes ?? []);
        addEdgeTypes(metaSpecification.edgeTypes ?? []);
        addCustomTypes(metaSpecification.customTypes ?? []);
        addAppearances(metaSpecification.appearances ?? []);
        addStyles(metaSpecification.styles ?? []);
    }

    export function addTypes(types: any[], typeAccessor: string, idAccessor: string): void {
        const newTypes = (types ?? []).filter(
            // take those who do not already exist
            (e1: any) =>
                ((META_SPECIFICATION as any)[typeAccessor] ?? []).filter((e2: any) => e1[idAccessor] === e2[idAccessor]).length <= 0
        );
        for (const newType of newTypes) {
            console.debug('Found new Type of [' + typeAccessor + ']: ' + newType[idAccessor]);
        }
        (META_SPECIFICATION as any)[typeAccessor] = ((META_SPECIFICATION as any)[typeAccessor] ?? []).concat(newTypes);
    }

    export function addNodeTypes(types: NodeType[]): void {
        addTypes(types, 'nodeTypes', 'elementTypeId');
    }

    export function addGraphTypes(types: GraphType[]): void {
        addTypes(types, 'graphTypes', 'elementTypeId');
    }

    export function addEdgeTypes(types: EdgeType[]): void {
        addTypes(types, 'edgeTypes', 'elementTypeId');
    }

    export function addCustomTypes(types: CustomType[]): void {
        addTypes(types, 'customTypes', 'elementTypeId');
    }

    export function addAppearances(types: Appearance[]): void {
        addTypes(types, 'appearances', 'name');
    }

    export function addStyles(types: Style[]): void {
        addTypes(types, 'styles', 'name');
    }

    /**
     * Clears the meta-specification. Can be useful for a reload functionality
     */
    export function clear(): void {
        META_SPECIFICATION = {};
    }
}

/**
 * Style
 */

export interface Font {
    fontName: string;
    size?: number;
    isBold?: boolean;
    isItalic?: boolean;
}

export interface Color {
    r: number;
    g: number;
    b: number;
}

export namespace ConnectionType {
    export const FreeForm = 'FreeForm';
    export const Manhattan = 'Manhattan'; // need a router
    export const Bezier = 'Bezier'; // TODO: add other routing-styles
    export const Curved = 'Curved'; // TODO: add other routing-styles
}

export namespace LineStyle {
    export const SOLID = 'SOLID';
    export const DASH = 'DASH';
    export const DASHDOT = 'DASHDOT';
    export const DASHDOTDOT = 'DASHDOTDOT';
    export const DOT = 'DOT';
}

export namespace VAlignment {
    export const TOP = 'TOP';
    export const MIDDLE = 'MIDDLE';
    export const BOTTOM = 'BOTTOM';
}

export namespace HAlignment {
    export const LEFT = 'LEFT';
    export const RIGHT = 'RIGHT';
    export const CENTER = 'CENTER';
}

export namespace DecoratorShape {
    export const ARROW = 'ARROW';
    export const DIAMOND = 'DIAMOND';
    export const CIRCLE = 'CIRCLE';
    export const TRIANGLE = 'TRIANGLE';
}

export interface PredefinedDecorator {
    shape: typeof DecoratorShape & string;
    appearance: string | Appearance;
}

export interface ConnectionDecorator {
    name?: number;
    location?: number; // 1.0 := End of the edge | 0.0 := Beginning of the edge
    moveable?: boolean; // TODO: not implemented
    decoratorShape?: GraphicsAlgorithm;
    predefinedDecorator?: PredefinedDecorator;
}

export interface Size {
    widthFixed?: boolean;
    width: number;
    heightFixed?: boolean;
    height: number;
}

export interface Point {
    x: number;
    y: number;
}

export interface Alignment extends AbstractPosition {
    horizontal: typeof HAlignment & string;
    xMargin?: number;
    vertical: typeof VAlignment & string;
    yMargin?: number;
}

export namespace Alignment {
    export function is(object: any): object is Alignment {
        return object !== undefined && hasStringProp(object, 'horizontal') && hasStringProp(object, 'vertical');
    }
}
export interface AbsolutePosition extends AbstractPosition {
    xPos: number;
    yPos: number;
}

export namespace AbsolutePosition {
    export function is(object: any): object is AbsolutePosition {
        return object !== undefined && hasNumberProp(object, 'xPos') && hasNumberProp(object, 'yPos');
    }
}

export interface AbstractPosition { }

export namespace AbstractPosition {
    export function is(object: any): object is AbstractPosition {
        return object !== undefined && AbsolutePosition.is(object) && Alignment.is(object);
    }
}

export interface Polygon extends ContainerShape, GraphicsAlgorithm {
    type: typeof AbstractShape.POLYGON;
    points: Point[];
}

export namespace Polygon {
    export function is(object: any): object is Polygon {
        return object !== undefined && object.type === AbstractShape.POLYGON;
    }
}

export interface Ellipse extends ContainerShape, GraphicsAlgorithm {
    type: typeof AbstractShape.ELLIPSE;
}

export namespace Ellipse {
    export function is(object: any): object is Ellipse {
        return object !== undefined && object.type === AbstractShape.ELLIPSE;
    }
}

export interface RoundedRectangle extends ContainerShape {
    type: typeof AbstractShape.ROUNDEDRECTANGLE;
    size: Size;
    cornerWidth?: number;
    cornerHeight?: number;
}

export namespace RoundedRectangle {
    export function is(object: any): object is RoundedRectangle {
        return object !== undefined && object.type === AbstractShape.ROUNDEDRECTANGLE;
    }
}

export interface Rectangle extends ContainerShape {
    type: typeof AbstractShape.RECTANGLE;
}

export namespace Rectangle {
    export function is(object: any): object is Rectangle {
        return object !== undefined && object.type === AbstractShape.RECTANGLE;
    }
}

export interface Polyline extends Shape, GraphicsAlgorithm {
    type: typeof AbstractShape.POLYLINE;
    appearance?: string | Appearance;
    points: Point[];
    size?: Size;
}

export namespace Polyline {
    export function is(object: any): object is Polyline {
        return object !== undefined && object.type === AbstractShape.POLYLINE;
    }
}

export interface WebView extends Shape, GraphicsAlgorithm {
    type: typeof AbstractShape.WEBVIEW;
    position?: AbstractPosition | AbsolutePosition | Alignment;
    size?: Size;
    content: string; // filePath or content
    scrollable?: boolean;
    padding?: number;
}

export namespace WebView {
    export function is(object: any): object is WebView {
        return object !== undefined && object.type === AbstractShape.WEBVIEW;
    }
}

export interface Image extends Shape, GraphicsAlgorithm {
    type: typeof AbstractShape.IMAGE;
    position?: AbstractPosition | AbsolutePosition | Alignment;
    size: Size;
    path: string;
}

export namespace Image {
    export function is(object: any): object is Image {
        return object !== undefined && object.type === AbstractShape.IMAGE;
    }
}

export interface MultiText extends Shape, GraphicsAlgorithm {
    type: typeof AbstractShape.MULTITEXT;
    appearance?: string | Appearance;
    position?: AbstractPosition | AbsolutePosition | Alignment;
    editable?: boolean; // TODO: not implemented
    scrollable?: boolean; // TODO: currently not working
    value: string;
}

export namespace MultiText {
    export function is(object: any): object is MultiText {
        return object !== undefined && object.type === AbstractShape.MULTITEXT;
    }
}

export interface Text extends Shape, GraphicsAlgorithm {
    type: typeof AbstractShape.TEXT;
    appearance?: string | Appearance;
    position?: AbstractPosition | AbsolutePosition | Alignment;
    editable?: boolean; // TODO: not implemented
    value: string;
}

export namespace Text {
    export function is(object: any): object is Text {
        return object !== undefined && object.type === AbstractShape.TEXT;
    }
}

export interface GraphicsAlgorithm { }

export interface ContainerShape extends AbstractShape {
    appearance?: string | Appearance;
    position?: AbstractPosition | AbsolutePosition | Alignment;
    size?: Size;
    children?: AbstractShape[];
}

export namespace ContainerShape {
    export function is(object: any): object is ContainerShape {
        return (
            object !== undefined &&
            (object.type === AbstractShape.RECTANGLE ||
                object.type === AbstractShape.ROUNDEDRECTANGLE ||
                object.type === AbstractShape.ELLIPSE ||
                object.type === AbstractShape.POLYGON)
        );
    }
}

export interface Shape extends AbstractShape {
    type: string;
}

export namespace Shape {
    export function is(object: any): object is Shape {
        return (
            object !== undefined &&
            (object.type === AbstractShape.TEXT ||
                object.type === AbstractShape.MULTITEXT ||
                object.type === AbstractShape.IMAGE ||
                object.type === AbstractShape.POLYLINE ||
                object.type === AbstractShape.WEBVIEW)
        );
    }
}

export namespace AbstractShape {
    export const TEXT = 'TEXT';
    export const MULTITEXT = 'MULTITEXT';
    export const IMAGE = 'IMAGE';
    export const POLYLINE = 'POLYLINE';
    export const RECTANGLE = 'RECTANGLE';
    export const ROUNDEDRECTANGLE = 'ROUNDEDRECTANGLE';
    export const ELLIPSE = 'ELLIPSE';
    export const POLYGON = 'POLYGON';
    export const WEBVIEW = 'WEBVIEW';

    export function is(object: any): object is AbstractShape {
        return object !== undefined && (Shape.is(object) || ContainerShape.is(object));
    }
}

export interface AbstractShape {
    type: string;
    anchor?: boolean; // default false
    name?: string;
}

export interface Appearance {
    name?: string;
    parent?: string; // this instance extends parent
    foreground?: Color;
    background?: Color;
    filled?: boolean;
    font?: Font;
    lineStyle?: string;
    lineWidth?: number;
    transparency?: number;
    imagePath?: string;
}

export interface NodeStyle extends Style {
    fixed?: boolean;
    shape?: AbstractShape;
}

export namespace NodeStyle {
    export function is(object: any): object is NodeStyle {
        return Style.is(object) && object.styleType === Style.NODE_STYLE;
    }
}

export interface EdgeStyle extends Style {
    appearance?: string | Appearance;
    type: typeof ConnectionType & string; // default: FreeForm
    decorator?: ConnectionDecorator[];
}

export namespace EdgeStyle {
    export function is(object: any): object is EdgeStyle {
        return Style.is(object) && object.styleType === Style.EDGE_STYLE;
    }
}

export interface GraphModelStyle extends Style { }

export namespace GraphModelStyle {
    export function is(object: any): object is GraphModelStyle {
        return Style.is(object) && object.styleType === Style.GRAPHMODEL_STYLE;
    }
}

export interface Style {
    styleType: string;
    name: string;
    parameterCount?: number;
    appearanceProvider?: string;
}

export namespace Style {
    export const NODE_STYLE = 'NodeStyle';
    export const EDGE_STYLE = 'EdgeStyle';
    export const GRAPHMODEL_STYLE = 'GraphModelStyle';

    export function is(object: any): object is Style {
        return object !== undefined && hasStringProp(object, 'name');
    }
}

export interface GraphModelView extends View { }

export namespace GraphModelView {
    export function is(object: any): object is GraphModelView {
        return View.is(object);
    }
}

export interface NodeView extends View {
    layoutOptions?: any; // glsp-specific
    style?: string | NodeStyle;
}

export namespace NodeView {
    export function is(object: any): object is NodeView {
        return (
            View.is(object) &&
            (hasObjectProp(object, 'layoutOptions') ||
                hasStringProp(object, 'style') ||
                (hasObjectProp(object, 'style') && NodeStyle.is((object as NodeView).style)))
        );
    }
}

export interface EdgeView extends View {
    routerKind?: string; // glsp specific & fallback for ConnectionType of EdgeStyle
    style?: string | EdgeStyle;
}

export namespace EdgeView {
    export function is(object: any): object is EdgeView {
        return (
            View.is(object) &&
            (hasStringProp(object, 'routerKind') ||
                hasStringProp(object, 'style') ||
                (hasObjectProp(object, 'style') && EdgeStyle.is((object as EdgeView).style)))
        );
    }
}

export interface View {
    cssClass?: string[];
    style?: string | Style;
    styleParameter?: string[];
}

export namespace View {
    export function is(object: any): object is View {
        return (
            object !== undefined &&
            (true || hasArrayProp(object, 'cssClass') || hasStringProp(object, 'style') || hasObjectProp(object, 'style'))
        );
    }
}

/**
 * Annotations and Properties
 */

export interface Annotation {
    name: string;
    values: string[];
}

export interface Constraint {
    // intervall
    lowerBound: number; // n >= 0 || * := n < 0
    upperBound: number; // n >= 0 || * := n < 0

    // associated elements
    elements?: string[];
}

/**
 * Properties And Types
 */

/**
 * Modeltypes
 */

export interface CompositionSpecification {
    graphTypes?: GraphType[];
    nodeTypes?: NodeType[];
    edgeTypes?: EdgeType[];
    customTypes?: CustomType[];
    styles?: Style[];
    appearances?: Appearance[];
}

export namespace CompositionSpecification {
    export function is(object: any): object is CompositionSpecification {
        return (
            (object !== undefined && hasArrayProp(object, 'graphTypes')) ||
            hasArrayProp(object, 'nodeTypes') ||
            hasArrayProp(object, 'edgeTypes') ||
            hasArrayProp(object, 'customTypes') ||
            hasArrayProp(object, 'styles') ||
            hasArrayProp(object, 'appearances')
        );
    }
}

export interface Type {
    elementTypeId: string;
    label: string;
    annotations?: Annotation[];
    abstract?: boolean;
    parent?: string;
}

export namespace Type {
    export function is(object: any): object is Type {
        return object !== undefined && hasStringProp(object, 'elementTypeId') && hasStringProp(object, 'label');
    }
}

export interface ElementType extends Type {
    icon?: string;
    view?: View;
    superTypes?: string[];
    annotations?: Annotation[];
    attributes?: Attribute[];
}

export namespace ElementType {
    export function is(object: any): object is ElementType {
        return Type.is(object);
    }
}

export interface GraphType extends ModelElementContainer, ElementType {
    view?: GraphModelView;
    diagramExtension: string;
}

export namespace GraphType {
    export function is(object: any): object is GraphType {
        const isSpecified = (MetaSpecification.get().graphTypes?.filter(e => e.elementTypeId === object?.elementTypeId).length ?? -1) > 0;
        return ElementType.is(object) && (isSpecified || ModelElementContainer.is(object));
    }
}

export interface NodeType extends ElementType {
    reparentable: boolean;
    width: number;
    height: number;
    containments?: Constraint[];
    outgoingEdges?: Constraint[];
    incomingEdges?: Constraint[];
    palettes?: string[] | undefined;
    view?: NodeView;
    primeReference?: ReferencedModelElement;
}

export interface ReferencedModelElement {
    annotation?: Annotation;
    name: string;
    type: string;
}

export interface ModelElementContainer {
    containments?: Constraint[];
}

export namespace ModelElementContainer {
    export function is(object: any): object is ModelElementContainer {
        return hasArrayProp(object, 'containments') && (!NodeType.is(object) || (object.containments?.length ?? 0.0) > 0);
    }
}

export namespace NodeType {
    export function is(object: any): object is NodeType {
        const isSpecified = (MetaSpecification.get().nodeTypes?.filter(e => e.elementTypeId === object?.elementTypeId).length ?? -1) > 0;
        return (
            object !== undefined &&
            (isSpecified || (hasBooleanProp(object, 'reparentable') && hasNumberProp(object, 'width') && hasNumberProp(object, 'height')))
        );
    }
}

export interface EdgeType extends ElementType {
    routable: boolean;
    palettes?: string[];
    view?: EdgeView;
    name?: string;
}

export namespace EdgeType {
    export function is(object: any): object is EdgeType {
        const isSpecified = (MetaSpecification.get().edgeTypes?.filter(e => e.elementTypeId === object?.elementTypeId).length ?? -1) > 0;
        return ElementType.is(object) && (isSpecified || hasBooleanProp(object, 'routable'));
    }
}

export interface Attribute {
    name: string;
    type: string;
    bounds?: Constraint;
    final?: boolean;
    unique?: false;
    defaultValue?: string;
    annotations?: Annotation[];
}

export interface CustomType extends Type { }

export namespace CustomType {
    export function is(object: any): object is CustomType {
        return UserDefinedType.is(object) || Enum.is(object);
    }
}

export interface Enum extends CustomType {
    literals: string[];
}

export namespace Enum {
    export function is(object: any): object is Enum {
        return Type.is(object) && hasArrayProp(object, 'literals');
    }
}

export class UserDefinedType implements ElementType, CustomType {
    icon?: string | undefined;
    view?: View | undefined;
    superTypes?: string[];
    annotations?: Annotation[] | undefined;
    elementTypeId: string;
    label: string;
    attributes: Attribute[];
}

export namespace UserDefinedType {
    export function is(object: any): object is UserDefinedType {
        const isSpecified = (MetaSpecification.get().customTypes?.filter(e => e.elementTypeId === object?.elementTypeId).length ?? -1) > 0;
        return ElementType.is(object) && (
            (isSpecified && !Enum.is(object)) ||
            (hasStringProp(object, 'label') && hasArrayProp(object, 'attributes')));
    }
}

export interface PrimeNodePaletteCategory {
    primeElementTypeId: string;
    label: string;
    elementTypeIds: string[];
}

export namespace PrimeNodePaletteCategory {
    export function is(object: any): object is PrimeNodePaletteCategory {
        return hasStringProp(object, 'elementTypeId') && hasStringProp(object, 'label') && hasArrayProp(object, 'elementTypeIds');
    }
}

/**
 * Functions
 */

/**
 * Polymorphie
 */

export function isInstanceOf(type: string, superType: string): boolean {
    const spec = getSpecOf(type);
    return type === superType || (spec !== undefined && (spec!.superTypes ?? []).includes(superType));
}

/**
 * Properties and Types
 */

export function getCustomTypes(): CustomType[] {
    return getCompositionSpecification().customTypes ?? [];
}

export function getCustomType(elementTypeId: string): CustomType | undefined {
    return getCustomTypes().filter(t => t.elementTypeId === elementTypeId)[0];
}

export function getEnums(): Enum[] {
    const types = getCustomTypes().filter(t => Enum.is(t)) as Enum[];
    return types ?? [];
}

export function getEnum(elementTypeId: string): Enum | undefined {
    return getEnums().filter(t => t.elementTypeId === elementTypeId)[0] ?? undefined;
}

export function getUserDefinedTypes(): UserDefinedType[] {
    const types = getCustomTypes().filter(t => UserDefinedType.is(t)) as UserDefinedType[];
    return types ?? [];
}

export function getUserDefinedType(elementTypeId: string): UserDefinedType | undefined {
    return getUserDefinedTypes().filter(t => t.elementTypeId === elementTypeId)[0] ?? undefined;
}

export function getNonAbstractTypeOptions(parentType: Type): Type[] {
    let relevantTypes: Type[] = [];
    if (NodeType.is(parentType)) {
        relevantTypes = getNodeTypes();
    } else if (EdgeType.is(parentType)) {
        relevantTypes = getEdgeTypes();
    } else if (GraphType.is(parentType)) {
        relevantTypes = getGraphTypes();
    } else if (Enum.is(parentType)) {
        relevantTypes = getEnums();
    } else if (UserDefinedType.is(parentType)) {
        relevantTypes = getUserDefinedTypes();
    }

    return [parentType].concat(getNonAbstractTypeOptionsRecursive(parentType, relevantTypes)).filter(type => !type.abstract);
}

export function getNonAbstractTypeOptionsRecursive(parentType: Type, relevantTypes: Type[]): Type[] {
    const subTypes = relevantTypes.filter(type => type.parent === parentType.elementTypeId);
    subTypes.forEach(subType => subTypes.concat(getNonAbstractTypeOptionsRecursive(subType, relevantTypes)));
    return [...new Set(subTypes)]; // remove duplicates
}

export function getAttributesOf(elementTypeId: string): Attribute[] {
    const spec = getSpecOf(elementTypeId);
    return spec?.attributes ?? [];
}

export function getAttribute(elementTypeId: string, attributeName: string): Attribute | undefined {
    const spec = getSpecOf(elementTypeId) ?? getUserDefinedType(elementTypeId);
    const attributes = spec?.attributes?.filter(a => a.name === attributeName);
    return attributes ? attributes[0] : undefined;
}

export function canAdd(lengthIndex: number, constraint: Constraint): boolean {
    return constraint.upperBound < constraint.lowerBound || constraint.upperBound >= lengthIndex;
}

export function canDelete(lengthIndex: number, constraint: Constraint): boolean {
    return constraint.lowerBound >= 0 && lengthIndex >= constraint.lowerBound;
}

export function canAssign(lengthIndex: number, constraint: Constraint): boolean {
    return (constraint.upperBound < constraint.lowerBound || constraint.upperBound >= lengthIndex) && lengthIndex >= 0;
}

/**
 * Style and Appearance
 */

export function getShapes(shape: AbstractShape | undefined, filter?: (filterTarget: AbstractShape) => boolean): AbstractShape[] {
    let result: AbstractShape[] = [];
    if (shape) {
        const isPart = filter ? filter(shape) : true;
        if (isPart) {
            result.push(shape);
        }
        if (ContainerShape.is(shape) && shape.children) {
            for (const child of shape.children) {
                const childResult = getShapes(child, filter);
                result = result.concat(childResult);
            }
        }
    }
    return result;
}

export function getStyleOfElement(type: string | EdgeType): Style | undefined {
    let elementSpec: ElementType;
    if (typeof type === 'string') {
        elementSpec = getSpecOf(type) as ElementType;
    } else {
        elementSpec = type as EdgeType;
    }
    const style = elementSpec.view?.style;
    if (style) {
        if (typeof style === 'string') {
            // style is referenced
            return getStyleByNameOf(style);
        } else {
            // inline style
            return style;
        }
    }
    return undefined;
}

export function getAppearanceOfEdge(type: string | EdgeType): Appearance | undefined {
    const style = getStyleOfElement(type);
    const appearance = style ? (style as EdgeStyle).appearance : undefined;
    if (appearance) {
        if (typeof appearance === 'string') {
            return getAppearanceByNameOf(appearance);
        } else {
            return appearance;
        }
    }
    return undefined;
}

export function getAppearanceOfShape(type: Shape): Appearance | undefined {
    switch (type.type) {
        case AbstractShape.TEXT:
            return resolveAppearance((type as Text).appearance);
        case AbstractShape.MULTITEXT:
            return resolveAppearance((type as MultiText).appearance);
        case AbstractShape.IMAGE:
            return undefined;
        case AbstractShape.WEBVIEW:
            return undefined;
        case AbstractShape.POLYLINE:
            return resolveAppearance((type as Polyline).appearance);
        case AbstractShape.RECTANGLE:
            return resolveAppearance((type as Rectangle).appearance);
        case AbstractShape.ROUNDEDRECTANGLE:
            return resolveAppearance((type as RoundedRectangle).appearance);
        case AbstractShape.ELLIPSE:
            return resolveAppearance((type as Ellipse).appearance);
        case AbstractShape.POLYGON:
            return resolveAppearance((type as Polygon).appearance);
        default:
            return undefined;
    }
}

function resolveAppearance(app: string | Appearance | undefined): Appearance | undefined {
    if (typeof app === 'string') {
        return getAppearanceByNameOf(app);
    }
    return app;
}

/**
 * Annotation
 */

const handlerAnnotations = [
    'Hook',
    'CustomAction',
    'AppearanceProvider',
    'Validation',
    'GeneratorAction',
    'Interpreter',
    'DoubleClickAction',
    'SelectAction'
];

export function hasAppearanceProvider(type: string): boolean {
    return getAppearanceProvider(type).length > 0;
}

export function getAppearanceProvider(type: string): string[] {
    const result: Set<string> = new Set();
    const style = getStyleOfElement(type);
    const appearanceProviderValue = style?.appearanceProvider;
    if (appearanceProviderValue) {
        result.add(appearanceProviderValue);
    }
    const annotationValues = getAnnotationValues(type, 'AppearanceProvider');
    for (const ann of annotationValues) {
        if (ann && ann.length > 0) {
            ann.forEach(a => {
                if (!result.has(a)) {
                    result.add(a);
                }
            });
        }
    }
    return Array.from(result);
}

export function hasGeneratorAction(type: string): boolean {
    return hasAnnotation(type, 'GeneratorAction');
}

export function getGeneratorAction(elementTypeId: string): string[][] {
    return getAnnotationValues(elementTypeId, 'GeneratorAction');
}

export function hasCustomAction(type: string): boolean {
    return hasAnnotation(type, 'CustomAction');
}

export function getCustomActions(elementTypeId: string): string[][] {
    return getAnnotationValues(elementTypeId, 'CustomAction');
}

export function hasDoubleClickAction(type: string): boolean {
    return hasAnnotation(type, 'DoubleClickAction');
}

export function getDoubleClickActions(elementTypeId: string): string[][] {
    return getAnnotationValues(elementTypeId, 'DoubleClickAction');
}

export function hasSelectAction(type: string): boolean {
    return hasAnnotation(type, 'SelectAction');
}

export function getSelectActions(elementTypeId: string): string[][] {
    return getAnnotationValues(elementTypeId, 'SelectAction');
}

export function hasValidator(graphElementTypeId: string): boolean {
    return hasAnnotation(graphElementTypeId, 'Validation');
}

export function getValidators(graphElementTypeId: string): string[][] {
    return getAnnotationValues(graphElementTypeId, 'Validation');
}

export function getAnnotationValues(type: string, annotation: string): string[][] {
    const annotations = getAnnotations(type, annotation);
    return annotations.map(v => v.values);
}

export function hasAnnotation(type: string, annotation: string): boolean {
    const annotations = getAnnotations(type, annotation);
    return annotations.filter(a => a.name === annotation).length > 0;
}

export function getAnnotations(type: string, annotation: string): Annotation[] {
    const elementSpec = getSpecOf(type) as ElementType;
    return (elementSpec?.annotations ?? []).filter(a => a.name === annotation);
}

export function getAllAnnotations(type: string): Annotation[] {
    const elementSpec = getSpecOf(type) as ElementType;
    return elementSpec?.annotations ?? [];
}

export function getAllHandlerNames(): string[] {
    const elements = getModelElementSpecifications();
    let handlerNames: string[] = [];
    for (const element of elements) {
        // get style handler
        const appearanceProvider = getAppearanceProvider(element.elementTypeId);
        if (appearanceProvider) {
            handlerNames = handlerNames.concat(appearanceProvider);
        }

        // get mgl annotations
        const annotations = getAllAnnotations(element.elementTypeId);
        for (const ann of annotations.filter(a => handlerAnnotations.includes(a.name))) {
            switch (ann.name) {
                default:
                    {
                        const values = ann.values;
                        // handlerClassNames have to be the first value of annotations
                        if (values.length > 0) {
                            const handlerName = values[0];
                            if (!handlerNames.includes(handlerName)) {
                                handlerNames.push(handlerName);
                            }
                        }
                    }
                    break;
            }
        }
    }
    return handlerNames;
}

export function hasHooks(elementTypeId: string): boolean {
    return hasAnnotation(elementTypeId, 'Hook');
}

export function hasHooksOfTypes(elementTypeId: string, hookTypes: HookType[]): boolean {
    return getHooksOfTypes(elementTypeId, hookTypes).length > 0;
}

export function getHooksOfTypes(elementTypeId: string, hookTypes: HookType[]): string[][] {
    return getAllHooks(elementTypeId).filter(values => {
        for (const h of hookTypes) {
            if (values.includes(h.valueOf())) {
                return true;
            }
        }
        return false;
    });
}

export function getHookTypes(elementTypeId: string, hookClassName: string): string[] {
    const hook = getAllHooks(elementTypeId).filter(values => values.length > 0 && values.at(0) === hookClassName);
    if (hook.length > 0) {
        return Array.from(new Set(hook.flat().filter(values => values !== hookClassName)));
    }
    return [];
}

export function getHooksOfType(elementTypeId: string, hookType: HookType): string[][] {
    return getAllHooks(elementTypeId).filter(values => values.includes(hookType.valueOf()));
}

export function getAllHooks(elementTypeId: string): string[][] {
    return getAnnotationValues(elementTypeId, 'Hook');
}

export function isResizeable(type: string): boolean {
    return getAnnotations(type, 'disable').filter(a => a.values.includes('resize')).length <= 0;
}

export function isMovable(type: string): boolean {
    return getAnnotations(type, 'disable').filter(a => a.values.includes('move')).length <= 0;
}

export function isDeletable(type: string): boolean {
    return getAnnotations(type, 'disable').filter(a => a.values.includes('delete')).length <= 0;
}

export function isCreateable(type: string): boolean {
    return getAnnotations(type, 'disable').filter(a => a.values.includes('create')).length <= 0;
}

export function isSelectable(type: string): boolean {
    return getAnnotations(type, 'disable').filter(a => a.values.includes('select')).length <= 0;
}

/**
 * Icon
 */

export function getIconClass(elementTypeId: string | undefined): string | undefined {
    if (!elementTypeId) {
        return undefined;
    }
    const elementSpec = getSpecOf(elementTypeId) as ElementType;
    if (NodeType.is(elementSpec) || EdgeType.is(elementSpec)) {
        return elementSpec.elementTypeId.replace(':', '_');
    }
    return undefined;
}

function getIconFromAnnotation(elementTypeId: string): string | undefined {
    const iconValues = getAnnotations(elementTypeId, 'icon')
        .map(a => a.values)
        .flat();
    if (iconValues.length > 0) {
        return iconValues[0];
    }
    return undefined;
}

export function getIcon(elementTypeId: string | undefined): string | undefined {
    if (!elementTypeId) {
        return undefined;
    }
    const elementSpec = getSpecOf(elementTypeId) as ElementType;
    if (NodeType.is(elementSpec) || EdgeType.is(elementSpec)) {
        return elementSpec.icon ?? getIconFromAnnotation(elementTypeId) ?? undefined;
    }
    return undefined;
}

/**
 * Palettes
 */

export function getPaletteIconClass(paletteCategory: string | undefined): string | undefined {
    if (!paletteCategory) {
        return undefined;
    }
    const paletteAnnotations = getAllPaletteAnnotations();
    // all annotations with categoryName that have two values, e.g.: @palette(paletteCategory, iconPath)
    const annotations = paletteAnnotations.filter(a => a.values.length >= 2 && a.values[0] === paletteCategory);
    if (annotations.length > 0) {
        return 'icon_palette_' + annotations[0].values[0].replace(':', '_').toLowerCase();
    }
    return undefined;
}

export function getPaletteIconPath(paletteCategory: string | undefined): string | undefined {
    if (!paletteCategory) {
        return undefined;
    }
    const paletteAnnotations = getAllPaletteAnnotations();
    // all annotations with categoryName that have two values, e.g.: @palette(paletteCategory, iconPath)
    const annotations = paletteAnnotations.filter(a => a.values.length >= 2 && a.values[0] === paletteCategory);
    if (annotations.length > 0) {
        return annotations[0].values[1];
    }
    return undefined;
}

function getPalettesFromAnnotation(elementTypeId: string): string[][] {
    return getAnnotations(elementTypeId, 'palette').map(a => a.values);
}

export function hasPalette(elementTypeId: string, palette: string): boolean {
    const elementSpec = getSpecOf(elementTypeId) as ElementType;
    if (NodeType.is(elementSpec) || EdgeType.is(elementSpec)) {
        const palettes = getPalettes(elementSpec.elementTypeId);
        if (palettes.indexOf(palette) >= 0) {
            return true;
        }
    }
    return false;
}

export function getPalettes(elementTypeId: string | undefined): string[] {
    if (!elementTypeId) {
        return [];
    }
    const elementSpec = getSpecOf(elementTypeId) as ElementType;
    let result: string[] = [];
    if (NodeType.is(elementSpec) || EdgeType.is(elementSpec)) {
        const paletteNamesOfAnnotations = getPalettesFromAnnotation(elementTypeId).map(a => a[0]);
        result = (elementSpec.palettes ?? []).concat(paletteNamesOfAnnotations);
        if (result.length <= 0 && isPrime(elementTypeId)) {
            result.push((elementSpec as NodeType).primeReference!.name);
        }
    }
    return result;
}

export function getNodePalettes(): string[] {
    const palettes: string[] = [];
    getNodeTypes()
        .map((e, i, a) => getPalettes(e.elementTypeId))
        .flat()
        .forEach((paletteElement: string) => (palettes.indexOf(paletteElement) < 0 ? palettes.push(paletteElement) : undefined));
    return palettes;
}

export function getPrimeNodePaletteCategories(): PrimeNodePaletteCategory[] {
    return getNodeTypes((e: NodeType) => isPrimeReference(e.elementTypeId))
        .map(e => getPrimeNodePaletteCategoryOf(e.elementTypeId))
        .filter(e => e !== undefined) as PrimeNodePaletteCategory[];
}

export function getPrimeNodePaletteCategoriesOf(elementTypeId: string): PrimeNodePaletteCategory[] {
    const graphType = getGraphSpecOf(elementTypeId);
    if (!graphType) {
        return [];
    }
    return getContainmentsOf(graphType)
        .filter(e => NodeType.is(e))
        .filter((e: NodeType) => isPrimeReference(e.elementTypeId))
        .map(e => getPrimeNodePaletteCategoryOf(e.elementTypeId))
        .filter(e => e !== undefined) as PrimeNodePaletteCategory[];
}

export function getPrimeNodePaletteCategoryOf(elementTypeId: string): PrimeNodePaletteCategory | undefined {
    if (!isPrimeReference(elementTypeId)) {
        return undefined;
    }
    const spec = getNodeSpecOf(elementTypeId)!; // isPrimeReference already made sure, that this is a node
    const primeReference = spec.primeReference!;
    return {
        primeElementTypeId: spec.elementTypeId,
        label: primeReference.name,
        elementTypeIds: [primeReference.type]
    };
}

export function getEdgePalettes(): string[] {
    const palettes: string[] = [];
    getEdgeTypes()
        .map((e, i, a) => getPalettes(e.elementTypeId))
        .flat()
        .forEach((paletteElement: string | undefined) =>
            palettes.indexOf(paletteElement ?? '') < 0 ? palettes.push(paletteElement ?? '') : undefined
        );
    return palettes;
}

export function getAllPaletteCategories(primePalettes = true): string[] {
    return getNodePalettes().concat(
        getEdgePalettes().concat(primePalettes ? Array.from(new Set(getPrimeNodePaletteCategories().map(e => e.label))) : [])
    );
}

export function getAllPaletteAnnotations(): Annotation[] {
    const modelElements = getModelElementSpecifications().filter(m => EdgeType.is(m) || NodeType.is(m));
    return modelElements.map(e => (e.annotations ?? []).filter(a => a.name === 'palette')).flat();
}

/**
 * Containment
 */

export function isContainer(containerType: string): boolean {
    const containerSpec = getSpecOf(containerType) as NodeType;
    return ModelElementContainer.is(containerSpec);
}

export function getContainersOf(e: NodeType): NodeType[] {
    return getNodeTypes(
        (n: NodeType) =>
            n.containments !== undefined &&
            n.containments
                .map((c: Constraint) => c.elements)
                .flat()
                .indexOf(e.elementTypeId) > 0
    );
}

export function getContainerNodes(): NodeType[] {
    return getNodeTypes((n: NodeType) => isContainer(n.elementTypeId));
}

export function getContainmentsOf(e: ModelElementContainer): NodeType[] {
    if (e?.containments !== undefined) {
        const containmentTypesIds = e.containments.map((c: Constraint) => c.elements ?? []).flat();
        return containmentTypesIds.map(id => getNodeSpecOf(id)).filter((n: NodeType | undefined) => n !== undefined) as NodeType[];
    }
    return [];
}

export function canBeCreated(containerType: string, containmentType: string, seenContainments: NodeType[] = []): boolean {
    const containerSpec = getSpecOf(containerType);
    if (containerSpec === undefined || !ModelElementContainer.is(containerSpec)) {
        return false;
    }
    const containments = getContainmentsOf(containerSpec);
    const nodeSpec = getNodeSpecOf(containmentType);
    const edgeSpec = getEdgeSpecOf(containmentType);
    if (nodeSpec) {
        // is NodeType
        return (
            containments.length > 0 &&
            // tested type is either is a defined containment, or ...
            (containments.filter((e: NodeType) => e.elementTypeId === containmentType).length > 0 ||
                containments.filter(
                    c =>
                        // ... there is a containable container c that is not a containment of container above (prevent recursion)...
                        seenContainments.filter(s => s.elementTypeId === c.elementTypeId).length <= 0 &&
                        // then for c holds, if that containment (with type containmentType) is containable by the containble container c,
                        // then containmentType is also defined as a creatable containment of this container
                        canBeCreated(c.elementTypeId, containmentType, seenContainments.concat(containments))
                ).length > 0) // ... not creatable
        );
    } else if (edgeSpec) {
        // is EdgeType
        const sources = getEdgeSources(edgeSpec);
        const target = getEdgeTargets(edgeSpec);
        // there has to be at least one source and one target node, that is creatable inside the containerType!
        return (
            sources.find(s => canBeCreated(containerType, s.elementTypeId)) !== undefined &&
            target.find(s => canBeCreated(containerType, s.elementTypeId)) !== undefined
        );
    }
    return false;
}

/**
 * Edges
 */

export function getEdgeTargets(e: EdgeType): NodeType[] {
    const incomingEdgeFor = getNodeTypes(
        (n: NodeType) =>
            n.incomingEdges !== undefined &&
            n.incomingEdges
                .map((c: Constraint) => c.elements)
                .flat()
                .indexOf(e.elementTypeId) >= 0
    );
    return incomingEdgeFor;
}

export function getEdgeSources(e: EdgeType): NodeType[] {
    const outgoingEdgeFor = getNodeTypes(
        (n: NodeType) =>
            n.outgoingEdges !== undefined &&
            n.outgoingEdges
                .map((c: Constraint) => c.elements)
                .flat()
                .indexOf(e.elementTypeId) >= 0
    );
    return outgoingEdgeFor;
}

/**
 * Specification
 */

export function isDiagramExtension(filePath: string, elementTypeId: string): boolean {
    return filePath !== undefined && elementTypeId !== undefined && getFileExtension(filePath) === getDiagramExtension(elementTypeId);
}

export function getFileExtension(filePath: string): string {
    return filePath.slice(filePath.lastIndexOf('.') + 1);
}

export function getDiagramExtension(elementTypeId: string): string | undefined {
    return getGraphSpecOf(elementTypeId)?.diagramExtension;
}

export function getGraphModelOfFileType(diagramExtension: string): GraphType | undefined {
    return getGraphSpecByFilterOf(e => e.diagramExtension === diagramExtension);
}

export function getDiagramExtensions(): string[] {
    return Array.from(new Set(getGraphTypes().map(g => g.diagramExtension)));
}

export function getModelElementSpecifications(): ElementType[] {
    const nodeTypes: NodeType[] = getNodeTypes();
    const edgeTypes: EdgeType[] = getEdgeTypes();
    const graphTypes: GraphType[] = getGraphTypes();
    let elementTypes: ElementType[] = [];
    elementTypes = elementTypes.concat(nodeTypes);
    elementTypes = elementTypes.concat(edgeTypes);
    elementTypes = elementTypes.concat(graphTypes);
    return elementTypes;
}

export function getSpecOf(elementTypeId: string): ElementType | undefined {
    const nodeSpec = getNodeSpecOf(elementTypeId);
    if (nodeSpec) {
        return nodeSpec;
    }
    const edgeSpec = getEdgeSpecOf(elementTypeId);
    if (edgeSpec) {
        return edgeSpec;
    }
    const graphType = getGraphSpecOf(elementTypeId);
    if (graphType) {
        return graphType;
    }
    if (elementTypeId === undefined) {
        throw Error('Specification not found for type: ' + elementTypeId);
    }
    return undefined;
}

export function getGraphSpecOf(elementTypeId: string): GraphType | undefined {
    return getGraphSpecByFilterOf(e => e.elementTypeId === elementTypeId);
}

export function getNodeSpecOf(elementTypeId: string): NodeType | undefined {
    return getNodeSpecByFilterOf(e => e.elementTypeId === elementTypeId);
}

export function getEdgeSpecOf(elementTypeId: string): EdgeType | undefined {
    return getEdgeSpecByFilterOf(e => e.elementTypeId === elementTypeId);
}

export function getStyleByNameOf(name: string | undefined): Style | undefined {
    return getStyleByFilterOf(e => e.name === name);
}

export function getAppearanceByNameOf(name: string | undefined): Appearance | undefined {
    return getAppearanceByFilterOf(e => e.name === name);
}

export function getGraphSpecByFilterOf(filter: (e: GraphType) => boolean): GraphType | undefined {
    const found = getGraphTypes(filter);
    return found.length >= 0 ? found[0] : undefined;
}

export function getNodeSpecByFilterOf(filter: (e: NodeType) => boolean): NodeType | undefined {
    const found = getNodeTypes(filter);
    return found.length >= 0 ? found[0] : undefined;
}

export function getEdgeSpecByFilterOf(filter: (e: EdgeType) => boolean): EdgeType | undefined {
    const found = getEdgeTypes(filter);
    return found.length >= 0 ? found[0] : undefined;
}

export function getStyleByFilterOf(filter: (e: Style) => boolean): Style | undefined {
    const found = getStyles(filter);
    return found.length >= 0 ? found[0] : undefined;
}

export function getAppearanceByFilterOf(filter: (e: Appearance) => boolean): Appearance | undefined {
    const found = getAppearances(filter);
    return found.length >= 0 ? found[0] : undefined;
}

export function getGraphTypes(filter?: (e: GraphType) => boolean): GraphType[] {
    const types: GraphType[] = getCompositionSpecification().graphTypes ?? [];
    if (filter) {
        return types.filter(e => filter(e));
    }
    return types;
}

export function getNodeTypes(filter?: (e: NodeType) => boolean): NodeType[] {
    const nodeTypes: NodeType[] = getCompositionSpecification().nodeTypes ?? [];
    if (filter) {
        return nodeTypes.filter(e => filter(e));
    }
    return nodeTypes;
}

export function getEdgeTypes(filter?: (e: EdgeType) => boolean): EdgeType[] {
    const types: EdgeType[] = getCompositionSpecification().edgeTypes ?? [];
    if (filter) {
        return types.filter(e => filter(e));
    }
    return types;
}

export function getStyles(filter?: (e: Style) => boolean): Style[] {
    const types: Style[] = getCompositionSpecification().styles ?? [];
    if (filter) {
        return types.filter(e => filter(e));
    }
    return types;
}

export function getAppearances(filter?: (e: Appearance) => boolean): Appearance[] {
    const types: Appearance[] = getCompositionSpecification().appearances ?? [];
    if (filter) {
        return types.filter(e => filter(e));
    }
    return types;
}

function getCompositionSpecification(): CompositionSpecification {
    return MetaSpecification.get();
}

/**
 * Prime
 */

export function isPrime(elementTypeId: string): boolean {
    return isPrimeReference(elementTypeId);
}

export function isPrimeReference(elementTypeId: string): boolean {
    const elementSpec = getSpecOf(elementTypeId) as ElementType;
    if (NodeType.is(elementSpec)) {
        if (elementSpec.primeReference !== undefined && elementSpec.primeReference.name && elementSpec.primeReference.type) {
            return true;
        }
    }
    return false;
}
