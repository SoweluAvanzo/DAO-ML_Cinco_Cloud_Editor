grammar Mgl

entry MglModel:
    (imports+=Import)*
    (annotations+=Annotation)*
    ('id' package=QNAME)?
    'stylePath' stylePath=STRING
	('includeResources' '(' includeResources+=STRING (',' includeResources+=STRING)* ')')?
    modelElements+=ModelElement*
;

Annotation:
	'@' name=ID ('(' (value+=STRING | value+=ID) (',' (value+=STRING | value+=ID))* ')')?;

Attribute:
	PrimitiveAttribute
	| ComplexAttribute
;

ComplexAttribute:
	(annotations+=Annotation)*
	(notChangeable?='final')? (unique?='unique')? (override?='override')? 'attr' type=[CustomDataType:ID] 'as' name=ID ('[' lowerBound=INT (',' upperBound=('*'|INT))? ']')? (':'? '=' defaultValue=STRING)?
;

CustomDataType:
	Enum
	| UserDefinedType
    | NodeType
    | Edge
	| GraphModel
;

Edge:
	(annotations+=Annotation)*
	(isAbstract?='abstract')?
	'edge' name=ID ('extends' (externalExtension=ExternalReference | localExtension=[Edge:ID]))?
	('{'
		('style' usedStyle=ID ('(' styleParameters+=STRING (',' styleParameters+=STRING)* ')')? )?
		(attributes+=Attribute | defaultValueOverrides+=DefaultValueOverride)*
	'}')?
;

EdgeElementConnection:
	(
		externalConnection=ExternalReference
		| localConnection+=[Edge:ID]
        | '{' localConnection+=[Edge:ID] (',' localConnection+=[Edge:ID])* '}'
	)
	('[' lowerBound=INT ',' upperBound=('*'|INT) ']')?
;

Enum:
	(annotations+=Annotation)*
	'enum' name=ID
	'{'
		(literals+=STRING | literals+=ID)+
	'}'
;

ExternalReference:
    import=[Import] '::' (elements+=ID | '{' elements+=ID (',' elements+=ID)* '}');

DefaultValueOverride:
	'override' attribute=[PrimitiveAttribute:ID] (':'? '=' defaultValue=STRING)?
;

GraphicalElementContainment:
	(
		externalContainment=ExternalReference
		| localContainments+=[NodeType:ID]
        | ('{' localContainments+=[NodeType:ID] (',' localContainments+=[NodeType:ID])* '}')
	)
	('[' lowerBound=INT ',' upperBound=('*'|INT) ']')?
;

GraphModel:
	(annotations+=Annotation)*
	(isAbstract?='abstract')?
	'graphModel' name=ID ('extends' (externalExtension=ExternalReference | localExtension=[GraphModel:ID]))?
	('{'
		('iconPath' iconPath=STRING)?
		('diagramExtension' fileExtension=STRING)?
		('containableElements' '('( (containableElements+=GraphicalElementContainment | containmentWildcards+=Wildcard) (',' (containableElements+=GraphicalElementContainment | containmentWildcards+=Wildcard))* )')')?
        (attributes+=Attribute | defaultValueOverrides+=DefaultValueOverride)*
	'}')
;

Import:
    (stealth?='stealth')? 'import' (external?='external')? importURI=STRING 'as' name=ID;

ModelElement:
    NodeType
    | Edge
    | CustomDataType
	| GraphModel
;

Node:
	(annotations+=Annotation)*
	(isAbstract?='abstract')?
	'node' name=ID ('extends' (externalExtension=ExternalReference | localExtension=[Node:ID]))?
	('{'(
	    ('style' usedStyle=ID ('(' styleParameters+=STRING (',' styleParameters+=STRING)* ')')? )?
	    ('incomingEdges' '(' (incomingEdgeConnections+=EdgeElementConnection | incomingWildcards+=Wildcard) (',' (incomingEdgeConnections+=EdgeElementConnection | incomingWildcards+=Wildcard))* ')')?
	 	('outgoingEdges' '(' (outgoingEdgeConnections+=EdgeElementConnection | outgoingWildcards+=Wildcard) (',' (outgoingEdgeConnections+=EdgeElementConnection | outgoingWildcards+=Wildcard))* ')')?
	 	(primeReference=(ReferencedEClass|ReferencedModelElement))?
	 	(attributes+=Attribute | defaultValueOverrides+=DefaultValueOverride)*
	)'}')?
