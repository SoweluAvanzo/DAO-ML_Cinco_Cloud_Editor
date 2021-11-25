package de.jabc.cinco.meta.plugin.pyro.backend.connector

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import java.util.HashSet
import java.util.LinkedList
import java.util.List
import java.util.Set
import mgl.Attribute
import mgl.ComplexAttribute
import mgl.ContainingElement
import mgl.Edge
import mgl.Enumeration
import mgl.GraphModel
import mgl.GraphicalModelElement
import mgl.MGLModel
import mgl.ModelElement
import mgl.Node
import mgl.NodeContainer
import mgl.Type
import mgl.UserDefinedType
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EEnumLiteral
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EReference

class DataConnector extends Generatable {
	
	public  List<Model> models = new LinkedList
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	// DEPRECATED-INHERITANCE
	private def newModel(String fqn, String name, String inherits) {
		val m = new Model(fqn, name, inherits)
		models.add(m)
		m
	}
	
	private def newModel(String fqn,String name) {
		val m = new Model(fqn,name)
		models.add(m)
		m
	}
	
	def generateFiles() {
		
		// generate ProjectServices
		for(s:gc.projectServices) {
			val PyroProjectService = newModel("entity.core", s.projectServiceName.toString)
			for(attr:s.value.subList(2,s.value.length)) {
				PyroProjectService.singlePrimitiveAttribute(attr.escapeJava, "String")
			}
			for(attr:s.value.subList(2,s.value.length)) {
				PyroProjectService.generateGetter(attr.fuEscapeJava, "String", attr.escapeJava)
				PyroProjectService.generateSetter(attr.fuEscapeJava, "String", attr.escapeJava)
			}
			
		}
		
		// generate enumerations
		gc.mglModels.forEach[
			enumerations.forEach[
				generateEnum(it)
			]
		]
		
		for(m:gc.mglModels) {
			// graphModels
			m.graphmodels.filter[!isAbstract].forEach[generateGraphModel]
			// container
			m.nodes.filter[!isAbstract].filter(NodeContainer).forEach[generateContainer]
			// nodes
			m.nodes.filter[!isAbstract].filter[!(it instanceof NodeContainer)].forEach[generateNode]
			// edges
			m.edges.filter[!isAbstract].forEach[generateEdge]
			// types
			m.elements.filter(UserDefinedType).filter[!isAbstract].forEach[generateType]
		}
		
		// ecores
		for(e:gc.ecores) {
			e.generateEcore
		}
	}
	
	private def generateGraphModel(GraphModel g) {
		val GraphModel = newModel(g.modelPackage.entityFQNBase.toString,g.name.fuEscapeJava)
		GraphModel.singlePrimitiveAttribute("router","String")
		GraphModel.singlePrimitiveAttribute("connector","String")
		GraphModel.singlePrimitiveAttribute("width","long")
		GraphModel.singlePrimitiveAttribute("height","long")
		GraphModel.singlePrimitiveAttribute("scale","double")
		GraphModel.singlePrimitiveAttribute("isPublic","boolean")
		GraphModel.singlePrimitiveAttribute("filename","String")
		GraphModel.singlePrimitiveAttribute("extension","String")
		
		val possibleModelElements = g.containableElementsDefinition
		val nodesAndEdges = possibleModelElements.filter[it instanceof Node || it instanceof Edge]
		
		GraphModel.createMultiAttribute(nodesAndEdges, "modelElements", "container_"+g.name.fuEscapeJava);
		GraphModel.generateAttributes(g)
		GraphModel.createDeleteFunction(g)
		GraphModel.generateReferences(g)
	}
	
	private def generateNode(Node n) {
		val mglModel = n.MGLModel
		val Node = newModel(mglModel.entityFQNBase.toString,n.name.fuEscapeJava)
		Node.singlePrimitiveAttribute("x","long")
		Node.singlePrimitiveAttribute("y","long")
		Node.singlePrimitiveAttribute("width","long")
		Node.singlePrimitiveAttribute("height","long")
		
		val possibleContainer = n.resolvePossibleContainer.map[Type.cast(it)]
		val possibleIncoming = n.possibleIncoming
		val possibleOutgoing = n.possibleOutgoing
		Node.createSingleAttribute(n, possibleContainer, "container")
		Node.createMultiAttribute(possibleIncoming, "incoming", "target_"+n.name.escapeJava);
		Node.createMultiAttribute(possibleOutgoing, "outgoing", "source_"+n.name.escapeJava);
		
		Node.generateAttributes(n)
		
		Node.generateReferences(n)
		
		Node.createDeleteFunction(n)
		
		Node
	}
	
	private def generateContainer(NodeContainer nc) {
		val Container = nc.generateNode
		
		var possibleContainments = nc.containableElementsDefinition
		Container.createMultiAttribute(possibleContainments, "modelElements", "container_"+nc.name.fuEscapeJava);
	}
	
	private def generateEdge(Edge e) {
		val mglModel = e.MGLModel
		val Edge = newModel(mglModel.entityFQNBase.toString,e.name.fuEscapeJava)
		
		val possibleContainer = e.resolvePossibleContainer.map[Type.cast(it)]
		val possibleSource = e.possibleSources
		val possibleTargets = e.possibleTargets
		Edge.createSingleAttribute(e, possibleContainer, "container")
		Edge.createSingleAttribute(e, possibleSource, "source")
		Edge.createSingleAttribute(e, possibleTargets, "target")
		Edge.multiAttribute("bendingPoints","entity.core.BendingPointDB",null)

		generateAttributes(Edge,e)
		Edge.generateReferences(e)
		Edge.createDeleteFunction(e)
	}
	
	private def generateType(UserDefinedType nc) {
		val mglModel = nc.MGLModel
		val t = newModel(mglModel.entityFQNBase.toString,nc.name.fuEscapeJava)
		
		generateAttributes(t,nc)
		t.generateReferences(nc)
		t.createDeleteFunction(nc)
	}
	
