/********************************************************************************
 * Copyright (c) 2023 Cinco Cloud.
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
    AbstractPosition,
    AbstractShape,
    Alignment,
    Appearance,
    ConnectionDecorator,
    ContainerShape,
    Ellipse,
    HAlignment,
    Image,
    LineStyle,
    MultiText,
    Point,
    Polygon,
    Polyline,
    Rectangle,
    RoundedRectangle,
    Size,
    Text,
    VAlignment,
    WebView,
    getAppearanceByNameOf
} from '@cinco-glsp/cinco-glsp-common';
import {
    Bounds,
    IActionDispatcher,
    GLabel,
    GModelElement,
    boundsFeature,
    createFeatureSet,
    edgeLayoutFeature,
    editLabelFeature,
    fadeFeature,
    getSubType,
    layoutableChildFeature
} from '@eclipse-glsp/client';
import * as React from 'react';
import { JsxVNodeChild, VNode, VNodeStyle, h } from 'snabbdom';
import * as jsx from 'sprotty/lib/lib/jsx';
import * as uuid from 'uuid';
import { CincoEdge, CincoNode } from '../../model/model';
import { WorkspaceFileService } from '../../utils/workspace-file-service';

/**
 * HELPER-FUNCTIONS
 */

export const CSS_RESOURCE_BASE = '../../../languages/';
export const CSS_SHAPE_PREFIX = 'cc-shape-';
export const CSS_DECORATOR_PREFIX = 'cc-decorator-';
export const CSS_STYLE_PREFIX = 'cc-style-';
export const USE_MARGIN_FIX = true;

export const RESOURCE_MAP: Map<string, { location: string; url: string; content: string | undefined } | undefined> = new Map();

export function resolveChildrenRecursivly(currentNode: VNode): VNode[] {
    let children: VNode[] = [];
    if (currentNode.children && currentNode.children.length > 0) {
        children = children.concat(currentNode.children as VNode[]).filter(c => {
            const id = (c.data?.attrs?.class as string) ?? (c.data?.attrs?.className as string);
            return c.sel && id && id.indexOf(CSS_SHAPE_PREFIX) >= 0;
        }); // only those with selector
        for (const c of children as VNode[]) {
            children = children.concat(resolveChildrenRecursivly(c));
        }
        currentNode.children = currentNode.children.filter(c => typeof c === 'string' || !children.includes(c));
    }
    return children;
}

/**
 * Tries to parse the given path. It first searches inside the internal resources, i.e. languages folder.
 * If the path cannot be resolved to an existing resource, it will search inside the workspace folder for the
 * given resource. If a resource can not be resolved from there either, it will interpret the given path
 * as URL.
 * @param path relative path of the resource
 * @returns returns http-link to the resource
 */
export function fromPathToURL(
    path: string,
    workspaceFileService: WorkspaceFileService,
    options = {
        contentMode: false
    }
): string {
    const existsIn = workspaceFileService.servedExistsIn(path);
    existsIn.then(fileURIString => {
        if (!fileURIString) {
            // resource does not exist
            RESOURCE_MAP.delete(path);
        } else {
            if (options.contentMode || !RESOURCE_MAP.has(path)) {
                // resource is not yet served, serving it
                updateResourceServing(path, fileURIString, workspaceFileService, options);
            } else {
                if (RESOURCE_MAP.get(path)?.location !== fileURIString) {
                    // resource does not exist in same location anymore
                    RESOURCE_MAP.delete(path);
                }
            }
        }
    });
    if (options.contentMode) {
        // return current cached resource
        return `${RESOURCE_MAP.get(path)?.content}`;
    }
    // return current cached resource
    return `${RESOURCE_MAP.get(path)?.url}`;
}

function updateResourceServing(
    path: string,
    location: string,
    workspaceFileService: WorkspaceFileService,
    options = {
        contentMode: false
    }
): void {
    // search inside internal resource folder
    workspaceFileService.serveFile(path).then(servedLink => {
        if (servedLink === undefined) {
            // resource does not exist
            RESOURCE_MAP.delete(path);
        } else {
            if (options.contentMode) {
                workspaceFileService.download(servedLink).then(content => {
                    if (!RESOURCE_MAP.has(path) || (RESOURCE_MAP.has(path) && RESOURCE_MAP.get(path)?.content !== content)) {
                        RESOURCE_MAP.set(path, { location: location, url: `${servedLink}`, content: content ?? '' });
                    }
                });
            } else {
                RESOURCE_MAP.set(path, { location: location, url: `${servedLink}`, content: undefined });
            }
        }
    });
}

export function mergeAppearance(
    baseAppearance: Appearance | string | undefined,
    updatedAppearance: Appearance | string | undefined
): Appearance {
    let base: Appearance;
    let update: Appearance;
    if (typeof baseAppearance == 'string') {
        base = { ...(getAppearanceByNameOf(baseAppearance) as Appearance) };
    } else {
        base = { ...baseAppearance };
    }
    if (typeof updatedAppearance == 'string') {
        update = { ...(getAppearanceByNameOf(updatedAppearance) as Appearance) };
    } else {
        update = { ...updatedAppearance };
    }
    if (!updatedAppearance) {
        return base;
    }
    if (!baseAppearance) {
        return update;
    }
    return Object.assign(base, update);
}

/**
 * Translates the meta-specified appearance object into a react compatible style object.
 * @param appearance the appearance that styles the shape of the node
 * @param shape the shape that styles the node (containing an appearance object).
 * @param options additional fallback-options to style a shape
 * @returns the react compatible style object
 */
