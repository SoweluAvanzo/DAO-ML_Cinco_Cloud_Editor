package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.rest

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import org.eclipse.emf.ecore.ENamedElement
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EReference

class EcoreRestTO extends Generatable{
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filenamePackage(EPackage p)'''«p.name.fuEscapeJava».java'''
	
	def filenameStructural(ENamedElement p)'''«p.name.fuEscapeJava».java'''
	
	def filenameEcore(EPackage p)'''«p.name.fuEscapeJava»List.java'''
	
	def contentPackage(EPackage p)
	'''
		package «p.restFQNBase»;

		
		/**
		 * Author zweihoff
		 */
		
		@com.fasterxml.jackson.annotation.JsonTypeName("«p.restFQN»")
		public class «p.name.fuEscapeJava» implements info.scce.pyro.core.graphmodel.IdentifiableElement
		{
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
				return this.id;
			}
			
			@Override
			public void setId(long id) {
				this.id = id;
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
			
			private String extension;
			
			@com.fasterxml.jackson.annotation.JsonProperty("extension")
			public String getextension() {
				return this.extension;
			}
			
			@com.fasterxml.jackson.annotation.JsonProperty("extension")
			public void setextension(final String extension) {
				this.extension = extension;
			}
			«FOR c:p.elements»
				
				private java.util.List<«c.restFQN»> «c.name.escapeJava»;
							
				@com.fasterxml.jackson.annotation.JsonProperty("«c.name.escapeJava»")
				public java.util.List<«c.restFQN»> get«c.name.escapeJava»() {
				    return this.«c.name.escapeJava»;
				}
				
				@com.fasterxml.jackson.annotation.JsonProperty("«c.name.escapeJava»")
				public void set«c.name.fuEscapeJava»(final java.util.Collection<«c.restFQN»> «c.name.escapeJava») {
				    this.«c.name.escapeJava» = «c.name.escapeJava».stream().collect(java.util.stream.Collectors.toList());
				}
			«ENDFOR»
		
		    public static «p.name.fuEscapeJava» fromEntity(final «p.entityFQN» entity, info.scce.pyro.rest.ObjectCache objectCache) {
				if(objectCache != null && objectCache.containsRestTo(entity)){
					return objectCache.getRestTo(entity);
				}
				final «p.name.fuEscapeJava» result;
				result = new «p.name.fuEscapeJava»();
				if(objectCache != null) {
					objectCache.putRestTo(entity, result);
				}
				result.setId(entity.id);
				result.set__type("«p.name.lowEscapeDart».«p.name.fuEscapeDart»");
				result.setfilename(entity.filename);
				result.setextension(entity.extension);
				
				«FOR c:p.elements»
					«handleListReference(c, p, c.name.escapeJava)»
				«ENDFOR»
				return result;
		    }
		}
	'''
	
	def contentEcore(EPackage p)
	'''
		package info.scce.pyro.«p.name.lowEscapeJava».rest;
		
		/**
		 * Author zweihoff
		 */
		
		@com.fasterxml.jackson.annotation.JsonTypeName("«p.restFQN»List")
		@com.fasterxml.jackson.annotation.JsonIdentityInfo(generator = com.voodoodyne.jackson.jsog.JSOGGenerator.class)
		public class «p.name.fuEscapeJava»List
		{
			private java.util.List<«p.restFQN»> list;
			
			@com.fasterxml.jackson.annotation.JsonProperty("list")
			public java.util.List<«p.restFQN»> getlist() {
				return this.list;
			}
			
			@com.fasterxml.jackson.annotation.JsonProperty("list")
			public void setlist(final java.util.List<«p.restFQN»> list) {
				this.list = list;
			}
			
			public static «p.name.fuEscapeJava»List fromEntity(final java.util.Collection<«p.entityFQN»> entity, info.scce.pyro.rest.ObjectCache objectCache) {
				«p.name.fuEscapeJava»List result = new «p.name.fuEscapeJava»List();
				result.setlist(
					entity.stream().map((n)->«p.restFQN».fromEntity(n,objectCache)).collect(java.util.stream.Collectors.toList())
				);
				return result;
			}

		}
	'''
	
