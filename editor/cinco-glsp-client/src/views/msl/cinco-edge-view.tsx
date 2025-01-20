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
    AbstractShape,
    Appearance,
    ConnectionDecorator,
    ConnectionType,
    DecoratorShape,
    EdgeStyle,
    EdgeView,
    GraphicsAlgorithm,
    PredefinedDecorator,
    Size,
    getAppearanceByNameOf
} from '@cinco-glsp/cinco-glsp-common';
import {
    AbstractEdgeRouter,
    BY_DESCENDING_X_THEN_DESCENDING_Y,
    BY_DESCENDING_X_THEN_Y,
    BY_X_THEN_DESCENDING_Y,
    BY_X_THEN_Y,
    EdgeRouterRegistry,
    IViewArgs,
    IntersectingRoutedPoint,
    Intersection,
    Point,
    PointToPointLine,
    RenderingContext,
    RoutableView,
    RoutedPoint,
    GRoutableElement,
    angleOfPoint,
    isIntersectingRoutedPoint,
    svg,
    toDegrees
} from '@eclipse-glsp/client';
import { inject, injectable } from 'inversify';
import { VNode, VNodeStyle } from 'snabbdom';
import { CincoEdge } from '../../model/model';
import { WorkspaceFileService } from '../../utils/workspace-file-service';
import {
    CSS_STYLE_PREFIX,
    appearanceToStyle,
    buildShape,
    calculateEdgeLocation,
    isUnknownEdgeType,
    mergeAppearance,
    normalizeDecoratorLocation,
    resolveChildrenRecursivly,
    toCSSDecoratorName
} from './cinco-view-helper';
import { UNKNOWN_DECORATOR_SIZE, UNKNOWN_ELEMENT_CSS, getUnknownEdgeShape } from './unknown-definitions';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const JSX = { createElement: svg };

enum LINE_CROSS_TYPE {
    GAP,
    JUMP,
    CROSS
}

enum LINE_TYPE {
    BEZIER,
    POLYLINE
}

abstract class MergedGLSPEdgeRenderingView extends RoutableView {
    @inject(EdgeRouterRegistry) edgeRouterRegistry: EdgeRouterRegistry;
    lineType: LINE_TYPE = LINE_TYPE.POLYLINE;
    lineCrossType: LINE_CROSS_TYPE = LINE_CROSS_TYPE.GAP;
    jumpingPolylineParameters = {
        jumpOffsetBefore: 5,
        jumpOffsetAfter: 5,
        skipOffsetBefore: 3,
        skipOffsetAfter: 2
    };
    gappedPolylineParamters = {
        skipOffsetBefore: 3,
        skipOffsetAfter: 3
    };

    /**
     * SWITCHES
     */

    protected renderLine(
        lineType: LINE_TYPE,
        lineCrossType: LINE_CROSS_TYPE,
        edge: CincoEdge,
        segments: Point[],
        context: RenderingContext,
        args?: IViewArgs
    ): VNode {
        switch (lineType) {
            case LINE_TYPE.BEZIER:
                return this.renderLineBezier(edge, segments, context, args);
            case LINE_TYPE.POLYLINE:
                return this.renderLinePolyline(lineCrossType, edge, segments, context, args);
            default:
                return this.renderLinePolyline(lineCrossType, edge, segments, context, args);
        }
    }

    protected renderAdditionals(
        lineType: LINE_TYPE,
        edge: CincoEdge,
        segments: Point[],
        context: RenderingContext,
        args?: IViewArgs
    ): VNode[] {
        switch (lineType) {
            case LINE_TYPE.BEZIER:
                return this.renderAdditionalsBezier(edge, segments, context);
            case LINE_TYPE.POLYLINE:
                return this.renderAdditionalsPolyline(edge, segments, context);
            default:
                return this.renderAdditionalsPolyline(edge, segments, context);
        }
    }

    protected renderDanglingEdge(message: string): VNode {
        return (
            <text class-sprotty-edge-dangling={true} xlinkTitle={message}>
                ?
            </text>
        ) as unknown as VNode;
    }

