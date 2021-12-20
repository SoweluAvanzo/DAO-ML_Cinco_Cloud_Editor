  package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.Attribute
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.ENamedElement
import org.eclipse.emf.ecore.EClass
import java.util.LinkedList
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EClassifier

class EcoreElementImplementation extends Generatable {
	
	protected extension ModelElementHook = new ModelElementHook
	
	new(GeneratorCompound gc) {
		super(gc)
	}
		
	def filename(ENamedElement me)'''«me.name.fuEscapeJava»Impl.java'''
	
	def content(ENamedElement me, EPackage g)
	{
		'''
			package «g.apiImplFQNBase»;
			import «dbTypeFQN»;
			
			public class «me.name.fuEscapeJava»Impl implements «me.apiFQN» {
				
				private final «me.entityFQN» delegate;
				
				public «me.name.fuEscapeJava»Impl(
					«me.entityFQN» delegate
				) {
					this.delegate = delegate;
				}
				
				public «me.name.fuEscapeJava»Impl(
				) {
					this.delegate = new «me.entityFQN»();
				}
				
				public «me.apiFQN» eClass() {
					return this;
				}
				«IF me instanceof EPackage»
					
					@Override
					public void setExtension(String extension) {
						this.delegate.extension = extension;
						delegate.persist();
					}
					
					@Override
					public String getExtension() {
						return this.delegate.extension;
					}
					
					@Override
					public void setFilename(String filename) {
						this.delegate.filename = filename;
						delegate.persist();
					}
					
					@Override
					public String getFilename() {
						return this.delegate.filename;
					}
				«ENDIF»
				
				@Override
				public boolean equals(Object obj) {
					return obj!=null
						&& obj instanceof «me.apiFQN»
						&& ((«me.apiFQN») obj).getId().equals(getId());
				}
				
				@Override
				public int hashCode() {
					return delegate.id.intValue();
				}
				
				@Override
				public String getId() {
					return Long.toString(this.delegate.id);
				}
				
				@Override
				public long getDelegateId() {
					return this.delegate.id;
				}
				
				@Override
				public «me.entityFQN» getDelegate() {
					return this.delegate;
				}
				
				@Override
			    public org.eclipse.emf.ecore.EObject eContainer() {
			        return null;
			    }
				
				@Override
				public void delete() {
					this.delegate.delete();
				}
				«attributes(me, g)»
			}
		'''
	}
	
	def attributes(ENamedElement me, EPackage g) {
		'''
			«IF me instanceof EPackage»
				«attributesOfEPackage(me, g)»
			«ELSEIF me instanceof EClass»
				«attributesOfEClass(me, g)»
			«ENDIF»
		'''
	}
	
	def attributesOfEPackage(EPackage me, EPackage g) {
		val types = g.EClassifiers.filter(EClass)
		'''
			
			public String getName() {
				return "«me.name»";
			}
			
			// Contents
			«FOR type:types SEPARATOR "\n"»
				«handleListReference(me, type, g, type.name)»
			«ENDFOR»
		'''
	}
	
	def attributesOfEClass(EClass me, EPackage g) {
		val superTypes = g.resolveSuperTypesAndType(me)
		val attributes =  new LinkedList<EAttribute>
		val references = new LinkedList<EReference>
		superTypes.forEach[ superType |
			// resolve attributes
			val resultAttr = superType.eContents.filter(EAttribute)
			attributes.addAll(resultAttr)
			// resolve references
			val resultRef = superType.eContents.filter(EReference)
			references.addAll(resultRef)
		]
		'''
			
			// EAttributes
			«FOR attr:attributes SEPARATOR "\n"»
				«{
					val refType = attr.EType
					val isList = attr.isList
					'''
						«IF refType instanceof EEnum»
							«IF isList»
								«handleListReference(me, refType, g, attr.name)»
							«ELSE»
								«handleReference(refType, g, attr.name)»
							«ENDIF»
						«ELSE»
							«IF isList»
								«handlePrimitiveList(attr, g, attr.name)»
							«ELSE»
								«handlePrimitive(attr, g, attr.name)»
							«ENDIF»
						«ENDIF»
					'''
				}»
			«ENDFOR»
			«IF !attributes.exists[it.name == "name"]»
				public String getName() {
					return "«me.name»";
				}
			«ENDIF»
			
			// EReferences
			«FOR refs:references SEPARATOR "\n"»
				«{
					val refType = refs.EType
					val isList = refs.isList
					'''
						«IF isList»
							«handleListReference(me, refType, g, refs.name)»
						«ELSE»
							«handleReference(refType, g, refs.name)»
						«ENDIF»
					'''
				}»
			«ENDFOR»
		'''
	}
	