export function appearanceToStyle(appearance: Appearance | string | undefined, options?: any): VNodeStyle {
    const style: any = {};
    if (typeof appearance == 'string') {
        appearance = getAppearanceByNameOf(appearance) as Appearance;
    }

    // foreground, background, filled
    let background;
    let foreground;
    if (options.isText) {
        background = getProperty(appearance, a => a.foreground) ?? options?.background;
        foreground = getProperty(appearance, a => a.background) ?? options?.foreground;
    } else {
        foreground = getProperty(appearance, a => a.foreground) ?? options?.foreground;
        background = getProperty(appearance, a => a.background) ?? options?.background;
    }

    const filled = getProperty(appearance, a => a.filled) ?? options?.filled;
    const borderColor = foreground ?? background;
    const fillColor = filled ? foreground ?? background : background;
    if (!options.isEdge) {
        /*
         * edges are discrimnated, because 'fill' breaks the
         * selection mechanism for edges of the GLSP. If 'fill' is set,
         * edges can cover other edges. Those edges can subsequently not be selected.
         */
        style['fill'] =
            options.isEdge && !background
                ? `rgba(${fillColor?.r ?? 0},${fillColor?.g ?? 0},${fillColor?.b ?? 0}, 0)`
                : `rgb(${fillColor?.r ?? 0},${fillColor?.g ?? 0},${fillColor?.b ?? 0})`;
        style['background-color'] = `rgb(${background?.r ?? 0},${background?.g ?? 0},${background?.b ?? 0})`;
    }

    // font
    const fontName =
        getProperty(appearance, a => a.font?.fontName) ?? options?.fontName ?? '"Helvetica Neue", Helvetica, Arial, sans-serif';
    const bold = getProperty(appearance, a => a.font?.isBold) ?? options?.isBold ? 'bold' : 'normal';
    const italic = getProperty(appearance, a => a.font?.isItalic) ?? options?.isItalic ? 'italic' : 'normal';
    const fontSize = getProperty(appearance, a => a.font?.size) ?? options?.size ?? 10;
    style['font-family'] = `${fontName}`;
    style['font-style'] = `${bold}`;
    style['font-weight'] = ` ${italic}`;
    style['font-size'] = `${fontSize ?? 10}px`;

    // lineStyle, lineWidth, foreground if not filled
    const lineStyle = getProperty(appearance, a => a.lineStyle) ?? options?.lineStyle ?? LineStyle.SOLID;
    const lineWidth = getProperty(appearance, a => a.lineWidth) ?? options?.lineWidth ?? 0.0;
    style['strokeWidth'] = `${lineWidth}`;
    style['stroke'] = `rgb(${borderColor?.r ?? 0},${borderColor?.g ?? 0},${borderColor?.b ?? 0})`;
    if (lineStyle) {
        const doubleLineWidth = lineWidth * 2;
        switch (lineStyle) {
            case LineStyle.DASH:
                style['strokeDasharray'] = `${doubleLineWidth} ${doubleLineWidth}`;
                break;

            case LineStyle.DOT:
                style['strokeDasharray'] = `1 ${doubleLineWidth}`;
                break;

            case LineStyle.SOLID:
                style['strokeDasharray'] = '0';
                break;

            case LineStyle.DASHDOT:
                style['strokeDasharray'] = `1 ${doubleLineWidth} ${doubleLineWidth}`;
                break;

            case LineStyle.DASHDOTDOT:
                style['strokeDasharray'] = `${doubleLineWidth} ${doubleLineWidth} 1 ${doubleLineWidth} 1`;
                break;
        }
    } else {
        // default `solid`
        style['strokeDasharray'] = '0';
    }
    // Decide between, e.g. Rectangle and RoundedRectangle
    style['strokeLinecap'] = 'round';
    if (!options.strokeRound) {
        style['strokeLinejoin'] = 'miter';
    } else {
        style['strokeLinejoin'] = 'round';
    }

    // transparency
    const transparency = getProperty(appearance, a => a.transparency) ?? options?.transparency;
    if (!options.isEdge || transparency !== undefined) {
        /*
         * edges are discrimnated, because the transparency effect is used
         * by the glsp for hovering over edges, for selection.
         */
        style['opacity'] = `${transparency ?? 1.0}`;
    }

    return style as VNodeStyle;
}

/**
 * @param text the text to measure the width of
 * @param style the style object, that contains the font information
 * @returns the width
 */
export function getTextWidth(text: string, style: any): number {
    const font = `${style['font-style']} ${style['font-weight']} ${style['font-size']} ${style['font-family']}`;
    const canvas = document.createElement('canvas');
    const context = canvas.getContext('2d');
    if (!context) {
        return 0;
    }
    context.font = font || getComputedStyle(document.body).font;
    return context.measureText(text).width;
}
/**
 * Returns property of appearance, in respect to polymorphy.
 * @param app appearance of the element specified by the meta-specification
 * @param getter a getter to receive the property from an apparance object (since it can have a parent)
 * @returns the property to get by the getter
 */
export function getProperty<T>(app: Appearance | undefined, getter: (a: Appearance) => T): T | undefined {
    if (!app) {
        return undefined;
    }
    const result = getter(app);
    if (result) {
        return result;
    } else {
        let parent: string | Appearance | undefined = app.parent;
        if (typeof parent === 'string') {
            parent = getAppearanceByNameOf(parent);
        }
        if (parent) {
            return getProperty(parent, getter);
        }
    }
    return undefined;
}

/**
 * Translates a meta-specified size to a react compatible size object.
 * @param shapeSize size of the element specified by the meta-specification
 * @param parentScale scale of the parent shape, that will be applied
 * @returns
 */
export function translateSize(shapeSize: Size, parentScale: Point): Size {
    const widthFixed = shapeSize.widthFixed ?? false;
    const heightFixed = shapeSize.heightFixed ?? false;
    const width = widthFixed ? shapeSize.width : shapeSize.width * parentScale.x;
    const height = heightFixed ? shapeSize.height : shapeSize.height * parentScale.y;
    return { width: width, height: height };
}

/**
 * @param route the points that connect to an edge
 * @param location the position from 0.0 to 1.0 on the edge, where 0.0 is the base/starting point of the edge and 1.0 the tail/target point.
 * @returns a calculated point on the Canvas, that is the point on the edge with given location
 *          and the real start and target point that surround it. If the route has no points it returns undefined.
 */
export function calculateEdgeLocation(route: Point[], location: number): { point: Point; start: Point; target: Point } | undefined {
    if (route.length <= 0) {
        return undefined;
    }
    // normalize location
    location = location < 0 ? 0.0 : location > 1.0 ? 1.0 : location;
    // base for next iteration
    let currentPoint = route[0];
    let currentDistance = 0;
    // initialize working set
    const distances: { start: number; end: number; startPoint: Point; targetPoint: Point }[] = [];
    let result: { point: Point; start: Point; target: Point } = {
        point: currentPoint,
        start: currentPoint,
        target: currentPoint
    };
    for (const target of route) {
        const distance = Math.hypot(target.x - currentPoint.x, target.y - currentPoint.y);
        const nextDistance = currentDistance + distance;
        distances.push({ start: currentDistance, end: nextDistance, startPoint: currentPoint, targetPoint: target });
        // base for next iteration
        currentDistance = nextDistance;
        currentPoint = target;
    }
    // search location
    const searchedLocation = currentDistance * location;
    for (const distance of distances) {
        if (distance.start < searchedLocation && searchedLocation <= distance.end) {
            // location is in intervall, calculate new Position
            const relativeDistance = (searchedLocation - distance.start) / (distance.end - distance.start);
            const vector = {
                x: distance.targetPoint.x - distance.startPoint.x,
                y: distance.targetPoint.y - distance.startPoint.y
            };
            const vectorPosition = {
                x: vector.x * relativeDistance,
                y: vector.y * relativeDistance
            };
            result = {
                point: {
                    x: distance.startPoint.x + vectorPosition.x,
                    y: distance.startPoint.y + vectorPosition.y
                },
                start: distance.startPoint,
                target: distance.targetPoint
            };
        }
    }
    return result;
}

