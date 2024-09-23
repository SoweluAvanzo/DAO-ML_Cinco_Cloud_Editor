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

export class Direction {

    public static readonly TOP          = new Direction(0, 'TOP',          '↑');
    public static readonly TOP_RIGHT    = new Direction(1, 'TOP_RIGHT',    '↗︎');
    public static readonly RIGHT        = new Direction(2, 'RIGHT',        '→');
    public static readonly BOTTOM_RIGHT = new Direction(3, 'BOTTOM_RIGHT', '↘︎');
    public static readonly BOTTOM       = new Direction(4, 'BOTTOM',       '↓');
    public static readonly BOTTOM_LEFT  = new Direction(5, 'BOTTOM_LEFT',  '↙︎');
    public static readonly LEFT         = new Direction(6, 'LEFT',         '←');
    public static readonly TOP_LEFT     = new Direction(7, 'TOP_LEFT',     '↖︎');

    public static get values(): Direction[] {
        return [
            Direction.TOP,
            Direction.TOP_RIGHT,
            Direction.RIGHT,
            Direction.BOTTOM_RIGHT,
            Direction.BOTTOM,
            Direction.BOTTOM_LEFT,
            Direction.LEFT,
            Direction.TOP_LEFT
        ];
    }

    public readonly index: number;
    public readonly name:  string;
    public readonly arrow: string;

    private constructor(index: number, name: string, arrow: string) {
        this.index = index;
        this.name  = name;
        this.arrow = arrow;
    }

}