    /**
     * RENDER LINE
     */

    protected renderLinePolyline(
        lineCrossType: LINE_CROSS_TYPE,
        edge: CincoEdge,
        segments: Point[],
        context: RenderingContext,
        args?: IViewArgs
    ): VNode {
        switch (lineCrossType) {
            case LINE_CROSS_TYPE.CROSS:
                return this.renderLinePolylineCross(edge, segments, context, args);
            case LINE_CROSS_TYPE.GAP: // A gap is just a jump to the 4th dimension
                return this.renderLineEvadingPolyline(lineCrossType, edge, segments, context, args);
            case LINE_CROSS_TYPE.JUMP:
                return this.renderLineEvadingPolyline(lineCrossType, edge, segments, context, args);
        }
    }

    protected renderLinePolylineCross(edge: CincoEdge, segments: Point[], context: RenderingContext, args?: IViewArgs): VNode {
        const firstPoint = segments[0];
        let path = `M ${firstPoint.x},${firstPoint.y}`;
        for (let i = 1; i < segments.length; i++) {
            const p = segments[i];
            path += ` L ${p.x},${p.y}`;
        }
        return (<path d={path} />) as unknown as VNode;
    }

    private renderLineEvadingPolyline(
        lineCrossType: LINE_CROSS_TYPE,
        edge: CincoEdge,
        segments: Point[],
        context: RenderingContext,
        args?: IViewArgs
    ): VNode {
        let path = '';
        for (let i = 0; i < segments.length; i++) {
            const p = segments[i];
            if (i === 0) {
                path = `M ${p.x},${p.y}`;
            }
            if (isIntersectingRoutedPoint(p)) {
                path += this.intersectionPath(lineCrossType, edge, segments, p, args);
            }
            if (i !== 0) {
                path += ` L ${p.x},${p.y}`;
            }
        }
        return (<path d={path} />) as unknown as VNode;
    }

    protected renderLineBezier(edge: CincoEdge, segments: Point[], context: RenderingContext, args?: IViewArgs): VNode {
        /**
         * Example for two splines:
         * SVG:
         * <path d="M0,300 C0,150 300,150 300,300 S600,450 600,300" />
         *
         * Segments input layout:
         * bendPoints[0] = source;
         * bendPoints[1] = controlForSource;
         * bendPoints[2] = controlForSegment1;
         * bendPoints[3] = segment;
         * bendPoints[4] = controlForSegment2;
         * bendPoints[5] = controlForTarget;
         * bendPoints[6] = target;
         */
        let path = '';
        if (segments.length >= 4) {
            path += this.buildMainSegment(segments);
            const pointsLeft = segments.length - 4;
            if (pointsLeft > 0 && pointsLeft % 3 === 0) {
                for (let i = 4; i < segments.length; i += 3) {
                    path += this.addSpline(segments, i);
                }
            }
        }
        return (<path d={path} />) as unknown as VNode;
    }

    private buildMainSegment(segments: Point[]): string {
        const s = segments[0];
        const h1 = segments[1];
        const h2 = segments[2];
        const t = segments[3];
        return `M${s.x},${s.y} C${h1.x},${h1.y} ${h2.x},${h2.y} ${t.x},${t.y}`;
    }

    private addSpline(segments: Point[], index: number): string {
        // We have two controls for each junction, but SVG does not therefore index is jumped over
        const c = segments[index + 1];
        const p = segments[index + 2];
        return ` S${c.x},${c.y} ${p.x},${p.y}`;
    }