/**
 * Translates a cinco AbstractPosition to a react compatible position object.
 * @param position position of the element specified by the meta-specification
 * @param localSize the size of the element, whichs position will be translated
 * @param localCentered decides if the element has its coordiante-base in it's center
 * @param parentSize the size of the parent of the element, whichs position will be translated
 * @param parentCentered decides if the parent of the element has its coordiante-base in it's center
 * @param fallback the position to fallback to, if the position is unknown or undefined
 * @returns the translated position as a Point
 */
export function translatePosition(
    position: AbstractPosition | undefined,
    localSize: Size,
    localCentered: boolean,
    parentSize: Size,
    parentCentered: boolean,
    fallback: Point
): Point {
    let cx = 0;
    let cy = 0;
    if (AbsolutePosition.is(position)) {
        cx += position?.xPos ? position.xPos : 0;
        cy += position?.yPos ? position.yPos : 0;
    } else if (Alignment.is(position)) {
        const hAlignment = position.horizontal;
        const vAlignment = position.vertical;
        // x
        switch (hAlignment) {
            case HAlignment.LEFT:
                cx = (parentCentered ? 0.0 - parentSize.width / 2.0 : 0.0) + (localCentered ? localSize.width / 2.0 : 0.0);
                break;
            case HAlignment.CENTER:
                cx = (parentCentered ? 0.0 : parentSize.width / 2.0) + (localCentered ? 0.0 : 0.0 - localSize.width / 2);
                break;
            case HAlignment.RIGHT:
                cx =
                    (parentCentered ? parentSize.width / 2.0 : parentSize.width) +
                    (localCentered ? 0.0 - localSize.width / 2.0 : 0.0 - localSize.width);
                break;
        }
        // y
        switch (vAlignment) {
            case VAlignment.TOP:
                cy = (parentCentered ? 0.0 - parentSize.height / 2.0 : 0.0) + (localCentered ? localSize.height / 2.0 : 0.0);
                break;
            case VAlignment.MIDDLE:
                cy = (parentCentered ? 0.0 : parentSize.height / 2.0) + (localCentered ? 0.0 : 0.0 - localSize.height / 2);
                break;
            case VAlignment.BOTTOM:
                cy =
                    (parentCentered ? parentSize.height / 2.0 : parentSize.height) +
                    (localCentered ? 0.0 - localSize.height / 2.0 : 0.0 - localSize.width);
                break;
        }
    } else {
        cx = fallback.x;
        cy = fallback.y;
    }
    return { x: cx, y: cy };
}

/**
 * Returns the specified margin of the position.
 * @param position
 * @returns
 */
export function getMargin(position: AbstractPosition | undefined): Point {
    if (Alignment.is(position)) {
        return { x: position.xMargin ?? 0, y: position.yMargin ?? 0 };
    }
    return { x: 0, y: 0 };
}

/**
 * Calculates the radius from a given width and height.
 * @param width
 * @param height
 * @returns
 */
export function getRadius(width: number, height: number): number {
    const d = Math.min(width, height);
    return d > 0 ? d / 2 : 0;
}

/**
 * Resolves the value of the following `text-shape`, by injecting indexed parameters for `%1$s` and `%2$s` or "%s" and "%s":
 * text {
 *		appearance labelFont
 *		position (CENTER, TOP 4)
 *		value "%1$s :%2$s" // or: value "%s :%s"
 *	}
 *
 * @param e  the element containing the properties used as parameters.
 * @param text the value of the text-shape.
 * @param parameterCount the number of parameters that will be replaced
 * @returns
 */
export function resolveText(e: CincoNode | CincoEdge, text: string, parameterCount: number): string {
    const result = resolveTextIterative(e, text);
    return resolveTextIndexed(e, result, parameterCount);
}

/**
 * Inside the string 'text' use '{{<any property name>>}}', e.g.:
 * A CincoNode has the property name with the value 'Peter'. If a webview is used containing the following string:
 * "Put here the name: {{name}}". That string will resolve to: "Put here the name: Peter"
 *
 * @param node  the node containing the properties used as parameters.
 * @param text that can contain substrings of the regex-form '{{(\w\.)+}}'.
 * @returns returns the resolved string
 */
export function resolveTextByProperties(node: CincoNode | CincoEdge, text: string): string {
    let result: string = text;
    const placeholderPattern = /({{)((\w|\.)+)(}})/g;
    let currentFind;
    while ((currentFind = placeholderPattern.exec(result))) {
        const startIndex = currentFind.index;
        const endIndex = startIndex + currentFind[0].length;
        const prefix = result.substring(0, startIndex);
        const postfix = result.substring(endIndex, result.length);
        const parameterText = currentFind[2];
        let property = '';
        if (parameterText) {
            // resolve parameter from attribute
            property = resolveAttribute(node, parameterText);
        }
        result = prefix + property + postfix;
    }
    return result;
}

/**
 * Resolves the value of the following `text-shape`, by injecting parameters for `%s` and `%s`:
 * text {
 *		appearance labelFont
 *		position (CENTER, TOP 4)
 *		value "%s :%s"
 *	}
 *
 * @param node  the node containing the properties used as parameters.
 * @param text the value of the text-shape.
 * @returns returns the resolved string
 */
export function resolveTextIterative(node: CincoNode | CincoEdge, text: string): string {
    let result: string = text;
    const iterativePlaceholderPattern = /%s/g;
    const styleParameters: string[] | undefined = node.view?.styleParameter;
    if (styleParameters) {
        let currentFind;
        let currentIndex = 0;
        while ((currentFind = iterativePlaceholderPattern.exec(result))) {
            const startIndex = currentFind.index;
            const endIndex = startIndex + currentFind.length + 1;
            const prefix = result.substring(0, startIndex);
            const postfix = result.substring(endIndex, result.length);
            const parameterText = styleParameters[currentIndex];
            let property = '';
            if (parameterText) {
                // resolve parameter from attribute
                property = resolveParameter(node, parameterText);
            }
            result = prefix + property + postfix;
            currentIndex += 1;
        }
    }
    return result;
}

/**
 * Resolves the value of the following `text-shape`, by injecting indexed parameters for `%1$s` and `%2$s`:
 * text {
 *		appearance labelFont
 *		position (CENTER, TOP 4)
 *		value "%1$s :%2$s"
 *	}
 *
 * @param node  the node containing the properties used as parameters.
 * @param text the value of the text-shape.
 * @param parameterCount the number of parameters that will be replaced
 * @returns
 */