	private def generateEnum(Enumeration nc) {
		val mglModel = nc.MGLModel
		val t = newModel(mglModel.entityFQNBase.toString,nc.name.fuEscapeJava)
		nc.literals.forEach[t.enumLiteral(it.toUnderScoreCase.escapeJava)]
	}
	
	private def generateAttributes(Model m, ModelElement me) {
		if(me instanceof Node) {
			if(me.prime) {
				m.generatePrimReferenceAttribute(me)
			}
		}
		for(attribute: me.attributesExtended) {
			if(attribute.isPrimitive) {
				m.generatePrimitiveAttribute(attribute)
			}
			else {
				val complexAttribute = attribute as ComplexAttribute
				m.generateComplexAttribute(me, complexAttribute)
			}
		}
	}
	
	private def generatePrimReferenceAttribute(Model m, Node me) {
		val refType = me.primeReference.type
		val referencedName = me.primeReference.name.escapeJava
		
		if(refType.modelPackage instanceof MGLModel) {
			val t = refType as ModelElement
			m.createSingleAttribute(me, t, referencedName, false)
		} else if(refType.modelPackage instanceof EPackage) {
			val refGraph = refType.modelPackage as EPackage
			val t = refType as EClass
			m.createSingleAttribute(me, refGraph, t, referencedName, false)
		} 
	}
	
	private def generatePrimitiveAttribute(Model m, Attribute attribute) {
		val mglModel = attribute.MGLModel
		if(getEnum(attribute.attributeTypeName,mglModel)!==null){
			// is enum
			if(attribute.isList) {
				m.multiEnumAttribute(attribute.name.escapeJava, attribute.entityFQN)
			} else {
				m.singleEnumAttribute(attribute.name.escapeJava, attribute.entityFQN)
			}
		} else {
			// no enum
			if(attribute.isList) {
				m.multiPrimitiveAttribute(attribute.name.escapeJava, attribute.attributeTypeName.getPrimitveObjectTypeLiteral)
			} else {
				m.singlePrimitiveAttribute(attribute.name.escapeJava, attribute.attributeTypeName.getPrimitveTypeLiteral)
			}
		}		
	}
	
	private def generateComplexAttribute(Model m, ModelElement me, ComplexAttribute complexAttribute) {
		//complex attribute
		if(complexAttribute.isList) {
			m.createMultiAttribute(complexAttribute.getType, complexAttribute.name.escapeJava, null);
		} else {
			m.createSingleAttribute(me, complexAttribute.getType, complexAttribute.name.escapeJava)
		}
	}
	
	private def generateEcore(EPackage g) {
		val GraphModel = newModel(g.modelPackage.entityFQNBase.toString, g.name.fuEscapeJava)
		GraphModel.singlePrimitiveAttribute("filename","String")
		GraphModel.singlePrimitiveAttribute("extension","String")
		GraphModel.singlePrimitiveAttribute("name","String")
		
		// create complex dataTypeLists for EPackage-Entity
		val types = g.EClassifiers
		types.forEach[ type |
			if(type instanceof EEnum)
				GraphModel.multiEnumAttribute(type.name.lowEscapeJava, type.entityFQN.toString)
			else
				GraphModel.createMultiAttribute(g, type, type.name.escapeJava, null)
		]
		
		// create delete function
		GraphModel.createDeleteFunction(g, g)
		
		// create for non-abstract types an entity
		types.filter(EClass).filter[!abstract].forEach[g.generateEcore(it)]
		
		// create eenum
		types.filter(EEnum).forEach[g.generateEcoreEEnum(it)]		
	}
	
	private def generateEcore(EPackage g, EClass type) {
		val Type = newModel(g.modelPackage.entityFQNBase.toString,type.name.fuEscapeJava)
		
		val superTypes = g.resolveSuperTypesAndType(type)
		val attributes =  type.getAttributes(g, superTypes)
		val references = type.getReferences(g, superTypes)
		
		attributes.forEach[ attr | Type.generateEcorePrimitive(g, attr)]
		references.forEach[ attr | Type.generateEcoreReference(type, g, attr)]
		
		Type.createDeleteFunction(g, type)
	}
	
	private def getReferences(EClass type, EPackage g, List<EClass> superTypes) {
		val references = new LinkedList<EReference>
		superTypes.forEach[ superType |
			// resolve references
			val resultRef = superType.eContents.filter(EReference)
			references.addAll(resultRef)
		]
		return references
	}
	
	private def getAttributes(EClass type, EPackage g, List<EClass> superTypes) {
		val attributes =  new LinkedList<EAttribute>
		superTypes.forEach[ superType |
			// resolve attributes
			val resultAttr = superType.eContents.filter(EAttribute)
			attributes.addAll(resultAttr)
		]
		return attributes
	}
	
	private def generateEcorePrimitive(Model m, EPackage g, EAttribute attr) {
		val refType = attr.EType
		if(refType instanceof EEnum){
			if(attr.isList) {
				m.multiEnumAttribute(attr.name.lowEscapeJava, refType.entityFQN)
			} else {
				m.singleEnumAttribute(attr.name.lowEscapeJava, refType.entityFQN)
			}
		} else {
			if(attr.isList) {
				m.multiPrimitiveAttribute(attr.name.lowEscapeJava, attr.ecoreType(g))
			} else {
				m.singlePrimitiveAttribute(attr.name.lowEscapeJava, attr.ecoreType(g))
			}
		}
	}
	
	private def generateEcoreReference(Model m, EObject me, EPackage g, EReference attr) {
		val refType = attr.EType
		if(attr.isList) {
			m.createMultiAttribute(g, refType, attr.name.escapeJava, null)
		} else {
			m.createSingleAttribute(me, g, refType, attr.name.escapeJava)
		}
	}
	
	private def generateEcoreEEnum(EPackage g, EEnum en) {
		val E = newModel(g.modelPackage.entityFQNBase.toString,en.name.fuEscapeJava)
		en.eContents.filter(EEnumLiteral).forEach[l|E.enumLiteral(l.name.toUnderScoreCase.escapeJava)]
	}
	