    /**
     * Returns a path that takes the intersections into account by drawing a line jump or a gap for intersections on that path.
     */
    protected intersectionPath(
        lineCrossType: LINE_CROSS_TYPE,
        edge: CincoEdge,
        segments: Point[],
        intersectingPoint: IntersectingRoutedPoint,
        args?: IViewArgs
    ): string {
        if (intersectingPoint.intersections.length < 1) {
            return '';
        }
        const segment = this.getLineSegment(edge, intersectingPoint.intersections[0], args, segments);
        const intersections = this.getIntersectionsSortedBySegmentDirection(segment, intersectingPoint) ?? [];
        let path = '';
        for (const intersection of intersections) {
            const otherLineSegment = this.getOtherLineSegment(edge, intersection, args);
            if (otherLineSegment === undefined) {
                continue;
            }
            const currentLineSegment = this.getLineSegment(edge, intersection, args, segments);
            const intersectionPoint = intersection.intersectionPoint;
            if (this.shouldDrawLineJumpOnIntersection(lineCrossType, currentLineSegment, otherLineSegment)) {
                path += this.createJumpPath(intersectionPoint, currentLineSegment);
            } else if (this.shouldDrawLineGapOnIntersection(lineCrossType, currentLineSegment, otherLineSegment)) {
                path += this.createGapPath(lineCrossType, intersectionPoint, currentLineSegment);
            }
        }
        return path;
    }

    protected getLineSegment(edge: GRoutableElement, intersection: Intersection, args?: IViewArgs, segments?: Point[]): PointToPointLine {
        const route = segments ? segments : this.edgeRouterRegistry.route(edge, args);
        const index = intersection.routable1 === edge.id ? intersection.segmentIndex1 : intersection.segmentIndex2;
        return new PointToPointLine(route[index], route[index + 1]);
    }

    /**
     * Returns the intersections sorted by the direction of the `lineSegment`.
     *
     * The coordinate system goes from left to right and top to bottom.
     * Thus, x increases to the right and y increases downwards.
     *
     * We need to draw the intersections in the order of the direction of the line segment.
     * To draw a line pointing north, we need to order intersections by Y in a descending order.
     * To draw a line pointing south, we need to order intersections by Y in an ascending order.
     */
    protected getIntersectionsSortedBySegmentDirection(
        lineSegment: PointToPointLine,
        intersectingPoint: IntersectingRoutedPoint
    ): Intersection[] {
        switch (lineSegment.direction) {
            case 'north':
            case 'north-east':
                return intersectingPoint.intersections.sort(BY_X_THEN_DESCENDING_Y);

            case 'south':
            case 'south-east':
            case 'east':
                return intersectingPoint.intersections.sort(BY_X_THEN_Y);

            case 'south-west':
            case 'west':
                return intersectingPoint.intersections.sort(BY_DESCENDING_X_THEN_Y);

            case 'north-west':
                return intersectingPoint.intersections.sort(BY_DESCENDING_X_THEN_DESCENDING_Y);
        }
    }

    protected getOtherLineSegment(currentEdge: CincoEdge, intersection: Intersection, args?: IViewArgs): PointToPointLine | undefined {
        const otherEdgeId = intersection.routable1 === currentEdge.id ? intersection.routable2 : intersection.routable1;
        const otherEdge = currentEdge.index.getById(otherEdgeId);
        if (!(otherEdge instanceof GRoutableElement)) {
            return undefined;
        }
        return this.getLineSegment(otherEdge, intersection, args);
    }

    private shouldDrawLineJumpOnIntersection(
        lineCrossType: LINE_CROSS_TYPE,
        currentLineSegment: PointToPointLine,
        otherLineSegment: PointToPointLine
    ): boolean {
        switch (lineCrossType) {
            case LINE_CROSS_TYPE.JUMP:
                return this.shouldDrawLineJumpOnIntersectionJumping(currentLineSegment, otherLineSegment);
            case LINE_CROSS_TYPE.GAP:
                return this.shouldDrawLineJumpOnIntersectionGapped(currentLineSegment, otherLineSegment);
            default:
                return this.shouldDrawLineJumpOnIntersectionJumping(currentLineSegment, otherLineSegment);
        }
    }

