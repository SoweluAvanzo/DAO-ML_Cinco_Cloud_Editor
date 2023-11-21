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
import { ChangeAwareRoutingPointsOperation } from '@cinco-glsp/cinco-glsp-common';
import {
    AnchorComputerRegistry,
    Bounds,
    Connectable,
    CursorCSS,
    DragAwareMouseListener,
    DrawFeedbackEdgeSourceAction,
    EdgeEditTool,
    EdgeRouterRegistry,
    FeedbackEdgeRouteMovingMouseListener,
    FeedbackEdgeSourceMovingMouseListener,
    FeedbackEdgeTargetMovingMouseListener,
    HideEdgeReconnectHandlesFeedbackAction,
    ISnapper,
    MoveAction,
    Point,
    GConnectableElement,
    GEdge,
    GModelElement,
    GModelRoot,
    GNode,
    GReconnectHandle,
    GRoutableElement,
    GRoutingHandle,
    ShowEdgeReconnectHandlesFeedbackAction,
    SwitchRoutingModeAction,
    TYPES,
    canEditRouting,
    cursorFeedbackAction,
    findChildrenAtPosition,
    findParentByFeature,
    getAbsolutePosition,
    isBoundsAware,
    isConnectable,
    isReconnectHandle,
    isReconnectable,
    isRoutable,
    isRoutingHandle,
    isSelected,
    isSourceRoutingHandle,
    isTargetRoutingHandle,
    SelectionService,
    ISelectionListener
} from '@eclipse-glsp/client';
import {
    DrawFeedbackEdgeAction,
    FeedbackEdgeEnd,
    feedbackEdgeEndId,
    feedbackEdgeId,
    RemoveFeedbackEdgeAction
} from '@eclipse-glsp/client/lib/features/tools/edge-creation/dangling-edge-feedback';
import { Action, ReconnectEdgeOperation } from '@eclipse-glsp/protocol';
import { inject, injectable, optional } from 'inversify';
import { CincoEdge, CincoNode } from '../model/model';
import { getCurrentMousePosition, getNodeBehindEdge } from '../utils/canvas-utils';
import { canBeEdgeSource, canBeEdgeTarget } from '../utils/constraint-utils';

/*
 * TODO: This is a fix for a Sprotty/GLSP Bug. Please review when the glsp is updated! This could very well be omitted!
 * GLSP-Client-Version: 1.0.0
 */
export class CincoFeedbackEdgeSourceMovingMouseListener extends FeedbackEdgeSourceMovingMouseListener {
    override mouseMove(target: GModelElement, event: MouseEvent): Action[] {
        const root = target.root;
        const edgeEnd = root.index.getById(feedbackEdgeEndId(root));
        if (!(edgeEnd instanceof FeedbackEdgeEnd) || !edgeEnd.feedbackEdge) {
            return [];
        }

        const edge = edgeEnd.feedbackEdge;
        const position = getAbsolutePosition(edgeEnd, event);
        const endAtMousePosition = findChildrenAtPosition(target.root, position).find(
            e => isConnectable(e) && e.canConnect(edge, 'source')
        );

        if (endAtMousePosition instanceof GConnectableElement && edge.target && isBoundsAware(edge.target)) {
            const anchor = this.computeAbsoluteAnchor(endAtMousePosition, Bounds.center(edge.target.bounds));
            return [MoveAction.create([{ elementId: edgeEnd.id, toPosition: anchor }], { animate: false })];
        } else {
            return [MoveAction.create([{ elementId: edgeEnd.id, toPosition: position }], { animate: false })];
        }
    }
}

/*
 * TODO: This is a fix for a Sprotty/GLSP Bug. Please review when the glsp is updated! This could very well be omitted!
 * GLSP-Client-Version: 1.0.0
 */