	def handlePrimitive(EAttribute type, EPackage g, String name) {
		'''
			@Override
			public «type.ecoreType(g)» get«name.toFirstUpper»() {
				return this.delegate.«name.lowEscapeJava»;
			}
			
			@Override
			public void set«name.toFirstUpper»(«type.ecoreType(g)» e) {
				this.delegate.«name.lowEscapeJava» = e;
				this.delegate.persist();
			}
		'''
	}
	
	def handlePrimitiveList(EAttribute type, EPackage g, String name) {
		'''
			@Override
			public java.util.List<«type.ecoreType(g)»> get«type.name.toFirstUpper»() {
				return this.delegate.«type.name.lowEscapeJava».stream().collect(java.util.stream.Collectors.toList());
			}
			
			@Override
			public void set«type.name.toFirstUpper»(java.util.Collection<«type.ecoreType(g)»> e) {
				this.delegate.«type.name.lowEscapeJava» = e;
				this.delegate.persist();
			}
		'''
	}
	
	def handleReference(EClassifier type, EPackage g, String name) {
		'''
			@Override
			public «type.apiFQN» get«name.toFirstUpper»() {
				«{
					val subTypes = type.resolveSubTypesAndType
					'''
						«type.apiFQN» result = null;
						«dbTypeName» dbEntity = this.delegate.get«name.fuEscapeJava»();
						«FOR subType:subTypes SEPARATOR " else "
						»if(dbEntity instanceof «subType.entityFQN») {
							result = new «subType.apiImplFQN»((«subType.entityFQN») dbEntity);
						}«
						ENDFOR»
						return result;
					'''
				}»
			}
			
			@Override
			public void set«name.toFirstUpper»(«type.apiFQN» e) {
				«dbTypeName» entity = e.getDelegate();
				this.delegate.set«name.fuEscapeJava»(entity, true);
				this.delegate.persist();
			}
		'''
	}
	
	def handleListReference(ENamedElement me, EClassifier type, EPackage g, String name) {
		'''
			@Override
			public java.util.List<«type.apiFQN»> get«name.fuEscapeJava»() {
				java.util.Collection<«dbTypeName»> entityList = this.delegate.get«name.fuEscapeJava»();
				java.util.List<«type.apiFQN»> apiList = entityList.stream().map(n->
					«IF type.isAbstractType»
						«{
							val subTypes = type.resolveSubTypesAndType
							// TODO: SAMI: typeRegistry for Ecores, maybe?
							'''
								{
									«FOR subType:subTypes SEPARATOR " else "
									»if(n instanceof «subType.entityFQN») {
										return («type.apiFQN»)
											new «subType.apiImplFQN»(
												(«subType.entityFQN») n
											);
									}«
									ENDFOR»
									return null;
								}
							'''
						}»
					«ELSE»
						new «type.apiImplFQN»(
							(«type.entityFQN») n
						)
					«ENDIF»
				).collect(java.util.stream.Collectors.toList());
				return apiList;
			}
			
			@Override
			public void set«name.fuEscapeJava»(java.util.Collection<«type.apiFQN»> e) {
				java.util.Collection<«dbTypeName»> entityList = e.stream().map(n->
					n.getDelegate()
				).collect(java.util.stream.Collectors.toList());
				this.delegate.set«name.fuEscapeJava»(entityList);
				this.delegate.persist();
			}
			«/* TODO: SAMI: same for GraphModelImplementation */»
			@Override
			public boolean remove«name.fuEscapeJava»(«type.apiFQN» apiElement, boolean delete) {
				«dbTypeName» dbElement = apiElement.getDelegate();
				return ((«me.entityFQN») this.delegate).remove«name.fuEscapeJava»(dbElement, delete);
			}
			
			@Override
			public boolean remove«name.fuEscapeJava»(«type.apiFQN» apiElement) {
				return remove«name.fuEscapeJava»(apiElement, true);
			}
			
			@Override
			public void clear«name.toFirstUpper»() {
				((«me.entityFQN») this.delegate).clear«name.fuEscapeJava»();
			}
			
			@Override
			public void clear«name.toFirstUpper»(boolean delete) {
				((«me.entityFQN») this.delegate).clear«name.fuEscapeJava»(delete);
			}
			
			@Override
			public void addAll«name.toFirstUpper»(java.util.Collection<«type.apiFQN»> apiElements) {
				for(«type.apiFQN» apiElement : apiElements) {
					add«name.toFirstUpper»(apiElement);
				}
			}
			
			@Override
			public void add«name.toFirstUpper»(«type.apiFQN» apiElement) {
				«dbTypeName» dbElement = apiElement.getDelegate();
				((«me.entityFQN») this.delegate).add«name.fuEscapeJava»(dbElement);
			}
		'''
	}
	