export function resolveTextIndexed(node: CincoNode | CincoEdge, text: string, parameterCount: number): string {
    let result: string = text;
    const styleParameters: string[] | undefined = node.view?.styleParameter;
    for (let i = 0; i < parameterCount; i++) {
        let currentFind;
        const indexedPlaceholderPattern = RegExp('(%(' + (i + 1) + ')(\\$s))', 'g');
        do {
            currentFind = indexedPlaceholderPattern.exec(result);
            if (currentFind) {
                const startIndex = currentFind.index;
                const endIndex = startIndex + currentFind[0].length;
                const prefix = result.substring(0, startIndex);
                const postfix = result.substring(endIndex, result.length);
                const foundIndex = Number.parseInt(currentFind[2], 10) - 1;
                const parameterText = styleParameters?.at(foundIndex);
                let property = '';
                if (parameterText) {
                    // resolve parameter from attribute
                    property = resolveParameter(node, parameterText);
                }
                result = prefix + property + postfix;
            }
        } while (currentFind);
    }
    return result;
}

export function resolveParameter(node: CincoNode | CincoEdge, parameter: string): string {
    const parameterPattern = RegExp('(\\$\\{(.*)\\})', 'g');
    let result: string = parameter;
    let currentFind;
    do {
        currentFind = parameterPattern.exec(result);
        if (currentFind) {
            const startIndex = currentFind.index;
            const endIndex = startIndex + currentFind[0].length;
            const prefix = result.substring(0, startIndex);
            const postfix = result.substring(endIndex, result.length);
            const attributeName = currentFind[2];
            if (attributeName) {
                // resolve parameter from attribute
                result = prefix + resolveAttribute(node, attributeName) + postfix;
            }
        }
    } while (currentFind);
    return result;
}

export function resolveAttribute(node: CincoNode | CincoEdge, attributeName: string): string {
    if (attributeName === 'id') {
        return node.id;
    }
    if (attributeName === 'type') {
        return node.type;
    }
    if (attributeName === 'specification') {
        return JSON.stringify({ ...node.specification });
    }
    if (attributeName === 'size.width') {
        return node.size.width;
    }
    if (attributeName === 'size.height') {
        return node.size.height;
    }
    const properties = node.properties;
    return properties ? properties[attributeName] ?? '' : '';
}

/**
 * Since the react components don't apply margin on the shape of children,
 * this function will help to apply margin on the current x and y position,
 * but only in repsect to parent, not siblings.
 * @param position position specified by the meta-specification
 * @param cx current x position
 * @param cy current y position
 * @param margin x-margin and y-margin to apply
 * @returns
 */
export function fixByApplyMargin(
    position: Alignment | AbstractPosition | AbsolutePosition | undefined,
    cx: number,
    cy: number,
    margin: Point
): { x: number; y: number } {
    let px = cx;
    let py = cy;
    if (Alignment.is(position)) {
        if (position.horizontal === HAlignment.LEFT) {
            px += margin.x;
        } else if (position.horizontal === HAlignment.RIGHT) {
            px -= margin.x;
        }
        if (position.vertical === VAlignment.TOP) {
            py += margin.y;
        } else if (position.vertical === VAlignment.BOTTOM) {
            py -= margin.y;
        }
    } else {
        px += margin.x;
        py += margin.y;
    }
    return { x: px, y: py };
}

export function calculatePointSetSize(points: Point[], scale: Point): Size {
    const left = points.filter(p1 => points.every(p2 => p1.x <= p2.x));
    const right = points.filter(p1 => points.every(p2 => p1.x >= p2.x));
    const bottom = points.filter(p1 => points.every(p2 => p1.y <= p2.y));
    const top = points.filter(p1 => points.every(p2 => p1.y >= p2.y));
    const width = right[0].x - left[0].x;
    const height = top[0].y - bottom[0].y;
    return { width: width * (scale ? scale.x : 1), height: height * (scale ? scale.y : 1) };
}

export function toCSSShapeName(shapeStyle: AbstractShape): string {
    return fromStringToCSSShapeName(shapeStyle.name ?? shapeStyle.type);
}

export function fromStringToCSSShapeName(shapeName: string): string {
    return `${CSS_SHAPE_PREFIX}` + shapeName;
}

export function toCSSDecoratorName(decorator: ConnectionDecorator): string {
    return `${CSS_DECORATOR_PREFIX}` + (decorator.name ?? 'decorator');
}

export function normalizeDecoratorLocation(location: number): number {
    return location > 1.0 ? 1.0 : location < 0.0 ? 0.0 : location;
}

/**
 * BUILD SHAPE
 */

/**
 * @param element the element object.
 * @param shapeStyle the shape that styles the element (containing an appearance object).
 * @param parentSize Width and height of the parent shape
 * @param parentScale scale of width and height of the parent shape affected by the modifiable bounds of the element
 * @param parentPosition absolute position of parent shape inside the element
 * @param parentCentered is the base of the parent in the center of the shape
 *                      (e.g. ellipse it is centered, rectangle it is the upper left corner)
 * @param parameterCount the number of parameters that are rendered from the element onto the shape.
 * @returns a react compatible and msl styled VNode, that corresponds to a container shape.
 */
export function buildShape(
    element: CincoNode | CincoEdge,
    shapeStyle: AbstractShape | undefined,
    parentSize: Size,
    parentScale: Point,
    parentPosition: Point | undefined,
    parentCentered: boolean,
    parameterCount: number,
    workspaceFileService: WorkspaceFileService
): VNode | undefined {
    if (Text.is(shapeStyle)) {
        return buildTextShape(element, shapeStyle, parentSize, parentPosition ?? { x: 0, y: 0 }, parentCentered, parameterCount);
    } else if (MultiText.is(shapeStyle)) {
        return buildMultiTextShape(element, shapeStyle, parentSize, parentPosition ?? { x: 0, y: 0 }, parentCentered, parameterCount);
    } else if (Image.is(shapeStyle)) {
        return buildImageShape(
            element,
            shapeStyle,
            parentSize,
            parentScale,
            parentPosition ?? { x: 0, y: 0 },
            parentCentered,
            parameterCount,
            workspaceFileService
        );
    } else if (WebView.is(shapeStyle)) {
        return buildWebviewShape(
            element,
            shapeStyle,
            parentSize,
            parentScale,
            parentPosition ?? { x: 0, y: 0 },
            parentCentered,
            parameterCount,
            workspaceFileService
        );
    } else if (Polyline.is(shapeStyle)) {
        return buildPolylineShape(shapeStyle, parentSize, parentScale, parentPosition);
    } else if (Rectangle.is(shapeStyle)) {
        return buildContainerShape(
            element,
            shapeStyle,
            parentSize,
            parentScale,
            parentPosition ?? { x: 0, y: 0 },
            parentCentered,
            parameterCount,
            workspaceFileService
        );
    } else if (RoundedRectangle.is(shapeStyle)) {
        return buildContainerShape(
            element,
            shapeStyle,
            parentSize,
            parentScale,
            parentPosition ?? { x: 0, y: 0 },
            parentCentered,
            parameterCount,
            workspaceFileService
        );
    } else if (Ellipse.is(shapeStyle)) {
        return buildContainerShape(
            element,
            shapeStyle,
            parentSize,
            parentScale,
            parentPosition ?? { x: 0, y: 0 },
            parentCentered,
            parameterCount,
            workspaceFileService
        );
    } else if (Polygon.is(shapeStyle)) {
        return buildContainerShape(
            element,
            shapeStyle,
            parentSize,
            parentScale,
            parentPosition ?? { x: 0, y: 0 },
            parentCentered,
            parameterCount,
            workspaceFileService
        );
    } else {
        return buildDefaultShape(element);
    }
}

