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
    SChildElement,
    SGraph,
    SModelElement,
    SModelRoot,
    SNode,
    SParentElement,
    SShapeElement,
    toAbsoluteBounds
} from '@eclipse-glsp/client';

/**
 * Calculates absolute position in respect to arbitrary many
 * nested containers.
 */
export function calcAbsolutePosition(element: SModelElement): Point {
    if (!(element instanceof SNode)) {
        return Point.ORIGIN;
    }
    if (element.parent instanceof SGraph) {
        // is absolute position
        return element.position;
    } else {
        return Point.add(element.position, calcAbsolutePosition(element.parent));
    }
}

export function getHoveredContainer(
    mousePosition: Point,
    mouseTarget: SModelElement,
    selectedElements: Array<SModelElement>
): SParentElement {
    if (mouseTarget instanceof SGraph) {
        // graphModel
        return mouseTarget;
    } else if (!(mouseTarget instanceof SNode) && mouseTarget instanceof SChildElement) {
        // something else like a container
        return getHoveredContainer(mousePosition, mouseTarget.parent, selectedElements);
    } else if (mouseTarget instanceof SNode && selectedElements.indexOf(mouseTarget) < 0) {
        // a selected node
        return mouseTarget;
    }
    // mouse is potencially ontop of a selectedElement and
    // hovers over potencial container
    else if (mouseTarget instanceof SNode) {
        const children = Array.from(mouseTarget.root.index.all());
        const potencialContainers: SParentElement[] = children
            .filter(n => isContainer(n.type) && n instanceof SParentElement)
            .filter(n => {
                // all container that lie under the dragged element
                if (n instanceof SNode && mouseTarget !== n) {
                    const absoluteBounds = toAbsoluteBounds(n);
                    const left = absoluteBounds.x;
                    const right = absoluteBounds.x + absoluteBounds.width;
                    const top = absoluteBounds.y;
                    const bottom = absoluteBounds.y + absoluteBounds.height;
                    return left <= mousePosition.x && mousePosition.x <= right && top <= mousePosition.y && mousePosition.y <= bottom;
                }
                return false;
            })
            // sprotty seems to draw all SModelElements in order.
            // That means the last container should be the one the mouse hovers over
            .reverse() as SParentElement[];
        if (potencialContainers.length > 0) {
            return potencialContainers[0];
        }
    }
    if (mouseTarget.root instanceof SGraph || mouseTarget.root instanceof SModelRoot) {
        // no container was found, fallback to root-model
        return mouseTarget.root;
    }
    throw Error('Type is out of scope');
}

export function createFromToPosition(
    element: SModelElement,
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
export function getHierachyAwareRelativePosition(position: Point, container: SParentElement): Bounds {
    if (container instanceof SChildElement) {
        const positionInParent = getHierachyAwareRelativePosition(position, container.parent);
        return container.parentToLocal(positionInParent);
    } else if (container instanceof SGraph) {
        return Bounds.translate(Bounds.EMPTY, position);
    } else {
        return container.parentToLocal(position);
    }
}

export function getCurrentMousePosition(root: SModelRoot, event: MouseEvent): Bounds {
    return root.parentToLocal({ x: event.offsetX, y: event.offsetY });
}

export function getSelectedElements(root: SModelRoot): Array<SShapeElement> {
    return Array.from(
        root.index
            .all()
            .filter(element => isSelectable(element) && element.selected)
            .filter(e => e instanceof SShapeElement)
    ) as Array<SShapeElement>;
}

function snap(snapper: ISnapper | undefined, pos: Point, element: SModelElement, event: MouseEvent): Point {
    if (!event.shiftKey && snapper) {
        return snapper.snap(pos, element);
    } else {
        return pos;
    }
}
