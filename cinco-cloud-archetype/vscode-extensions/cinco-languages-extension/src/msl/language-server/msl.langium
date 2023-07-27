grammar Msl

entry Styles:
    (appearances+=Appearance)*
    (styles += Style)+
;

Style:
    NodeStyle | EdgeStyle;

NodeStyle:
    'nodeStyle' name=ID ('(' parameterCount=INT ')')? '{'
		('appearanceProvider' '('appearanceProvider=STRING')')? 
		(fixed?='fixed')?
		mainShape=AbstractShape
	'}';

EdgeStyle:
    'edgeStyle' name=ID ('(' parameterCount=INT ')')? '{'
		('appearanceProvider' '('appearanceProvider=STRING')')? 
		(('appearance' referencedAppearance=[Appearance]) | (inlineAppearance=InlineAppearance) )?
		('type' connectionType=ConnectionType)?
		(decorator+=ConnectionDecorator)*
	'}';

AbstractShape:
    ContainerShape | Shape;

ContainerShape:
    Rectangle | RoundedRectangle | Ellipse | Polygon;

GraphicsAlgorithm:
    Text | MultiText | Image | Polyline | Ellipse | Polygon;

Shape:
    Text | MultiText | Image | Polyline;

Rectangle:
    (anchorShape?='anchor')? 'rectangle' (name=STRING)?
	'{'
		(('appearance' referencedAppearance=[Appearance]) | (inlineAppearance=InlineAppearance) )? 
		('position' position = AbstractPosition)?
		('size' size = Size)?
		(children+=AbstractShape)*
	'}';

RoundedRectangle:
    (anchorShape?='anchor')? 'roundedRectangle' (name=STRING)?
	'{'
		(('appearance' referencedAppearance=[Appearance]) | (inlineAppearance=InlineAppearance) )? 
		('position' position = AbstractPosition)?
		'size' size = Size
		'corner' '('cornerWidth=INT','cornerHeight=INT')'
		(children+=AbstractShape)*
	'}';

Ellipse:
    (anchorShape?='anchor')? 'ellipse' (name=STRING)?
	'{'
		(('appearance' referencedAppearance=[Appearance]) | (inlineAppearance=InlineAppearance) )? 
		('position' position = AbstractPosition)?
		('size' size = Size)?
		(children+=AbstractShape)*
	'}';

Polygon:
    (anchorShape?='anchor')? 'polygon' (name=STRING)?
	'{'
		(('appearance' referencedAppearance=[Appearance]) | (inlineAppearance=InlineAppearance) )? 
		('position' position = AbstractPosition)?
		'points' '['(points+=Point)*']'
		('size' size = Size)?
		(children+=AbstractShape)*
	'}';

Text:
    (anchorShape?='anchor')? 'text' (name=STRING)?
	'{'
		(('appearance' referencedAppearance=[Appearance]) | (inlineAppearance=InlineAppearance) )? 
		('position' position = AbstractPosition)?
		'value' value=STRING?
	'}';

MultiText:
    (anchorShape?='anchor')? 'multiText' (name=STRING)?
    '{'
		(('appearance' referencedAppearance=[Appearance]) | (inlineAppearance=InlineAppearance) )? 
		('position' position = AbstractPosition)?
		'value' value = STRING
	'}';

Image:
(anchorShape?='anchor')? 'image' (name=STRING)?
	'{'
		('position' position = AbstractPosition)?
		'size' size = Size
		'path' '('path=STRING')'
	'}';

Polyline:
    (anchorShape?='anchor')? 'polyline' (name=STRING)?
	'{'
		(('appearance' referencedAppearance=[Appearance]) | (inlineAppearance=InlineAppearance) )? 
		'points' '[' (points+=Point)* ']'	
		('size' size = Size)?
	'}';

AbstractPosition:
    AbsolutePosition | Alignment;

AbsolutePosition:
    '('xPos=INT','yPos=INT')';

Alignment:
    '('horizontal=HAlignment (xMargin=INT)? ',' vertical=VAlignment (yMargin=INT)?')';

Point:
    '('x=INT','y=INT')';

Size:
    '(' (widthFixed?='fix')? width=INT','(heightFixed?='fix')? height=INT')';

ConnectionDecorator:
    'decorator' (name=ID)? '{'
		('location' '(' location=DOUBLE ')'
		(movable?='movable')?
		(decoratorShape=GraphicsAlgorithm | predefinedDecorator=PredefinedDecorator))
	'}';

PredefinedDecorator:
    shape=DecoratorShape (('appearance' referencedAppearance=[Appearance]) | (inlineAppearance=InlineAppearance) )?;

Color:
    "("r=INT","g=INT","b=INT")";

Appearance:
    'appearance' name=ID ('extends' parent=[Appearance])? '{'
		AppearanceFragment
	'}';

InlineAppearance:
    'appearance' ('extends' parent=[Appearance])? '{'
		AppearanceFragment
	'}';

fragment AppearanceFragment:
		('background' background=Color)?
		('foreground' foreground=Color)?
		('font' font=Font)?
		('lineStyle' lineStyle=LineStyle)?
		('lineWidth' lineWidth=INT)?
		('transparency' transparency=DOUBLE)?
		('filled' filled = Boolean)?
		('imagePath' '('imagePath=STRING')')?;

Font:
    '('fontName=STRING(','isBold?='BOLD')?(','isItalic?='ITALIC')?','size=INT')';

DecoratorShape:
	ARROW='ARROW' | DIAMOND='DIAMOND' | CIRCLE='CIRCLE' | TRIANGLE='TRIANGLE'
;

ContainerAlignment:
	HORIZONTAL='HORIZONTAL' | VERTICAL='VERTICAL'
;
HAlignment: 
 	LEFT='LEFT' | RIGHT='RIGHT' | CENTER='CENTER'
 ;
 
VAlignment:
	BOTTOM='BOTTOM' | MIDDLE='MIDDLE' | TOP='TOP'
;
 
LineStyle:
	DASH='DASH' | DASHDOT='DASHDOT' | DASHDOTDOT='DASHDOTDOT' | DOT='DOT' | SOLID='SOLID'
;

Boolean:
	TRUE='true' | FALSE='false'
;

ConnectionType:
	FreeForm = 'freeform'
;

hidden terminal WS: /\s+/;
terminal QNAME: /(?!.*\.{2})[a-zA-Z]+\.[a-zA-Z]+(\.[a-zA-Z]+)*/;
terminal ID: /[_a-zA-Z][\w_]*/;
terminal DOUBLE returns number: /[0-9]*\.[0-9]+/;
terminal INT returns number: /(-)?[0-9]+/;
terminal STRING: /"(\\.|[^"\\])*"|'(\\.|[^'\\])*'/;

hidden terminal ML_COMMENT: /\/\*[\s\S]*?\*\//;
hidden terminal SL_COMMENT: /\/\/[^\n\r]*/;