/**
 * @param element the element object.
 */
export function buildDefaultShape(element: CincoNode | CincoEdge): VNode {
    return createRectangleShape(undefined, element.cssClasses?.join(' ') ?? fromStringToCSSShapeName('default'), element.bounds, {
        x: 0,
        y: 0
    });
}

/**
 * @param element the element object.
 * @param shapeStyle the shape that styles the element (containing an appearance object).
 * @param parentSize Width and height of the parent shape
 * @param parentPosition absolute position of parent shape inside the element
 * @param parentCentered is the base of the parent in the center of the shape
 *                      (e.g. ellipse it is centered, rectangle it is the upper left corner)
 * @param parameterCount the number of parameters that are rendered from the element onto the shape.
 * @returns a react compatible and msl styled VNode, that corresponds to an image shape.
 */
export function buildTextShape(
    element: CincoNode | CincoEdge,
    shapeStyle: Text,
    parentSize: Size,
    parentPosition: Point,
    parentCentered: boolean,
    parameterCount: number
): VNode | undefined {
    // css reference by shapeName
    const cssShapeName = toCSSShapeName(shapeStyle);

    // appearance to style
    let appearance = shapeStyle.appearance;
    if (typeof appearance == 'string') {
        appearance = getAppearanceByNameOf(appearance);
    }
    const style = appearanceToStyle(appearance, {
        lineWidth: 0.0,
        background: { r: 255, g: 255, b: 255 },
        foreground: { r: 0, g: 0, b: 0 },
        isText: true
    });
    const value = resolveText(element, shapeStyle.value, parameterCount);
    const measuredWidth = getTextWidth(value, style);
    const fontSize = appearance?.font?.size ?? 10;

    // position
    const localSize = { width: measuredWidth, height: fontSize };
    const localCentered = true;
    const relativeBasePosition = translatePosition(shapeStyle.position, localSize, localCentered, parentSize, parentCentered, {
        x: localCentered ? parentSize.width / 2 : 0,
        y: localCentered ? parentSize.height / 2 : 0
    });
    // apply margin
    if (style) {
        const position = shapeStyle.position;
        const margin = getMargin(position);
        // currently fix margin by position
        if (USE_MARGIN_FIX) {
            const marginedPosition = fixByApplyMargin(position, relativeBasePosition.x, relativeBasePosition.y, margin);
            relativeBasePosition.x = marginedPosition.x;
            relativeBasePosition.y = marginedPosition.y;
        } else {
            if (margin.x) {
                style['marginLeft'] = `${margin.x}px`;
                style['marginRight'] = `${margin.x}px`;
            }
            if (margin.y) {
                style['marginTop'] = `${margin.y}px`;
                style['marginBottom'] = `${margin.y}px`;
            }
        }
    }
    const localPosition = { x: parentPosition.x + relativeBasePosition.x, y: parentPosition.y + relativeBasePosition.y };

    // setup shape
    const label = createLabel(element, value, localPosition as Bounds);
    const subType = getSubType(label);

    return createTextShape(style, `${cssShapeName} ${subType}`, label.id, localPosition, fontSize, value) as unknown as VNode;
}

/**
 * @param element the element object.
 * @param shapeStyle the shape that styles the element (containing an appearance object).
 * @param parentSize Width and height of the parent shape
 * @param parentPosition absolute position of parent shape inside the element
 * @param parentCentered is the base of the parent in the center of the shape
 *                      (e.g. ellipse it is centered, rectangle it is the upper left corner)
 * @param parameterCount the number of parameters that are rendered from the element onto the shape.
 * @returns a react compatible and msl styled VNode, that corresponds to an image shape.
 */
export function buildMultiTextShape(
    element: CincoNode | CincoEdge,
    shapeStyle: MultiText,
    parentSize: Size,
    parentPosition: Point,
    parentCentered: boolean,
    parameterCount: number
): VNode | undefined {
    // css reference by shapeName
    const cssShapeName = toCSSShapeName(shapeStyle);

    // appearance to style
    const style = appearanceToStyle(shapeStyle.appearance, {
        lineWidth: 0.0,
        background: { r: 255, g: 255, b: 255 },
        foreground: { r: 0, g: 0, b: 0 },
        isText: true
    });

    // position
    const localSize = parentSize;
    const localCentered = false;
    const relativeBasePosition = translatePosition(shapeStyle.position, localSize, localCentered, parentSize, parentCentered, {
        x: localCentered ? parentSize.width / 2 : 0,
        y: localCentered ? parentSize.height / 2 : 0
    });
    // apply margin
    if (style) {
        const position = shapeStyle.position;
        const margin = getMargin(position);
        // currently fix margin by position
        if (USE_MARGIN_FIX) {
            const marginedPosition = fixByApplyMargin(position, relativeBasePosition.x, relativeBasePosition.y, margin);
            relativeBasePosition.x = marginedPosition.x;
            relativeBasePosition.y = marginedPosition.y;
        } else {
            if (margin.x) {
                style['marginLeft'] = `${margin.x}px`;
                style['marginRight'] = `${margin.x}px`;
            }
            if (margin.y) {
                style['marginTop'] = `${margin.y}px`;
                style['marginBottom'] = `${margin.y}px`;
            }
        }
    }
    const localPosition = { x: parentPosition.x + relativeBasePosition.x, y: parentPosition.y + relativeBasePosition.y };

    // setup shape
    const value = resolveText(element, shapeStyle.value, parameterCount);
    const shape = createMultiTextShape(
        style,
        cssShapeName,
        parentSize,
        localPosition,
        value,
        shapeStyle.editable,
        shapeStyle.scrollable,
        shapeStyle.name
    );
    return shape;
}