	/*
	 * GRAPHMODEL-ENTITY-DELETE
	 */
	 
	 private def createDeleteFunction(Model m, ModelElement me) {
	 	val mglModel = me.MGLModel
	 	m.createDelete[
	 	'''
			«IF me instanceof ContainingElement»
				// clear and delete all contained modelElements
				this.clearModelElements(true);
				
			«ENDIF»
			«IF me instanceof GraphicalModelElement»
				«IF me.hasContainer(mglModel)»
					// decouple from container
					«Model.dbType» c = this.getContainer();
					«{	
						var possibleContainer = me.possibleContainmentTypes.map[Type.cast(it)].resolveSubTypesAndType
						'''
						«FOR container:possibleContainer»
							«{
								val containerType = container.entityFQN
								'''
									if(c instanceof «containerType») {
										«containerType» container = («containerType») c;
										container.removeModelElements(this);
										container.persist();
										this.setContainer(null);
									}
								'''
							}»
						«ENDFOR»
						'''
					}»
					
				«ENDIF»
			«ENDIF»
			«IF me instanceof Edge»
				// remove bendingPoints
				for(entity.core.BendingPointDB b : bendingPoints) {
					b.delete();
				}
				bendingPoints.clear();
				
				«IF me.hasSources(mglModel)»
					// decouple from source
					«Model.dbType» dbSource = this.getSource();
					«{
						val possibleSources = me.resolvePossibleSources
						'''
							«FOR source:possibleSources»
								«{
									val sourceType = source.entityFQN
									'''
										if(dbSource instanceof «sourceType») {
											«sourceType» source = («sourceType») dbSource;
											source.removeOutgoing(this);
											source.persist();
										}
									'''
								}»
							«ENDFOR»
						'''
					}»
					this.setSource(null);
					
				«ENDIF»
				«IF me.hasTargets(mglModel)»
					// decouple from target
					«Model.dbType» dbTarget = this.getTarget();
					«{
						val possibleTargets = me.resolvePossibleTargets
						'''
							«FOR target:possibleTargets»
								«{
									val targetType = target.entityFQN
									'''
										if(dbTarget instanceof «targetType») {
											«targetType» target = («targetType») dbTarget;
											target.removeIncoming(this);
											target.persist();
										}
									'''
								}»
							«ENDFOR»
						'''
					}»
					this.setTarget(null);
					
				«ENDIF»
			«ENDIF»
			«IF me instanceof Node»
				«IF me.hasIncoming(mglModel)»
					// decouple from incoming
					this.clearIncoming(true);
					
				«ENDIF»
				«IF me.hasOutgoing(mglModel)»
					// decouple from outgoing
					this.clearOutgoing(true);
					
				«ENDIF»
				«IF me.prime»
					«{
						val referencedName = me.primeReference.name.escapeJava
						'''
						// decouple from primeReference
						this.set«referencedName.fuEscapeJava»(null);
						
						'''
					}»
				«ENDIF»
			«ENDIF»
			«m.generateDecoupleFromReferences(me)»
			«IF !me.attributesExtended.filter[!isPrimitive].empty»
				// cleanup all complex-attributes
				«FOR attribute: me.attributesExtended.filter[!isPrimitive]»
					«IF attribute.isList»
						this.clear«attribute.name.fuEscapeJava»(«IF (attribute as ComplexAttribute).type instanceof UserDefinedType»true«ENDIF»);
					«ELSE»
						this.set«attribute.name.fuEscapeJava»(null«IF (attribute as ComplexAttribute).type instanceof UserDefinedType», true«ENDIF»);
					«ENDIF»
				«ENDFOR»
				
			«ENDIF»
			«IF me.attributesExtended.exists[annotations.exists[name.equals("file")]]»
				// cleanup files
				info.scce.pyro.core.FileController fC = new info.scce.pyro.core.FileController();
				«FOR attr : me.attributesExtended.filter[isPrimitive && annotations.exists[name.equals("file")]]»
					«IF attr.isList»
						this.«attr.name.escapeJava».stream().forEach(
							(path) -> {
								if(path != null) {
									fC.deleteFile(path);
								}
							}
						);
					«ELSE»
						if(this.«attr.name.escapeJava» != null && !this.«attr.name.escapeJava».isEmpty()) {
							fC.deleteFile(this.«attr.name.escapeJava»);
						}
					«ENDIF»
				«ENDFOR»
				
			«ENDIF»
			// delete entity
			super.delete();
		''']
	 }

	/*
	 * GRAPHMODEL-ENTITY-SINGLE-ATTRIBUTES
	 */
	
	/**
	 * Convenience Function
	 */
	private def <T extends Type> createSingleAttribute(Model m, ModelElement me, Iterable<T> possibleAttributeTypes, String superAttributeName) {
		m.createSingleAttribute(me, possibleAttributeTypes, superAttributeName, false);
	}
	
	/**
	 * Convenience Function
	 */
	private def <T extends Type> createSingleAttribute(Model m, ModelElement me, T possibleAttributeType, String superAttributeName) {
		m.createSingleAttribute(me, possibleAttributeType, superAttributeName, false);
	}
	
	/**
	 * generates the singelAttribute for each (non-abstract) subType, resolved from it's subTypes,
	 * into the Model of an Entity, including functions.
	 * (get, set, set with delete)
	 * 
	 * @param g the mgl.GraphModel containing the entity that holds the attributes
	 * @param m the model that generates the entity
	 * @param possibleAttributeTypes the set of types for the attribute
	 * @param superAttributeName the name of the attribute inside the mgl
	 * @param joinColumn decides if a joinColumn will be generated
	 */
	private def <T extends Type> createSingleAttribute(Model m, ModelElement e, Iterable<T> possibleAttributeTypes, String superAttributeName, boolean joinColumn) {
		// resolve subTypes
		val attributeTypes = possibleAttributeTypes.map[Type.cast(it)]
		var resolvedTypes = new HashSet<Type>
		for(me : attributeTypes) {
			// resolves all subTypes recursively
			resolvedTypes.addAll(me.resolveSubTypesAndType(resolvedTypes.toList))
		}
		// create Attributes + Functions
		m.writeSingleAttributeCode(e, superAttributeName, resolvedTypes, joinColumn)
	}
	
