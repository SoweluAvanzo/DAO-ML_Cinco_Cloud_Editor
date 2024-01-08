import { Size } from '../../generated/ast';

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
	palettes: Object[];
	view: Object;
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
    customTypes: CustomType[];
	graphTypes: GraphType[];
	nodeTypes: NodeType[];
	styles: Style[];
}


export interface CustomType extends Element {}

export interface Enum extends CustomType {
    literals: string[];
}

export interface UserDefinedType extends CustomType {
    attributes: Attribute[];
}

export interface Attribute {
    name: string;
    type: string;
    bounds?: Constraint;
    final?: boolean;
    unique?: false;
    defaultValue?: string;
    annotations?: Annotation[];
}

export interface Constraint {
    // intervall
    lowerBound: number; // n >= 0 || * := n < 0
    upperBound: number; // n >= 0 || * := n < 0

    // associated elements
    elements?: string[];
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
