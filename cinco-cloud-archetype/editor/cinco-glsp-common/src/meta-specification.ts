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
import { hasArrayProp, hasNumberProp, hasObjectProp, hasStringProp } from './protocol/type-utils';

/**
 * Data model
 */

export namespace MetaSpecification {
    let PRIOR_META_SPECIFICATION: CompositionSpecification = {};
    let META_SPECIFICATION: CompositionSpecification = {};
    let cacheReady = false;

    // preprocessed
    const SPEC_MAP: Map<string, ElementType> = new Map();

    // delta
    let changedAnnotations = false;

    // cached
    let CACHED_CONTAINMENTS: Map<ModelElementContainer, ElementType[]> = new Map();
    const CACHED_EDGE_TARGETS: Map<string, NodeType[]> = new Map();
    const CACHED_EDGE_SOURCES: Map<string, NodeType[]> = new Map();

    // precomputated (Convention: <elementTypeId, information>)
    const IS_NODE_TYPE: Map<string, boolean> = new Map();
    const IS_EDGE_TYPE: Map<string, boolean> = new Map();
    const IS_GRAPH_TYPE: Map<string, boolean> = new Map();
    const IS_CUSTOM_TYPE: Map<string, boolean> = new Map();
    const HAS_APPEARANCE_PROVIDER: Map<string, boolean> = new Map();
    const HAS_VALUE_PROVIDER: Map<string, boolean> = new Map();
    const HAS_VALIDATION: Map<string, boolean> = new Map();

    export function get(): CompositionSpecification {
        return META_SPECIFICATION;
    }

    export function exportCachedComputations(): {
        cachedContainments: Map<ModelElementContainer, ElementType[]>;
        changedAnnotations: boolean;
    } {
        return {
            cachedContainments: CACHED_CONTAINMENTS,
            changedAnnotations
        };
    }

    export function importCachedComputations(computations: {
        cachedContainments: Map<ModelElementContainer, NodeType[]>;
        changedAnnotations: boolean;
    }): void {
        CACHED_CONTAINMENTS = computations.cachedContainments;
        changedAnnotations = computations.changedAnnotations;
    }

    export function merge(metaSpecification: CompositionSpecification): void {
        cacheReady = false;
        addGraphTypes(metaSpecification.graphTypes ?? []);
        addNodeTypes(metaSpecification.nodeTypes ?? []);
        addEdgeTypes(metaSpecification.edgeTypes ?? []);
        addCustomTypes(metaSpecification.customTypes ?? []);
        addAppearances(metaSpecification.appearances ?? []);
        addStyles(metaSpecification.styles ?? []);
    }

    export function prepareCache(): void {
        createSpecMap();
        computeClassifications();
        cacheEdgeRelations();
        cacheContainments();
        detectChanges();
        computateAnnotationClassifications();
        PRIOR_META_SPECIFICATION = get();
        cacheReady = true;
    }

    export function isCacheReady(): boolean {
        return cacheReady;
    }

    /**
     * Precomputations
     */

    function createSpecMap(): void {
        SPEC_MAP.clear();
        const spec = get();
        const elements = ((spec.nodeTypes as ElementType[]) ?? [])
            .concat(spec.edgeTypes ?? [])
            .concat(spec.graphTypes ?? [])
            .concat(spec.customTypes ?? []);
        elements.forEach(e => SPEC_MAP.set(e.elementTypeId, e));
    }

    export function getSpecMap(): Map<string, ElementType> {
        return SPEC_MAP;
    }

    function cacheEdgeRelations(): void {
        CACHED_EDGE_TARGETS.clear();
        getEdgeTypes().forEach(edgeType => {
            const targets = getEdgeTargetsForCaching(edgeType);
            CACHED_EDGE_TARGETS.set(edgeType.elementTypeId, targets);
        });
        CACHED_EDGE_SOURCES.clear();
        getEdgeTypes().forEach(edgeType => {
            const sources = getEdgeSourcesForCaching(edgeType);
            CACHED_EDGE_SOURCES.set(edgeType.elementTypeId, sources);
        });
    }

    export function getCachedEdgeTargets(elementTypeId: string): NodeType[] {
        return CACHED_EDGE_TARGETS.get(elementTypeId) ?? [];
    }

    export function getCachedEdgeSources(elementTypeId: string): NodeType[] {
        return CACHED_EDGE_SOURCES.get(elementTypeId) ?? [];
    }

