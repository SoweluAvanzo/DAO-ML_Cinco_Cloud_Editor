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
    BoundsAware,
    ChangeBoundsListener,
    ChangeBoundsOperation,
    ChangeBoundsTool,
    CompoundOperation,
    ElementAndBounds,
    ElementMove,
    HideChangeBoundsToolResizeFeedbackAction,
    MoveAction,
    Operation,
    GChildElement,
    GGraph,
    GModelElement,
    GModelRoot,
    GParentElement,
    SResizeHandle,
    GShapeElement,
    createMovementRestrictionFeedback,
    findParentByFeature,
    isMoveable,
    isNonRoutableSelectedMovableBoundsAware,
    isSelectable,
    isViewport,
    removeMovementRestrictionFeedback,
    toAbsolutePosition,
    ISelectionListener
} from '@eclipse-glsp/client';
import { CursorCSS, applyCssClasses, cursorFeedbackAction } from '@eclipse-glsp/client/lib/base/feedback/css-feedback';
import { Action, Bounds, ChangeContainerOperation, Point } from '@eclipse-glsp/protocol';
import { inject, injectable } from 'inversify';
import {
    getCurrentMousePosition,
    getHierachyAwareRelativePosition,
    getHoveredContainer,
    getSelectedElements
} from '../../utils/canvas-utils';
import { isContainableByConstraints } from '../../utils/constraint-utils';
import { EnvironmentProvider, IEnvironmentProvider } from '../../api/environment-provider';

@injectable()
export class ChangeContainerTool extends ChangeBoundsTool {
    static override ID = 'change-container-tool';

    private changeContainerListener: ChangeContainerAndBoundsListener;

    @inject(EnvironmentProvider) protected readonly environmentProvider: IEnvironmentProvider;

    override get id(): string {
        return ChangeContainerTool.ID;
    }

    override enable(): void {
        // install change container listener for client-side container changing of containments
        this.changeContainerListener = this.createChangeContainerListener();
        this.mouseTool.register(this.changeContainerListener);
        this.selectionService.onSelectionChanged(change =>
            this.changeContainerListener.selectionChanged(change.root, change.selectedElements)
        );
    }

    override disable(): void {
        this.mouseTool.deregister(this.changeContainerListener);
        this.selectionService.onSelectionChanged(change => {
            this.environmentProvider.selectedElementsChanged(change.selectedElements);
        });
        this.deregisterFeedback(this.changeContainerListener, [HideChangeBoundsToolResizeFeedbackAction.create()]);
    }

    protected createChangeContainerListener(): ChangeContainerAndBoundsListener {
        return new ChangeContainerAndBoundsListener(this);
    }
}
@injectable()
export class ChangeContainerAndBoundsListener extends ChangeBoundsListener implements ISelectionListener {
    private __isMouseDown = false;
    private __isMouseDrag = false;
    protected hasDragged = false;
    protected startDragPosition: Point | undefined; // initial dragging position of mouse
    protected elementId2startPos = new Map<string, Point>(); // dragging positions of elements (updated on each move)
    private sourcePositions = new Map<string, Bounds>(); // start positions from mouse down to drop (for invalid container change)

    constructor(protected override tool: ChangeContainerTool) {
        super(tool);
    }

    override mouseDown(target: GModelElement, event: MouseEvent): Action[] {
        const result: Action[] = [];
        this.__isMouseDown = true;

        if (event.button === 0) {
            if (!(target instanceof SResizeHandle)) {
                // initiate moving start position of dragging
                const moveable = findParentByFeature(target, isMoveable);
                if (moveable !== undefined) {
                    this.startDragPosition = { x: event.pageX, y: event.pageY };
                } else {
                    this.startDragPosition = undefined;
                }
                this.hasDragged = false;
            }

            if (!(target instanceof GModelRoot)) {
                // check if we have a resize handle (only single-selection)
                if (this.activeResizeElement && target instanceof SResizeHandle) {
                    this.activeResizeHandle = target;
                } else {
                    this.setActiveResizeElement(target);
                }
                if (this.activeResizeElement) {
                    // initDragPosition
                    this.initPosition(event);
                } else {
                    this.reset();
                }
            }
        }

        // set selected elements sourcePositions (for reset of invalid actions) and bring them to front
        this.sourcePositions.clear();
        const selectedElements = getSelectedElements(target.root);
        this.bringToFront(selectedElements);
        selectedElements.forEach(e => this.sourcePositions.set(e.id, e.bounds));

        return result;
    }

    clearOutChildrenOfSelectedContainer(elements: GShapeElement[]): GShapeElement[] {
        const parents = elements.filter(e => e instanceof GParentElement) as GParentElement[];
        return elements.filter(
            e =>
                !(e instanceof GChildElement)
                    ? true // if element is either no child...
                    : parents.filter(p => p.children.indexOf(e) >= 0).length <= 0 // (all elements that are a parent of e)
        ); // ...or have no parents inside the collection
    }

