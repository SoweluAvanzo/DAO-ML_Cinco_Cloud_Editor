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
import { Font, Appearance, Text, Size, Ellipse, HAlignment, VAlignment, Rectangle } from '@cinco-glsp/cinco-glsp-common';

export const UNKNOWN_ELEMENT_CSS = 'cinco-unknown';
export const UNKNOWN_NODE_CSS = UNKNOWN_ELEMENT_CSS + '-node';
export const UNKNOWN_EDGE_CSS = UNKNOWN_ELEMENT_CSS + '-edge';

export const UNKNOWN_WIDTH = 100;
export const UNKNOWN_HEIGHT = 100;

export const UNKNOWN_NODE_SIZE = {
    width: UNKNOWN_WIDTH,
    height: UNKNOWN_HEIGHT
};

export const UNKNOWN_DECORATOR_SIZE = {
    width: UNKNOWN_WIDTH / 4.0,
    height: UNKNOWN_HEIGHT / 4.0
};

export const UNKNOWN_APPEARANCE = {
    background: {
        r: 255,
        g: 100,
        b: 100
    },
    foreground: {
        r: 255,
        g: 100,
        b: 100
    },
    lineWidth: 1,
    font: {
        size: 12
    } as Font
} as Appearance;

export const UNKNOWN_APPEARANCE_TEXT = {
    foreground: {
        r: 255,
        g: 255,
        b: 255
    },
    background: {
        r: 255,
        g: 255,
        b: 255
    },
    lineWidth: 1,
    font: {
        size: 12
    } as Font
} as Appearance;

export const UNKNOWN_ELEMENT_LABEL = {
    type: 'TEXT',
    value: '%s',
    position: {
        horizontal: HAlignment.CENTER,
        vertical: VAlignment.MIDDLE
    },
    appearance: UNKNOWN_APPEARANCE_TEXT
} as Text;

export const UNKNOWN_NODE_SHAPE = {
    type: 'RECTANGLE',
    appearance: UNKNOWN_APPEARANCE,
    children: [UNKNOWN_ELEMENT_LABEL],
    size: UNKNOWN_NODE_SIZE
} as Rectangle;

export const UNKNOWN_DECORATOR_SHAPE = {
    type: 'ELLIPSE',
    appearance: UNKNOWN_APPEARANCE,
    children: [UNKNOWN_ELEMENT_LABEL],
    size: UNKNOWN_NODE_SIZE
} as Ellipse;

export function getUnknownNodeShape(size: Size, label?: string): Rectangle {
    const result = UNKNOWN_NODE_SHAPE;
    if (label && result.children && result.children.length > 0) {
        (result.children![0] as Text).value = label;
    }
    result.size = size;
    return result;
}

export function getUnknownEdgeShape(size: Size, label?: string): Ellipse {
    const result = UNKNOWN_DECORATOR_SHAPE;
    if (label && result.children && result.children.length > 0) {
        (result.children![0] as Text).value = label;
    }
    result.size = size;
    return result;
}