	def contentStructural(EClassifier p,EPackage pack) {
		var superType = '''implements info.scce.pyro.core.graphmodel.IdentifiableElement'''
		if(p instanceof EClass) {
			val superTypes = p.ESuperTypes
			if(!superTypes.empty)
				superType = '''extends «p.ESuperTypes.get(0).restFQN»'''
		}
		'''
			package «pack.restFQNBase»;
			
			import «dbTypeFQN»;
			
			/**
			 * Author zweihoff
			 */
			
			@com.fasterxml.jackson.annotation.JsonTypeName("«p.restFQN»")
			public class «p.name.fuEscapeJava» «superType» 
			{
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
					return this.id;
				}
				
				@Override
				public void setId(long id) {
					this.id = id;
				}
				«IF p instanceof EClass»
					«FOR attr:p.EAllAttributes»
						
						private «attr.getToJavaType(pack)» «attr.name.escapeJava»;
												
						@com.fasterxml.jackson.annotation.JsonProperty("«attr.name.escapeJava»")
						public «attr.getToJavaType(pack)» get«attr.name.escapeJava»() {
						    return this.«attr.name.escapeJava»;
						}
						
						@com.fasterxml.jackson.annotation.JsonProperty("«attr.name.escapeJava»")
						public void set«attr.name.fuEscapeJava»(final «attr.getToJavaType(pack)» «attr.name.escapeJava») {
						    this.«attr.name.escapeJava» = «attr.name.escapeJava»;
						}
					«ENDFOR»
					«FOR attr:p.EAllReferences»
						
						private «attr.complexJavaType(pack)» «attr.name.escapeJava»;
														
						@com.fasterxml.jackson.annotation.JsonProperty("«attr.name.escapeJava»")
						public «attr.complexJavaType(pack)» get«attr.name.escapeJava»() {
						    return this.«attr.name.escapeJava»;
						}
						
						@com.fasterxml.jackson.annotation.JsonProperty("«attr.name.escapeJava»")
						public void set«attr.name.fuEscapeJava»(final «attr.complexJavaType(pack)» «attr.name.escapeJava») {
						    this.«attr.name.escapeJava» = «attr.name.escapeJava»;
						}
					«ENDFOR»
				«ENDIF»
				
				public static «p.name.fuEscapeJava» fromEntityProperties(final «dbTypeName» dbEntity, info.scce.pyro.rest.ObjectCache o) {
					info.scce.pyro.rest.ObjectCache objectCache = o;
					if(objectCache == null)
						objectCache = new info.scce.pyro.rest.ObjectCache();
					
					return fromEntity(dbEntity,objectCache);
				}
				
				public static «p.name.fuEscapeJava» fromEntity(final «dbTypeName» dbEntity, info.scce.pyro.rest.ObjectCache objectCache) {
					«IF p.isAbstract»
						«{
							var types = p.resolveSubTypesAndType
							'''
								«FOR type:types SEPARATOR " else "
								»if(dbEntity instanceof «type.entityFQN») {
									return «type.restFQN».fromEntity(dbEntity, objectCache);
								}«
								ENDFOR»
							'''
						}»
					«ELSE»
						if(dbEntity instanceof «p.entityFQN») {
							«p.entityFQN» entity = («p.entityFQN») dbEntity;
							«p.fromEntity(pack)»
						}
						«{
							var subTypes = p.resolveSubTypesAndType.filter[!equals(p)]
							'''
								«IF !subTypes.empty»
									// delegating to subTypes
									«FOR type:subTypes SEPARATOR " "
									»else if(dbEntity instanceof «type.entityFQN») {
										return «type.restFQN».fromEntity(dbEntity, objectCache);
									}«
									ENDFOR»
								«ENDIF»
							'''
						}»
					«ENDIF»
					return null;
				}
			}
		'''
	}
	
	def fromEntity(ENamedElement p,EPackage g) {
		'''
			if(objectCache!=null && objectCache.containsRestTo(entity)){
				return objectCache.getRestTo(entity);
			}
			final «p.name.fuEscapeJava» result;
			result = new «p.name.fuEscapeJava»();
			if(objectCache!=null) {
				objectCache.putRestTo(entity, result);
			}
			result.setId(entity.id);
			result.set__type("«p.typeName»");

			«IF p instanceof EClass»
				«FOR attr:p.EAllAttributes»
					«handlePrimitiveAttribute(attr, g)»
				«ENDFOR»
				«FOR attr:p.EReferences»
					«handleReference(attr, g)»
				«ENDFOR»
			«ELSEIF p instanceof EPackage»
				«FOR attr:p.EClassifiers»
					«IF attr instanceof EClass»
						«handleReference(attr, g, attr.name.escapeJava)»
					«ELSEIF attr instanceof EAttribute»
						«handlePrimitiveAttribute(attr, g)»
					«ENDIF»
				«ENDFOR»
			«ENDIF»
			return result;
		'''
	}
	
	def handlePrimitiveAttribute(EAttribute attr, EPackage g) {
		var type = attr.EType
		'''
			«IF attr.list»
				«IF attr.EType instanceof EEnum»
					«handleListReference(attr.EType, g, attr.name.escapeJava)»
				«ELSE»
					result.set«attr.name.fuEscapeJava»(entity.«attr.name.lowEscapeJava»);
				«ENDIF»
			«ELSE»
				«IF attr.EType instanceof EEnum»
					result.set«attr.name.fuEscapeJava»(
						«type.restFQN».fromEntity(
							entity.«attr.name.lowEscapeJava»,
							objectCache
						)
					);
				«ELSE»
					result.set«attr.name.fuEscapeJava»(entity.«attr.name.lowEscapeJava»);
				«ENDIF»
			«ENDIF»
		'''
	}
	
	def handleReference(EReference attr, EPackage g) {
		var type = attr.EType
		'''
			«IF attr.list»
				«handleListReference(type, g, attr.name.escapeJava)»
			«ELSE»
				«handleReference(type, g, attr.name.escapeJava)»
			«ENDIF»
		'''
	}
	
	def handleListReference(EClassifier type, EPackage g, String attributeName)
	'''
		result.set«attributeName.fuEscapeJava»(
			entity.get«attributeName.fuEscapeJava»().stream().map((n)->
				«type.restFQN».fromEntity(
					n,
					objectCache
				)
			).collect(java.util.stream.Collectors.toList())
		);
	'''
	
	def handleReference(EClassifier type, EPackage g, String attributeName)
	'''
		result.set«attributeName.fuEscapeJava»(
			«type.restFQN».fromEntity(
				entity.get«attributeName.fuEscapeJava»(),
				objectCache
			)
		);
	'''
	
	def getToJavaType(EAttribute attr, EPackage g) {
		if(attr.EType instanceof EEnum){
			if(attr.list){
				return '''java.util.Collection<«attr.EType.name.fuEscapeJava»>'''
			}
			return attr.EType.name.fuEscapeJava
		}
		if(attr.list){
			'''java.util.Collection<«attr.ecoreType(g)»>'''
		} else {
			'''«attr.ecoreType(g)»'''
		}
	}
}
