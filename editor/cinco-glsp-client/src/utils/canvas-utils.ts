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

import { isContainer } from '@cinco-glsp/cinco-glsp-common';
import {
    Bounds,
    findParentByFeature,
    ISnapper,
    isSelectable,
    isViewport,
    Point,
    GChildElement,
    GEdge,
    GGraph,
    GModelElement,
    GModelRoot,
    GNode,
    GParentElement,
    GShapeElement,
    toAbsoluteBounds
} from '@eclipse-glsp/client';

/**
 * Calculates absolute position in respect to arbitrary many
 * nested containers.
 */
export function calcAbsolutePosition(element: GModelElement): Point {
    if (!(element instanceof GNode)) {
        return Point.ORIGIN;
    }
    if (element.parent instanceof GGraph) {
        // is absolute position
        return element.position;
    } else {
        return Point.add(element.position, calcAbsolutePosition(element.parent));
    }
}

export function getHoveredContainer(
    mousePosition: Point,
    mouseTarget: GModelElement,
    selectedElements: Array<GModelElement>
): GParentElement {
    if (mouseTarget instanceof GGraph) {
        // graphModel
        return mouseTarget;
    } else if (!(mouseTarget instanceof GNode) && mouseTarget instanceof GChildElement) {
        // something else like a container
        return getHoveredContainer(mousePosition, mouseTarget.parent, selectedElements);
    } else if (mouseTarget instanceof GNode && selectedElements.indexOf(mouseTarget) < 0) {
        // a selected node
        return mouseTarget;
    }
    // mouse is potencially ontop of a selectedElement and
    // hovers over potencial container
    else if (mouseTarget instanceof GNode) {
        const potencialTargets = getNodeAtPosition(mouseTarget.root, mousePosition, n => n !== mouseTarget).filter(
            n => isContainer(n.type) && n instanceof GParentElement
        ) as GParentElement[];
        if (potencialTargets.length > 0) {
            return potencialTargets[0];
        }
    }
    if (mouseTarget.root instanceof GGraph || mouseTarget.root instanceof GModelRoot) {
        // no container was found, fallback to root-model
        return mouseTarget.root;
    }
    throw Error('Type is out of scope');
}

export function getNodeBehindEdge(mousePosition: Point, mouseTarget: GModelElement): GModelElement {
    if (mouseTarget instanceof GNode) {
        // a selected node
        return mouseTarget;
    }
    // mouse is potencially hovering ontop of a targeted node
    else if (mouseTarget instanceof GEdge) {
        const potencialTargets = getNodeAtPosition(mouseTarget.root, mousePosition);
        if (potencialTargets.length > 0) {
            return potencialTargets[0];
        }
    } else if (!(mouseTarget instanceof GNode || mouseTarget instanceof GModelRoot) && mouseTarget instanceof GChildElement) {
        // something else like a container
        return getNodeBehindEdge(mousePosition, mouseTarget.parent);
    }
    // graphModel (there cannot be something behind a GGraph)
    return mouseTarget;
}

export function getNodeAtPosition(root: GParentElement, position: Point, filter?: (n: GNode) => boolean): GModelElement[] {
    const children = Array.from(root.index.all());
    const potencialTarget: GModelElement[] = children
        .filter(n => {
            // all container that lie under the dragged element
            if (n instanceof GNode) {
                if (filter !== undefined && !filter(n)) {
                    return false;
                }
                const absoluteBounds = toAbsoluteBounds(n);
                const left = absoluteBounds.x;
                const right = absoluteBounds.x + absoluteBounds.width;
                const top = absoluteBounds.y;
                const bottom = absoluteBounds.y + absoluteBounds.height;
                return left <= position.x && position.x <= right && top <= position.y && position.y <= bottom;
            }
            return false;
        })
        // sprotty seems to draw all GModelElements in order.
        // That means the last container should be the one the mouse hovers over
        .reverse() as GParentElement[];
    return potencialTarget;
}

export function createFromToPosition(
    element: GModelElement,
    startDragPosition: Point,
    startPosition: Point,
    event: MouseEvent,
    snapper?: ISnapper
): Point | undefined {
    if (!startDragPosition) {
        return undefined;
    }
    const viewport = findParentByFeature(element, isViewport);
    const zoom = viewport ? viewport.zoom : 1;
    const delta = {
        x: (event.pageX - startDragPosition.x) / zoom,
        y: (event.pageY - startDragPosition.y) / zoom
    };
    if (element) {
        const pos = {
            x: startPosition.x + delta.x,
            y: startPosition.y + delta.y
        };
        return snap(snapper, pos, element, event);
    } else {
        return undefined;
    }
}

/**
 * @param position should be absolutePosition
 * @param container the container that serves the new base for the position
 * @returns the relative position inside the container
 */
export function getHierachyAwareRelativePosition(position: Point, container: GParentElement): Bounds {
    if (container instanceof GChildElement) {
        const positionInParent = getHierachyAwareRelativePosition(position, container.parent);
        return container.parentToLocal(positionInParent);
    } else if (container instanceof GGraph) {
        return Bounds.translate(Bounds.EMPTY, position);
    } else {
        return container.parentToLocal(position);
    }
}

export function getCurrentMousePosition(root: GModelRoot, event: MouseEvent): Bounds {
    return root.parentToLocal({ x: event.offsetX, y: event.offsetY });
}

export function getSelectedElements(root: GModelRoot): Array<GShapeElement> {
    return Array.from(
        root.index
            .all()
            .filter(element => isSelectable(element) && element.selected)
            .filter(e => e instanceof GShapeElement)
    ) as Array<GShapeElement>;
}

function snap(snapper: ISnapper | undefined, pos: Point, element: GModelElement, event: MouseEvent): Point {
    if (!event.shiftKey && snapper) {
        return snapper.snap(pos, element);
    } else {
        return pos;
    }
}
