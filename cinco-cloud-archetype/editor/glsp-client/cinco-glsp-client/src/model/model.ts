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
    ElementType,
    getSpecOf,
    getStyleByNameOf,
    NodeStyle,
    Style,
    View
} from '@cinco-glsp/cinco-glsp-server/lib/src/shared/meta-specification';
import { RoutingPoint } from '@cinco-glsp/cinco-glsp-server/lib/src/shared/protocol/routingpoint-protocol';
import {
    AbstractEdgeRouter,
    Bounds,
    DIAMOND_ANCHOR_KIND,
    ELLIPTIC_ANCHOR_KIND,
    GLSPGraph,
    Point,
    RECTANGULAR_ANCHOR_KIND,
    SEdge,
    SGraphIndex,
    SModelElement,
    SModelElementSchema,
    SNode
} from '@eclipse-glsp/client';
import { FluentIterable } from 'sprotty/lib/utils/iterable';
import { canContain } from '../utils/constraint-utils';

export class CincoNode extends SNode {
    [x: string]: any;
    override type: string;
    protected specification?: ElementType;
    protected _view?: View; // runtime view
    protected _properties?: Record<string, any>;

    get elementType(): string | undefined {
        return this.type;
    }

    get properties(): Record<string, any> | undefined {
        // 1. Runtime View
        if (!this._properties) {
            // 2. Persisted View
            const args = this['args'] as any;
            if (args !== undefined && args.properties) {
                const properties = JSON.parse(args.properties);
                this._properties = properties;
                return this._properties;
            } else {
                // 3. fallback to new runtime array
                this._properties = [];
            }
        }
        return this._properties;
    }

    get view(): View | undefined {
        // 1. Runtime View
        if (this._view) {
            return this._view;
        }
        // 2. Persisted View
        const args = this['args'] as any;
        if (args !== undefined && args.persistedView) {
            const persistedView = JSON.parse(args.persistedView) as View;
            this._view = persistedView;
            return this._view;
        }
        // 3. Fallback: Specification
        if (!this.elementType) {
            return undefined;
        }
        this.specification = getSpecOf(this.elementType);
        return this.specification?.view;
    }

    get style(): Style | undefined {
        if (!this.view) {
            return undefined;
        }
        let style = this.view.style;
        if (typeof style === 'string') {
            style = getStyleByNameOf(style);
        }
        return style;
    }

    set style(style: Style | undefined) {
        if (this._view) {
            this._view.style = { ...style } as Style;
        }
    }

    override get anchorKind(): string {
        if (NodeStyle.is(this.style)) {
            const shape = this.style.shape;
            switch (shape?.type) {
                case AbstractShape.RECTANGLE:
                    return RECTANGULAR_ANCHOR_KIND;
                case AbstractShape.ROUNDEDRECTANGLE:
                    return RECTANGULAR_ANCHOR_KIND;
                case AbstractShape.ELLIPSE:
                    return ELLIPTIC_ANCHOR_KIND;
                case AbstractShape.POLYGON:
                    return DIAMOND_ANCHOR_KIND;
            }
        }
        return ELLIPTIC_ANCHOR_KIND;
    }

    override get incomingEdges(): FluentIterable<SEdge> {
        if (this.index instanceof SGraphIndex) {
            return this.index.getIncomingEdges(this);
        } else {
            return [];
        }
    }

    /**
     * The outgoing edges of this connectable element. They are resolved by the index, which must
     * be an `SGraphIndex`.
     */
    override get outgoingEdges(): FluentIterable<SEdge> {
        if (this.index instanceof SGraphIndex) {
            return this.index.getOutgoingEdges(this);
        } else {
            return [];
        }
    }
}

export class CincoEdge extends SEdge {
    [x: string]: any;
    override type: string;
    protected specification?: ElementType;
    protected _bendPoints?: Point[];
    protected _movingBendPoint?: Point;
    protected _movingBendPointIndex?: number;
    protected _movingBendPointPosition?: Point;
    protected _creatingBendPoint = false;
    protected _view?: View; // runtime view
    protected _properties?: Record<string, any>;

    get elementType(): string | undefined {
        return this.type;
    }

    get movingBendPoint(): Point | undefined {
        return this._movingBendPoint;
    }

    set movingBendPoint(update: Point | undefined) {
        this._movingBendPoint = update;
    }

    get creatingBendPoint(): boolean {
        return this._creatingBendPoint;
    }

    set creatingBendPoint(update: boolean) {
        this._creatingBendPoint = update;
    }

    get movingBendPointIndex(): number | undefined {
        return this._movingBendPointIndex;
    }

    set movingBendPointIndex(update: number | undefined) {
        this._movingBendPointIndex = update;
    }

    get movingBendPointPosition(): Point | undefined {
        return this._movingBendPointPosition;
    }

    set movingBendPointPosition(update: Point | undefined) {
        this._movingBendPointPosition = update;
    }

    get bendPoints(): Point[] {
        // 1. Runtime View
        if (!this._bendPoints) {
            // 2. Persisted View
            const args = this['args'] as any;
            if (args !== undefined && args.routingPoints) {
                const routingPoints = JSON.parse(args.routingPoints) as RoutingPoint[];
                this._bendPoints = routingPoints;
            } else {
                // 3. fallback to new runtime array
                this._bendPoints = [];
            }
        }
        return this._bendPoints;
    }

    set bendPoints(bendPoints: Point[] | undefined) {
        this._bendPoints = bendPoints;
    }

    get properties(): Record<string, any> | undefined {
        // 1. Runtime View
        if (!this._properties) {
            // 2. Persisted View
            const args = this['args'] as any;
            if (args !== undefined && args.properties) {
                const properties = JSON.parse(args.properties);
                this._properties = properties;
                return this._properties;
            } else {
                // 3. fallback to new runtime array
                this._properties = [];
            }
        }
        return this._properties;
    }