export class CincoFeedbackEdgeTargetMovingMouseListener extends FeedbackEdgeTargetMovingMouseListener {
    override mouseMove(target: GModelElement, event: MouseEvent): Action[] {
        const root = target.root;
        const edgeEnd = root.index.getById(feedbackEdgeEndId(root));
        if (!(edgeEnd instanceof FeedbackEdgeEnd) || !edgeEnd.feedbackEdge) {
            return [];
        }

        const edge = edgeEnd.feedbackEdge;
        const position = getAbsolutePosition(edgeEnd, event);
        const endAtMousePosition = findChildrenAtPosition(target.root, position).find(
            e => isConnectable(e) && e.canConnect(edge, 'target')
        );

        if (endAtMousePosition instanceof GConnectableElement && edge.target && isBoundsAware(edge.target)) {
            const anchor = this.computeAbsoluteAnchor(endAtMousePosition, Bounds.center(edge.target.bounds));
            return [MoveAction.create([{ elementId: edgeEnd.id, toPosition: anchor }], { animate: false })];
        } else {
            return [MoveAction.create([{ elementId: edgeEnd.id, toPosition: position }], { animate: false })];
        }
    }
}

@injectable()
export class RoutingPointAwareEdgeEditTool extends EdgeEditTool {
    static override ID = 'cinco.edge-edit-tool';

    @inject(SelectionService) override selectionService: SelectionService;
    @inject(AnchorComputerRegistry) override anchorRegistry: AnchorComputerRegistry;
    @inject(EdgeRouterRegistry) @optional() override edgeRouterRegistry?: EdgeRouterRegistry;
    @inject(TYPES.ISnapper) @optional() override snapper?: ISnapper;

    override feedbackEdgeSourceMovingListener: FeedbackEdgeSourceMovingMouseListener;
    override feedbackEdgeTargetMovingListener: FeedbackEdgeTargetMovingMouseListener;
    override feedbackMovingListener: FeedbackEdgeRouteMovingMouseListener;
    protected routingPointAwareEdgeEditListener: RoutingPointAwareEdgeEditListener;

    override get id(): string {
        return RoutingPointAwareEdgeEditTool.ID;
    }

    override enable(): void {
        this.routingPointAwareEdgeEditListener = new RoutingPointAwareEdgeEditListener(this);
        this.mouseTool.register(this.routingPointAwareEdgeEditListener);
        this.selectionService.onSelectionChanged(change =>
            this.routingPointAwareEdgeEditListener.selectionChanged(change.root, change.selectedElements)
        );

        // install feedback move mouse listener for client-side move updates
        this.feedbackEdgeSourceMovingListener = new CincoFeedbackEdgeSourceMovingMouseListener(
            this.anchorRegistry,
            this.feedbackDispatcher
        );
        this.feedbackEdgeTargetMovingListener = new CincoFeedbackEdgeTargetMovingMouseListener(
            this.anchorRegistry,
            this.feedbackDispatcher
        );
        this.feedbackMovingListener = new FeedbackEdgeRouteMovingMouseListener(this.edgeRouterRegistry, this.snapper);
    }

    override registerFeedbackListeners(): void {
        this.mouseTool.register(this.feedbackMovingListener);
        this.mouseTool.register(this.feedbackEdgeSourceMovingListener);
        this.mouseTool.register(this.feedbackEdgeTargetMovingListener);
    }

    override deregisterFeedbackListeners(): void {
        this.mouseTool.deregister(this.feedbackEdgeSourceMovingListener);
        this.mouseTool.deregister(this.feedbackEdgeTargetMovingListener);
        this.mouseTool.deregister(this.feedbackMovingListener);
    }

    override disable(): void {
        this.routingPointAwareEdgeEditListener.reset();
        this.selectionService.onSelectionChanged(change => {});
        this.deregisterFeedbackListeners();
        this.mouseTool.deregister(this.routingPointAwareEdgeEditListener);
    }
}

class RoutingPointAwareEdgeEditListener extends DragAwareMouseListener implements ISelectionListener {
    // active selection data
    protected edge?: GRoutableElement;
    protected routingHandle?: GRoutingHandle;
    DELETE_MODE = false;

    // new connectable (source or target) for edge
    protected newConnectable?: GModelElement & Connectable;

    // active reconnect handle data
    protected reconnectMode?: 'NEW_SOURCE' | 'NEW_TARGET';

    constructor(protected tool: RoutingPointAwareEdgeEditTool) {
        super();
        window.addEventListener('keydown', e => {
            if (e.altKey) {
                this.DELETE_MODE = true;
            }
        });
        window.addEventListener('keyup', e => {
            if (!e.altKey) {
                this.DELETE_MODE = false;
            }
        });
    }

    protected isValidEdge(edge?: GRoutableElement): edge is GRoutableElement {
        return edge !== undefined && edge.id !== feedbackEdgeId(edge.root) && isSelected(edge);
    }