/**
 * @param element the element object.
 * @param shapeStyle the shape that styles the element (containing an appearance object).
 * @param parentSize Width and height of the parent shape
 * @param parentScale scale of width and height of the parent shape affected by the modifiable bounds of the element
 * @param parentPosition absolute position of parent shape inside the element
 * @param parentCentered is the base of the parent in the center of the shape
 *                      (e.g. ellipse it is centered, rectangle it is the upper left corner)
 * @param parameterCount the number of parameters that are rendered from the element onto the shape.
 * @returns a react compatible and msl styled VNode, that corresponds to an image shape.
 */
export function buildImageShape(
    element: CincoNode | CincoEdge,
    shapeStyle: Image,
    parentSize: Size,
    parentScale: Point,
    parentPosition: Point,
    parentCentered: boolean,
    parameterCount: number,
    workspaceFileService: WorkspaceFileService
): VNode | undefined {
    // css reference by shapeName
    const cssShapeName = toCSSShapeName(shapeStyle);

    // size
    const localSize = translateSize(shapeStyle.size, parentScale);

    // position
    const localCentered = false;
    const relativeBasePosition = translatePosition(shapeStyle.position, localSize, localCentered, parentSize, parentCentered, {
        x: localCentered ? parentSize.width / 2 : 0,
        y: localCentered ? parentSize.height / 2 : 0
    });
    // apply margin
    const position = shapeStyle.position;
    const margin = getMargin(position);
    // currently fix margin by position
    if (USE_MARGIN_FIX) {
        const marginedPosition = fixByApplyMargin(position, relativeBasePosition.x, relativeBasePosition.y, margin);
        relativeBasePosition.x = marginedPosition.x;
        relativeBasePosition.y = marginedPosition.y;
    }
    const localPosition = { x: parentPosition.x + relativeBasePosition.x, y: parentPosition.y + relativeBasePosition.y };

    // setup shape
    const imagePath = resolveText(element, shapeStyle.path ?? '', parameterCount);
    const shape = createImageShape(imagePath, cssShapeName, localSize, localPosition, workspaceFileService);

    return shape;
}

/**
 * @param element the element object.
 * @param shapeStyle the shape that styles the element (containing an appearance object).
 * @param parentSize Width and height of the parent shape
 * @param parentScale scale of width and height of the parent shape affected by the modifiable bounds of the element
 * @param parentPosition absolute position of parent shape inside the element
 * @param parentCentered is the base of the parent in the center of the shape
 *                      (e.g. ellipse it is centered, rectangle it is the upper left corner)
 * @param parameterCount the number of parameters that are rendered from the element onto the shape.
 * @returns a react compatible and msl styled VNode, that corresponds to an image shape.
 */
export function buildWebviewShape(
    element: CincoNode | CincoEdge,
    shapeStyle: WebView,
    parentSize: Size,
    parentScale: Point,
    parentPosition: Point,
    parentCentered: boolean,
    parameterCount: number,
    workspaceFileService: WorkspaceFileService
): VNode | undefined {
    // css reference by shapeName
    const cssShapeName = toCSSShapeName(shapeStyle);

    // size
    const localSize = translateSize(parentSize, parentScale);

    // position
    const localCentered = false;
    const relativeBasePosition = translatePosition(shapeStyle.position, localSize, localCentered, parentSize, parentCentered, {
        x: localCentered ? parentSize.width / 2 : 0,
        y: localCentered ? parentSize.height / 2 : 0
    });
    // apply margin
    const position = shapeStyle.position;
    const margin = getMargin(position);
    // currently fix margin by position
    if (USE_MARGIN_FIX) {
        const marginedPosition = fixByApplyMargin(position, relativeBasePosition.x, relativeBasePosition.y, margin);
        relativeBasePosition.x = marginedPosition.x;
        relativeBasePosition.y = marginedPosition.y;
    }
    const localPosition = { x: parentPosition.x + relativeBasePosition.x, y: parentPosition.y + relativeBasePosition.y };

    // setup shape
    const webviewContent = resolveText(element, shapeStyle.content ?? '', parameterCount);
    const shape = createWebviewShape(
        webviewContent,
        shapeStyle.padding ?? 5,
        shapeStyle.scrollable ?? false,
        cssShapeName,
        localSize,
        localPosition,
        workspaceFileService,
        element
    );

    return shape;
}

/**
 * @param shapeStyle the shape that styles the element (containing an appearance object).
 * @param parentSize Width and height of the parent shape
 * @param parentScale scale of width and height of the parent shape affected by the modifiable bounds of the element
 * @returns a react compatible and msl styled VNode, that corresponds to a polyline
 */
export function buildPolylineShape(shapeStyle: Polyline, parentSize: Size, parentScale: Point, offset?: Point): VNode | undefined {
    // css reference by shapeName
    const cssShapeName = toCSSShapeName(shapeStyle);

    // size
    const calcSize = translateSize(shapeStyle.size ?? { width: parentSize.width, height: parentSize.height }, parentScale);

    // appearance to style
    const style = appearanceToStyle(shapeStyle.appearance, { lineWidth: 1.0, isEdge: true });

    // setup shape
    return createPolylineShape(style, cssShapeName, shapeStyle.points, parentScale, calcSize ?? { width: 0, height: 0 }, offset);
}

/**
 * @param element the element object.
 * @param shapeStyle the shape that styles the element (containing an appearance object).
 * @param parentSize Width and height of the parent shape
 * @param parentScale scale of width and height of the parent shape affected by the modifiable bounds of the element
 * @param parentPosition absolute position of parent shape inside the element
 * @param parentCentered is the base of the parent in the center of the shape
 *                      (e.g. ellipse it is centered, rectangle it is the upper left corner)
 * @param parameterCount the number of parameters that are rendered from the element onto the shape.
 * @returns a react compatible and msl styled VNode, that corresponds to a container shape.
 */
