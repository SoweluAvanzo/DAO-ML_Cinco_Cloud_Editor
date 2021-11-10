package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.rest

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.Annotatable
import mgl.Attribute
import mgl.ContainingElement
import mgl.Edge
import mgl.Enumeration
import mgl.GraphModel
import mgl.GraphicalModelElement
import mgl.MGLModel
import mgl.ModelElement
import mgl.Node
import mgl.NodeContainer
import mgl.UserDefinedType
import style.Styles

class GraphModelRestTO extends Generatable{
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(Annotatable t)'''«t.name.fuEscapeJava».java'''
	
	def content(ModelElement t,Styles styles) {
		val modelPackage = t.modelPackage as MGLModel
		'''
			package «modelPackage.restFQNBase»;
			
			/**
			 * Author zweihoff
			 */
			
			import «modelPackage.typeRegistryFQN»;
			import «dbTypeFQN»;
			
			@com.fasterxml.jackson.annotation.JsonTypeName("«t.restFQN»")
			public «IF t.isIsAbstract»abstract «ENDIF»class «t.name.fuEscapeJava» «IF t.isExtending»extends«ELSE»implements«ENDIF» «t.restExtending»«IF t.hasToExtendContainer» implements info.scce.pyro.core.graphmodel.Container«ENDIF»
			{
				«t.attributes.map[attributeDeclaration(modelPackage)].join("\n")»
				
				private String __type;
				
				@com.fasterxml.jackson.annotation.JsonProperty("__type")
				public String get__type() {
					return this.__type;
				}
				
				@com.fasterxml.jackson.annotation.JsonProperty("__type")
				public void set__type(final String __type) {
					this.__type = __type;
				}
				
				@com.fasterxml.jackson.annotation.JsonProperty(info.scce.pyro.util.Constants.PYRO_ID)
				private long id;
				
				@Override
				public long getId() {
					return id;
				}
				
				@Override
				public void setId(long id) {
					this.id = id;
				}
				«IF t instanceof GraphicalModelElement»
					
					private info.scce.pyro.core.graphmodel.IdentifiableElement container;
					
					@com.fasterxml.jackson.annotation.JsonProperty("a_container")
					public info.scce.pyro.core.graphmodel.IdentifiableElement getcontainer() {
						return this.container;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("container")
					public void setcontainer(final info.scce.pyro.core.graphmodel.IdentifiableElement container) {
						this.container = container;
					}
				«ENDIF»
				«IF t instanceof Node»
					«IF t.prime»
						«{
							val refElem = t.primeReference.type
							'''
								protected «refElem.restFQN» «t.primeReference.name.escapeJava»;
								
								@com.fasterxml.jackson.annotation.JsonProperty("b_«t.primeReference.name.escapeJava»")
								public «refElem.restFQN» get«t.primeReference.name.escapeJava»() {
								    return this.«t.primeReference.name.escapeJava»;
								}
								
								@com.fasterxml.jackson.annotation.JsonProperty("«t.primeReference.name.escapeJava»")
								public void set«t.primeReference.name.escapeJava»(final «refElem.restFQN» «t.primeReference.name.escapeJava») {
								    this.«t.primeReference.name.escapeJava» = «t.primeReference.name.escapeJava»;
								}
							'''
						}»
					«ENDIF»
					
					private long x;
					
					@com.fasterxml.jackson.annotation.JsonProperty("a_x")
					public long getx() {
						return this.x;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("x")
					public void setx(final long x) {
						this.x = x;
					}
					
					private long y;
					
					@com.fasterxml.jackson.annotation.JsonProperty("a_y")
					public long gety() {
						return this.y;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("y")
					public void sety(final long y) {
						this.y = y;
					}
					
					private long angle;
					
					@com.fasterxml.jackson.annotation.JsonProperty("a_angle")
					public long getangle() {
						return this.angle;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("angle")
					public void setangle(final long angle) {
						this.angle = angle;
					}
					
					private long width;
					
					@com.fasterxml.jackson.annotation.JsonProperty("a_width")
					public long getwidth() {
						return this.width;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("width")
					public void setwidth(final long width) {
						this.width = width;
					}
					
					private long height;
					
					@com.fasterxml.jackson.annotation.JsonProperty("a_height")
					public long getheight() {
						return this.height;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("height")
					public void setheight(final long height) {
						this.height = height;
					}
					
					private java.util.List<info.scce.pyro.core.graphmodel.Edge> incoming;
					
					@com.fasterxml.jackson.annotation.JsonProperty("a_incoming")
					public java.util.List<info.scce.pyro.core.graphmodel.Edge> getincoming() {
						return this.incoming;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("incoming")
					public void setincoming(final java.util.List<info.scce.pyro.core.graphmodel.Edge> incoming) {
						this.incoming = incoming;
					}
					
					private java.util.List<info.scce.pyro.core.graphmodel.Edge> outgoing;
					
					@com.fasterxml.jackson.annotation.JsonProperty("a_outgoing")
					public java.util.List<info.scce.pyro.core.graphmodel.Edge> getoutgoing() {
						return this.outgoing;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("outgoing")
					public void setoutgoing(final java.util.List<info.scce.pyro.core.graphmodel.Edge> outgoing) {
						this.outgoing = outgoing;
					}
				«ENDIF»
				«IF t instanceof Edge»
					private info.scce.pyro.core.graphmodel.Node sourceElement;
					
					@com.fasterxml.jackson.annotation.JsonProperty("a_sourceElement")
					public info.scce.pyro.core.graphmodel.Node getsourceElement() {
					    return this.sourceElement;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("sourceElement")
					public void setsourceElement(final info.scce.pyro.core.graphmodel.Node sourceElement) {
					    this.sourceElement = sourceElement;
					}
					
					private info.scce.pyro.core.graphmodel.Node targetElement;
					
					@com.fasterxml.jackson.annotation.JsonProperty("a_targetElement")
					public info.scce.pyro.core.graphmodel.Node gettargetElement() {
					    return this.targetElement;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("targetElement")
					public void settargetElement(final info.scce.pyro.core.graphmodel.Node targetElement) {
					    this.targetElement = targetElement;
					}
					
					private java.util.List<info.scce.pyro.core.graphmodel.BendingPoint> bendingPoints;
					
					@com.fasterxml.jackson.annotation.JsonProperty("a_bendingPoints")
					public java.util.List<info.scce.pyro.core.graphmodel.BendingPoint> getbendingPoints() {
					    return this.bendingPoints;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("bendingPoints")
					public void setbendingPoints(final java.util.List<info.scce.pyro.core.graphmodel.BendingPoint> bendingPoints) {
					    this.bendingPoints = bendingPoints;
					}
					
				«ENDIF»
				«IF t instanceof ContainingElement»
					private java.util.List<info.scce.pyro.core.graphmodel.ModelElement> modelElements = new java.util.LinkedList<>();
					
					@com.fasterxml.jackson.annotation.JsonProperty("a_modelElements")
					public java.util.List<info.scce.pyro.core.graphmodel.ModelElement> getmodelElements() {
					   return this.modelElements;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("modelElements")
					public void setmodelElements(final java.util.List<info.scce.pyro.core.graphmodel.ModelElement> modelElements) {
					   this.modelElements = modelElements;
					}
				«ENDIF»
				«IF t instanceof GraphModel»
					private Double scale;
					
					@com.fasterxml.jackson.annotation.JsonProperty("a_scale")
					public Double getscale() {
					    return this.scale;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("scale")
					public void setscale(final Double scale) {
					    this.scale = scale;
					}
					
					private Long width;
					
					@com.fasterxml.jackson.annotation.JsonProperty("a_width")
					public Long getwidth() {
					    return this.width;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("width")
					public void setwidth(final Long width) {
					    this.width = width;
					}
					
					private Long height;
					
					@com.fasterxml.jackson.annotation.JsonProperty("a_height")
					public Long getheight() {
					    return this.height;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("height")
					public void setheight(final Long height) {
					    this.height = height;
					}
					
					private String filename;
					
					@com.fasterxml.jackson.annotation.JsonProperty("filename")
					public String getfilename() {
					    return this.filename;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("filename")
					public void setfilename(final String filename) {
					    this.filename = filename;
					}
					
					private boolean isPublic;
									
					@com.fasterxml.jackson.annotation.JsonProperty("isPublic")
					public boolean getisPublic() {
					    return this.isPublic;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("isPublic")
					public void setisPublic(final boolean isPublic) {
					    this.isPublic = isPublic;
					}
					
					private String extension;
					
					@com.fasterxml.jackson.annotation.JsonProperty("extension")
					public String getextension() {
					    return this.extension;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("extension")
					public void setextension(final String extension) {
					    this.extension = extension;
					}
					
					
					private String router;
					
					@com.fasterxml.jackson.annotation.JsonProperty("a_router")
					public String getrouter() {
					    return this.router;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("router")
					public void setrouter(final String router) {
					    this.router = router;
					}
					
					private String connector;
					
					@com.fasterxml.jackson.annotation.JsonProperty("a_connector")
					public String getconnector() {
					    return this.connector;
					}
					
					@com.fasterxml.jackson.annotation.JsonProperty("connector")
					public void setconnector(final String connector) {
					    this.connector = connector;
					}
				«ENDIF»
					
				public static «t.name.fuEscapeJava» fromEntity(final «dbTypeName» dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
					«IF t.isAbstract»
						«{
							var types = t.resolveSubTypesAndType
							'''
								«FOR type:types SEPARATOR " else "
								»if(dbEntity instanceof «type.entityFQN») {
									return «type.restFQN».fromEntity(dbEntity, objectCache);
								}«
								ENDFOR»
							'''
						}»
					«ELSE»
						if(dbEntity instanceof «t.entityFQN») {
							«t.entityFQN» entity = («t.entityFQN») dbEntity;
							«t.parse(modelPackage,false,styles)»
						}
						«{
							val subTypes = t.resolveSubTypesAndType.filter[!equals(t)]
							'''
								«IF !subTypes.empty»
									// delegating to subTypes
									«FOR s:subTypes
									»else if(dbEntity instanceof «s.entityFQN») {
										return «s.restFQN».fromEntity(dbEntity, objectCache);
									}«
									ENDFOR»
								«ENDIF»
							'''
						}»
					«ENDIF»
					else
						return null;
				}
				
				public static «t.name.fuEscapeJava» fromEntityProperties(final «dbTypeName» dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
					«IF t.isAbstract»
						«{
							var types = t.resolveSubTypesAndType
							'''
								«FOR type:types SEPARATOR " else "
								»if(dbEntity instanceof «type.entityFQN») {
									return «type.restFQN».fromEntityProperties(dbEntity, objectCache);
								}«
								ENDFOR»
							'''
						}»
					«ELSE»
						if(dbEntity instanceof «t.entityFQN») {
							«t.entityFQN» entity = («t.entityFQN») dbEntity;
							«t.parse(modelPackage,true,styles)»
						}
						«{
							val subTypes = t.resolveSubTypesAndType.filter[!equals(t)]
							'''
								«IF !subTypes.empty»
									// delegating to subTypes
									«FOR s:subTypes
									»else if(dbEntity instanceof «s.entityFQN») {
										return «s.restFQN».fromEntityProperties(dbEntity, objectCache);
									}«
									ENDFOR»
								«ENDIF»
							'''
						}»
					«ENDIF»
					else
						return null;
				}
			}
		'''
	}
	