	/**
	 * generates the singelAttribute for each (non-abstract) subType, resolved from it's subTypes,
	 * into the Model of an Entity, including functions.
	 * (get, set, set with delete)
	 * 
	 * @param g the mgl.GraphModel containing the entity that holds the attributes
	 * @param m the model that generates the entity
	 * @param possibleAttributeType the type for the attribute that will be resolved
	 * @param superAttributeName the name of the attribute inside the mgl
	 * @param joinColumn decides if a joinColumn will be generated
	 */
	private def <T extends Type> createSingleAttribute(Model m, ModelElement me, T possibleAttributeType, String superAttributeName, boolean joinColumn) {
		// resolve subTypes
		val resolvedTypes = possibleAttributeType.resolveSubTypesAndType
		// create Attributes + Functions
		m.writeSingleAttributeCode(me, superAttributeName, resolvedTypes, joinColumn)
	}

	/*
	 * GRAPHMODEL-ENTITY-MULTI-ATTRIBUTES
	 */
	
	/**
	 * generates the multiAttribute for each (non-abstract) subType, resolved from it's subTypes,
	 * into the Model of an Entity, including collection-functions.
	 * (add, get, set, clear, remove, contains, isEmpty, size)
	 * 
	 * @param g the mgl.GraphModel containing the entity that holds the attributes
	 * @param m the model that generates the entity
	 * @param possibleAttributeTypes the set of types for the attribute
	 * @param superAttributeName the name of the attribute inside the mgl
	 * @param mappedBy the name of a column inside the generated attribute-entity referencing this generated entity of the model
	 */
	private def <T extends Type> createMultiAttribute(Model m, Iterable<T> possibleAttributeTypes, String superAttributeName, String mappedBy) {
		// resolve subTypes
		val attributeTypes = possibleAttributeTypes.map[Type.cast(it)]
		var resolvedTypes = new LinkedList<Type>
		for(me : attributeTypes) {
			// resolves all subTypes
			resolvedTypes = me.resolveSubTypesAndType(resolvedTypes) as LinkedList<Type>
		}
		// create Attributes + Functions
		m.writeMultiAttributeCode(superAttributeName, resolvedTypes, mappedBy)
	}
	
	/**
	 * generates the multiAttribute for each (non-abstract) subType, resolved from it's subTypes,
	 * into the Model of an Entity, including collection-functions.
	 * (add, get, set, clear, remove, contains, isEmpty, size)
	 * 
	 * @param g the mgl.GraphModel containing the entity that holds the attributes
	 * @param m the model that generates the entity
	 * @param possibleAttributeType the type for the attribute
	 * @param superAttributeName the name of the attribute inside the mgl
	 * @param mappedBy the name of a column inside the generated attribute-entity referencing this generated entity of the model
	 */
	private def <T extends Type> createMultiAttribute(Model m, T possibleAttributeType, String superAttributeName, String mappedBy) {
		// resolve subTypes
		val resolvedTypes = possibleAttributeType.resolveSubTypesAndType
		// create Attributes + Functions
		m.writeMultiAttributeCode(superAttributeName, resolvedTypes, mappedBy)
	}
	
	/*
	 * GRAPHMODEL-ENTITY WRITE FUNCTION-MODEL
	 */
	
	private def <T extends Type> writeSingleAttributeCode(Model m, ModelElement me, String superAttributeName, Iterable<T> resolvedTypes, boolean joinColumn) {
		resolvedTypes.forEach[ t |
			m.writeSingleAttribute(t, superAttributeName, joinColumn)
		]
		m.writeSingleAttributeFunctions(me, superAttributeName.fuEscapeJava, superAttributeName, resolvedTypes);
	}
	
	private def <T extends Type> void writeSingleAttribute(Model m, T element, String superAttributeName, boolean joinColumn) {
		val subTypeAttributeName = superAttributeName.subTypeAttributeName(element)
		val subTypeAttributeType = element.entityClassName
		m.singleAttribute(
			subTypeAttributeName,
			subTypeAttributeType,
			false,
			joinColumn
		)
	}
		
	private def <T extends Type> writeSingleAttributeFunctions(Model m, ModelElement me, CharSequence functionName, CharSequence superAttributeName, Iterable<T> types) {
		val subTypeAttributeNames = types.map[superAttributeName.subTypeAttributeName(it)];
		// GETTER
		m.createGetter(Model.dbType, functionName, [
			'''
				«FOR subTypeAttributeName : subTypeAttributeNames SEPARATOR " else "
				»if(«subTypeAttributeName» != null) {
					return «subTypeAttributeName»;
				}«
				ENDFOR»
				return null;
			'''
		])
		// SETTER
		m.createSetter(functionName, '''«Model.dbType» e''', [
			'''
				set«functionName»(e, false);
			'''
		])
		// SETTER WITH DELETE
		m.createSetter(functionName, '''«Model.dbType» e, boolean deleteOld''', [
			'''
				// guard
				«dbTypeName» old = this.get«superAttributeName.toString().fuEscapeJava»();
				// if no element to delete or the element to delete is
				// same that will be set then dont delete
				if(old != null && old.equals(e)
					|| e == null && old == null
				) {
					// nothing changed
					return;
				}
				
				«IF me instanceof Node»
					«IF me.prime»
						// decouple from old references
						«FOR other : types SEPARATOR " else "
						»if(old instanceof «other.entityFQN») {
							((«other.entityFQN») old).removeReference(this);
						}«
						ENDFOR»
						
					«ENDIF»
				«ENDIF»
				// potencially delete all old elements
				if(deleteOld) {
					if(old != null) {
						old.delete();
					}
				}
				
				// set new and null others
				«FOR t : types SEPARATOR " else "
				»if(e instanceof «t.entityClassName») {
					// null all other types
					«FOR other : types.filter[!equals(t)]»
						«superAttributeName.subTypeAttributeName(other)» = null;
					«ENDFOR»
					// set element
					«superAttributeName.subTypeAttributeName(t)» = («t.entityClassName») e;
					«IF me instanceof Node»
						«IF me.prime»
							«superAttributeName.subTypeAttributeName(t)».addReference(this);
						«ENDIF»
					«ENDIF»
					return;
				}«
				ENDFOR»
				
				// default-case
				// null all types
				«FOR other : types»
					«superAttributeName.subTypeAttributeName(other)» = null;
				«ENDFOR»
			'''
		])
	}
	