    function cacheContainments(): void {
        // pre resolve container
        CACHED_CONTAINMENTS.clear();
        const containers = (getGraphTypes() as ModelElementContainer[]).concat(getContainerNodes());
        for (const container of containers) {
            const containments = getDeepContainmentsOf(container);
            CACHED_CONTAINMENTS.set(container, containments);
        }
    }

    export function getCachedContainments(containerType: ModelElementContainer): ElementType[] {
        return CACHED_CONTAINMENTS.get(containerType) ?? [];
    }

    function detectChanges(): void {
        changedAnnotations = annotationsHaveChanged(PRIOR_META_SPECIFICATION, META_SPECIFICATION);
    }

    export function annotationsChanged(): boolean {
        return changedAnnotations;
    }

    function computateAnnotationClassifications(): void {
        HAS_APPEARANCE_PROVIDER.clear();
        HAS_VALUE_PROVIDER.clear();
        HAS_VALIDATION.clear();

        // AppearanceProvider
        for (const elementTypeId of getSpecMap().keys()) {
            const hasAnn = hasAppearanceProvider(elementTypeId);
            HAS_APPEARANCE_PROVIDER.set(elementTypeId, hasAnn);
        }

        // ValueProvider
        for (const elementTypeId of getSpecMap().keys()) {
            const hasAnn = hasValueProvider(elementTypeId);
            HAS_APPEARANCE_PROVIDER.set(elementTypeId, hasAnn);
        }

        // Validation
        for (const elementTypeId of getSpecMap().keys()) {
            const hasAnn = hasValidation(elementTypeId);
            HAS_APPEARANCE_PROVIDER.set(elementTypeId, hasAnn);
        }
    }

    export function _hasAppearanceProvider(elementTypeId: string): boolean {
        return HAS_APPEARANCE_PROVIDER.get(elementTypeId) ?? false;
    }

    export function _hasValueProvider(elementTypeId: string): boolean {
        return HAS_VALUE_PROVIDER.get(elementTypeId) ?? false;
    }

    export function _hasValidation(elementTypeId: string): boolean {
        return HAS_VALIDATION.get(elementTypeId) ?? false;
    }

    function computeClassifications(): void {
        IS_NODE_TYPE.clear();
        IS_EDGE_TYPE.clear();
        IS_GRAPH_TYPE.clear();
        IS_CUSTOM_TYPE.clear();

        // graphs
        (get().graphTypes ?? []).forEach(t => IS_GRAPH_TYPE.set(t.elementTypeId, true));
        (get().edgeTypes ?? []).forEach(t => IS_GRAPH_TYPE.set(t.elementTypeId, false));
        (get().nodeTypes ?? []).forEach(t => IS_GRAPH_TYPE.set(t.elementTypeId, false));
        (get().customTypes ?? []).forEach(t => IS_GRAPH_TYPE.set(t.elementTypeId, false));
        // nodes
        (get().nodeTypes ?? []).forEach(t => IS_NODE_TYPE.set(t.elementTypeId, true));
        (get().graphTypes ?? []).forEach(t => IS_NODE_TYPE.set(t.elementTypeId, false));
        (get().edgeTypes ?? []).forEach(t => IS_NODE_TYPE.set(t.elementTypeId, false));
        (get().customTypes ?? []).forEach(t => IS_NODE_TYPE.set(t.elementTypeId, false));
        // edges
        (get().edgeTypes ?? []).forEach(t => IS_EDGE_TYPE.set(t.elementTypeId, true));
        (get().graphTypes ?? []).forEach(t => IS_EDGE_TYPE.set(t.elementTypeId, false));
        (get().nodeTypes ?? []).forEach(t => IS_EDGE_TYPE.set(t.elementTypeId, false));
        (get().customTypes ?? []).forEach(t => IS_EDGE_TYPE.set(t.elementTypeId, false));
        // customType
        (get().customTypes ?? []).forEach(t => IS_CUSTOM_TYPE.set(t.elementTypeId, true));
        (get().graphTypes ?? []).forEach(t => IS_CUSTOM_TYPE.set(t.elementTypeId, false));
        (get().nodeTypes ?? []).forEach(t => IS_CUSTOM_TYPE.set(t.elementTypeId, false));
        (get().edgeTypes ?? []).forEach(t => IS_CUSTOM_TYPE.set(t.elementTypeId, false));
    }