	def parse(ModelElement t, MGLModel modelPackage, boolean onlyProperties,Styles styles)
	'''
		if(objectCache!=null&&objectCache.containsRestTo(entity)){
			return objectCache.getRestTo(entity);
		}
		final «t.name.fuEscapeJava» result;
		result = new «t.name.fuEscapeJava»();
		if(objectCache!=null) {
			objectCache.putRestTo(entity, result);
		}
		result.setId(entity.id);
		result.set__type(«typeRegistryName».getTypeOf(entity));
		«IF !onlyProperties»
			«IF t instanceof ContainingElement»
				«IF modelPackage.elements.filter(GraphicalModelElement).filter[!isIsAbstract].empty»
					result.setmodelElements(new java.util.LinkedList<>());
				«ELSE»
					java.util.Collection<«dbTypeName»> dbModelElements = entity.getModelElements();
					result.getmodelElements().addAll(
						dbModelElements.stream()
							.map( (n) -> (info.scce.pyro.core.graphmodel.ModelElement) «typeRegistryName».getDBToRest(n, objectCache))
							.collect(java.util.stream.Collectors.toList())
					);
				«ENDIF»
			«ENDIF»
			«IF t instanceof GraphicalModelElement»
				«t.serializeContainer»
			«ENDIF»
			«IF t instanceof GraphModel»
				result.setscale(entity.scale);
				result.setwidth(entity.width);
				result.setheight(entity.height);
				result.setrouter(entity.router);
				result.setconnector(entity.connector);
				result.setfilename(entity.filename);
				result.setextension(entity.extension);
				result.setisPublic(entity.isPublic);
			«ENDIF»
			«IF t instanceof Node»
				result.setwidth(entity.width);
				result.setheight(entity.height);
				result.setx(entity.x);
				result.sety(entity.y);
				«IF t.prime»
				«{
					val refElem = t.primeReference.type
					'''
						result.set«t.primeReference.name.escapeJava»(
							«refElem.restFQN».fromEntityProperties(
								entity.get«t.primeReference.name.fuEscapeJava»(), objectCache
							)
						);
					'''				
				}»
				
				«ENDIF»
				«t.serializeEdges("incoming")»
				«t.serializeEdges("outgoing")»
			«ENDIF»
			«IF t instanceof Edge»
				result.setbendingPoints(entity.bendingPoints.stream().map(n->info.scce.pyro.core.graphmodel.BendingPoint.fromEntity(n)).collect(java.util.stream.Collectors.toList()));
				«t.serializeConnection("target")»
				«t.serializeConnection("source")»

			«ENDIF»
		«ENDIF»
		//additional attributes
		«t.attributesExtended.map[attributeSerialization(modelPackage,onlyProperties)].join("")»
		return result;
	'''