    protected shouldDrawLineGapOnIntersection(
        lineCrossType: LINE_CROSS_TYPE,
        currentLineSegment: PointToPointLine,
        otherLineSegment: PointToPointLine
    ): boolean {
        switch (lineCrossType) {
            case LINE_CROSS_TYPE.JUMP:
                return this.shouldDrawLineGapOnIntersectionJumping(currentLineSegment, otherLineSegment);
            case LINE_CROSS_TYPE.GAP:
                return this.shouldDrawLineGapOnIntersectionGapped(currentLineSegment, otherLineSegment);
            default:
                return this.shouldDrawLineGapOnIntersectionJumping(currentLineSegment, otherLineSegment);
        }
    }

    protected createGapPath(lineCrossType: LINE_CROSS_TYPE, intersectionPoint: Point, lineSegment: PointToPointLine): string {
        switch (lineCrossType) {
            case LINE_CROSS_TYPE.JUMP:
                return this.createGapPathJumping(intersectionPoint, lineSegment);
            case LINE_CROSS_TYPE.GAP:
                return this.createGapPathGapped(intersectionPoint, lineSegment);
            default:
                return this.createGapPathJumping(intersectionPoint, lineSegment);
        }
    }

    protected shouldDrawLineJumpOnIntersectionJumping(currentLineSegment: PointToPointLine, otherLineSegment: PointToPointLine): boolean {
        return Math.abs(currentLineSegment.slopeOrMax) < Math.abs(otherLineSegment.slopeOrMax);
    }

    private shouldDrawLineJumpOnIntersectionGapped(currentLineSegment: PointToPointLine, otherLineSegment: PointToPointLine): boolean {
        return false;
    }

    protected shouldDrawLineGapOnIntersectionJumping(currentLineSegment: PointToPointLine, otherLineSegment: PointToPointLine): boolean {
        return !this.shouldDrawLineJumpOnIntersectionJumping(currentLineSegment, otherLineSegment);
    }

    private shouldDrawLineGapOnIntersectionGapped(currentLineSegment: PointToPointLine, otherLineSegment: PointToPointLine): boolean {
        return Math.abs(currentLineSegment.slopeOrMax) >= Math.abs(otherLineSegment.slopeOrMax);
    }

    protected createJumpPath(intersectionPoint: Point, lineSegment: PointToPointLine): string {
        const anchorBefore = Point.shiftTowards(intersectionPoint, lineSegment.p1, this.jumpingPolylineParameters.jumpOffsetBefore);
        const anchorAfter = Point.shiftTowards(intersectionPoint, lineSegment.p2, this.jumpingPolylineParameters.jumpOffsetAfter);
        const rotation = lineSegment.p1.x < lineSegment.p2.x ? 1 : 0;
        return ` L ${anchorBefore.x},${anchorBefore.y} A 1,1 0,0 ${rotation} ${anchorAfter.x},${anchorAfter.y}`;
    }

    protected createGapPathJumping(intersectionPoint: Point, lineSegment: PointToPointLine): string {
        let offsetBefore;
        let offsetAfter;
        if (intersectionPoint.y < lineSegment.p1.y) {
            offsetBefore = -this.jumpingPolylineParameters.skipOffsetBefore;
            offsetAfter = this.jumpingPolylineParameters.jumpOffsetAfter + this.jumpingPolylineParameters.skipOffsetAfter;
        } else {
            offsetBefore = this.jumpingPolylineParameters.jumpOffsetBefore + this.jumpingPolylineParameters.skipOffsetAfter;
            offsetAfter = -this.jumpingPolylineParameters.skipOffsetBefore;
        }
        const anchorBefore = Point.shiftTowards(intersectionPoint, lineSegment.p1, offsetBefore);
        const anchorAfter = Point.shiftTowards(intersectionPoint, lineSegment.p2, offsetAfter);
        return ` L ${anchorBefore.x},${anchorBefore.y} M ${anchorAfter.x},${anchorAfter.y}`;
    }