	private def <T extends Type> writeMultiAttributeCode(Model m, String superAttributeName, List<T> resolvedTypes, String mappedBy) {
		if(mappedBy !== null) {
			// if mappedBy is defined, a bidirectional-mapping can be build on the object-graph,
			// while having a parent-directed mapping from the child in the db-model
			resolvedTypes.forEach[ t | 
				m.writeMultiAttribute(t, superAttributeName, mappedBy)
			]
		} else {
			// if mappedBy is not defined, a manyToMany relation is possible, by creating a
			// joinTable for the MultiAttribute/Collection
			resolvedTypes.forEach[ t | 
				m.writeMultiAttributeJoinTable(t, superAttributeName)
			]	
		}
		writeMultiAttributeFunctions(m, superAttributeName.fuEscapeJava, superAttributeName, resolvedTypes);
	}
	
	private def <T extends Type> void writeMultiAttributeJoinTable(Model m, T element, String superAttributeName) {	
		val subTypeAttributeName = superAttributeName.subTypeAttributeName(element)
		val subTypeAttributeType = element.entityClassName
		
		// create mapping
		val joinColumn = '''parent_«m.name.lowEscapeJava»db_id'''
		val inverseJoinColumn = '''child_«element.name.lowEscapeJava»db_id'''
		
		m.multiAttributeJoinTable(
			subTypeAttributeName,
			subTypeAttributeType,
			joinColumn,
			inverseJoinColumn
		)
	}
	
	private def <T extends Type> void writeMultiAttribute(Model m, T element, String superAttributeName, String mappedBy) {	
		val subTypeAttributeName = superAttributeName.subTypeAttributeName(element)
		val subTypeAttributeType = element.entityClassName
		m.multiAttribute(
			subTypeAttributeName,
			subTypeAttributeType,
			mappedBy
		)
	}
	
	private def <T extends Type> writeMultiAttributeFunctions(Model m, CharSequence functionName, CharSequence superAttributeName, List<T> types) {
		val subTypeAttributeNames = types.map[superAttributeName.subTypeAttributeName(it)];
		
		// GET (ALL)
		m.createGetter('''java.util.Collection<«Model.dbType»>''', functionName, [
			'''
				java.util.Collection<«Model.dbType»> «superAttributeName» = new java.util.ArrayList<>();	
				«IF subTypeAttributeNames.empty»
					// no attributes
				«ELSE»
					«FOR subTypeAttributeName : subTypeAttributeNames»
						«superAttributeName».addAll(«subTypeAttributeName»);
					«ENDFOR»
				«ENDIF»
				return «superAttributeName»;
			'''
		])
		// CLEAR
		m.createCollectionClear(functionName, null, [
			'''
				clear«functionName»(false);
			'''
		])
		// CLEAR WITH DELETE
		m.createCollectionClear(functionName, '''boolean delete''', [
			'''
				«IF subTypeAttributeNames.empty»
					// no attributes
				«ELSE»
					if(delete) {
						// delete all entries
						«FOR type : types»
							«{
								val subTypeAttributeName = superAttributeName.subTypeAttributeName(type);
								val iterator = '''iter_«subTypeAttributeName»'''
								'''
									java.util.Iterator<«type.entityFQN»> «iterator» = «subTypeAttributeName».iterator();
									while(«iterator».hasNext()) {
										«type.entityFQN» e = «iterator».next();
										if(e != null) {
											e.delete();
											«subTypeAttributeName».remove(e);
										}
										«iterator» = «subTypeAttributeName».iterator();
									}
								'''
							}»
						«ENDFOR»
					} else {
						// clear all collections
						«FOR subTypeAttributeName : subTypeAttributeNames»
							«subTypeAttributeName».clear();
						«ENDFOR»
					}
				«ENDIF»
			'''
		])
		// SET (ALL)
		m.createSetter(functionName, '''java.util.Collection<«Model.dbType»> eList''', [
			'''
				// clear all attribute-type-lists
				clear«functionName»();
				// add e to type-specific collections
				for(«Model.dbType» e : eList) {
					add«functionName»(e);
				}
			'''
		])
		// ADD ALL
		m.createCollectionAddAll(functionName, '''java.util.Collection<«Model.dbType»> eList''', [
			'''
				for(«Model.dbType» e : eList) {
					add«functionName»(e);
				}
			'''
		])
		// ADD
		m.createCollectionAdd(functionName, '''«Model.dbType» e''', [
			'''
				// add the entity into it's type-specific list
				«IF types.empty»
					// no attributes
				«ELSE»
					«FOR t : types SEPARATOR " else "
					»«{
						val className = t.entityClassName
						val subTypeAttributeName = superAttributeName.subTypeAttributeName(t)
						'''
							if(e instanceof «className») {
								«subTypeAttributeName».add((«className») e);
							}
						'''
					}»«
					ENDFOR»
				«ENDIF»
			'''
		])
		// REMOVE
		m.createCollectionRemove(functionName, '''«Model.dbType» e''', [
			'''
				return remove«functionName»(e, false);
			'''
		])
		m.createCollectionRemove(functionName, '''«Model.dbType» e, boolean delete''', [
			'''
				// removes the entity from it's type-specific list
				«IF types.empty»
					// no attributes
				«ELSE»
					«FOR t : types SEPARATOR " else "
					»«{
						val className = t.entityClassName
						val subTypeAttributeName = superAttributeName.subTypeAttributeName(t)
						'''
						if(e instanceof «className») {
							«className» definitiveEntity = («className») e;
							if(«subTypeAttributeName».contains(definitiveEntity)) {
								boolean result = «subTypeAttributeName».remove(definitiveEntity);
								if(delete && result) {
									definitiveEntity.delete();
								}
								return result;
							}
						}
						'''
					}»«
					ENDFOR»
				«ENDIF»
				return false;
			'''
		])
		// CONTAINS
		m.createCollectionContains(functionName, '''«Model.dbType» e''', [
			'''
				// containment-check of the entities type-specific list
				«IF types.empty»
					// no attributes
				«ELSE»
					«FOR t : types SEPARATOR " else "
					»«{
						val className = t.entityClassName
						val subTypeAttributeName = superAttributeName.subTypeAttributeName(t)
						'''
						if(e instanceof «className») {
							«className» definitiveEntity = («className») e;
							return «subTypeAttributeName».contains(definitiveEntity);
						}
						'''
					}»«
					ENDFOR»
				«ENDIF»
				return false;
			'''
		])
		
		// IS EMPTY
		m.createCollectionIsEmpty(functionName, [
			'''
				return get«functionName»().isEmpty();
			'''
		])
		
		// SIZE
		m.createCollectionSize(functionName, [
			'''
				return get«functionName»().size();
			'''
		])
	}
	