	def attributeSerialization(Attribute attr, MGLModel modelPackage, boolean onlyProperties)
	'''
		«IF attr.isList»
			«IF attr.isPrimitive»
				«IF attr.attributeTypeName.getEnum(modelPackage)!==null»
					if(entity.«attr.name.escapeJava»!=null){
						result.set«attr.name.escapeJava»(entity.«attr.name.escapeJava».stream()
							.map(n->info.scce.pyro.core.graphmodel.PyroEnum.fromEntity(n.toString()))
							.collect(java.util.stream.Collectors.toList()));
					}
				«ELSE»
					result.set«attr.name.escapeJava»(«IF attr.isList»new java.util.ArrayList<>(«ENDIF»entity.«attr.name.escapeJava»«IF attr.isList»)«ENDIF»);
				«ENDIF»
			«ELSE»
				java.util.Collection<«dbTypeName»> db«attr.name.fuEscapeJava» = entity.get«attr.name.fuEscapeJava»();
				result.set«attr.name.escapeJava»(db«attr.name.fuEscapeJava».stream()
					.map((n)-> («attr.restFQN») «typeRegistryName».getDBToRest(n, objectCache, «onlyProperties»))
					.collect(java.util.stream.Collectors.toList()));
			«ENDIF»
		«ELSE»
			«IF attr.isPrimitive»
				«IF attr.attributeTypeName.getEnum(modelPackage)!==null»
					result.set«attr.name.escapeJava»(info.scce.pyro.core.graphmodel.PyroEnum.fromEntity(entity.«attr.name.escapeJava».toString()));
				«ELSE»
					result.set«attr.name.escapeJava»(entity.«attr.name.escapeJava»);
				«ENDIF»
			«ELSE»
				«dbTypeName» db«attr.name.fuEscapeJava» = entity.get«attr.name.fuEscapeJava»();
				«attr.restFQN» rest«attr.name.fuEscapeJava» = («attr.restFQN») «typeRegistryName».getDBToRest(db«attr.name.fuEscapeJava», objectCache, «onlyProperties»);
				result.set«attr.name.escapeJava»(rest«attr.name.fuEscapeJava»);	
			«ENDIF»
		«ENDIF»
	'''
	