    private createGapPathGapped(intersectionPoint: Point, lineSegment: PointToPointLine): string {
        const anchorBefore = Point.shiftTowards(intersectionPoint, lineSegment.p1, this.gappedPolylineParamters.skipOffsetBefore);
        const anchorAfter = Point.shiftTowards(intersectionPoint, lineSegment.p2, this.gappedPolylineParamters.skipOffsetAfter);
        return ` L ${anchorBefore.x},${anchorBefore.y} M ${anchorAfter.x},${anchorAfter.y}`;
    }

    /**
     * ADDITIONALS
     */

    protected renderAdditionalsBezier(edge: CincoEdge, segments: Point[], context: RenderingContext): VNode[] {
        const p1 = segments[segments.length - 2];
        const p2 = segments[segments.length - 1];
        const arrow: any = (
            <path
                class-sprotty-edge={true}
                class-arrow={true}
                d='M 1,0 L 10,-4 L 10,4 Z'
                transform={`rotate(${toDegrees(angleOfPoint({ x: p1.x - p2.x, y: p1.y - p2.y }))} ${p2.x} ${p2.y}) translate(${p2.x} ${
                    p2.y
                })`}
            />
        );
        return [arrow];
    }

    protected renderAdditionalsPolyline(edge: CincoEdge, segments: Point[], context: RenderingContext): VNode[] {
        const p1 = segments[segments.length - 2];
        const p2 = segments[segments.length - 1];
        const arrow: any = (
            <path
                class-sprotty-edge={true}
                class-arrow={true}
                d='M 1,0 L 10,-4 L 10,4 Z'
                transform={`rotate(${toDegrees(angleOfPoint({ x: p1.x - p2.x, y: p1.y - p2.y }))} ${p2.x} ${p2.y}) translate(${p2.x} ${
                    p2.y
                })`}
            />
        );
        return [arrow];
    }
}

@injectable()
export class CincoEdgeView extends MergedGLSPEdgeRenderingView {
    @inject(WorkspaceFileService) workspaceFileService: WorkspaceFileService;
    ARROW_PATH = (lengthX: number, lengthY: number, offsetX = 0, offsetY = 0): string =>
        `M ${offsetX},${offsetY}
        L ${lengthX + offsetX},-${lengthY / 2.0 + offsetY}
        L ${offsetX},${offsetY}
        L ${lengthX + offsetX},${lengthY / 2.0 + offsetY}
        Z`;
    TRIANGLE_PATH = (lengthX: number, lengthY: number, offsetX = 0, offsetY = 0): string =>
        `M ${offsetX},${offsetY} L ${lengthX + offsetX},-${lengthY / 2.0 + offsetY} L ${lengthX + offsetX},${lengthY / 2.0 + offsetY} Z`;
    DIAMOND_PATH = (lengthX: number, lengthY: number, offsetX: number, offsetY: number): string =>
        `M ${offsetX},${offsetY}
        L ${lengthX / 2 + offsetX},${-(lengthY / 2.0) + offsetY}
        L ${lengthX + offsetX},${offsetY}
        L ${lengthX / 2 + offsetX},${lengthY / 2.0 + offsetY}
        Z`;

