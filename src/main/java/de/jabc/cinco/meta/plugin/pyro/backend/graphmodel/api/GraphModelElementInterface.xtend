package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.ContainingElement
import mgl.Enumeration
import mgl.GraphModel
import mgl.GraphicalModelElement
import mgl.MGLModel
import mgl.ModelElement
import mgl.Node
import mgl.Type

class GraphModelElementInterface extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(Type me)'''«me.name.fuEscapeJava».java'''
	
	def contentEnum(Enumeration me) {
		val g = me.modelPackage
		'''
			package «g.apiFQNBase»;
			
			public enum «me.name.fuEscapeJava» {
				«me.literals.map['''«it.toUnderScoreCase.escapeJava»'''].join(",")»
			}
		'''
	}
	
	def content(ModelElement me, boolean isTransient) {
		val g = me.modelPackage as MGLModel
		'''
			package «g.apiFQNBase»;
			
			public interface «me.name.fuEscapeJava» extends «me.javaExtending»«IF me.hasToExtendContainer», graphmodel.Container«ENDIF» {
				«/* NOTE: SAMI: "public entity.«g.name.lowEscapeJava».«me.name.fuEscapeJava»DB getDelegate();" cannot be used, 
				 * since entity-classes are not inheriting eachother, leading to type-incompatibility.
				 * Need to typecast on each use of this method to the corresponding entity-class.
				 * (if this would be possible, abstract-types wouldn't be allowed to have this method, since
				 * they don't have a corresponding entity-class)
				 */» 
				public «me.name.fuEscapeJava» get«me.name.fuEscapeJava»View();«/* TODO: currently just a workaround */»
				«IF me instanceof GraphicalModelElement»
					public «coreAPIFQN("GraphModel")» getRootElement();
					public «coreAPIFQN("ModelElementContainer")» getContainer();
				«ENDIF»
				«IF me instanceof GraphModel»
					«IF !isTransient»
						public String getRouter();
						public String getConnector();
						public long getWidth();
						public long getHeight();
						public double getScale();
						public String getFileName();
						public String getExtension();
					«ENDIF»
					«me.embeddedEdges» 
				«ENDIF»
				«IF me instanceof Node»
					«IF me.isEcorePrime»
						«{
							val refType = me.primeReference.type
							'''
								public «refType.apiFQN» get«me.primeReference.name.fuEscapeJava»();
							'''
						}»
					«ELSEIF me.isPrime»
						public «me.primeReference.type.apiFQN» get«me.primeReference.name.fuEscapeJava»();
					«ENDIF»
					«connectedNodeMethods(me,g)»
				«ENDIF»
				«IF me instanceof ContainingElement»
					«embeddedNodeMethods(me,g, isTransient)»
				«ENDIF»
				«FOR attr:me.attributes»					
					«attr.name.getSet('''«IF attr.isList»java.util.List<«ENDIF»«attr.javaType(g)»«IF attr.list»>«ENDIF»''')»
					«IF attr.isPrimitive && attr.annotations.exists[name.equals("file")]»
						«IF attr.isList»
							public java.util.List<java.io.File> get«attr.name.fuEscapeJava»File();
						«ELSE»
							public java.io.File get«attr.name.fuEscapeJava»File();
						«ENDIF»
					«ENDIF»
				«ENDFOR»
				«IF me.attributesExtended.exists[it.annotations.exists[name.equals("file")]]»
					public java.io.File getFile(String path);
					public java.io.File getFile(entity.core.BaseFileDB baseFile);
					public entity.core.BaseFileDB getBaseFile(String path);
				«ENDIF»
			}
	'''
	}
	
	def embeddedEdges(GraphModel g)
	'''
	«FOR edge:g.edges»
		public java.util.List<«edge.name.fuEscapeJava»> get«edge.name.fuEscapeJava»s();
	«ENDFOR»
	'''
	
	def connectedNodeMethods(Node node, MGLModel g)
	'''
		«FOR incoming:node.possibleIncoming»
			«'''incoming«incoming.name.fuEscapeJava»s'''.toString.getMethod('''java.util.List<«incoming.name.fuEscapeJava»>''')»
		«ENDFOR»
		«FOR outgoing:node.possibleOutgoing»
			«'''outgoing«outgoing.name.fuEscapeJava»s'''.toString.getMethod('''java.util.List<«outgoing.name.fuEscapeJava»>''')»
		«ENDFOR»
		«FOR source:node.possibleIncoming.map[possibleSources].flatten.toSet»
			«'''«source.name.fuEscapeJava»Predecessors'''.toString.getMethod('''java.util.List<«source.name.fuEscapeJava»>''')»
		«ENDFOR»
		«FOR target:node.possibleOutgoing.map[possibleTargets].flatten.toSet»
			«'''«target.name.fuEscapeJava»Successors'''.toString.getMethod('''java.util.List<«target.name.fuEscapeJava»>''')»
		«ENDFOR»
		«FOR outgoing:node.possibleOutgoing.filter[!isAbstract]»
			public «outgoing.apiFQN» new«outgoing.name.fuEscapeJava»(graphmodel.Node target);
		«ENDFOR»
	'''
	
	def embeddedNodeMethods(ContainingElement ce, MGLModel g, boolean isTransient)
	'''
		«FOR em:ce.possibleEmbeddingTypes(g)» 
			«IF !em.isIsAbstract»
				«IF em.isPrime || em.isEcorePrime»
					«{
						val refElem = (em as Node).primeReference.type
						'''
							public «em.name.fuEscapeJava» new«em.name.fuEscapeJava»(
								«IF isTransient»
									«refElem.apiFQN» object,
								«ELSE»
									long primeId,
								«ENDIF»
								int x,
								int y
							);
							public «em.name.fuEscapeJava» new«em.name.fuEscapeJava»(
								«IF isTransient»
									«refElem.apiFQN» object,
								«ELSE»
									long primeId,
								«ENDIF»
								int x,
								int y,
								int width,
								int height
							);
						'''
					}»
				«ELSE»
					public «em.name.fuEscapeJava» new«em.name.fuEscapeJava»(int x, int y, int width, int height);
					public «em.name.fuEscapeJava» new«em.name.fuEscapeJava»(int x, int y);
				«ENDIF»
			«ENDIF»
			public java.util.List<«em.name.fuEscapeJava»> get«em.name.fuEscapeJava»s();
		«ENDFOR»
	'''
	
	def getSet(String name,String type)
	'''
		«name.getMethod(type)»
		«name.setMethod(type)»
	'''
	
	def getMethod(String name,String type)
	'''«type» «IF type.toLowerCase.contains("boolean")»is«ELSE»get«ENDIF»«name.fuEscapeJava»();'''
	
	def setMethod(String name,String type)
	'''void set«name.fuEscapeJava»(«type» «name.lowEscapeJava»);'''
	
	def voidMethod(String name,String args)
	'''void «name»(«args»);'''
	
}
