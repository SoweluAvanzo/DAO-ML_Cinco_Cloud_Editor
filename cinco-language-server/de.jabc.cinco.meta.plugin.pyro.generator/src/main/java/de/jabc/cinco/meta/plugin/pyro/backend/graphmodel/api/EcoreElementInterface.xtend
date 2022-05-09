package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EClass
import java.util.LinkedList
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EEnumLiteral
import org.eclipse.emf.ecore.ENamedElement

class EcoreElementInterface extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(ENamedElement me)'''«me.name.fuEscapeJava».java'''
	
	def contentEnum(EEnum me,EPackage g)
	'''
		package «g.apiFQNBase»;
		
		public enum «me.name.fuEscapeJava» {
			«me.eContents.filter(EEnumLiteral).map['''«it.name.escapeJava»'''].join(",")»
		}
	'''
	
	def content(ENamedElement me, EPackage g) {
		val eContainer = me instanceof EPackage ? me : me.eContainer
		'''
			package «g.apiFQNBase»;
			
			import «dbTypeFQN»;
			
			public interface «me.name.fuEscapeJava» extends «me.getExtension(g)» {
				
				// Mandatory
				public «dbTypeName» getDelegate();
				public void delete();
				public «eContainer.apiFQN» getContainer();
				public void setContainer(«eContainer.apiFQN» c);
				«IF me instanceof EPackage»
					public void setExtension(String extension);
					public void setFilename(String filename);
					public String getExtension();
					public String getFilename();
					«{
						val types = g.EClassifiers.filter(EClass)
						'''
							
							// Contents
							«FOR type:types»
								public java.util.List<«type.apiFQN»> get«type.name.fuEscapeJava»();
								public void set«type.name.fuEscapeJava»(java.util.Collection<«type.apiFQN»> e);
								public boolean remove«type.name.toFirstUpper»(«type.apiFQN» apiElement, boolean delete);
								public boolean remove«type.name.toFirstUpper»(«type.apiFQN» apiElement);
								public void clear«type.name.toFirstUpper»();
								public void clear«type.name.toFirstUpper»(boolean delete);
								public void addAll«type.name.toFirstUpper»(java.util.Collection<«type.apiFQN»> apiElements);
								public void add«type.name.toFirstUpper»(«type.apiFQN» apiElement);
							«ENDFOR»
						'''
					}»
				«ELSEIF me instanceof EClass»
					«{
						val attributes =  new LinkedList<EAttribute>
						val references = new LinkedList<EReference>
						val resultAttr = me.eContents.filter(EAttribute)
						attributes.addAll(resultAttr)
						val resultRef = me.eContents.filter(EReference)
						references.addAll(resultRef)
						'''
							«FOR attr:attributes BEFORE "\n// EAttributes\n"»
								«{
									val refType = attr.EType
									val isList = attr.isList
									'''
										«IF refType instanceof EEnum»
											public «IF isList»java.util.List<«ENDIF»«refType.apiFQN»«IF isList»>«ENDIF» get«attr.name.toFirstUpper»();
											public void set«attr.name.toFirstUpper»(«IF isList»java.util.Collection<«ENDIF»«refType.apiFQN»«IF isList»>«ENDIF» e);
										«ELSE»
											public «IF isList»java.util.List<«ENDIF»«attr.ecoreType(g)»«IF isList»>«ENDIF» «IF !isList && attr.ecoreType(g).equals("boolean")»is«ELSE»get«ENDIF»«attr.name.toFirstUpper»();
											public void set«attr.name.toFirstUpper»(«IF isList»java.util.Collection<«ENDIF»«attr.ecoreType(g)»«IF isList»>«ENDIF» e);
										«ENDIF»
									'''
								}»
							«ENDFOR»
							«FOR refs:references BEFORE "\n// EReferences\n"»
								«{
									val refType = refs.EType
									val isList = refs.isList
									'''
										public «IF isList»java.util.List<«ENDIF»«refType.apiFQN»«IF isList»>«ENDIF» get«refs.name.toFirstUpper»();
										public void set«refs.name.toFirstUpper»(«IF isList»java.util.Collection<«ENDIF»«refType.apiFQN»«IF isList»>«ENDIF» e);
										«IF isList»
											public boolean remove«refs.name.toFirstUpper»(«refType.apiFQN» apiElement, boolean delete);
											public boolean remove«refs.name.toFirstUpper»(«refType.apiFQN» apiElement);
											public void clear«refs.name.toFirstUpper»();
											public void clear«refs.name.toFirstUpper»(boolean delete);
											public void addAll«refs.name.toFirstUpper»(java.util.Collection<«refType.apiFQN»> apiElements);
											public void add«refs.name.toFirstUpper»(«refType.apiFQN» apiElement);
										«ENDIF»
									'''
								}»
							«ENDFOR»
						'''
					}»
				«ENDIF»
				«IF me instanceof EPackage || !me.isAbstract»
					«/* TODO: SAMI: same for graphModelElementInterface */»
					public static java.util.List<«me.apiFQN»> find(String query, Object... params) {
						return «me.entityFQN».find(query, params)
							.list()
							.stream()
							.map(n -> new «me.apiImplFQN»((«me.entityFQN») n))
							.collect(java.util.stream.Collectors.toList());
					}
				«ENDIF»
			}
		'''
	}
	
	def getExtension(ENamedElement me, EPackage g) {
		if(me instanceof EClass) {
			if(!me.ESuperTypes.empty) {
				return '''«me.ESuperTypes.get(0).apiFQN»'''
			}
		}
		return '''org.eclipse.emf.ecore.EObject'''
	}
}