    export function isNodeType(elementTypeId: string): boolean {
        return IS_NODE_TYPE.get(elementTypeId) ?? false;
    }

    export function isEdgeType(elementTypeId: string): boolean {
        return IS_EDGE_TYPE.get(elementTypeId) ?? false;
    }

    export function isGraphType(elementTypeId: string): boolean {
        return IS_GRAPH_TYPE.get(elementTypeId) ?? false;
    }

    export function isCustomType(elementTypeId: string): boolean {
        return IS_GRAPH_TYPE.get(elementTypeId) ?? false;
    }

    /*
     * TYPES
     */

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

export interface AbstractPosition {}

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

export interface GraphicsAlgorithm {}

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

export interface GraphModelStyle extends Style {}

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

export interface GraphModelView extends View {}

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
        const isSpecified = MetaSpecification.isGraphType(object?.elementTypeId);
        return ElementType.is(object) && isSpecified && ModelElementContainer.is(object);
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
        const isSpecified = MetaSpecification.isNodeType(object?.elementTypeId);
        return object !== undefined && isSpecified;
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
        const isSpecified = MetaSpecification.isEdgeType(object?.elementTypeId);
        return ElementType.is(object) && isSpecified;
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

export interface CustomType extends Type {}

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
        const isSpecified = MetaSpecification.isCustomType(object?.elementTypeId);
        return (
            ElementType.is(object) &&
            isSpecified &&
            (!Enum.is(object) || (hasStringProp(object, 'label') && hasArrayProp(object, 'attributes')))
        );
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
    const spec = getSpecOf(elementTypeId);
    return UserDefinedType.is(spec) ? spec : undefined;
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

export function getStyleOfElement(type: NodeType | EdgeType): Style | undefined {
    const style = type.view?.style;
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
    'ValueProvider',
    'Validation',
    'GeneratorAction',
    'Interpreter',
    'DoubleClickAction',
    'SelectAction'
];

export function hasAppearanceProvider(elementTypeId: string): boolean {
    if (MetaSpecification.isCacheReady()) {
        return MetaSpecification._hasAppearanceProvider(elementTypeId);
    }
    return getAppearanceProvider(elementTypeId).length > 0;
}

export function getAppearanceProvider(elementTypeId: string): string[] {
    const type = getSpecOf(elementTypeId);
    if (!type || (!NodeType.is(type) && !EdgeType.is(type))) {
        return [];
    }
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

export function hasValueProvider(elementTypeId: string): boolean {
    if (MetaSpecification.isCacheReady()) {
        return MetaSpecification._hasValueProvider(elementTypeId);
    }
    return getValueProvider(elementTypeId).length > 0;
}

export function getValueProvider(elementTypeId: string): string[] {
    const type = getSpecOf(elementTypeId);
    if (!type) {
        return [];
    }
    const result: Set<string> = new Set();
    const annotationValues = getAnnotationValues(type, 'ValueProvider');
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

export function hasGeneratorAction(elementTypeId: string): boolean {
    const type = getSpecOf(elementTypeId);
    if (!type) {
        return false;
    }
    return hasAnnotation(type, 'GeneratorAction');
}

export function getGeneratorAction(elementTypeId: string): string[][] {
    const type = getSpecOf(elementTypeId);
    if (!type) {
        return [];
    }
    return getAnnotationValues(type, 'GeneratorAction');
}

export function hasCustomAction(elementTypeId: string): boolean {
    const type = getSpecOf(elementTypeId);
    if (!type) {
        return false;
    }
    return hasAnnotation(type, 'CustomAction');
}

export function getCustomActions(elementTypeId: string): string[][] {
    const type = getSpecOf(elementTypeId);
    if (!type) {
        return [];
    }
    return getAnnotationValues(type, 'CustomAction');
}

export function hasDoubleClickAction(elementTypeId: string): boolean {
    const type = getSpecOf(elementTypeId);
    if (!type) {
        return false;
    }
    return hasAnnotation(type, 'DoubleClickAction');
}

export function getDoubleClickActions(elementTypeId: string): string[][] {
    const type = getSpecOf(elementTypeId);
    if (!type) {
        return [];
    }
    return getAnnotationValues(type, 'DoubleClickAction');
}

export function hasSelectAction(elementTypeId: string): boolean {
    const type = getSpecOf(elementTypeId);
    if (!type) {
        return false;
    }
    return hasAnnotation(type, 'SelectAction');
}

export function getSelectActions(elementTypeId: string): string[][] {
    const type = getSpecOf(elementTypeId);
    if (!type) {
        return [];
    }
    return getAnnotationValues(type, 'SelectAction');
}

export function hasValidation(elementTypeId: string): boolean {
    if (MetaSpecification.isCacheReady()) {
        return MetaSpecification._hasValidation(elementTypeId);
    }
    const type = getSpecOf(elementTypeId);
    if (!type) {
        return false;
    }
    return hasAnnotation(type, 'Validation');
}

export function getValidators(elementTypeId: string): string[][] {
    const type = getSpecOf(elementTypeId);
    if (!type) {
        return [];
    }
    return getAnnotationValues(type, 'Validation');
}

export function getAnnotationValues(type: ElementType, annotation: string): string[][] {
    const annotations = getAnnotations(type, annotation);
    return annotations.map(v => v.values);
}

function hasAnnotation(type: ElementType, annotation: string): boolean {
    const annotations = getAnnotations(type, annotation);
    return annotations.filter(a => a.name === annotation).length > 0;
}

function getAnnotations(elementSpec: ElementType, annotation: string): Annotation[] {
    return (elementSpec.annotations ?? []).filter(a => a.name === annotation);
}

export function getAllAnnotations(type: string): Annotation[] {
    const elementSpec = getSpecOf(type) as ElementType;
    return elementSpec?.annotations ?? [];
}

export function getAllHandlerNames(): string[] {
    const elements = getModelElementSpecifications();
    let handlerNames: string[] = [];
    for (const element of elements) {
        // get style handler (special as it can also be specified in msl)
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
    const type = getSpecOf(elementTypeId);
    if (!type) {
        return false;
    }
    return hasAnnotation(type, 'Hook');
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
    const type = getSpecOf(elementTypeId);
    if (!type) {
        return [];
    }
    return getAnnotationValues(type, 'Hook');
}

export function isResizeable(elementTypeId: string): boolean {
    const type = getSpecOf(elementTypeId);
    if (!type) {
        return false;
    }
    return getAnnotations(type, 'disable').filter(a => a.values.includes('resize')).length <= 0;
}

export function isMovable(elementTypeId: string): boolean {
    const type = getSpecOf(elementTypeId);
    if (!type) {
        return false;
    }
    return getAnnotations(type, 'disable').filter(a => a.values.includes('move')).length <= 0;
}

export function isDeletable(elementTypeId: string): boolean {
    const type = getSpecOf(elementTypeId);
    if (!type) {
        return false;
    }
    return getAnnotations(type, 'disable').filter(a => a.values.includes('delete')).length <= 0;
}

export function isCreateable(elementTypeId: string): boolean {
    const type = getSpecOf(elementTypeId);
    if (!type) {
        return false;
    }
    return getAnnotations(type, 'disable').filter(a => a.values.includes('create')).length <= 0;
}

export function isSelectable(elementTypeId: string): boolean {
    const type = getSpecOf(elementTypeId);
    if (!type) {
        return false;
    }
    return getAnnotations(type, 'disable').filter(a => a.values.includes('select')).length <= 0;
}

function annotationsHaveChanged(oldSpec: CompositionSpecification | undefined, newSpec: CompositionSpecification): boolean {
    if (!oldSpec) {
        return true;
    }
    const getAnnotatable = (spec: CompositionSpecification): Map<string, Annotation[]> => {
        const annotateable = (spec.nodeTypes ?? ([] as Type[]))
            .concat(spec.edgeTypes ?? [])
            .concat(spec.graphTypes ?? [])
            .concat(spec.customTypes ?? []);
        const map = new Map<string, Annotation[]>();
        annotateable.forEach(e => map.set(e.elementTypeId, e.annotations ?? []));
        return map;
    };
    const oldAnnotateble = getAnnotatable(oldSpec);
    const newAnnotateble = getAnnotatable(newSpec);

    const allKeys = new Set(Array.from(oldAnnotateble.keys()).concat(Array.from(newAnnotateble.keys())));
    for (const type of allKeys) {
        if (
            (!newAnnotateble.has(type) && oldAnnotateble.get(type)!.length > 0) || // old annotated type was deleted
            (!oldAnnotateble.has(type) && newAnnotateble.get(type)!.length > 0) || // new annotated type was added
            (oldAnnotateble.has(type) &&
                newAnnotateble.has(type) &&
                annotationsAreDifferent(oldAnnotateble.get(type)!, newAnnotateble.get(type)!))
        ) {
            return true;
        }
    }

    return false;
}

export function annotationsAreDifferent(a: Annotation[], b: Annotation[]): boolean {
    if (a.length !== b.length) {
        return true;
    }
    const allNames = new Set(a.map(an => an.name).concat(b.map(an => an.name)));
    for (const name of allNames) {
        const annotationsOfNameInA = a.filter(an => an.name === name);
        const annotationsOfNameInB = b.filter(an => an.name === name);
        if (annotationsOfNameInA.length !== annotationsOfNameInB.length) {
            // though lengths are equal, an annotation changed it's name
            return true;
        } else {
            const valuesOfNameInA = annotationsOfNameInA.map(an => an.values);
            const valuesOfNameInB = annotationsOfNameInB.map(an => an.values);
            if (valuesOfNameInA.flat().length !== valuesOfNameInB.flat().length) {
                // a value of an annotation type was added or removed
                return true;
            } else {
                // all value strings per annotation of both A and B
                const valueStrings = new Set(valuesOfNameInA.map(vs => vs.toString()).concat(valuesOfNameInB.map(vs => vs.toString())));
                for (const v of valueStrings) {
                    // the serialized valueSet v of the annotation must be present the same nummber in both
                    // sets
                    const presentInB = valuesOfNameInB.filter(vb => vb.toString() === v).length;
                    const presentInA = valuesOfNameInA.filter(vb => vb.toString() === v).length;
                    if (presentInA !== presentInB) {
                        return true;
                    }
                }
            }
        }
    }
    return false;
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

function getIconFromAnnotation(type: ElementType): string | undefined {
    const iconValues = getAnnotations(type, 'icon')
        .map(a => a.values)
        .flat();
    if (iconValues.length > 0) {
        return iconValues[0];
    }
    return undefined;
}

export function getIcon(elementTypeId: string | undefined): string | undefined {
    const type = getSpecOf(elementTypeId);
    if (!type) {
        return undefined;
    }
    if (NodeType.is(type) || EdgeType.is(type)) {
        return type.icon ?? getIconFromAnnotation(type) ?? undefined;
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

function getPalettesFromAnnotation(type: ElementType): string[][] {
    return getAnnotations(type, 'palette').map(a => a.values);
}

export function hasPalette(elementTypeId: string, palette: string): boolean {
    const elementSpec = getSpecOf(elementTypeId) as ElementType;
    if (NodeType.is(elementSpec) || EdgeType.is(elementSpec)) {
        const palettes = getPalettes(elementSpec);
        if (palettes.indexOf(palette) >= 0) {
            return true;
        }
    }
    return false;
}

export function getPalettes(type: ElementType | undefined): string[] {
    if (!type) {
        return [];
    }
    let result: string[] = [];
    const paletteNamesOfAnnotations = getPalettesFromAnnotation(type).map(a => a[0]);
    result = ((type as any).palettes ?? []).concat(paletteNamesOfAnnotations);
    if (result.length <= 0 && isPrime(type)) {
        result.push((type as any).primeReference.name);
    }
    return result;
}

export function getNodePalettes(): string[] {
    const palettes: string[] = [];
    getNodeTypes()
        .map((e, i, a) => getPalettes(e))
        .flat()
        .forEach((paletteElement: string) => (palettes.indexOf(paletteElement) < 0 ? palettes.push(paletteElement) : undefined));
    return palettes;
}

export function getPrimeNodePaletteCategories(): PrimeNodePaletteCategory[] {
    return getNodeTypes((e: NodeType) => isPrimeReference(e))
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
        .filter((e: ElementType) => isPrimeReference(e))
        .map(e => getPrimeNodePaletteCategoryOf(e.elementTypeId))
        .filter(e => e !== undefined) as PrimeNodePaletteCategory[];
}

export function getPrimeNodePaletteCategoryOf(elementTypeId: string): PrimeNodePaletteCategory | undefined {
    const spec = getNodeSpecOf(elementTypeId)!;
    if (!spec || !isPrimeReference(spec)) {
        return undefined;
    }
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
        .map((e, i, a) => getPalettes(e))
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
    const nodePalleteAnnotations = getNodeTypes()
        .map(e => (e.annotations ?? []).filter(a => a.name === 'palette'))
        .flat();
    const edgePaletteAnnotations = getEdgeTypes()
        .map(e => (e.annotations ?? []).filter(a => a.name === 'palette'))
        .flat();
    return nodePalleteAnnotations.concat(edgePaletteAnnotations);
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

export function getContainerNodes(): ModelElementContainer[] {
    return getNodeTypes((n: NodeType) => isContainer(n.elementTypeId));
}

export function getContainmentsOf(e: ModelElementContainer): ElementType[] {
    if (e?.containments !== undefined) {
        const containmentTypesIds = e.containments.map((c: Constraint) => c.elements ?? []).flat();
        const nodeContainments = containmentTypesIds
            .map(id => getNodeSpecOf(id))
            .filter((n: NodeType | undefined) => n !== undefined) as NodeType[];

        const edgeTypes = Array.from(
            new Set(
                nodeContainments.map(n =>
                    // incoming edges
                    (n.outgoingEdges ?? [])
                        .map((c: Constraint) => c.elements)
                        .flat()
                        .concat(
                            // incoming edges
                            (n.incomingEdges ?? []).map((c: Constraint) => c.elements).flat()
                        )
                )
            )
        ).flat();
        const edgeContainments = edgeTypes.map(eT => getEdgeSpecOf(eT!)).filter(element => element !== undefined) as ElementType[];
        const allContainments = (nodeContainments as ElementType[]).concat(edgeContainments);
        return allContainments;
    }
    return [];
}

export function getDeepContainmentsOf(e: ModelElementContainer, seenContainments: ElementType[] = []): ElementType[] {
    let containments = getContainmentsOf(e);
    // containments of containments
    containments = Array.from(
        new Set(
            containments.concat(
                containments
                    .filter(c => ModelElementContainer.is(c) && !seenContainments.includes(c))
                    .map(c => getDeepContainmentsOf(c as ModelElementContainer, containments))
                    .flat()
            )
        )
    );
    return containments;
}

export function canBeCreated(containerType: string, containmentType: string): boolean {
    const containerSpec = getSpecOf(containerType);
    if (containerSpec === undefined || !ModelElementContainer.is(containerSpec)) {
        return false;
    }
    const containments = MetaSpecification.getCachedContainments(containerSpec);
    return (
        containments.length > 0 &&
        // tested type is either is a defined containment, or ...
        containments.filter((e: ElementType) => e.elementTypeId === containmentType).length > 0
    );
}

/**
 * Edges
 */

export function getEdgeTargetsForCaching(e: EdgeType): NodeType[] {
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

export function getEdgeSourcesForCaching(e: EdgeType): NodeType[] {
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

export function getEdgeSources(elementTypeId: string): NodeType[] {
    return MetaSpecification.getCachedEdgeSources(elementTypeId);
}

export function getEdgeTargets(elementTypeId: string): NodeType[] {
    return MetaSpecification.getCachedEdgeTargets(elementTypeId);
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
    return Array.from(MetaSpecification.getSpecMap().values());
}

export function getSpecOf(elementTypeId: string | undefined): ElementType | undefined {
    if (elementTypeId === undefined) {
        console.log('Specification not found for type: ' + elementTypeId);
        return undefined;
    }
    const spec = MetaSpecification.getSpecMap().get(elementTypeId);
    return spec;
}

export function getGraphSpecOf(elementTypeId: string): GraphType | undefined {
    const spec = getSpecOf(elementTypeId);
    return GraphType.is(spec) ? spec : undefined;
}

export function getNodeSpecOf(elementTypeId: string): NodeType | undefined {
    const spec = getSpecOf(elementTypeId);
    return NodeType.is(spec) ? spec : undefined;
}

export function getEdgeSpecOf(elementTypeId: string): EdgeType | undefined {
    const spec = getSpecOf(elementTypeId);
    return EdgeType.is(spec) ? spec : undefined;
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

export function isPrime(type: ElementType): boolean {
    return isPrimeReference(type);
}

export function isPrimeReference(type: ElementType | undefined): boolean {
    if (type && NodeType.is(type)) {
        if (type.primeReference !== undefined && type.primeReference.name && type.primeReference.type) {
            return true;
        }
    }
    return false;
}