	def primeAttributes(Node t, GraphModel g, boolean onlyProperties)
	{
		val refElem = t.primeReference.type
		val refGraph = refElem.MGLModel
		val sameGraphModelType = refGraph.name.equals(g.name)
		'''
		if(entity.get«t.primeReference.name.escapeJava»()!=null) {
			
			«IF sameGraphModelType»
				if(info.scce.pyro.core.graphmodel.Node.getRootGraphModel(entity.get«t.primeReference.name.escapeJava»()).equals(info.scce.pyro.core.graphmodel.Node.getRootGraphModel(entity))){
					result.set«t.primeReference.name.escapeJava»(
						info.scce.pyro.«refGraph.name.lowEscapeJava».rest.«refElem.name.fuEscapeJava».fromEntity«IF onlyProperties»Properties«ENDIF»(
							entity.get«t.primeReference.name.escapeJava»(),objectCache
						)
					);
				} else {
			«ENDIF»
			result.set«t.primeReference.name.escapeJava»(
				info.scce.pyro.«refGraph.name.lowEscapeJava».rest.«refElem.name.fuEscapeJava».fromEntityProperties(
					entity.get«t.primeReference.name.escapeJava»(),
					objectCache
				)
			);
			«IF sameGraphModelType»
			}
			«ENDIF»
		}
		'''		
	}
	
	
	def attributeDeclaration(Attribute attr, MGLModel modelPackage)
	'''
		private «attr.wrapJavaType(modelPackage)» «attr.name.escapeJava»;
		
		@com.fasterxml.jackson.annotation.JsonProperty("c_«attr.name.escapeJava»")
		public «attr.wrapJavaType(modelPackage)» get«attr.name.escapeJava»() {
		    return this.«attr.name.escapeJava»;
		}
		
		@com.fasterxml.jackson.annotation.JsonProperty("«attr.name.escapeJava»")
		public void set«attr.name.escapeJava»(final «attr.wrapJavaType(modelPackage)» «attr.name.escapeJava») {
		    this.«attr.name.escapeJava» = «attr.name.escapeJava»;
		}
	'''
	
