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
    isContainer,
    isDeletable,
    isMovable,
    isSelectable,
    NodeStyle,
    RoutingPoint,
    Style,
    View
} from '@cinco-glsp/cinco-glsp-common';
import {
    AbstractEdgeRouter,
    Bounds,
    boundsFeature,
    connectableFeature,
    deletableFeature,
    DIAMOND_ANCHOR_KIND,
    editFeature,
    ELLIPTIC_ANCHOR_KIND,
    fadeFeature,
    GChildElement,
    GConnectableElement,
    GEdge,
    GGraph,
    GGraphIndex,
    GModelElement,
    GModelElementSchema,
    GNode,
    hoverFeedbackFeature,
    isGModelElementSchema,
    layoutContainerFeature,
    moveFeature,
    Point,
    popupFeature,
    RECTANGULAR_ANCHOR_KIND,
    selectFeature
} from '@eclipse-glsp/client';
import { FluentIterable } from 'sprotty/lib/utils/iterable';
import { canContain } from '../utils/constraint-utils';

export interface CincoModelElement {
    id: string;
    spec?: ElementType;
    type: string;
}
export namespace CincoModelElement {
    export function is(object: any): object is CincoModelElement {
        return object instanceof CincoEdge || object instanceof CincoNode || object instanceof CincoGraphModel;
    }
}

export class CincoNode extends GNode implements CincoModelElement {
    [x: string]: any;
    override type: string;
    spec?: ElementType;
    protected _view?: View; // runtime view
    protected _properties?: Record<string, any>;

    get elementType(): string | undefined {
        return this.type;
    }

    static getDefaultFeatures(elementTypeId: string): symbol[] {
        const features = [
            connectableFeature,
            boundsFeature,
            moveFeature,
            layoutContainerFeature,
            fadeFeature,
            hoverFeedbackFeature,
            popupFeature
        ];
        if (isDeletable(elementTypeId)) {
            features.push(deletableFeature);
        }
        if (isSelectable(elementTypeId)) {
            features.push(selectFeature);
        }
        if (isMovable(elementTypeId)) {
            features.push(moveFeature);
        }
        return features;
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
        if (this._view && (this._view.style || this._view?.cssClass)) {
            return this._view;
        }
        // 2. Persisted View
        const args = this['args'] as any;
        if (args !== undefined && args.persistedView && (args.persistedView.style || args.persistedView.cssClass)) {
            const persistedView = JSON.parse(args.persistedView) as View;
            this._view = persistedView;
            return this._view;
        }
        // 3. Fallback: Specification
        if (!this.elementType) {
            return undefined;
        }
        this.spec = getSpecOf(this.elementType);
        return this.spec?.view;
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
            this._view = { ...this._view } as View;
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

    override get incomingEdges(): FluentIterable<GEdge> {
        if (this.index instanceof GGraphIndex) {
            return this.index.getIncomingEdges(this);
        } else {
            return [];
        }
    }

    /**
     * The outgoing edges of this connectable element. They are resolved by the index, which must
     * be an `SGraphIndex`.
     */
    override get outgoingEdges(): FluentIterable<GEdge> {
        if (this.index instanceof GGraphIndex) {
            return this.index.getOutgoingEdges(this);
        } else {
            return [];
        }
    }

    get isContainer(): boolean {
        return this.elementType !== undefined && isContainer(this.elementType);
    }

    get specification(): ElementType | undefined {
        return this.spec;
    }
}

export class CincoEdge extends GEdge implements CincoModelElement {
    [x: string]: any;
    override type: string;
    spec?: ElementType;
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

    static getDefaultFeatures(elementTypeId: string): symbol[] {
        const features = [editFeature, fadeFeature, hoverFeedbackFeature];
        if (isDeletable(elementTypeId)) {
            features.push(deletableFeature);
        }
        if (isSelectable(elementTypeId)) {
            features.push(selectFeature);
        }
        return features;
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
        if (this._view && (this._view.style || this._view?.cssClass)) {
            return this._view;
        }
        // 2. Persisted View
        const args = this['args'] as any;
        if (args !== undefined && args.persistedView && (args.persistedView.style || args.persistedView.cssClass)) {
            const persistedView = JSON.parse(args.persistedView) as View;
            this._view = persistedView;
            return this._view;
        }
        // 3. Fallback: Specification
        if (!this.elementType) {
            return undefined;
        }
        this.spec = getSpecOf(this.elementType);
        return this.spec?.view;
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

    get specification(): ElementType | undefined {
        return this.spec;
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

    override get source(): GConnectableElement | undefined {
        const element = this.index.getById(this.sourceId);
        if (!element) {
            return undefined;
        }
        const result = fixNode(element);
        return result as GConnectableElement | undefined;
    }

    override get target(): GConnectableElement | undefined {
        const element = this.index.getById(this.targetId);
        if (!element) {
            return undefined;
        }
        const result = fixNode(element);
        return result as GConnectableElement | undefined;
    }
}

export class CincoGraphModel extends GGraph implements CincoModelElement {
    override isContainableElement(input: string | GModelElement | GModelElementSchema): boolean {
        const targetType = input instanceof GModelElement ? input.type : isGModelElementSchema(input) ? input.type : input;
        return canContain(this, targetType);
    }

    override move(child: GChildElement, newIndex: number): void {
        const children = this.children as GChildElement[];
        const childInModel = children.filter(c => c.id === child.id);
        const isContained = childInModel.length > 0;
        if (!isContained) {
            throw new Error(`No such child ${child.id}`);
        } else {
            if (newIndex < 0 || newIndex > children.length - 1) {
                throw new Error(`Child index ${newIndex} out of bounds (0..${children.length})`);
            }
            const i = children.indexOf(childInModel[0]);
            children.splice(i, 1);
            children.splice(newIndex, 0, child);
        }
    }
}

function fixNode(element: GModelElement): GModelElement {
    // fix procedure, if element is not identified as a CincoNode
    const result = element as any;
    if (!result.bounds) {
        result.bounds = {
            x: result.position.x,
            y: result.position.y,
            width: result.layoutOptions ? (result.layoutOptions.prefWidth as number) : 1.0,
            height: result.layoutOptions ? (result.layoutOptions.prefHeight as number) : 1.0
        };
    }
    return result as GModelElement;
}
