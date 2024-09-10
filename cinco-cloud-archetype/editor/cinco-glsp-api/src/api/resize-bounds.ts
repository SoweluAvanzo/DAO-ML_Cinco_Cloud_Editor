/********************************************************************************
 * Copyright (c) 2024 Cinco Cloud.
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

import { Direction } from './direction';
import { Dimension, Point } from '@eclipse-glsp/server';
import { ResizeArgument } from '@cinco-glsp/cinco-glsp-common';

/**
 * This class helps with determining the directions in which a 2D object has
 * been resized.
 *
 * The calculated values `top`, `right`, `bottom` and `left` describe the
 * movement of the respective edge away form the center. Thus, negative values
 * describe movement towards the center. The attribute `anchor` gives a
 * simplified view and can only convey the resizing of sides and corners. It
 * shows which side or corner the user dragged to resize the object. Resizing
 * opposite sides (e. g. top and bottom) results in `undefined`. You can use
 * `toString()` for visualization.
 *
 * #### Example 1
 *
 * The bottom right corner was dragged 10 pixels to the left and 10 pixels bottom.
 *
 * ```ts
 * var rd = new ResizeBounds(100, 100, 100, 100,
 *                            90, 110, 100, 100);
 * rd.anchor;        // Direction.BOTTOM_RIGHT
 * rd.anchor?.arrow; // '↘︎'
 * rd.toString();
 * ```
 *
 * ```
 *      ±0
 *    ┌─────┐
 * ±0 │     ← -10
 *    └─ ↓ ─┘
 *      +10
 * ```
 *
 * #### Example 2
 *
 * The object was moved and resized programmatically.
 *
 * ```ts
 * rd = new ResizeBounds(100, 100, 100, 100,
 *                        90, 120, 100,  90);
 * rd.anchor;        // undefined
 * rd.anchor?.arrow; // undefined
 * rd.toString();
 * ```
 *
 * ```
 *      +10
 *    ┌─ ↑ ─┐
 * ±0 │     ← -10
 *    └─ ↓ ─┘
 *      +10
 * ```
 */
export class ResizeBounds {

    public readonly oldWidth: number;
    public readonly oldHeight: number;
    public readonly oldSize: Dimension;

    public readonly oldX: number;
    public readonly oldY: number;
    public readonly oldPosition: Point;

    public readonly newWidth: number;
    public readonly newHeight: number;
    public readonly newSize: Dimension;

    public readonly newX: number;
    public readonly newY: number;
    public readonly newPosition: Point;

    public readonly widthDiff: number;
    public readonly heightDiff: number;
    public readonly sizeDiff: Dimension;

    public readonly xDiff: number;
    public readonly yDiff: number;
    public readonly positionDiff: Point;

    /**
     * This value describes the movement of the top edge.
     *
     * A positive value means the edge moved this many pixels away from the center / upwards.
     * A negative value means the edge moved towards the center / downwards.
     */
    public readonly top: number;

    /**
     * This value describes the movement of the right edge.
     *
     * A positive value means the edge moved this many pixels away from the center / to the right.
     * A negative value means the edge moved towards the center / to the left.
     */
    public readonly right: number;

    /**
     * This value describes the movement of the bottom edge.
     *
     * A positive value means the edge moved this many pixels away from the center / downwards.
     * A negative value means the edge moved towards the center / upwards.
     */
    public readonly bottom: number;

    /**
     * This value describes the movement of the left edge.
     *
     * A positive value means the edge moved this many pixels away from the center / to the left.
     * A negative value means the edge moved towards the center / to the right.
     */
    public readonly left: number;

    /**
     * This attribute gives a simplified view and can only convey the resizing of sides and corners.
     *
     * It shows which anchor (sides or corners) the user dragged to resize the object.
     * Resizing opposite sides (e. g. top and bottom) will result in `undefined`.
     */
    public readonly anchor: Direction | undefined;

    public constructor(oldWidth: number, oldHeight: number, oldX: number, oldY: number,
                       newWidth: number, newHeight: number, newX: number, newY: number) {

        this.oldWidth       = oldWidth;
        this.oldHeight      = oldHeight;
        this.oldSize        = { width: oldWidth, height: oldHeight };

        this.oldX           = oldX;
        this.oldY           = oldY;
        this.oldPosition    = { x: oldX, y: oldY };

        this.newWidth       = newWidth;
        this.newHeight      = newHeight;
        this.newSize        = { width: newWidth, height: newHeight };

        this.newX           = newX;
        this.newY           = newY;
        this.newPosition    = { x: newX, y: newY };

        this.widthDiff      = newWidth  - oldWidth;
        this.heightDiff     = newHeight - oldHeight;
        this.sizeDiff       = {width: this.widthDiff, height: this.heightDiff};

        this.xDiff          = newX - oldX;
        this.yDiff          = newY - oldY;
        this.positionDiff   = {x: this.xDiff, y: this.yDiff};

        this.top            = oldY - newY;
        this.left           = oldX - newX;
        this.bottom         = (newHeight - oldHeight) - this.top;
        this.right          = (newWidth  - oldWidth ) - this.left;

        this.anchor =
            ( this.top && !this.right && !this.bottom && !this.left) ? Direction.TOP          :
            ( this.top &&  this.right && !this.bottom && !this.left) ? Direction.TOP_RIGHT    :
            (!this.top &&  this.right && !this.bottom && !this.left) ? Direction.RIGHT        :
            (!this.top &&  this.right &&  this.bottom && !this.left) ? Direction.BOTTOM_RIGHT :
            (!this.top && !this.right &&  this.bottom && !this.left) ? Direction.BOTTOM       :
            (!this.top && !this.right &&  this.bottom &&  this.left) ? Direction.BOTTOM_LEFT  :
            (!this.top && !this.right && !this.bottom &&  this.left) ? Direction.LEFT         :
            ( this.top && !this.right && !this.bottom &&  this.left) ? Direction.TOP_LEFT     :
            undefined;

    }

    public static fromResizeArgument(resizeArgument: ResizeArgument): ResizeBounds {
        return new ResizeBounds(
            resizeArgument.oldSize.width,
            resizeArgument.oldSize.height,
            resizeArgument.oldPosition.x,
            resizeArgument.oldPosition.y,
            resizeArgument.newSize.width,
            resizeArgument.newSize.height,
            resizeArgument.newPosition.x,
            resizeArgument.newPosition.y
        );
    }

    public toString(): string {
        const format = (x: number) => (x > 0) ? `+${x}` : (x === 0) ? `±${x}` : `${x}`;
        const top    = format(this.top);
        const right  = format(this.right);
        const bottom = format(this.bottom);
        const left   = format(this.left);
        const topArrow    = (this.top    > 0) ? ' ↑ ' : (this.top    === 0) ? '───' : ' ↓ ';
        const rightArrow  = (this.right  > 0) ?  '→'  : (this.right  === 0) ?  '│'  :  '←' ;
        const bottomArrow = (this.bottom > 0) ? ' ↓ ' : (this.bottom === 0) ? '───' : ' ↑ ';
        const leftArrow   = (this.left   > 0) ?  '←'  : (this.left   === 0) ?  '│'  :  '→' ;
        const spaces = ' '.repeat(left.length);
        return `
${spaces}  ${top}
${spaces} ┌─${topArrow}─┐
${left} ${leftArrow}     ${rightArrow} ${right}
${spaces} └─${bottomArrow}─┘
${spaces}  ${bottom}
`;
    }

}