export function buildContainerShape(
    element: CincoNode | CincoEdge,
    shapeStyle: ContainerShape,
    parentSize: Size,
    parentScale: Point,
    parentPosition: Point,
    parentCentered: boolean,
    parameterCount: number,
    workspaceFileService: WorkspaceFileService
): VNode {
    // css reference by shapeName
    const cssShapeName = toCSSShapeName(shapeStyle);

    // appearance to style
    const style = appearanceToStyle(shapeStyle.appearance, { strokeRound: !Rectangle.is(shapeStyle) });

    // size
    const localSize: Size =
        Polygon.is(shapeStyle) && shapeStyle.points.length > 0
            ? calculatePointSetSize(shapeStyle.points ?? [], parentScale)
            : translateSize(shapeStyle.size ?? parentSize, parentScale);

    // position
    const localCentered = Ellipse.is(shapeStyle) || Polygon.is(shapeStyle);
    const relativeBasePosition = translatePosition(shapeStyle.position, localSize, localCentered, parentSize, parentCentered, {
        x: localCentered ? parentSize.width / 2 : 0,
        y: localCentered ? parentSize.height / 2 : 0
    });
    // apply margin
    if (style) {
        const position = shapeStyle.position;
        const margin = getMargin(position);
        // currently fix margin by position
        if (USE_MARGIN_FIX) {
            const marginedPosition = fixByApplyMargin(position, relativeBasePosition.x, relativeBasePosition.y, margin);
            relativeBasePosition.x = marginedPosition.x;
            relativeBasePosition.y = marginedPosition.y;
        } else {
            if (margin.x) {
                style['marginLeft'] = `${margin.x}px`;
                style['marginRight'] = `${margin.x}px`;
            }
            if (margin.y) {
                style['marginTop'] = `${margin.y}px`;
                style['marginBottom'] = `${margin.y}px`;
            }
        }
    }
    const localPosition = { x: parentPosition.x + relativeBasePosition.x, y: parentPosition.y + relativeBasePosition.y };

    // calculate relations
    const localScale = {
        x: localSize.width / (shapeStyle.size?.width ?? localSize.width),
        y: localSize.height / (shapeStyle.size?.height ?? localSize.height)
    };

    // children shapes
    const children: VNode[] = [];
    const childrenShapes: AbstractShape[] = shapeStyle.children ?? [];
    childrenShapes.forEach(childShapeStyle => {
        const childShape = buildShape(
            element,
            childShapeStyle,
            localSize,
            localScale,
            localPosition,
            localCentered,
            parameterCount,
            workspaceFileService
        );
        if (childShape) {
            children.push(childShape);
        }
    });

    // setup shape
    let shape: VNode;
    if (Ellipse.is(shapeStyle)) {
        shape = createEllipseShape(style, cssShapeName, localSize, localPosition, children);
    } else if (Rectangle.is(shapeStyle)) {
        shape = createRectangleShape(style, cssShapeName, localSize, localPosition, children);
    } else if (RoundedRectangle.is(shapeStyle)) {
        shape = createRectangleShape(style, cssShapeName, localSize, localPosition, children);
    } else if (Polygon.is(shapeStyle)) {
        shape = createPolygonShape(style, cssShapeName, localSize, localPosition, shapeStyle.points, parentScale, children);
    } else {
        shape = buildDefaultShape(element);
    }
    return shape;
}

/**
 * HELPER-SHAPES
 */

export function createLabel(element: GModelElement, text: string, bounds: Bounds): GLabel {
    const label = new GLabel();
    label.text = text;
    label.bounds = bounds;
    label.type = 'label';
    label.features = createFeatureSet([
        boundsFeature,
        layoutableChildFeature,
        edgeLayoutFeature,
        fadeFeature,
        editLabelFeature
    ]);
    label.id = element.id + '_label_' + uuid.v4();
    return label;
}

/**
 * SHAPES
 */
export function createEllipseShape(
    style: VNodeStyle | undefined,
    cssShapeName: string,
    localSize: Size,
    localPosition: Point,
    children: VNode[]
): VNode {
    const result = createJSXElement(
        'ellipse',
        {
            rx: Math.max(localSize.width / 2.0, 0),
            ry: Math.max(localSize.height / 2.0, 0),
            cx: localPosition.x,
            cy: localPosition.y,
            style: style as React.CSSProperties
        },
        children
    ) as unknown as VNode;
    result.data!.attrs!['class'] = `${cssShapeName}`;
    return result;
}

export function createRectangleShape(
    style: VNodeStyle | undefined,
    cssShapeName: string,
    localSize: Size,
    localPosition: Point,
    children?: VNode[]
): VNode {
    const result = createJSXElement(
        'rect',
        {
            width: Math.max(localSize.width, 0),
            height: Math.max(localSize.height, 0),
            x: localPosition.x,
            y: localPosition.y,
            style: style as React.CSSProperties
        },
        children
    ) as unknown as VNode;
    result.data!.attrs!['class'] = `${cssShapeName}`;
    return result;
}

export function createPolygonShape(
    style: VNodeStyle | undefined,
    cssShapeName: string,
    localSize: Size,
    localPosition: Point,
    points: Point[],
    parentScale: Point = { x: 1.0, y: 1.0 },
    children?: VNode[]
): VNode {
    const shapePoints = points
        .map(p => {
            const x = localPosition.x + p.x * parentScale.x;
            const y = localPosition.y + p.y * parentScale.y;
            return `${x},${y}`;
        })
        .join(' ');
    const result = createJSXElement(
        'polygon',
        {
            width: Math.max(localSize.width, 0),
            height: Math.max(localSize.height, 0),
            style: style as React.CSSProperties,
            points: `${shapePoints}`
        },
        children
    ) as unknown as VNode;
    result.data!.attrs!['class'] = `${cssShapeName}`;
    return result;
}

export function createPolylineShape(
    style: VNodeStyle | undefined,
    cssShapeName: string,
    points: Point[],
    parentScale: Point = { x: 1.0, y: 1.0 },
    localSize: Size,
    offset: Point = { x: 0.0, y: 0.0 }
): VNode {
    const shapePoints = points
        .map(p => {
            const x = offset.x + p.x * parentScale.x;
            const y = offset.y + p.y * parentScale.y;
            return `${x},${y}`;
        })
        .join(' ');
    const result = createJSXElement('polyline', {
        width: localSize.width,
        height: localSize.height,
        style: style as React.CSSProperties,
        points: `${shapePoints}`
    }) as unknown as VNode;
    result.data!.attrs!['class'] = `${cssShapeName}`;
    return result;
}

export function createImageShape(
    imagePath: string,
    cssShapeName: string,
    localSize: Size,
    localPosition: Point,
    workspaceFileService: WorkspaceFileService
): VNode {
    const childWidth = Math.max(localSize.width, 0);
    const childHeight = Math.max(localSize.height, 0);
    const uri = fromPathToURL(imagePath, workspaceFileService);
    const child = createJSXElement('image', {
        x: localPosition.x,
        y: localPosition.y,
        href: uri
    }) as unknown as VNode;
    child.data = child.data ?? {};
    child.data.attrs = child.data.attrs ?? {};
    child.data.style = child.data.style ?? {};
    child.data.attrs['width'] = `${childWidth}px`;
    child.data.attrs['height'] = `${childHeight}px`;
    child.data!.attrs!['class'] = `${cssShapeName}`;
    return child;
}