;

NodeContainer:
	(annotations+=Annotation)*
	(isAbstract?='abstract')?
	'container' name=ID ('extends' (externalExtension=ExternalReference | localExtension=[NodeType:ID]))?
	('{'(
	    ('style' usedStyle=ID ('(' styleParameters+=STRING (',' styleParameters+=STRING)* ')')? )?
	    ('incomingEdges' '(' (incomingEdgeConnections+=EdgeElementConnection | incomingWildcards+=Wildcard) (',' (incomingEdgeConnections+=EdgeElementConnection | incomingWildcards+=Wildcard))* ')')?
	 	('outgoingEdges' '(' (outgoingEdgeConnections+=EdgeElementConnection | outgoingWildcards+=Wildcard) (',' (outgoingEdgeConnections+=EdgeElementConnection | outgoingWildcards+=Wildcard))* ')')?
		('containableElements' '('( (containableElements+=GraphicalElementContainment | containmentWildcards+=Wildcard) (',' (containableElements+=GraphicalElementContainment | containmentWildcards+=Wildcard))* )')')?
	 	(primeReference=(ReferencedEClass|ReferencedModelElement))?
	 	(attributes+=Attribute | defaultValueOverrides+=DefaultValueOverride)*
	)'}')?
;

NodeType:
	Node
	| NodeContainer;

PrimitiveAttribute:
	(annotations+=Annotation)*
	(notChangeable?='final')? (unique?='unique')? 'attr' dataType=DataType 'as' name=ID ('[' lowerBound=INT (',' upperBound=('*'|INT))? ']')? (':'? '=' defaultValue=(DefaultValue))?
;

// TODO Review this if Ecore is ever being removing
ReferencedEClass:
	(annotations+=Annotation)*
    // TODO Somehow this requires a space between the Import ref and the dot
	'prime' import=[Import:ID] '.' eClass=ID 'as' name=ID
	(copiedAttributes+=ReferencedEStructuralFeature)*
;

ReferencedEStructuralFeature:
	(parameter?='primeparam' | 'primeattr') feature=QNAME 'as' name=ID
;

ReferencedMGLAttribute:
	'primeattr' feature=[Attribute:ID] 'as' name=ID
;

ReferencedModelElement:
	(annotations+=Annotation)*
	'prime' ((import=[Import] '::') referencedModelElement=ID | 'this' '::' modelElement=[ModelElement:ID]) 'as' name=ID
	(copiedAttributes+=ReferencedMGLAttribute)*
;

UserDefinedType:
	(annotations+=Annotation)*
	(isAbstract?='abstract')?
	'type' name=ID ('extends' (externalExtension=ExternalReference | localExtension=[UserDefinedType:ID]))?
	('{'(
		(attributes+=Attribute | defaultValueOverrides+=DefaultValueOverride)*
	)'}')?
;

Wildcard:
	(selfWildcard?='*' | (referencedImport=[Import] '::' '*'))
	('[' lowerBound=INT ',' upperBound=('*'|INT) ']')?
;

DataType returns string:
    'string'
    | 'boolean'
    | 'number'
    | 'Date'
;

DefaultValue returns string:
	INT | NUMBER | STRING | BOOL
;

hidden terminal WS: /\s+/;
terminal BOOL: /true|false|TRUE|FALSE|True|False/;
terminal QNAME: /(?!.*\.{2})[a-zA-Z]+\.[a-zA-Z]+(\.[a-zA-Z]+)*/;
terminal ID: /[_a-zA-Z][\w_]*/;
terminal INT returns number: /[0-9]+/;
terminal NUMBER returns number: /[0-9]+(\.[0-9]+)?/;
terminal STRING: /"(\\.|[^"\\])*"|'(\\.|[^'\\])*'/;

hidden terminal ML_COMMENT: /\/\*[\s\S]*?\*\//;
hidden terminal SL_COMMENT: /\/\/[^\n\r]*/;