    render(target: Readonly<CincoEdge>, context: RenderingContext, args: { edgeRouterRegistry: EdgeRouterRegistry }): VNode | undefined {
        const isUnknown = isUnknownEdgeType(target);

        this.edgeRouterRegistry = args.edgeRouterRegistry;

        // update routingPoints
        let edge: CincoEdge;
        if (!(target instanceof CincoEdge)) {
            edge = Object.assign(new CincoEdge(), target);
        } else {
            edge = target;
        }

        const edgeRouter = this.edgeRouterRegistry.get(undefined) as AbstractEdgeRouter; // needed for anchor
        edge.updateRoutingPoints(edgeRouter);

        // setup route
        const route = this.edgeRouterRegistry.route(edge);
        if (route.length === 0) {
            return this.renderDanglingEdge('Cannot compute route');
        }

        // setup edge styling
        const edgeStyle = (edge.style ? edge.style : {}) as EdgeStyle;
        this.lineType = this.connectionToLineType(edgeStyle.type ?? (edge?.view as EdgeView | undefined)?.routerKind ?? this.lineType);
        this.lineCrossType = LINE_CROSS_TYPE.GAP; // TODO: could be implemented in future msl
        const artificialCSSClass = `${CSS_STYLE_PREFIX}${edgeStyle?.name ?? 'default'}`;

        // appearance to style
        let edgeAppearance: Appearance | undefined;
        if (typeof edgeStyle.appearance == 'string') {
            edgeAppearance = getAppearanceByNameOf(edgeStyle.appearance) as Appearance;
        } else {
            edgeAppearance = edgeStyle.appearance ?? undefined;
        }

        const edgeVNodeStyle = appearanceToStyle(edgeAppearance, {
            filled: false,
            isEdge: true,
            // GLSP default values
            foreground: isUnknown ? { r: 255, g: 100, b: 100 } : { r: 178, g: 178, b: 178 },
            lineWidth: 1.5
        } as Appearance);

        // parameter and decorators
        const parameterCount = edgeStyle.parameterCount ?? 0;
        const connectionDecorator = edgeStyle.decorator ?? [];

        // create decorators/additionals
        const decorators = isUnknown
            ? this.createUnknownDecoratorShape(edge, route, 0.5)
            : this.createDecorators(edge, route, connectionDecorator, edgeAppearance, parameterCount);

        if (!this.isVisible(edge, route, context) && edge.children) {
            if (edge.children.length === 0) {
                return undefined;
            }
            const children = context.renderChildren(edge, { route });
            // The children of an edge are not necessarily inside the bounding box of the route,
            // so we need to render a group to ensure the children have a chance to be rendered.
            return (<g className={artificialCSSClass}>{children as Iterable<React.ReactNode>}</g>) as unknown as VNode;
        }

        // create edge
        const renderedEdge = this.renderLine(this.lineType, this.lineCrossType, edge, route, context, args);
        renderedEdge.data!.style = edgeVNodeStyle;

        // render edge
        const children = context.renderChildren(edge, { route });
        return (
            <g class-sprotty-edge={true} className={artificialCSSClass} class-mouseover={edge.hoverFeedback}>
                {renderedEdge as unknown as React.ReactNode}
                {decorators as Iterable<React.ReactNode>}
                {children as Iterable<React.ReactNode>}
            </g>
        ) as unknown as VNode;
    }

    protected connectionToLineType(type: typeof ConnectionType & string): LINE_TYPE {
        switch (type) {
            case ConnectionType.FreeForm:
                return LINE_TYPE.POLYLINE;
            case ConnectionType.Bezier:
                return LINE_TYPE.BEZIER;
            case ConnectionType.Curved: // TODO: reenable in msl?
                return LINE_TYPE.BEZIER;
            case ConnectionType.Manhattan:
                return LINE_TYPE.BEZIER; // TODO: implement Manhattan router
            default:
                return LINE_TYPE.POLYLINE;
        }
    }

    protected createDecorators(
        edge: CincoEdge,
        route: RoutedPoint[],
        decoratorShapes: ConnectionDecorator[],
        edgeAppearance: Appearance | undefined,
        parameterCount: number
    ): VNode[] {
        const result: VNode[] = [];
        for (const decoratorShape of decoratorShapes) {
            const decoratorNode = this.createDecorator(edge, route, decoratorShape, edgeAppearance, parameterCount);
            if (decoratorNode) {
                result.push(decoratorNode);
            }
        }
        return result;
    }

    protected createDecorator(
        edge: CincoEdge,
        route: RoutedPoint[],
        decorator: ConnectionDecorator,
        edgeAppearance: Appearance | undefined,
        parameterCount: number
    ): VNode | undefined {
        // css reference by shapeName
        const cssShapeName = toCSSDecoratorName(decorator);
        const location = normalizeDecoratorLocation(decorator.location ?? 1.0);
        if (decorator.decoratorShape) {
            return this.createDecoratorShape(edge, route, decorator.decoratorShape, parameterCount, location);
        } else if (decorator.predefinedDecorator) {
            return this.createPredefinedDecoratorShape(route, edgeAppearance, decorator.predefinedDecorator, cssShapeName, location);
        } else {
            // either decoratorShape or predefinedDecorator must be set
            return undefined;
        }
    }