export function createWebviewShape(
    webviewContent: string,
    padding: number,
    scrollable: boolean,
    cssShapeName: string,
    localSize: Size,
    localPosition: Point,
    workspaceFileService: WorkspaceFileService,
    e: CincoNode | CincoEdge
): VNode {
    const content = webviewContent.startsWith('<') || webviewContent.startsWith('{{') ? webviewContent : undefined;
    const foreignObject = createForeignObject(cssShapeName, localSize, localPosition, padding);
    let child;
    if (content) {
        const resolvedContent = resolveTextByProperties(e, content);
        child = convertHTMLToVNode(resolvedContent ?? '');
    } else {
        if (webviewContent.startsWith('http') || webviewContent.startsWith('https')) {
            // url reference
            const url = fromPathToURL(webviewContent, workspaceFileService);
            child = convertHTMLToVNode(
                url && url !== 'undefined'
                    ? `<iframe src="${url}" title="embedded link to: ${url}"
                        style="border: hidden; min-width: 100%; min-height: 100%;"></iframe>`
                    : 'undefined'
            ) as VNode;
            child!.data!.attrs!['style'] = 'border: hidden; width: 100%; height: 100%; min-height: 100%; min-width: 100%;';
        } else {
            // workspace reference
            const referencedContent = fromPathToURL(webviewContent, workspaceFileService, { contentMode: true });
            if (referencedContent && referencedContent !== 'undefined') {
                // resolve referenced properties
                const resolvedContent = resolveTextByProperties(e, referencedContent);
                // execute script tags afterwards
                child = convertHTMLToVNode('', 'iframe', {
                    border: 'hidden',
                    width: '100%',
                    height: '100%',
                    minheight: '100%',
                    minWidth: '100%'
                }) as VNode;
                child.data!.attrs!['style'] = 'border: hidden; width: 100%; height: 100%; min-height: 100%; min-width: 100%;';
                child.data!.attrs!['srcDoc'] = resolvedContent;
            } else {
                child = convertHTMLToVNode('undefined');
            }
        }
    }
    if (child) {
        if (typeof child !== 'string') {
            child.data = child.data ?? {};
            child.data.attrs = child.data.attrs ?? {};
            child.data.style = child.data.style ?? {};
            child.data.attrs.id = `${cssShapeName}_${e.id}`;
            child.data.style['overflow-y'] = scrollable === true ? 'scroll' : 'hidden';
            child.data.style['overflow-x'] = scrollable === true ? 'scroll' : 'hidden';

            const childWidth = Math.max(localSize.width, 0);
            const childHeight = Math.max(localSize.height, 0);
            child.data.attrs['width'] = childWidth;
            child.data.attrs['height'] = childHeight;
            child.data!.ns = 'http://www.w3.org/1999/xhtml';
        }
        foreignObject.children?.push(child);
    }
    return foreignObject;
}

function convertHTMLToVNode(htmlString: string, elementType = 'div', data?: any): VNode | string | undefined {
    const element = h(elementType ?? 'div', data);
    element.data = data !== undefined ? data : { props: { innerHTML: '' }, style: {}, attrs: {} };
    element.data!.attrs = element.data!.attrs ?? { ...{} };
    if (element.data) {
        element.data.props = element.data.props ?? { innerHTML: '' };
        element.data.style = element.data.style ?? { ...{} };
        element.data.props.innerHTML = htmlString;
    }
    return element;
}

export function createTextShape(
    style: VNodeStyle,
    cssShapeClasses: string,
    id: string,
    localPosition: Point,
    fontSize: number,
    text: string
): VNode {
    const result = createJSXElement(
        'text',
        {
            id: id,
            'class-sprotty-label': 'true',
            style: style as React.CSSProperties,
            x: `${localPosition.x}px`,
            y: `${localPosition.y}px`,
            dx: 0,
            dy: Math.max(fontSize / 3, 0)
        },
        text
    ) as unknown as VNode;
    result.data!.attrs!['class'] = `${cssShapeClasses}`;
    return result;
}

export function createMultiTextShape(
    style: VNodeStyle,
    cssShapeName: string,
    localSize: Size,
    localPosition: Point,
    content: string,
    editable: boolean | undefined,
    scrollable: boolean | undefined,
    placeholder: string | undefined
): VNode {
    const foreignObject = createForeignObject(cssShapeName, localSize, localPosition);
    const child = createJSXElement(
        'textarea',
        {
            readOnly: !editable,
            placeholder: placeholder,
            style: style as React.CSSProperties
        },
        content
    ) as unknown as VNode;
    const childWidth = Math.max(localSize.width - 6, 0); // 6 := border + padding
    const childHeight = Math.max(localSize.height - 6, 0); // 6 := border + padding
    child.data = child.data ?? {};
    child.data.attrs = child.data.attrs ?? {};
    child.data.style = child.data.style ?? {};
    child.data.style['overflow-y'] = scrollable === false ? 'hidden' : 'scroll';
    child.data!.ns = 'http://www.w3.org/1999/xhtml';
    child.data.attrs['placeholder'] = placeholder ?? '';
    child.data.style['width'] = `${childWidth}px`;
    child.data.style['height'] = `${childHeight}px`;
    child.data.style['max-width'] = `${childWidth}px`;
    child.data.style['max-height'] = `${childHeight}px`;
    foreignObject.children?.push(child);
    return foreignObject;
}

export function createForeignObject(cssShapeName: string, localSize: Size, localPosition: Point, padding = 10, content?: string): VNode {
    const foreignObject = createJSXElement(
        'foreignObject',
        {
            x: localPosition.x - padding,
            y: localPosition.y - padding
        },
        content
    ) as unknown as VNode;
    foreignObject.data!.attrs!['class'] = `${cssShapeName}`;
    foreignObject.data!.attrs!['style'] = `width: ${Math.max(localSize.width, 0) + padding * 2}; height: ${
        Math.max(localSize.height, 0) + padding * 2
    }; padding: ${padding}px`;
    return foreignObject;
}

function createJSXElement(type: string, props?: any | null, children?: VNode[] | JsxVNodeChild[] | string): VNode {
    return jsx.svg(type, props, (children ?? []) as unknown as JsxVNodeChild);
}

export function updatePalette(actionDispatcher: IActionDispatcher): void {
    const paletteUpdateAction = {
        kind: 'enableToolPalette'
    };
    actionDispatcher.dispatch(paletteUpdateAction);
}