    get view(): View | undefined {
        // 1. Runtime View
        if (this._view) {
            return this._view;
        }
        // 2. Persisted View
        const args = this['args'] as any;
        if (args !== undefined && args.persistedView) {
            const persistedView = JSON.parse(args.persistedView) as View;
            this._view = persistedView;
            return this._view;
        }
        // 3. Fallback: Specification
        if (!this.elementType) {
            return undefined;
        }
        this.specification = getSpecOf(this.elementType);
        return this.specification?.view;
    }

    get style(): Style | undefined {
        if (!this.view) {
            return undefined;
        }
        let style = this.view.style;
        if (typeof style === 'string') {
            style = getStyleByNameOf(style);
        }
        return style;
    }

    set style(style: Style | undefined) {
        if (this._view) {
            this._view.style = { ...style } as Style;
        }
    }

    addBendPoint(point: Point, index: number): void {
        // insert the point by index
        const bendPoints1 = this.bendPoints.slice(0, index);
        const bendPoints2 = this.bendPoints.slice(index, this.bendPoints.length);
        this.bendPoints = bendPoints1.concat([point]).concat(bendPoints2);
    }

    replaceBendPoint(point: Point, index: number): void {
        // replace the point by index
        const bendPoints1 = this.bendPoints.slice(0, index);
        const bendPoints2 = this.bendPoints.slice(index + 1, this.bendPoints.length);
        this.bendPoints = bendPoints1.concat([point]).concat(bendPoints2);
    }

    removeBendPoint(index: number): void {
        // remove the point by index
        const bendPoints1 = this.bendPoints.slice(0, index);
        const bendPoints2 = this.bendPoints.slice(index + 1, this.bendPoints.length);
        this.bendPoints = bendPoints1.concat(bendPoints2);
    }

    addRoutingPoint(point: Point, index: number): void {
        // insert the point by index
        const routingPoints1 = this.routingPoints.slice(0, index);
        const routingPoints2 = this.routingPoints.slice(index, this.routingPoints.length);
        this.routingPoints = routingPoints1.concat([point]).concat(routingPoints2);
    }

    replaceRoutingPoint(point: Point, index: number): void {
        // replace the point by index
        const routingPoints1 = this.routingPoints.slice(0, index);
        const routingPoints2 = this.routingPoints.slice(index + 1, this.routingPoints.length);
        this.routingPoints = routingPoints1.concat([point]).concat(routingPoints2);
    }

    removeRoutingPoint(index: number): void {
        // remove the point by index
        const routingPoints1 = this.routingPoints.slice(0, index);
        const routingPoints2 = this.routingPoints.slice(index + 1, this.routingPoints.length);
        this.routingPoints = routingPoints1.concat(routingPoints2);
    }

    clearRoutingPoints(): void {
        this.routingPoints = [];
    }

    clearBendPoints(): void {
        this.bendPoints = [];
    }

    /**
     * Update routing points (add bendpoints, source, target and moving bendpoint)
     * @param edgeRouter the edgeRouter that defines how the edge is drawn. Required for anchors
     */
    updateRoutingPoints(edgeRouter: AbstractEdgeRouter): void {
        // set bendpoints as routingpoints
        this.clearRoutingPoints();
        this.routingPoints = this.bendPoints;
        // add moving bendpoint
        if (this.movingBendPointIndex !== undefined && this.movingBendPointPosition) {
            if (!this.creatingBendPoint) {
                // if there is a movingBreakpoint, replace the original position with the current moving position
                this.replaceRoutingPoint(this.movingBendPointPosition, this.movingBendPointIndex);
            }
            // insert the point by index
            else {
                this.addRoutingPoint(this.movingBendPointPosition, this.movingBendPointIndex);
            }
        }
        // add source and target (for routing algorithms, e.g. polyline)
        if (this.routingPoints.length > 0) {
            const anchor = this.getAnchors(edgeRouter);
            if (anchor) {
                this.addRoutingPoint(anchor[0], 0);
                this.addRoutingPoint(anchor[1], this.routingPoints.length);
            }
        }
    }

    getAnchors(edgeRouter: AbstractEdgeRouter): [Point, Point] | undefined {
        if (this.target && this.source) {
            // Use the first bendpoint or target center as start anchor reference
            const startRef = this.routingPoints[0] ?? Bounds.center(this.target.bounds);
            const sourceAnchor = edgeRouter.getTranslatedAnchor(
                this.source,
                startRef,
                this.target.parent,
                this,
                this.sourceAnchorCorrection
            );
            // Use the last bendpoint or source center as end anchor reference
            const endRef = this.routingPoints[this.routingPoints.length - 1] ?? Bounds.center(this.source.bounds);
            const targetAnchor = edgeRouter.getTranslatedAnchor(this.target, endRef, this.source.parent, this, this.targetAnchorCorrection);
            return [sourceAnchor, targetAnchor];
        }
        return undefined;
    }

    protected printBendpoints(): void {
        console.log('--------------------------------------');
        for (const rp of this.routingPoints) {
            console.log(rp.x + ', ' + rp.y);
        }
        console.log(
            'moving bendpoint ' +
                this.id +
                ': ' +
                this.movingBendPointPosition?.x +
                ', ' +
                this.movingBendPointPosition?.y +
                ':: ' +
                this.movingBendPointIndex
        );
        console.log('--------------------------------------');
    }
}

export class CincoGraphModel extends GLSPGraph {
    override isContainableElement(input: string | SModelElement | SModelElementSchema): boolean {
        const targetType = input instanceof SModelElement ? input.type : SModelElementSchema.is(input) ? input.type : input;
        return canContain(this, targetType);
    }
}