	def primitiveGETConverter(Attribute attribute, String string) {
		return switch(attribute.attributeTypeName) {
			case "EInt":'''«IF attribute.list»«string».stream().map(n->Math.toIntExact(n)).collect(java.util.stream.Collectors.toList())«ELSE»Math.toIntExact(«string»)«ENDIF»'''
			case "EBigInteger":'''«IF attribute.list»«string».stream().map(n->Math.toIntExact(n)).collect(java.util.stream.Collectors.toList())«ELSE»Math.toIntExact(«string»)«ENDIF»'''
			case "ELong":'''«IF attribute.list»«string».stream().map(n->Math.toIntExact(n)).collect(java.util.stream.Collectors.toList())«ELSE»Math.toIntExact(«string»)«ENDIF»'''
			case "EByte":'''«IF attribute.list»«string».stream().map(n->Math.toIntExact(n)).collect(java.util.stream.Collectors.toList())«ELSE»Math.toIntExact(«string»)«ENDIF»'''
			case "EShort":'''«IF attribute.list»«string».stream().map(n->Math.toIntExact(n)).collect(java.util.stream.Collectors.toList())«ELSE»Math.toIntExact(«string»)«ENDIF»'''
			default:'''«IF attribute.list»«string».stream().collect(java.util.stream.Collectors.toList())«ELSE»«string»«ENDIF»'''
		}
	}
	
	def primitiveSETConverter(Attribute attribute, String string) {
		return switch(attribute.attributeTypeName) {
			case "EInt":'''«IF attribute.list»«string».stream().map(n->Long.valueOf(n)).collect(java.util.stream.Collectors.toList())«ELSE»Long.valueOf(«string»)«ENDIF»'''
			case "EBigInteger":'''«IF attribute.list»«string».stream().map(n->Long.valueOf(n)).collect(java.util.stream.Collectors.toList())«ELSE»Long.valueOf(«string»)«ENDIF»'''
			case "ELong":'''«IF attribute.list»«string».stream().map(n->Long.valueOf(n)).collect(java.util.stream.Collectors.toList())«ELSE»Long.valueOf(«string»)«ENDIF»'''
			case "EByte":'''«IF attribute.list»«string».stream().map(n->Long.valueOf(n)).collect(java.util.stream.Collectors.toList())«ELSE»Long.valueOf(«string»)«ENDIF»'''
			case "EShort":'''«IF attribute.list»«string».stream().map(n->Long.valueOf(n)).collect(java.util.stream.Collectors.toList())«ELSE»Long.valueOf(«string»)«ENDIF»'''
			default:'''«IF attribute.list»«string».stream().collect(java.util.stream.Collectors.toList())«ELSE»«string»«ENDIF»'''
		}
	}
}