	/*
	 * EPACKAGE-ENTITY-SINGLE-ATTRIBUTES
	 */
	
	/**
	 * Convenience Function
	 */
	private def <T extends EClassifier> createSingleAttribute(Model m, EObject me, EPackage g, T possibleAttributeType, String superAttributeName) {
		m.createSingleAttribute(me, g, possibleAttributeType, superAttributeName, false);
	}
	
	/**
	 * generates the singelAttribute for each (non-abstract) subType, resolved from it's subTypes,
	 * into the Model of an Entity, including functions.
	 * (get, set, set with delete)
	 * 
	 * @param g the EPackagecontaining the entity that holds the attributes
	 * @param m the model that generates the entity
	 * @param possibleAttributeType the type for the attribute that will be resolved
	 * @param superAttributeName the name of the attribute inside the mgl
	 * @param joinColumn decides if a joinColumn will be generated
	 */
	private def <T extends EClassifier> createSingleAttribute(Model m, EObject me, EPackage g, T possibleAttributeType, String superAttributeName, boolean joinColumn) {
		// resolve subTypes
		val resolvedTypes = possibleAttributeType.resolveSubTypesAndType.filter(EClassifier).toList
		// create Attributes + Functions
		m.writeSingleAttributeCode(g, superAttributeName, resolvedTypes, joinColumn)
	}
	
	/*
	 * EPACKAGE-ENTITY-MULTI-ATTRIBUTES
	 */
	
	/**
	 * generates the multiAttribute for each (non-abstract) subType, resolved from it's subTypes,
	 * into the Model of an Entity, including collection-functions.
	 * (add, get, set, clear, remove, contains, isEmpty, size)
	 * 
	 * @param g the mgl.GraphModel containing the entity that holds the attributes
	 * @param m the model that generates the entity
	 * @param possibleAttributeType the type for the attribute
	 * @param superAttributeName the name of the attribute inside the mgl
	 * @param mappedBy the name of a column inside the generated attribute-entity referencing this generated entity of the model
	 */
	private def <T extends EClassifier> createMultiAttribute(Model m, EPackage g, T possibleAttributeType, String superAttributeName, String mappedBy) {
		// resolve subTypes
		val resolvedTypes = possibleAttributeType.resolveSubTypesAndType.filter(EClassifier).toList
		// create Attributes + Functions
		m.writeMultiAttributeCode(g, superAttributeName, resolvedTypes, mappedBy)
	}
	
	/*
	 * EPACKAGE-ENTITY WRITE FUNCTION-MODEL
	 */
	
	private def <T extends EClassifier> writeSingleAttributeCode(Model m, EPackage g, String superAttributeName, List<T> resolvedTypes, boolean joinColumn) {
		resolvedTypes.forEach[ t | 
			m.writeSingleAttribute(g, t, superAttributeName, joinColumn)
		]
		m.writeSingleAttributeFunctions(g, superAttributeName.fuEscapeJava, superAttributeName, resolvedTypes);
	}
	
	private def <T extends EClassifier> void writeSingleAttribute(Model m, EPackage g, T element, String superAttributeName, boolean joinColumn) {
		val subTypeAttributeName = superAttributeName.subTypeAttributeName(element)
		val subTypeAttributeType = element.getEntityClassName
		m.singleAttribute(
			subTypeAttributeName,
			subTypeAttributeType,
			false,
			joinColumn
		)
	}
		
	private def <T extends EClassifier> writeSingleAttributeFunctions(Model m, EPackage g, CharSequence functionName, CharSequence superAttributeName, List<T> types) {
		val subTypeAttributeNames = types.map[superAttributeName.subTypeAttributeName(it)];
		// GETTER
		m.createGetter(Model.dbType, functionName, [
			'''
				«FOR subTypeAttributeName : subTypeAttributeNames SEPARATOR " else "
				»if(«subTypeAttributeName» != null) {
					return «subTypeAttributeName»;
				}«
				ENDFOR»
				return null;
			'''
		])
		// SETTER
		m.createSetter(functionName, '''«Model.dbType» e''', [
			'''
				set«functionName»(e, false);
			'''
		])
		// SETTER WITH DELETE
		m.createSetter(functionName, '''«Model.dbType» e, boolean deleteOld''', [
			'''
				// potencially delete all old elements
				if(deleteOld) {
					«FOR other : types»
						«{
							var attrName = superAttributeName.subTypeAttributeName(other)
							'''
								if(«attrName» != null) {
									«attrName».delete();
								}
							'''
						}»
					«ENDFOR»
				}
				
				// set new and null others
				«FOR t : types SEPARATOR " else "
				»if(e instanceof «t.getEntityClassName») {
					// null all other types
					«FOR other : types.filter[!equals(t)]»
						«superAttributeName.subTypeAttributeName(other)» = null;
					«ENDFOR»
					// set element
					«superAttributeName.subTypeAttributeName(t)» = («t.getEntityClassName») e;
					return;
				}«
				ENDFOR»
				
				// default-case
				// null all types
				«FOR other : types»
					«superAttributeName.subTypeAttributeName(other)» = null;
				«ENDFOR»
			'''
		])
	}
	