    protected setEdgeSelected(edge: GRoutableElement): void {
        if (this.edge && this.edge.id !== edge.id) {
            // reset from a previously selected edge
            this.reset();
        }

        this.edge = edge;
        // note: order is important here as we want the reconnect handles to cover the routing handles
        const feedbackActions = [];
        if (canEditRouting(edge)) {
            feedbackActions.push(SwitchRoutingModeAction.create({ elementsToActivate: [this.edge.id] }));
        }
        if (isReconnectable(edge)) {
            feedbackActions.push(ShowEdgeReconnectHandlesFeedbackAction.create(this.edge.id));
        }
        this.tool.registerFeedback(feedbackActions, this);
    }

    protected isEdgeSelected(): boolean {
        return this.edge !== undefined && isSelected(this.edge);
    }

    protected setReconnectHandleSelected(edge: GRoutableElement, reconnectHandle: GReconnectHandle): void {
        if (this.edge && this.edge.target && this.edge.source) {
            if (isSourceRoutingHandle(edge, reconnectHandle)) {
                this.tool.registerFeedback(
                    [
                        HideEdgeReconnectHandlesFeedbackAction.create(),
                        cursorFeedbackAction(CursorCSS.EDGE_RECONNECT),
                        DrawFeedbackEdgeSourceAction.create({ elementTypeId: this.edge.type, targetId: this.edge.targetId })
                    ],
                    this
                );
                this.reconnectMode = 'NEW_SOURCE';
            } else if (isTargetRoutingHandle(edge, reconnectHandle)) {
                this.tool.registerFeedback(
                    [
                        HideEdgeReconnectHandlesFeedbackAction.create(),
                        cursorFeedbackAction(CursorCSS.EDGE_CREATION_TARGET),
                        DrawFeedbackEdgeAction.create({ elementTypeId: this.edge.type, sourceId: this.edge.sourceId })
                    ],
                    this
                );
                this.reconnectMode = 'NEW_TARGET';
            }
        }
    }

    protected isReconnecting(): boolean {
        return this.reconnectMode !== undefined;
    }

    protected isReconnectingNewSource(): boolean {
        return this.reconnectMode === 'NEW_SOURCE';
    }

    protected setRoutingHandleSelected(edge: CincoEdge, position: Point, routingHandle?: GRoutingHandle): void {
        if (edge.target && edge.source) {
            // initialized := routingHandle !== undefined
            if (routingHandle) {
                this.routingHandle = routingHandle;
                edge.creatingBendPoint = edge.creatingBendPoint || routingHandle.type === 'volatile-routing-point';
                // routingHandle.pointIndex + (1 if it creates a new Point) + (1 if it is will be created in an 'unfocused mode')
                if (this.routingHandle.pointIndex < 0) {
                    // create out of scope
                    edge.movingBendPointIndex = this.routingHandle.pointIndex + 1; // starts at -1, creates the 0 indexed point
                    edge.movingBendPoint = position;
                } else if (edge.creatingBendPoint) {
                    // create
                    edge.movingBendPointIndex = this.routingHandle.pointIndex; // creates a non existing point of any index
                    edge.movingBendPoint = position;
                } else {
                    // move
                    edge.movingBendPointIndex = this.routingHandle.pointIndex - 1; // first is a startIndex
                    edge.movingBendPoint = edge.bendPoints[edge.movingBendPointIndex];
                }
            }
            edge.movingBendPointPosition = position;
        }
    }

    protected requiresReconnect(sourceId: string, targetId: string): boolean {
        return this.edge !== undefined && (this.edge.sourceId !== sourceId || this.edge.targetId !== targetId);
    }

    protected setNewConnectable(connectable?: GModelElement & Connectable): void {
        this.newConnectable = connectable;
    }

    protected isReadyToReconnect(): boolean | undefined {
        return this.edge && this.isReconnecting() && this.newConnectable !== undefined;
    }

    protected isReadyToReroute(): boolean {
        return this.routingHandle !== undefined;
    }