    override mouseMove(target: GModelElement, event: MouseEvent): Action[] {
        let result: Action[] = [];

        if (this.__isMouseDown) {
            // mouse is down, start dragging
            this.__isMouseDrag = true;
            if (this.activeResizeHandle) {
                // resize handling and feedback
                const actions: Action[] = [
                    cursorFeedbackAction(this.activeResizeHandle.isNwSeResize() ? CursorCSS.RESIZE_NWSE : CursorCSS.RESIZE_NESW),
                    applyCssClasses(this.activeResizeHandle, ChangeBoundsListener.CSS_CLASS_ACTIVE)
                ];
                const positionUpdate = this.pointPositionUpdater.updatePosition(target, { x: event.pageX, y: event.pageY }, !event.altKey);
                if (positionUpdate) {
                    const resizeActions = this.handleResizeOnClient(positionUpdate);
                    actions.push(...resizeActions);
                }
                result = actions;
            }
        }

        if (event.buttons === 0) {
            // move while mouse up
            result = result.concat(this.mouseUp(target, event));
        } else if (this.startDragPosition) {
            // move dragged element
            if (this.elementId2startPos.size === 0) {
                this.collectStartPositions(target.root);
            }
            this.hasDragged = true;
            const moveAction = this.getElementMoves(target, event, false);
            if (moveAction) {
                result.push(moveAction);
                // hierarchy aware feedback (potencial container change)
                const currentMousePosition = getCurrentMousePosition(target.root, event);
                const selectedElements = getSelectedElements(target.root);
                const currentContainer = getHoveredContainer(currentMousePosition, target, selectedElements);
                const feedback = this.handleDragFeedback(currentContainer);
                result.push(feedback);
            }
        }
        return result;
    }

    override mouseUp(element: GModelElement, event: MouseEvent): Action[] {
        const result: Action[] = [];
        this.__isMouseDown = false;
        if (this.__isMouseDrag) {
            const root = element.root;
            const selectedElements = getSelectedElements(element.root);
            const childrenlessSelectedElements = this.clearOutChildrenOfSelectedContainer(selectedElements);
            const mousePosition = getCurrentMousePosition(root, event);
            const targetContainer = getHoveredContainer(mousePosition, element, selectedElements);
            const changeContainerOperations: ChangeContainerOperation[] = [];
            // potencially change container for all selected elements
            for (const selectedElement of selectedElements) {
                let boundState: Bounds = selectedElement.bounds;
                // container change handling (if a parent is selected and it's child, only the parent can change container)
                if (
                    childrenlessSelectedElements.indexOf(selectedElement) >= 0 &&
                    selectedElement.parent.id !== targetContainer.id &&
                    selectedElement.id !== targetContainer.id // selectedElement is not allowed to contain itself
                ) {
                    // is different container
                    const isValidContainment = isContainableByConstraints(targetContainer, selectedElement);
                    if (isValidContainment) {
                        // containment can be contained, change container
                        const absolutePosition = toAbsolutePosition(selectedElement);
                        boundState = getHierachyAwareRelativePosition(absolutePosition, targetContainer);
                        boundState = {
                            x: boundState.x,
                            y: boundState.y,
                            width: selectedElement.size.width,
                            height: selectedElement.size.height
                        };
                        const changeContainerOperation = ChangeContainerOperation.create({
                            elementId: selectedElement.id,
                            targetContainerId: targetContainer.id,
                            location: boundState
                        });
                        changeContainerOperations.push(changeContainerOperation);

                        // change local for client
                        selectedElement.parent.remove(selectedElement);
                        targetContainer.add(selectedElement);
                    } else {
                        // changing container is invalid, return to source position
                        const sourcePosition = this.sourcePositions.get(selectedElement.id)!;
                        boundState = sourcePosition;
                    }
                }
                selectedElement.bounds = boundState;
            }
            if (changeContainerOperations.length > 0) {
                result.push(...[CompoundOperation.create(changeContainerOperations)]);
            }

            // movement handling
            if (!this.pointPositionUpdater.isLastDragPositionUndefined()) {
                if (this.activeResizeHandle) {
                    // Resize
                    result.push(...this.handleResize(this.activeResizeHandle));
                } else {
                    // Move
                    result.push(...this.handleMoveOnServer(element));
                }
            }

            // Reset feedback
            if (this.tool.movementRestrictor) {
                this.tool.deregisterFeedback(this, [removeMovementRestrictionFeedback(element, this.tool.movementRestrictor)]);
            }
            result.push(cursorFeedbackAction(CursorCSS.DEFAULT));
            // reset data
            this.resetPosition();
            this.hasDragged = false;
            this.startDragPosition = undefined;
            this.elementId2startPos.clear();
            this.__isMouseDrag = false;
        }
        return result;
    }

    override mouseEnter(target: GModelElement, event: MouseEvent): Action[] {
        if (target instanceof GModelRoot && event.buttons === 0 && !this.startDragPosition) {
            this.mouseUp(target, event);
        }
        return [];
    }