	private def <T extends EClassifier> writeMultiAttributeCode(Model m, EPackage g, String superAttributeName, List<T> resolvedTypes, String mappedBy) {
		if(mappedBy !== null) {
			// if mappedBy is defined, a bidirectional-mapping can be build on the object-graph,
			// while having a parent-directed mapping from the child in the db-model
			resolvedTypes.forEach[ t | 
				m.writeMultiAttribute(g, t, superAttributeName, mappedBy)
			]
		} else {
			// if mappedBy is not defined, a manyToMany relation is possible, by creating a
			// joinTable for the MultiAttribute/Collection
			resolvedTypes.forEach[ t | 
				m.writeMultiAttributeJoinTable(g, t, superAttributeName)
			]	
		}
		m.writeMultiAttributeFunctions(g, superAttributeName.fuEscapeJava, superAttributeName, resolvedTypes);
	}
	
	private def <T extends EClassifier> void writeMultiAttributeJoinTable(Model m, EPackage g,T element, String superAttributeName) {	
		val subTypeAttributeName = superAttributeName.subTypeAttributeName(element)
		val subTypeAttributeType = element.getEntityClassName
		
		// create mapping
		val joinColumn = '''parent_«m.name.lowEscapeJava»db_id'''
		val inverseJoinColumn = '''child_«element.name.lowEscapeJava»db_id'''
		
		m.multiAttributeJoinTable(
			subTypeAttributeName,
			subTypeAttributeType,
			joinColumn,
			inverseJoinColumn
		)
	}
	
	private def <T extends EClassifier> void writeMultiAttribute(Model m, EPackage g, T element, String superAttributeName, String mappedBy) {	
		val subTypeAttributeName = superAttributeName.subTypeAttributeName(element)
		val subTypeAttributeType = element.getEntityClassName
		m.multiAttribute(
			subTypeAttributeName,
			subTypeAttributeType,
			mappedBy
		)
	}
	
	private def <T extends EClassifier> writeMultiAttributeFunctions(Model m, EPackage g, CharSequence functionName, CharSequence superAttributeName, List<T> types) {
		val subTypeAttributeNames = types.map[superAttributeName.subTypeAttributeName(it)];
		
		// GET (ALL)
		m.createGetter('''java.util.Collection<«Model.dbType»>''', functionName, [
			'''
				java.util.Collection<«Model.dbType»> «superAttributeName» = new java.util.ArrayList<>();
				«FOR subTypeAttributeName : subTypeAttributeNames»
					«superAttributeName».addAll(«subTypeAttributeName»);
				«ENDFOR»
				return «superAttributeName»;
			'''
		])
		// CLEAR
		m.createCollectionClear(functionName, null, [
			'''
				clear«functionName»(false);
			'''
		])
		// CLEAR WITH DELETE
		m.createCollectionClear(functionName, '''boolean delete''', [
			'''
				«IF subTypeAttributeNames.empty»
					// no attributes
				«ELSE»
					if(delete) {
						// delete all entries
						«FOR type : types»
							«{
								val subTypeAttributeName = superAttributeName.subTypeAttributeName(type);
								val iterator = '''iter_«subTypeAttributeName»'''
								'''
									java.util.Iterator<«type.entityFQN»> «iterator» = «subTypeAttributeName».iterator();
									while(«iterator».hasNext()) {
										«type.entityFQN» e = «iterator».next();
										if(e != null) {
											e.delete();
											«subTypeAttributeName».remove(e);
										}
										«iterator» = «subTypeAttributeName».iterator();
									}
								'''
							}»
						«ENDFOR»
					} else {
						// clear all collections
						«FOR subTypeAttributeName : subTypeAttributeNames»
							«subTypeAttributeName».clear();
						«ENDFOR»
					}
				«ENDIF»
			'''
		])
		// SET (ALL)
		m.createSetter(functionName, '''java.util.Collection<«Model.dbType»> eList''', [
			'''
				// clear all attribute-type-lists
				clear«functionName»();
				// add e to type-specific collections
				for(«Model.dbType» e : eList) {
					add«functionName»(e);
				}
			'''
		])
		// ADD ALL
		m.createCollectionAddAll(functionName, '''java.util.Collection<«Model.dbType»> eList''', [
			'''
				for(«Model.dbType» e : eList) {
					add«functionName»(e);
				}
			'''
		])
		// ADD
		m.createCollectionAdd(functionName, '''«Model.dbType» e''', [
			'''
				// add the entity into it's type-specific list
				«FOR t : types SEPARATOR " else "
				»«{
					val className = t.getEntityClassName
					val subTypeAttributeName = superAttributeName.subTypeAttributeName(t)
					'''
					if(e instanceof «className») {
						«subTypeAttributeName».add((«className») e);
					}
					'''
				}»«
				ENDFOR»
			'''
		])
		// REMOVE
		m.createCollectionRemove(functionName, '''«Model.dbType» e''', [
			'''
				return remove«functionName»(e, false);
			'''
		])
		m.createCollectionRemove(functionName, '''«Model.dbType» e, boolean delete''', [
			'''
				// removes the entity from it's type-specific list
				«IF types.empty»
					// no attributes
				«ELSE»
					«FOR t : types SEPARATOR " else "
					»«{
						val className = t.getEntityClassName
						val subTypeAttributeName = superAttributeName.subTypeAttributeName(t)
						'''
						if(e instanceof «className») {
							«className» definitiveEntity = («className») e;
							if(«subTypeAttributeName».contains(definitiveEntity)) {
								boolean result = «subTypeAttributeName».remove(definitiveEntity);
								if(delete && result) {
									definitiveEntity.delete();
								}
								return result;
							}
						}
						'''
					}»«
					ENDFOR»
				«ENDIF»
				return false;
			'''
		])
		// CONTAINS
		m.createCollectionContains(functionName, '''«Model.dbType» e''', [
			'''
				// containment-check of the entities type-specific list
				«FOR t : types SEPARATOR " else "
				»«{
					val className = t.getEntityClassName
					val subTypeAttributeName = superAttributeName.subTypeAttributeName(t)
					'''
					if(e instanceof «className») {
						«className» definitiveEntity = («className») e;
						return «subTypeAttributeName».contains(definitiveEntity);
					}
					'''
				}»«
				ENDFOR»
				return false;
			'''
		])
		
		// IS EMPTY
		m.createCollectionIsEmpty(functionName, [
			'''
				return get«functionName»().isEmpty();
			'''
		])
		
		// SIZE
		m.createCollectionSize(functionName, [
			'''
				return get«functionName»().size();
			'''
		])
	}
	
	
	 private def createDeleteFunction(Model m, EPackage g, EObject me) {
	 	m.createDelete[
		 	'''
		 		«IF me instanceof EPackage»
					«{
						val types = g.elements					
						'''
							// cleanup all contained EClassifier
							«FOR type: types»
								this.clear«type.name.fuEscapeJava»(true);
							«ENDFOR»
						'''
					}»
		 		«ENDIF»
		 		«IF me instanceof EClass»
					«{
						val superTypes = g.resolveSuperTypesAndType(me)
						val attributes =  me.getAttributes(g, superTypes)
						val references = me.getReferences(g, superTypes)
						'''
							«FOR attr:attributes.filter[it.list]»
								this.«attr.name.lowEscapeJava».clear();
							«ENDFOR»
							«FOR ref:references»
								«IF ref.list»
									this.clear«ref.name.fuEscapeJava»(true);
								«ELSE»
									this.set«ref.name.fuEscapeJava»(null, true);
								«ENDIF»
							«ENDFOR»
						'''
					}»
		 		«ENDIF»
				
				// delete entity
				super.delete();
			'''
		]
	 }
	
	
	
