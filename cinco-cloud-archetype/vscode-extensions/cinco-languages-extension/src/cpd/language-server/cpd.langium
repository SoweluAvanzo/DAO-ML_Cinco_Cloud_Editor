grammar Cpd

entry CincoProduct:
    (annotations+=Annotation)*
    'cincoProduct' name=ID
    '{'
        ('id' id=QNAME)?
		('version' version=STRING)?
		(mgls+=MGLDescriptor)+
		// TODO Is this needed for Cinco Cloud?
		('defaultPerspective' defaultPerspective=QNAME)?
		// TODO Is this needed for Cinco Cloud?
		('splashScreen' splashScreen=SplashScreen)?
		// TODO Which of these images are needed for Cinco Cloud?
		// ('image16' image16=Path)?
		// ('image32' image32=Path)?
		// ('image48' image48=Path)?
		// ('image64' image64=Path)?
		// ('image128' image128=Path)?
		// ('image256' image256=Path)?
		// TODO Are these icons needed for Cinco Cloud?
		('linuxIcon' linuxIcon=Path)?
		('macOSIcon' macOSIcon=Path)?
		('windowsIcon' windowsIcon=Path)?
		// TODO Is this needed for Cinco Cloud?
		('about' '{' about=About '}')?
		// TODO Is this needed for Cinco Cloud?
		('plugins' '{'plugins+=QNAME (','plugins+=QNAME)*'}')?
		// TODO Is this needed for Cinco Cloud?
		('features' '{'features+=QNAME (','features+=QNAME)*'}')?
		// TODO Is this needed for Cinco Cloud?
		('extensionClasses''{'extensionClasses+=QNAME (','extensionClasses+=QNAME)*'}' )?
		// TODO Is this needed for Cinco Cloud?
		('genModels''{'(genModelDescriptions+=GenModelDescription)+'}')?
		('xtextLanguages''{'
			(xtextDescriptions+=XtextDescription)+
		'}')?
    '}'
;

Annotation:
	'@' name=ID ('(' (value+=STRING | value+=ID) (',' (value+=STRING | value+=ID))* ')')?;

About:
	('text' aboutText=STRING
	('imagePath'imagePath=Path)?)
;

Color:
	'textColor' '(' red=INT ',' green=INT ',' blue=INT ')'
;

GenModelDescription:
	'genModel''{' 
		'path' genModelPath=PlatformPath
		(editCode?='withEditCode')?
		(editorCode?='withEditorCode')?
		(testCode?='withTestCode')?
	'}'
;

// TODO Evaluate if unused keywords can be removed entirely
MGLDescriptor:
    (dontGenerate?='generateNOT')? (forceGenerate?='forceGenerate')? 'mgl' mglPath=STRING
;

SplashScreen:
	path=Path
	('{'
		('progressBar' '('pbXOffset=INT ',' pbYOffset=INT ',' pbWidth=INT ',' pbHeight=INT ')')?	
		('progressMessage' '('pmXOffset=INT ',' pmYOffset=INT ',' pmWidth=INT ',' pmHeight=INT ')'
		 (textColor=Color)?
		)?
	'}')?
;

XtextDescription:
	'xtextLanguage' '{'
		'project' projectName=QNAME
	'}'
;

Path returns string:
	('/')?QNAME('/'QNAME)*
;

PlatformPath returns string:
	('platform:/plugin'|'platform:/resource')'/'QNAME('/'QNAME)*
;

hidden terminal WS: /\s+/;
terminal QNAME: /(?!.*\.{2})[a-zA-Z]+\.[a-zA-Z]+(\.[a-zA-Z]+)*/;
terminal ID: /[_a-zA-Z][\w_]*/;
terminal INT returns number: /[0-9]+/;
terminal STRING: /"(\\.|[^"\\])*"|'(\\.|[^'\\])*'/;

hidden terminal ML_COMMENT: /\/\*[\s\S]*?\*\//;
hidden terminal SL_COMMENT: /\/\/[^\n\r]*/;