    override handleMoveOnServer(target: GModelElement): Action[] {
        const operations: Operation[] = [];
        operations.push(...this.handleMoveElementsOnServer(target));
        if (operations.length > 0) {
            return [CompoundOperation.create(operations)];
        }
        return operations;
    }

    override handleMoveElementsOnServer(target: GModelElement): Operation[] {
        const result: Operation[] = [];
        const newBounds: ElementAndBounds[] = [];
        const selectedElements: (GModelElement & BoundsAware)[] = getSelectedElements(target.root).filter(e =>
            isNonRoutableSelectedMovableBoundsAware(e)
        );
        const selectionSet: Set<GModelElement & BoundsAware> = new Set(selectedElements);
        selectedElements
            .filter(element => !this.isChildOfSelected(selectionSet, element))
            .map(element => this.createElementAndBounds(element))
            .forEach(bounds => newBounds.push(...bounds));
        if (newBounds.length > 0) {
            const op = ChangeBoundsOperation.create(newBounds);
            result.push(op);
        }
        return result;
    }

    bringToFront(elements: GModelElement[]): void {
        const childElements = elements.filter(e => e instanceof GChildElement) as GChildElement[];
        if (childElements.length > 0) {
            for (const element of childElements) {
                const parent = element.parent;
                if (!(parent instanceof GGraph)) {
                    this.bringToFront([parent]);
                }
                parent.move(element, parent.children.length - 1);
            }
        }
    }

    handleDragFeedback(currentContainer: GModelElement | undefined): Action {
        if (currentContainer !== undefined) {
            // if at least one selected element potencially changes it's container...
            const anyDifferentContainer =
                Array.from(
                    getSelectedElements(currentContainer.root).filter(
                        selectedElement => selectedElement instanceof GChildElement && selectedElement.parent !== currentContainer
                    )
                ).length > 0;
            if (anyDifferentContainer) {
                // ...and if at least one of the selected elements can be contained...
                const selectedElements = getSelectedElements(currentContainer.root);
                const childrenlessSelectedElements = this.clearOutChildrenOfSelectedContainer(selectedElements);
                const anyContainable =
                    childrenlessSelectedElements.map(e => isContainableByConstraints(currentContainer, e)).indexOf(true) >= 0;

                // ...show Feedback for allowed dropping (NODE_CREATION), else that it is not allowed (OPERATION_NOT_ALLOWED)
                return anyContainable
                    ? cursorFeedbackAction(CursorCSS.NODE_CREATION)
                    : cursorFeedbackAction(CursorCSS.OPERATION_NOT_ALLOWED);
            }
        }
        return cursorFeedbackAction(CursorCSS.MOVE);
    }

    protected collectStartPositions(root: GModelRoot): void {
        const selectedElements = root.index.all().filter(element => isSelectable(element) && element.selected);
        const elementsSet = new Set(selectedElements);
        selectedElements
            .filter(element => !this.isChildOfSelected(elementsSet, element))
            .forEach(element => {
                if (isMoveable(element)) {
                    this.elementId2startPos.set(element.id, element.position);
                }
            });
    }

    protected getElementMoves(target: GModelElement, event: MouseEvent, finished: boolean): MoveAction | undefined {
        if (!this.startDragPosition) {
            return undefined;
        }
        const elementMoves: ElementMove[] = [];
        const viewport = findParentByFeature(target, isViewport);
        const zoom = viewport ? viewport.zoom : 1;
        const delta = {
            x: (event.pageX - this.startDragPosition.x) / zoom,
            y: (event.pageY - this.startDragPosition.y) / zoom
        };
        this.elementId2startPos.forEach((startPosition, elementId) => {
            const element = target.root.index.getById(elementId);
            if (element) {
                let toPosition = this.snap(
                    {
                        x: startPosition.x + delta.x,
                        y: startPosition.y + delta.y
                    },
                    element,
                    !event.shiftKey
                );
                if (isMoveable(element)) {
                    toPosition = this.validateMove(startPosition, toPosition, element, finished);
                    const elementMove = {
                        elementId: element.id,
                        fromPosition: {
                            x: element.position.x,
                            y: element.position.y
                        },
                        toPosition
                    };
                    elementMoves.push(elementMove);
                }
            }
        });
        if (elementMoves.length > 0) {
            return MoveAction.create(elementMoves, { animate: false, finished });
        } else {
            return undefined;
        }
    }

    validateMove(startPostion: Point, toPosition: Point, element: GModelElement, isFinished: boolean): Point {
        let newPosition = toPosition;
        if (this.tool.movementRestrictor) {
            const valid = this.tool.movementRestrictor.validate(element, toPosition);
            let action;
            if (!valid) {
                action = createMovementRestrictionFeedback(element, this.tool.movementRestrictor);

                if (isFinished) {
                    newPosition = startPostion;
                }
            } else {
                action = removeMovementRestrictionFeedback(element, this.tool.movementRestrictor);
            }

            this.tool.registerFeedback([action], this);
        }
        return newPosition;
    }
}