	private def CharSequence generateGetter(Model model, CharSequence attr, CharSequence returnType, CharSequence variableName) {
		model.createGetter(
			returnType,
			attr,
			[
				'''
					return «variableName»;
				'''
			]
		)	
	}
	
	private def CharSequence generateSetter(Model model, CharSequence attr, CharSequence paramType, CharSequence variableName) {
		model.createSetter(
			attr,
			'''«paramType» param''',
			[
				'''
					«variableName» = param;
				'''
			]
		)	
	}
	
	/*
	 * Handle references
	 */
	
	private def generateReferences(Model m, Type e) {
		val referencingTypes = e.getPrimeReferencingElements(gc.mglModels)
		referencingTypes.forEach[
			m.writeMultiAttribute(it, it.getReferenceName(), it.primeReference.name+"_"+e.name.fuEscapeJava)
		]
		m.generateReferenceHandling(e)
	}
	
	private def generateDecoupleFromReferences(Model m, Type e) {
		val referencingTypes = e.getPrimeReferencingElements(gc.mglModels)
		'''
			«IF !referencingTypes.empty»
				// decouple from referencing elements
				«FOR ref:referencingTypes»
					{
						java.util.Iterator<«ref.entityFQN»> iterator = this.«ref.getReferenceName().subTypeAttributeName(ref)».iterator();
						while(iterator.hasNext()) {
							«ref.entityFQN» next = iterator.next();
							next.set«ref.primeReference.name.fuEscapeJava»(null);
							iterator = this.«ref.getReferenceName().subTypeAttributeName(ref)».iterator();
						}
					}
				«ENDFOR»
				
			«ENDIF»
		'''
	}
	
	private def generateReferenceHandling(Model m, Type e) {
		val referencingTypes = e.getPrimeReferencingElements(gc.mglModels)
		
		m.createFunction(null, "addReference", '''PanacheEntity e''', [
			'''
				if(e == null)
					return;
				«FOR ref:referencingTypes SEPARATOR " else "
				»if (e instanceof «ref.entityFQN») {
					this.«ref.getReferenceName().subTypeAttributeName(ref)».add((«ref.entityFQN») e);
				}«
				ENDFOR»
			'''
			],
			'''// add referencing element''',
			false
		)
		
		m.createFunction(null, "removeReference", '''PanacheEntity e''', [
			'''
				if(e == null)
					return;
				«FOR ref:referencingTypes SEPARATOR " else "
				»if (e instanceof «ref.entityFQN») {
					if(this.«ref.getReferenceName().subTypeAttributeName(ref)».contains((«ref.entityFQN») e)) {
						this.«ref.getReferenceName().subTypeAttributeName(ref)».remove((«ref.entityFQN») e);
					}
				}«
				ENDFOR»
			'''
			],
			'''// remove referencing element''',
			false
		)
	}
		
	/*
	 * Naming Conventions
	 */
	
	private def String subTypeAttributeName(CharSequence attributeName, Type subType) {
		attributeName.subTypeAttributeName(subType.name.fuEscapeJava)
	}
	
	private def String subTypeAttributeName(CharSequence attributeName, EClassifier subType) {
		attributeName.subTypeAttributeName(subType.name.fuEscapeJava)
	}
	
	private def String subTypeAttributeName(CharSequence attributeName, CharSequence subTypeName) {
		'''«attributeName»_«subTypeName»'''
	}

	private def String getEntityClassName(Type t) {
		'''«t.entityFQN»'''
	}
	
	private def String getEntityClassName(EClassifier t) {
		'''«t.entityFQN»'''
	}
}