    override mouseDown(target: GModelElement, event: MouseEvent): Action[] {
        const result: Action[] = super.mouseDown(target, event);
        if (event.button === 0) {
            const reconnectHandle = findParentByFeature(target, isReconnectHandle);
            const routingHandle = !reconnectHandle ? findParentByFeature(target, isRoutingHandle) : undefined;
            const edge = findParentByFeature(target, isRoutable);
            if (this.isEdgeSelected() && edge && reconnectHandle) {
                // PHASE 2 Reconnect: Select reconnect handle on selected edge
                this.setReconnectHandleSelected(edge, reconnectHandle);
            } else if (this.isEdgeSelected() && edge && routingHandle) {
                // PHASE 2 Reroute: Select routing handle on selected edge
                const newPosition = edge.root.parentToLocal({ x: event.offsetX, y: event.offsetY });
                if (edge instanceof CincoEdge) {
                    this.setRoutingHandleSelected(edge, newPosition, routingHandle);
                }
            } else if (this.isValidEdge(edge)) {
                // PHASE 1: Select edge
                this.tool.registerFeedbackListeners();
                this.setEdgeSelected(edge);
            }
        } else if (event.button === 2) {
            this.reset();
        }
        return result;
    }

    override mouseMove(target: GModelElement, event: MouseEvent): Action[] {
        const result = super.mouseMove(target, event);
        if (this.isMouseDrag && !(target instanceof CincoNode || target instanceof CincoEdge)) {
            // reset any selected connectables when we are dragging, maybe the user is just panning
            this.setNewConnectable(undefined);
        }
        // update dragged routingpoint
        if (this.edge) {
            const edge = isRoutingHandle(target)
                ? findParentByFeature(target, isRoutable)
                : findParentByFeature(this.routingHandle ?? this.edge, isRoutable) ?? this.edge;
            if (this.isEdgeSelected() && this.isMouseDown && edge instanceof CincoEdge) {
                const position = getCurrentMousePosition(edge.root, event);
                this.setRoutingHandleSelected(edge, position);
            }
        }
        return result;
    }

    override mouseUp(target: GModelElement, event: MouseEvent): Action[] {
        const result = super.mouseUp(target, event);
        if (target instanceof CincoNode) {
            this.setNewConnectable(target);
        }
        if (!this.isReadyToReconnect() && !this.isReadyToReroute()) {
            return result;
        }

        if (this.edge && this.newConnectable) {
            const type = this.edge.type;
            const index = this.edge.root.index;
            const sourceElementId = this.isReconnectingNewSource() ? this.newConnectable.id : this.edge.sourceId;
            const targetElementId = this.isReconnectingNewSource() ? this.edge.targetId : this.newConnectable.id;
            const sourceNode = index.getById(sourceElementId) as GNode;
            const targetNode = index.getById(targetElementId) as GNode;
            const changingEdgeFilter = (e: GEdge): boolean => false;
            if (
                sourceNode &&
                targetNode &&
                canBeEdgeSource(sourceNode, type, changingEdgeFilter) &&
                canBeEdgeTarget(targetNode, type, changingEdgeFilter)
            ) {
                if (this.requiresReconnect(sourceElementId, targetElementId)) {
                    result.push(ReconnectEdgeOperation.create({ edgeElementId: this.edge.id, sourceElementId, targetElementId }));
                }
            }
            this.reset();
        } else if (this.edge && this.routingHandle) {
            // we need to re-retrieve the edge as it might have changed due to a server update since we do not reset the state between
            // reroute actions
            const latestEdge = target.index.getById(this.edge.id);
            if (latestEdge && latestEdge instanceof CincoEdge && isRoutable(latestEdge)) {
                const changeRoutingPointOperation: ChangeAwareRoutingPointsOperation = this.createChangeRoutingPointOperation(
                    latestEdge,
                    { x: event.offsetX, y: event.offsetY },
                    this.routingHandle.type
                );
                result.push(changeRoutingPointOperation);
            }
        }
        return result;
    }

