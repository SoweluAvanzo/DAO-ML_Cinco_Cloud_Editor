grammar Msl

entry Styles:
    (
       (styles+=Style) | (appearances+=Appearance)
    )*
;

Style:
	NodeStyle | EdgeStyle;

NodeStyle:
	'nodeStyle' name=ID ('(' parameterCount=INT ')')? '{'
        (
            ('appearanceProvider' '('appearanceProvider=STRING')')
            & (fixed?='fixed')
            & mainShape=AbstractShape
        )
	'}';

EdgeStyle:
	'edgeStyle' name=ID ('(' parameterCount=INT ')')? '{'
        (
            ('appearanceProvider' '('appearanceProvider=STRING')')
            & ( ('appearance' referencedAppearance=[Appearance]) | (inlineAppearance=InlineAppearance) )
            // TODO: Reevaluate the connectionType property
            & ('type' connectionType=ConnectionType)
            & (decorator+=ConnectionDecorator)+
        )
	'}';

AbstractShape:
	ContainerShape | Shape;

ContainerShape:
	Rectangle | RoundedRectangle | Ellipse | Polygon;

GraphicsAlgorithm:
	Text | MultiText | Image | Polyline | Ellipse | Polygon | WebView;

Shape:
	Text | MultiText | Image | Polyline | WebView;

Rectangle:
	(anchorShape?='anchor')? 'rectangle' (name=STRING)?
	'{'
		(
            ( ('appearance' referencedAppearance=[Appearance]) | (inlineAppearance=InlineAppearance) )
            & ('position' position=AbstractPosition)
            & ('size' size=Size)
            & (children+=AbstractShape)+
        )
	'}';

RoundedRectangle:
	(anchorShape?='anchor')? 'roundedRectangle' (name=STRING)?
	'{'
        (
            ( ('appearance' referencedAppearance=[Appearance]) | (inlineAppearance=InlineAppearance) )
            & ('position' position=AbstractPosition)
            & ('size' size=Size)
            & ('corner' '(' cornerWidth=INT ',' cornerHeight=INT ')')
            & (children+=AbstractShape)+
        )
	'}';

Ellipse:
	(anchorShape?='anchor')? 'ellipse' (name=STRING)?
	'{'
        (
            ( ('appearance' referencedAppearance=[Appearance]) | (inlineAppearance=InlineAppearance) )
            & ('position' position=AbstractPosition)
            & ('size' size=Size)
            & (children+=AbstractShape)+
        )
	'}';

Polygon:
	(anchorShape?='anchor')? 'polygon' (name=STRING)?
	'{'
        (
            ( ('appearance' referencedAppearance=[Appearance]) | (inlineAppearance=InlineAppearance) )
            & ('position' position=AbstractPosition)
            & ('points' '['(points+=Point)*']')
            & ('size' size=Size)
            & (children+=AbstractShape)+
        )
	'}';

Text:
	(anchorShape?='anchor')? 'text' (name=STRING)?
	'{'
        (
            ( ('appearance' referencedAppearance=[Appearance]) | (inlineAppearance=InlineAppearance) )
            & ('position' position=AbstractPosition)
            & ('value' value=STRING)
        )
	'}';

MultiText:
	(anchorShape?='anchor')? 'multiText' (name=STRING)?
    '{'
        (
            ( ('appearance' referencedAppearance=[Appearance]) | (inlineAppearance=InlineAppearance) )
            & ('position' position=AbstractPosition)
            & ('value' value=STRING)
        )
	'}';

Image:
	(anchorShape?='anchor')? 'image' (name=STRING)?
	'{'
        (
            ('position' position=AbstractPosition)
            & ('size' size=Size)
            & ('path' '('path=STRING')')
        )
	'}';

WebView:
	(anchorShape?='anchor')? 'webView' (name=STRING)?
	'{'
        (
            ('position' position=AbstractPosition)
            & ('size' size=Size)
            & ('scrollable' scrollable=Boolean)
            & ('padding' padding=INT)
            & ('content' '(' content=STRING ')')
        )
	'}';

Polyline:
	(anchorShape?='anchor')? 'polyline' (name=STRING)?
	'{'
        (
            (('appearance' referencedAppearance=[Appearance]) | (inlineAppearance=InlineAppearance) )
            & ('points' '[' (points+=Point)* ']')
            & ('size' size=Size)
        )
	'}';

AbstractPosition:
	AbsolutePosition | Alignment;

AbsolutePosition:
	'('xPos=INT','yPos=INT')';

Alignment:
	'('
        (
            horizontal=HAlignment (xMargin=INT)? ',' vertical=VAlignment (yMargin=INT)?
        )
    ')';

Point:
	'('x=INT','y=INT')';

Size:
	'(' (widthFixed?='fix')? width=INT','(heightFixed?='fix')? height=INT')';

ConnectionDecorator:
	'decorator' (name=ID)? '{'
        (
            ('location' '(' location=DOUBLE ')'
            & (movable?='movable')
            & (decoratorShape=GraphicsAlgorithm | predefinedDecorator=PredefinedDecorator))
        )
	'}';

PredefinedDecorator:
	shape=DecoratorShape (('appearance' referencedAppearance=[Appearance]) | (inlineAppearance=InlineAppearance) )?;

Color:
	"(" r=INT "," g=INT "," b=INT ")";

Appearance:
	'appearance' name=ID ('extends' parent=[Appearance])? '{'
		AppearanceFragment
	'}';

InlineAppearance:
	'appearance' ('extends' parent=[Appearance])? '{'
		AppearanceFragment
	'}';

fragment AppearanceFragment:
    (
        ('background' background=Color)
        & ('filled' filled=Boolean)
        & ('foreground' foreground=Color)
        & ('font' font=Font)
        & ('imagePath' '('imagePath=STRING')')
        & ('lineStyle' lineStyle=LineStyle)
        & ('lineWidth' lineWidth=INT)
        & ('transparency' transparency=DOUBLE)
    )
;

Font:
    fontName=STRING
    | fontName=STRING '('
        (
            (isBold?='BOLD')
            & (isItalic?='ITALIC')
            & (size=INT)
        )
    ')' 
    | '('
        fontName=STRING
        (
            (',' isBold?='BOLD')
            & (',' isItalic?='ITALIC')
            & (',' size=INT)
        )
    ')'
;

DecoratorShape:
	shapeType='ARROW' | shapeType='DIAMOND' | shapeType='CIRCLE' | shapeType='TRIANGLE'
;

HAlignment:
	alignmentType='LEFT' | alignmentType='RIGHT' | alignmentType='CENTER'
;

VAlignment:
	alignmentType='BOTTOM' | alignmentType='MIDDLE' | alignmentType='TOP'
;

LineStyle:
	lineType='DASH' | lineType='DASHDOT' | lineType='DASHDOTDOT' | lineType='DOT' | lineType='SOLID'
;

Boolean:
	value='true' | value='false'
;

ConnectionType:
	FreeForm='freeform'
;

hidden terminal WS: /\s+/;
terminal QNAME: /(?!.*\.{2})[a-zA-Z]+\.[a-zA-Z]+(\.[a-zA-Z]+)*/;
terminal ID: /[_a-zA-Z][\w_]*/;
terminal DOUBLE returns number: /[0-9]*\.[0-9]+/;
terminal INT returns number: /(-)?[0-9]+/;
terminal STRING: /"(\\.|[^"\\])*"|'(\\.|[^'\\])*'/;

hidden terminal ML_COMMENT: /\/\*[\s\S]*?\*\//;
hidden terminal SL_COMMENT: /\/\/[^\n\r]*/;
