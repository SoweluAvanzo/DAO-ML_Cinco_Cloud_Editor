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
import { Size } from '../../generated/ast.js';

interface Annotation {
    name: string;
    values: string[];
}

interface Appearance {
    background: RgbColor;
    imagePath: string;
    filled: boolean;
    font?: string;
    foreground?: RgbColor;
    lineWidth: number;
    lineStyle: string;
    name: string;
    parent: string;
    transparency: string;
}

interface Containable {
    containments: Containment[];
}

export interface ContainerType extends Containable, NodeType {}

interface Containment {
    lowerBound: number;
    upperBound: number;
    elements: string[];
}

interface Decorator {
    location: number;
}

interface EdgeType extends Element {}

interface EdgeElementConnection {
    lowerBound: number;
    upperBound: number;
    elements: string[];
}

export interface EdgeStyle extends Style {
    connectionType?: string;
    decorator: Decorator;
    predefinedDecorator: PredefinedDecorator;
    styleType: 'EdgeStyle';
}

interface Element {
    elementTypeId: string;
    type: string;
    annotations: Annotation[];
}

interface GraphType extends Containable, Element {}

export interface NodeStyle extends Style {
    fixed: boolean;
    styleType: 'NodeStyle';
}

interface NodeType extends Element {
    deletable: boolean;
    height: number;
    incomingEdges: EdgeElementConnection[];
    outgoingEdges: EdgeElementConnection[];
    width: number;

    // TODO properly type
    palettes: unknown[];
    view: unknown;
}

interface PredefinedDecorator {
    appearance: string;
    shape: 'DecoratorShape.ARROW' | 'DecoratorShape.CIRCLE' | 'DecoratorShape.DIAMOND' | 'DecoratorShape.TRIANGLE';
}

interface RgbColor {
    b: number;
    g: number;
    r: number;
}

export interface Specification {
    appearances: Appearance[];
    edgeTypes: EdgeType[];
    graphTypes: GraphType[];
    nodeTypes: NodeType[];
    styles: Style[];
}

interface Style {
    name: string;
    appearance: string;
    parameterCount: number;
    shape: Shape;
}

interface Shape {
    type: string;
    size: Size;
}