    createChangeRoutingPointOperation(edge: CincoEdge, offset: Point, handlingType: string): ChangeAwareRoutingPointsOperation {
        const deleteRoutingPoint = this.DELETE_MODE;
        const routingPointChange = {
            elementId: edge.id,
            newRoutingPoints: [] as [Point, number][],
            removedRoutingPoints: [] as [Point, number][]
        };
        const modifiedIndex = edge.movingBendPointIndex!;
        const oldRoutingPoint = edge.movingBendPoint!;
        const newRoutingPoint = edge.root.parentToLocal(offset);
        const oldIndexedPoint: [Point, number] = [oldRoutingPoint, modifiedIndex];
        const newIndexedPoint: [Point, number] = [newRoutingPoint, modifiedIndex];
        switch (handlingType) {
            case 'routing-point':
                if (deleteRoutingPoint) {
                    // delete routign point
                    routingPointChange.removedRoutingPoints.push(oldIndexedPoint);
                    edge.removeBendPoint(modifiedIndex);
                } else {
                    if (edge.creatingBendPoint) {
                        // creating routing point
                        routingPointChange.newRoutingPoints.push(newIndexedPoint);
                        edge.addBendPoint(newRoutingPoint, modifiedIndex);
                    } else {
                        // moving routing point
                        routingPointChange.removedRoutingPoints.push(oldIndexedPoint);
                        routingPointChange.newRoutingPoints.push(newIndexedPoint);
                        edge.replaceBendPoint(newRoutingPoint, modifiedIndex);
                    }
                }
                break;
            case 'volatile-routing-point': {
                // creating routing point
                routingPointChange.newRoutingPoints.push(newIndexedPoint);
                edge.addBendPoint(newRoutingPoint, modifiedIndex);
                break;
            }
        }
        // reset
        this.routingHandle = undefined;
        edge.movingBendPoint = undefined;
        edge.movingBendPointPosition = undefined;
        edge.movingBendPointIndex = undefined;
        edge.creatingBendPoint = false;
        return ChangeAwareRoutingPointsOperation.create([routingPointChange]);
    }

    override mouseOver(target: GModelElement, event: MouseEvent): Action[] {
        if (this.edge && this.isReconnecting()) {
            const mousePosition = getCurrentMousePosition(target.root, event);
            const currentTarget = getNodeBehindEdge(mousePosition, target) as GModelElement & Connectable;
            // const isNewConnectable = (!this.newConnectable ||  currentTarget !== this.newConnectable);
            if (isConnectable(currentTarget)) {
                this.setNewConnectable(currentTarget);
                if (currentTarget instanceof GNode) {
                    if (
                        (this.reconnectMode === 'NEW_SOURCE' && currentTarget.canConnect(this.edge, 'source')) ||
                        (this.reconnectMode === 'NEW_TARGET' && currentTarget.canConnect(this.edge, 'target'))
                    ) {
                        this.tool.registerFeedback([cursorFeedbackAction(CursorCSS.EDGE_RECONNECT)], this);
                        return [];
                    }
                }
                if (!(currentTarget instanceof GModelRoot || currentTarget instanceof GEdge)) {
                    this.tool.registerFeedback([cursorFeedbackAction(CursorCSS.OPERATION_NOT_ALLOWED)], this);
                }
            }
        }
        return [];
    }

    selectionChanged(root: Readonly<GModelRoot>, selectedElements: string[]): void {
        if (this.edge) {
            if (selectedElements.indexOf(this.edge.id) > -1) {
                // our active edge is still selected, nothing to do
                return;
            }

            if (this.isReconnecting()) {
                // we are reconnecting, so we may have clicked on a potential target
                return;
            }

            // try to find some other selected element and mark that active
            for (const elementId of selectedElements.reverse()) {
                const element = root.index.getById(elementId);
                if (element) {
                    const edge = findParentByFeature(element, isRoutable);
                    if (this.isValidEdge(edge)) {
                        // PHASE 1: Select edge
                        this.setEdgeSelected(edge);
                        return;
                    }
                }
            }

            this.reset();
        }
    }

    public reset(): void {
        this.resetFeedback();
        this.resetData();
    }

    protected resetData(): void {
        this.edge = undefined;
        this.reconnectMode = undefined;
        this.newConnectable = undefined;
        this.routingHandle = undefined;
    }

    protected resetFeedback(): void {
        const result: Action[] = [];
        if (this.edge) {
            result.push(SwitchRoutingModeAction.create({ elementsToDeactivate: [this.edge.id] }));
        }
        result.push(...[HideEdgeReconnectHandlesFeedbackAction.create(), cursorFeedbackAction(), RemoveFeedbackEdgeAction.create()]);
        this.tool.deregisterFeedback(result);
        this.tool.deregisterFeedbackListeners();
    }
}