    /**
     * PREDEFINED DECORATORS
     */

    protected createPredefinedDecoratorShape(
        route: RoutedPoint[],
        edgeAppearance: Appearance | undefined,
        predefinedDecorator: PredefinedDecorator,
        cssShapeName: string,
        location: number
    ): VNode | undefined {
        const type = predefinedDecorator.shape;
        const appearance = mergeAppearance(edgeAppearance, predefinedDecorator.appearance);

        // keep predefined relation of circle size and border
        const length = appearance?.lineWidth ?? 10.0;
        const innerLength = length;
        appearance.lineWidth = length; // lineWidth is a fifth of the full length
        const decoratorStyle = appearanceToStyle(appearance, {
            isEdge: false,
            strokeRound: false
        });

        const size = { width: innerLength * 5, height: innerLength * 5 };
        switch (type) {
            case DecoratorShape.ARROW:
                return this.createArrow(route, cssShapeName, location, size, decoratorStyle);
            case DecoratorShape.CIRCLE:
                return this.createCircle(route, cssShapeName, location, size, decoratorStyle);
            case DecoratorShape.DIAMOND:
                return this.createDiamond(route, cssShapeName, location, size, decoratorStyle);
            case DecoratorShape.TRIANGLE:
                return this.createTriangle(route, cssShapeName, location, size, decoratorStyle);
        }
        return undefined;
    }

    protected createArrow(
        route: RoutedPoint[],
        cssShapeName: string,
        location: number,
        size: Size,
        decoratorStyle: VNodeStyle
    ): VNode | undefined {
        const position = calculateEdgeLocation(route, location);
        if (!position) {
            return undefined;
        }
        const degrees = toDegrees(angleOfPoint({ x: position.start.x - position.target.x, y: position.start.y - position.target.y }));
        const lengthX = size.width;
        const lengthY = size.height;
        const deco: any = (
            <path
                class-sprotty-edge={true}
                class-arrow={true}
                className={cssShapeName}
                style={decoratorStyle as React.CSSProperties}
                d={this.ARROW_PATH(lengthX, lengthY)}
                transform={`
                    rotate(${degrees} ${position.target.x} ${position.target.y})
                    translate(${position.point.x} ${position.point.y})`}
            />
        );
        return [deco] as unknown as VNode;
    }

    protected createTriangle(
        route: RoutedPoint[],
        cssShapeName: string,
        location: number,
        size: Size,
        decoratorStyle: VNodeStyle
    ): VNode | undefined {
        const position = calculateEdgeLocation(route, location);
        if (!position) {
            return undefined;
        }
        const degrees = toDegrees(angleOfPoint({ x: position.start.x - position.target.x, y: position.start.y - position.target.y }));
        const lengthX = size.width;
        const lengthY = size.height;
        const deco: any = (
            <path
                class-sprotty-edge={true}
                class-arrow={true}
                className={cssShapeName}
                style={decoratorStyle as React.CSSProperties}
                d={this.TRIANGLE_PATH(lengthX, lengthY)}
                transform={`
                    rotate(${degrees} ${position.target.x} ${position.target.y})
                    translate(${position.point.x} ${position.point.y})`}
            />
        );
        return [deco] as unknown as VNode;
    }

    protected createDiamond(
        route: RoutedPoint[],
        cssShapeName: string,
        location: number,
        size: Size,
        decoratorStyle: VNodeStyle
    ): VNode | undefined {
        const position = calculateEdgeLocation(route, location);
        if (!position) {
            return undefined;
        }
        const degrees = toDegrees(angleOfPoint({ x: position.start.x - position.target.x, y: position.start.y - position.target.y }));
        const lengthX = size.width;
        const lengthY = size.height;
        const deco: any = (
            <path
                class-sprotty-edge={true}
                class-arrow={true}
                className={cssShapeName}
                style={decoratorStyle as React.CSSProperties}
                d={this.DIAMOND_PATH(lengthX, lengthY / 1.33, 0, 0)}
                transform={`
                    rotate(${degrees} ${position.target.x} ${position.target.y})
                    translate(${position.point.x} ${position.point.y})`}
            />
        );
        return [deco] as unknown as VNode;
    }

