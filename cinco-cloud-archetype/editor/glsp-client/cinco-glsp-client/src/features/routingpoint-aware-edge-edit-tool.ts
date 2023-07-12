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
import { ChangeAwareRoutingPointsOperation } from '@cinco-glsp/cinco-glsp-server/lib/src/shared/protocol/routingpoint-protocol';
import {
    CursorCSS,
    DragAwareMouseListener,
    DrawFeedbackEdgeAction,
    DrawFeedbackEdgeSourceAction,
    EdgeEditTool,
    FeedbackEdgeRouteMovingMouseListener,
    FeedbackEdgeSourceMovingMouseListener,
    FeedbackEdgeTargetMovingMouseListener,
    HideEdgeReconnectHandlesFeedbackAction,
    Point,
    RemoveFeedbackEdgeAction,
    SReconnectHandle,
    ShowEdgeReconnectHandlesFeedbackAction,
    SwitchRoutingModeAction,
    TYPES,
    cursorFeedbackAction,
    feedbackEdgeId,
    isReconnectHandle,
    isReconnectable,
    isRoutable,
    isRoutingHandle,
    isSourceRoutingHandle,
    isTargetRoutingHandle
} from '@eclipse-glsp/client';
import { SelectionListener, SelectionService } from '@eclipse-glsp/client/lib/features/select/selection-service';
import { Action, ReconnectEdgeOperation } from '@eclipse-glsp/protocol';
import { inject, injectable, optional } from 'inversify';
import {
    AnchorComputerRegistry,
    Connectable,
    EdgeRouterRegistry,
    ISnapper,
    SModelElement,
    SModelRoot,
    SRoutableElement,
    SRoutingHandle,
    canEditRouting,
    findParentByFeature,
    isConnectable,
    isSelected
} from 'sprotty';
import { CincoEdge } from '../model/model';

@injectable()
export class RoutingPointAwareEdgeEditTool extends EdgeEditTool {
    static override ID = 'cinco.edge-edit-tool';

    @inject(TYPES.SelectionService) override selectionService: SelectionService;
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
        this.selectionService.register(this.routingPointAwareEdgeEditListener);

        // install feedback move mouse listener for client-side move updates
        this.feedbackEdgeSourceMovingListener = new FeedbackEdgeSourceMovingMouseListener(this.anchorRegistry);
        this.feedbackEdgeTargetMovingListener = new FeedbackEdgeTargetMovingMouseListener(this.anchorRegistry);
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
        this.selectionService.deregister(this.routingPointAwareEdgeEditListener);
        this.deregisterFeedbackListeners();
        this.mouseTool.deregister(this.routingPointAwareEdgeEditListener);
    }
}

class RoutingPointAwareEdgeEditListener extends DragAwareMouseListener implements SelectionListener {
    // active selection data
    protected edge?: SRoutableElement;
    protected routingHandle?: SRoutingHandle;
    DELETE_MODE = false;

    // new connectable (source or target) for edge
    protected newConnectable?: SModelElement & Connectable;

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

    protected isValidEdge(edge?: SRoutableElement): edge is SRoutableElement {
        return edge !== undefined && edge.id !== feedbackEdgeId(edge.root) && isSelected(edge);
    }

    protected setEdgeSelected(edge: SRoutableElement): void {
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
        this.tool.dispatchFeedback(feedbackActions);
    }

    protected isEdgeSelected(): boolean {
        return this.edge !== undefined && isSelected(this.edge);
    }

    protected setReconnectHandleSelected(edge: SRoutableElement, reconnectHandle: SReconnectHandle): void {
        if (this.edge && this.edge.target && this.edge.source) {
            if (isSourceRoutingHandle(edge, reconnectHandle)) {
                this.tool.dispatchFeedback([
                    HideEdgeReconnectHandlesFeedbackAction.create(),
                    cursorFeedbackAction(CursorCSS.EDGE_RECONNECT),
                    DrawFeedbackEdgeSourceAction.create({ elementTypeId: this.edge.type, targetId: this.edge.targetId })
                ]);
                this.reconnectMode = 'NEW_SOURCE';
            } else if (isTargetRoutingHandle(edge, reconnectHandle)) {
                this.tool.dispatchFeedback([
                    HideEdgeReconnectHandlesFeedbackAction.create(),
                    cursorFeedbackAction(CursorCSS.EDGE_CREATION_TARGET),
                    DrawFeedbackEdgeAction.create({ elementTypeId: this.edge.type, sourceId: this.edge.sourceId })
                ]);
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

    protected setRoutingHandleSelected(edge: CincoEdge, position: Point, routingHandle?: SRoutingHandle): void {
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

    protected setNewConnectable(connectable?: SModelElement & Connectable): void {
        this.newConnectable = connectable;
    }

    protected isReadyToReconnect(): boolean | undefined {
        return this.edge && this.isReconnecting() && this.newConnectable !== undefined;
    }

    protected isReadyToReroute(): boolean {
        return this.routingHandle !== undefined;
    }

    override mouseDown(target: SModelElement, event: MouseEvent): Action[] {
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

    override mouseMove(target: SModelElement, event: MouseEvent): Action[] {
        const result = super.mouseMove(target, event);
        if (this.isMouseDrag) {
            // reset any selected connectables when we are dragging, maybe the user is just panning
            this.setNewConnectable(undefined);
        }
        // update dragged routingpoint
        if (this.edge) {
            const edge = isRoutingHandle(target)
                ? findParentByFeature(target, isRoutable)
                : findParentByFeature(this.routingHandle ?? this.edge, isRoutable) ?? this.edge;
            if (this.isEdgeSelected() && this.isMouseDown && edge instanceof CincoEdge) {
                const position = edge.root.parentToLocal({ x: event.offsetX, y: event.offsetY });
                this.setRoutingHandleSelected(edge, position);
            }
        }
        return result;
    }

    override mouseUp(target: SModelElement, event: MouseEvent): Action[] {
        const result = super.mouseUp(target, event);
        if (!this.isReadyToReconnect() && !this.isReadyToReroute()) {
            return result;
        }

        if (this.edge && this.newConnectable) {
            const sourceElementId = this.isReconnectingNewSource() ? this.newConnectable.id : this.edge.sourceId;
            const targetElementId = this.isReconnectingNewSource() ? this.edge.targetId : this.newConnectable.id;
            if (this.requiresReconnect(sourceElementId, targetElementId)) {
                result.push(ReconnectEdgeOperation.create({ edgeElementId: this.edge.id, sourceElementId, targetElementId }));
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

    override mouseOver(target: SModelElement, event: MouseEvent): Action[] {
        if (this.edge && this.isReconnecting()) {
            const currentTarget = findParentByFeature(target, isConnectable);
            if (!this.newConnectable || currentTarget !== this.newConnectable) {
                this.setNewConnectable(currentTarget);
                if (currentTarget) {
                    if (
                        (this.reconnectMode === 'NEW_SOURCE' && currentTarget.canConnect(this.edge, 'source')) ||
                        (this.reconnectMode === 'NEW_TARGET' && currentTarget.canConnect(this.edge, 'target'))
                    ) {
                        this.tool.dispatchFeedback([cursorFeedbackAction(CursorCSS.EDGE_RECONNECT)]);
                        return [];
                    }
                }
                this.tool.dispatchFeedback([cursorFeedbackAction(CursorCSS.OPERATION_NOT_ALLOWED)]);
            }
        }
        return [];
    }

    selectionChanged(root: Readonly<SModelRoot>, selectedElements: string[]): void {
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