	def wrapJavaType(Attribute attribute, MGLModel modelPackage){
		var res = ""
		if(attribute.isList){
			res += "java.util.List<"
		}
		if(attribute.isPrimitive){
			if(attribute.isList){
				if(attribute.attributeTypeName.getEnum(modelPackage)!==null) {
					res += attribute.getToJavaType(modelPackage)
				} else {
					res += attribute.getToJavaType(modelPackage).toFirstUpper
				}
			} else {
				res += attribute.getToJavaType(modelPackage)
			}
		}
		else{
			res += attribute.restFQN
		}
		if(attribute.isList){
			res += ">"
		}
		res
	}
	
	def getToJavaType(Attribute attr, MGLModel modelPackage) {
		if(attr.attributeTypeName.getEnum(modelPackage)!==null){
			return "info.scce.pyro.core.graphmodel.PyroEnum"
		}
		switch(attr.attributeTypeName){
			case "EBoolean": {
				if(attr.list){
					return '''Boolean'''
				}
				return '''boolean'''
			}
			case "EInt":{
				if(attr.list){
					return '''Long'''
				}
				return '''Long'''
			} 
			case "EDouble":{
				if(attr.list) {
					return '''Double'''
				}
				return '''Double'''
			}
			case "ELong": {
				if(attr.list){
					return '''Long'''
				}
				return '''Long'''
			} 
			case "EBigInteger":{
				if(attr.list){
					return '''Long'''
				}
				return '''Long'''
			} 
			case "EByte": {
				if(attr.list){
					return '''Long'''
				}
				return '''Long'''
			} 
			case "EShort": {
				if(attr.list){
					return '''Long'''
				}
				return '''Long'''
			} 
			case "EFloat":{
				if(attr.list) {
					return '''Double'''
				}
				return '''Double'''
			}
			case "EBigDecimal": {
				if(attr.list) {
					return '''Double'''
				}
				return '''Double'''
			}
			default: return '''String'''
		}
	}
	
	def serializeEdges(Node n,String attr)
	'''
		«IF (n.modelPackage as MGLModel).elements.filter(Edge).filter[!isIsAbstract].empty»
			result.set«attr»(new java.util.LinkedList<>());
		«ELSE»
			java.util.List<info.scce.pyro.core.graphmodel.Edge> «attr»s = new java.util.LinkedList<>();
			java.util.Collection<«dbTypeName»> db«attr.fuEscapeJava» = entity.get«attr.fuEscapeJava»();
			«attr»s.addAll(db«attr.fuEscapeJava».stream()
				.map((n)-> (info.scce.pyro.core.graphmodel.Edge) «typeRegistryName».getDBToRest(n, objectCache))
				.collect(java.util.stream.Collectors.toList()));
			result.set«attr»(«attr»s);
	    «ENDIF»
	'''
	
	def serializeContainer(GraphicalModelElement n)
	'''
		«dbTypeName» dbContainer = entity.getContainer();
		info.scce.pyro.core.graphmodel.IdentifiableElement restContainer = «typeRegistryName».getDBToRest(dbContainer, objectCache);
		result.setcontainer(restContainer);
	'''
	
	def serializeConnection(Edge e,String attr) {
		val attrName = attr.fuEscapeJava
		'''
			«dbTypeName» db«attrName» = entity.get«attr.fuEscapeJava»();
			info.scce.pyro.core.graphmodel.Node rest«attrName» = (info.scce.pyro.core.graphmodel.Node) «typeRegistryName».getDBToRest(db«attrName», objectCache);
			result.set«attr»Element(rest«attrName»);
		'''
	}
	
	def restExtending(ModelElement element){
		switch element {
			GraphModel: {
				if (element.extends === null) return "info.scce.pyro.core.graphmodel.GraphModel"
				return element.extends.name
			}
			NodeContainer: {
				if (element.extends === null) return "info.scce.pyro.core.graphmodel.Container"
				return element.extends.name
			}
			Node: {
				if (element.extends === null) return "info.scce.pyro.core.graphmodel.Node"
				return element.extends.name
			}
			Edge: {
				if (element.extends === null) return "info.scce.pyro.core.graphmodel.Edge"
				return element.extends.name
			}
			UserDefinedType: {
				if (element.extends === null) return "info.scce.pyro.core.graphmodel.IdentifiableElement"
				return element.extends.name
			}
			Enumeration: {
				return ""
			}
		}
		return ""
	}
}