    protected createCircle(
        route: RoutedPoint[],
        cssShapeName: string,
        location: number,
        size: Size,
        decoratorStyle: VNodeStyle
    ): VNode | undefined {
        const position = calculateEdgeLocation(route, location);
        if (!position) {
            return undefined;
        }
        const degrees = toDegrees(angleOfPoint({ x: position.start.x - position.target.x, y: position.start.y - position.target.y }));
        const deco: any = (
            <ellipse
                class-sprotty-edge={true}
                class-circle={true}
                className={cssShapeName}
                style={decoratorStyle as React.CSSProperties}
                rx={Math.max(size.width / 2.0, 0)}
                ry={Math.max(size.height / 2.0, 0)}
                transform={`
                    rotate(${degrees} ${position.target.x} ${position.target.y})
                    translate(${position.point.x} ${position.point.y})`}
            />
        );
        return [deco] as unknown as VNode;
    }

    /**
     * DECORATOR SHAPES
     */

    protected createDecoratorShape(
        edge: CincoEdge,
        route: RoutedPoint[],
        shape: GraphicsAlgorithm,
        parameterCount: number,
        location: number
    ): VNode | undefined {
        if (!AbstractShape.is(shape)) {
            // there is no GraphicsAlgorithm that is no AbstractShape!
            return undefined;
        }
        const parentSize = { width: 0.0, height: 0.0 };
        const parentScale = { x: 1.0, y: 1.0 };
        const position = calculateEdgeLocation(route, location);
        if (!position) {
            return undefined;
        }

        const style = edge.style as EdgeStyle | undefined;
        const artificialCSSClass = `${CSS_STYLE_PREFIX}${style?.name ?? 'default'}`;

        const vnode = buildShape(edge, shape, parentSize, parentScale, position.point, false, parameterCount, this.workspaceFileService);
        if (vnode === undefined) {
            return undefined;
        }

        // resolve children (glsp needs flat hierarchy)
        const vNodeChildren = resolveChildrenRecursivly(vnode);
        const children = [vnode].concat(vNodeChildren);
        const mainContainer = (c: VNode[]): VNode =>
            (<g className={artificialCSSClass}>{c as Iterable<React.ReactNode>}</g>) as unknown as VNode;
        return mainContainer(children);
    }

    protected createUnknownDecoratorShape(edge: CincoEdge, route: RoutedPoint[], location: number): VNode | undefined {
        const position = calculateEdgeLocation(route, location);
        if (!position) {
            return undefined;
        }
        const artificialCSSClass = `${CSS_STYLE_PREFIX}${UNKNOWN_ELEMENT_CSS}`;
        const parentScale = { x: 1.0, y: 1.0 };

        const label = '' + edge.elementType + '\n' + '(' + edge.id + ')';
        const vnode = buildShape(
            edge,
            getUnknownEdgeShape({ width: UNKNOWN_DECORATOR_SIZE.width, height: UNKNOWN_DECORATOR_SIZE.height }, label),
            { width: 0, height: 0 },
            parentScale,
            position.point,
            false,
            0,
            this.workspaceFileService
        );
        if (vnode === undefined) {
            return undefined;
        }

        // resolve children (glsp needs flat hierarchy)
        const vNodeChildren = resolveChildrenRecursivly(vnode);
        const children = [vnode].concat(vNodeChildren);
        const mainContainer = (c: VNode[]): VNode =>
            (<g className={artificialCSSClass}>{c as Iterable<React.ReactNode>}</g>) as unknown as VNode;
        return mainContainer(children);
    }
}
