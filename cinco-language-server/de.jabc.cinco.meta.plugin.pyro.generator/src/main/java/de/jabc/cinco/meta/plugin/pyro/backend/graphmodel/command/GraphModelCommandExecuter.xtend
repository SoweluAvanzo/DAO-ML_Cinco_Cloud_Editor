package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.command

import de.jabc.cinco.meta.plugin.pyro.canvas.PyroAppearance
import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import de.jabc.cinco.meta.plugin.pyro.util.MGLExtension
import java.util.LinkedHashMap
import java.util.Map
import mgl.Attribute
import mgl.ComplexAttribute
import mgl.Edge
import mgl.GraphModel
import mgl.GraphicalModelElement
import mgl.MGLModel
import mgl.ModelElement
import mgl.Node
import mgl.UserDefinedType
import style.AbstractShape
import style.BooleanEnum
import style.ConnectionDecorator
import style.ContainerShape
import style.EdgeStyle
import style.NodeStyle
import style.Styles
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.ModelElementHook
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EPackage

class GraphModelCommandExecuter extends Generatable {
	
	protected extension ModelElementHook = new ModelElementHook
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(GraphModel g)'''«g.commandExecuter».java'''
	
	def content(GraphModel g,Styles styles) {
		val modelPackage = g.modelPackage as MGLModel
		val primeReferencedModels = g.resolveAllPrimeReferencedGraphModels
		
		'''
			package info.scce.pyro.core.command;
			
			import «modelPackage.typeRegistryFQN»;
			import info.scce.pyro.core.graphmodel.BendingPoint;
			import graphmodel.*;
			import entity.core.PyroUserDB;
			import info.scce.pyro.sync.GraphModelWebSocket;
			import java.util.List;
			import java.util.LinkedList;
			import java.util.stream.Collectors;
			import org.eclipse.emf.ecore.EObject;
			import info.scce.pyro.rest.ObjectCache;
			import info.scce.pyro.sync.WebSocketMessage;
			import «dbTypeFQN»;
			
			/**
			 * Author zweihoff
			 */
			public class «g.commandExecuter» extends CommandExecuter {
				
				private info.scce.pyro.rest.ObjectCache objectCache;
				
				public «g.commandExecuter»(
					PyroUserDB user,
					ObjectCache objectCache,
					GraphModelWebSocket graphModelWebSocket,
					«g.entityFQN» graph,
					List<info.scce.pyro.core.command.types.HighlightCommand> highlightings
				) {
					super(
						graphModelWebSocket,
						highlightings,
						user.id
					);
					this.objectCache = objectCache;
					super.batch = new BatchExecution(user,new «g.apiImplFQN»(graph,this));
				}
				
				/**
				 * NOTE: Use this if it is needed to utilize (/work on) the same batch of commands
				 * of the GraphModelCommandExecuter and on the one of a primeReferenced GraphModel
				 */
				public «g.commandExecuter»(
					BatchExecution batch,
					GraphModelWebSocket graphModelWebSocket,
					List<info.scce.pyro.core.command.types.HighlightCommand> highlightings,
					ObjectCache objectCache
				) {
					super(
						graphModelWebSocket,
						highlightings,
						batch.user.id
					);
					super.batch = batch;
					this.objectCache = objectCache;
				}
				
				public void remove«g.name.escapeJava»(«g.apiFQN» entity){
					// for complex props
					entity.delete();
				}
				«FOR e:g.nodes.filter[!isIsAbstract]»
					
					public «e.apiFQN» create«e.name.escapeJava»(long x, long y, long width, long height, ModelElementContainer mec, «e.restFQN» prev«IF e.prime»,long primeId«ENDIF»){
						«e.entityFQN» node = new «e.entityFQN»();
						node.width = width;
						node.height = height;
						node.x = x;
						node.y = y;
						
						«'''node'''.primitiveInit(e,false)»
						
						// setting container
						«dbTypeName» dbMec = TypeRegistry.getApiToDB(mec);
						node.setContainer(dbMec);
						
						«IF e.prime»
						    «{
						    	val refType = e.primeReference.type
						    	val types = refType.resolveSubTypesAndType
						    	'''
						    		«dbTypeName» prime = null;
						    		«FOR type: types»
						    			if(prime == null) {
						    				prime = «type.entityFQN».findById(primeId);
						    			}
						    		«ENDFOR»
						    		node.set«e.primeReference.name.fuEscapeJava»(prime);
						    		
						    	'''
						    }»
						«ENDIF»
					    node.persist();
					    
					    «e.apiFQN» apiNode = new «e.apiImplFQN»(node,this);
					    super.createNode(
					    	TypeRegistry.getTypeOf(apiNode),
					    	apiNode,mec,
					    	TypeRegistry.
					    	getTypeOf(mec),
					    	x,
					    	y,
					    	width,
					    	height,
					    	«IF e.prime»
					    		«{
					    			val refType = e.primeReference.type
									'''
										«refType.restFQN».fromEntityProperties(
											prime,
											new ObjectCache()
										),
									'''
								}»
							«ENDIF»
							«e.restFQN».fromEntityProperties(
								node,
								new ObjectCache()
							)
						);
						if(prev != null) {
					    	//create from copy
					    	this.update«e.name.fuEscapeJava»(apiNode,prev,true);
					    }
					    
						return apiNode;
					}
					
					public «e.apiFQN» create«e.name.escapeJava»(long x, long y, ModelElementContainer mec, «e.restFQN» prev«IF e.prime»,long primeId«ENDIF») {
						«{
							val nodeStyle = styling(e,styles) as NodeStyle
							val size = nodeStyle.mainShape.size
							'''
								return create«e.name.escapeJava»(
									x,
									y,
									«IF size!==null»
										«size.width»,
										«size.height»,
									«ELSE»
										«MGLExtension.DEFAULT_WIDTH»,
										«MGLExtension.DEFAULT_HEIGHT»,
									«ENDIF»
									mec,
									prev«IF e.prime»,
									primeId
									«ENDIF»
								);
							'''
						}»
					}
					
					public void remove«e.name.escapeJava»(
						«e.apiFQN» entity«IF e.prime || e.ecorePrime»,
						«{
					    	val refType = e.primeReference.type
					    	'''
				    			«refType.apiFQN» prime
					    	'''
						}»«
						ENDIF»
					){
						super.removeNode(
							TypeRegistry.getTypeOf(entity),
							entity,
							TypeRegistry.getTypeOf(entity.getContainer()),
							«IF e.prime»
								«{
							    	'''
							    		TypeRegistry.getApiToRest(prime),
							    	'''
								}»
							«ELSE»
								null,
							«ENDIF»
							«e.restFQN».fromEntityProperties(
								(«e.entityFQN») entity.getDelegate(),
								new ObjectCache()
							)
						);
					}
				«ENDFOR»
				«FOR e:g.edges.filter[!isAbstract]»
					
					public «e.apiFQN» create«e.name.escapeJava»(Node source, Node target, List<BendingPoint> positions, «e.restFQN» prev){
						«e.entityFQN» edge = new «e.entityFQN»();
						«'''edge'''.setDefault(e,false)»
						
						setEdgeDBComponents(edge, source, target, positions);
						edge.persist();
					
						«e.apiFQN» apiEdge = new «e.apiImplFQN»(edge,this);
						super.createEdge(
							TypeRegistry.getTypeOf(apiEdge),
							apiEdge,
							source,
							TypeRegistry.getTypeOf(source),
							target,
							TypeRegistry.getTypeOf(target),
							edge.bendingPoints,
							«e.restFQN».fromEntityProperties(
								edge,
								new ObjectCache()
							)
						);
						if(prev != null) {
							//create from copy
							this.update«e.name.fuEscapeJava»(apiEdge,prev,true);
						}
						
						«e.postCreate("apiEdge","this",gc)»
						
						return apiEdge;
					}
					
					public void addBendpoint«e.name.escapeJava»(«e.apiFQN» edge, long x,long y){
						entity.core.BendingPointDB bp = new entity.core.BendingPointDB();
						bp.x = x;
						bp.y = y;
						List<BendingPoint> bps = edge.getBendingPoints().stream()
							.map(n->BendingPoint.fromEntity((entity.core.BendingPointDB) n))
							.collect(java.util.stream.Collectors.toList());
						bps.add(BendingPoint.fromEntity(bp));
						bp.persist();
						super.updateBendingPoints(TypeRegistry.getTypeOf(edge),edge,bps);
					}
					
					public void update«e.name.fuEscapeJava»(«e.apiFQN» edge, List<BendingPoint> points){
						super.updateBendingPoints(TypeRegistry.getTypeOf(edge),edge,points);
					}
					
					public void remove«e.name.escapeJava»(«e.apiFQN» entity){
						super.removeEdge(
							TypeRegistry.getTypeOf(entity),
							entity,
							«e.restFQN».fromEntityProperties(
								(«e.entityFQN») entity.getDelegate(),
								new ObjectCache()
							),
							TypeRegistry.getTypeOf(entity.getSourceElement()),
							TypeRegistry.getTypeOf(entity.getTargetElement())
						);
					}
					
					public void set«e.name.fuEscapeJava»Container(«e.entityFQN» edge, «dbTypeName» container) {
						«{
							val possibleContainer = e.resolvePossibleContainer + #[g]
							
							'''container'''.typeInstanceSwitchTemplate(
								possibleContainer,
								[container|
									'''
										«container.entityFQN» containerDB = («container.entityFQN») container;
										containerDB.addModelElements(edge);
										edge.setContainer(container);
										container.persist();
									'''
								],
								[type|'''«type.entityFQN»'''],
								true
							)
						}»
					}
					
					public void set«e.name.fuEscapeJava»DBSource(«e.entityFQN» edge, Node source) {
						«{
							val possibleTypes = e.resolvePossibleSources.filter(ModelElement)
							'''source'''.typeInstanceSwitchTemplate(
								possibleTypes,
								[type|
									'''
										«type.entityFQN» o = («type.entityFQN») ((«type.apiFQN») source).getDelegate();
										edge.setSource(o);
										o.addOutgoing(edge);
										o.persist();
									'''
								],
								[type|'''«type.apiFQN»'''],
								false
							)
						}»
					}
					
					public void set«e.name.fuEscapeJava»DBTarget(«e.entityFQN» edge, Node target) {
						«{
							val possibleTypes = e.resolvePossibleTargets.filter(ModelElement)
							'''target'''.typeInstanceSwitchTemplate(
								possibleTypes,
								[type|
									'''
										«type.entityFQN» o = («type.entityFQN») ((«type.apiFQN») target).getDelegate();
										edge.setTarget(o);
										o.addIncoming(edge);
										o.persist();
									'''
								],
								[type|'''«type.apiFQN»'''],
								false
							)
						}»
					}
				«ENDFOR»
		
			    public void setEdgeDBComponents(«dbTypeName» edge, Node source, Node target, List<BendingPoint> bendingPoints) {
			    	«{
						val possibleTypes = g.edges.map[resolveSubTypesAndType].flatten.toSet.filter[!isAbstract].filter(ModelElement)
						'''
							«IF !possibleTypes.empty»
								graphmodel.GraphModel graphModel = source.getRootElement();
								«dbTypeName» e = TypeRegistry.getApiToDB(graphModel);
								
								// switch edge types
								«{
								'''edge'''.typeInstanceSwitchTemplate(
									possibleTypes,
									[e|
										'''
											«e.entityFQN» edgeDB = («e.entityFQN») edge;
											set«e.name.escapeJava»DBSource(edgeDB, source);
											set«e.name.escapeJava»DBTarget(edgeDB, target);
											set«e.name.fuEscapeJava»Container(edgeDB, e);
											bendingPoints.forEach( p -> {
												entity.core.BendingPointDB bp = new entity.core.BendingPointDB();
												bp.x = p.getx();
												bp.y = p.gety();
												bp.persist();
												edgeDB.bendingPoints.add(bp);
											});
										'''
									],
									[type|'''«type.entityFQN»'''],
									true
								)	
								}»
								else {
									throw new RuntimeException("Type not found!");
								}
							«ELSE»
								throw new RuntimeException("No Type specified!");
							«ENDIF»
						'''
					}»
			    }
			    
			    public void updateIdentifiableElement(IdentifiableElement entity, info.scce.pyro.core.graphmodel.IdentifiableElement prev) {
			    	«{
						val possibleTypes = g.elementsAndTypesAndGraphModels.filter[!isAbstract]
						'''entity'''.typeInstanceSwitchTemplate(
							possibleTypes,
							[e|
								'''
									«IF e instanceof GraphModel && !e.equals(g)»this.get«e.commandExecuter»().«ENDIF»update«e.name.fuEscapeJava»Properties((«e.apiFQN») entity, («e.restFQN») prev);
								'''
							],
							[type|'''«type.apiFQN»'''],
							false
						)
					}»
			    }
				«FOR e:g.elementsAndTypesAndGraphModels.filter[!isAbstract]»
					
					public void update«e.name.fuEscapeJava»Properties(«e.apiFQN» entity, «e.restFQN» prev) {
						super.updatePropertiesReNew(
							TypeRegistry.getTypeOf(entity),
							«e.restFQN».fromEntityProperties(
								(«e.entityFQN») entity.getDelegate(),
								new ObjectCache()
							),
							prev
						);
					}
				«ENDFOR»
				
				//FOR NODE EDGE GRAPHMODEL TYPE
				«FOR e:g.elementsAndTypesAndGraphModels»«val subTypes = e.resolveSubTypesAndType.filter[!isAbstract].filter[!(it.equals(e))].toSet»
					// update method for type «e.typeName»
					«IF !e.isType»
						public «e.apiFQN» update«e.name.fuEscapeJava»(«e.restFQN» update){
							«{
								'''
									«IF !subTypes.empty»
										// handle subTypes
										«FOR subType:subTypes SEPARATOR " else "
										»if(update.get__type().equals("«subType.typeName»")) {
											«IF subType instanceof GraphModel»
												// handling of graphModel-subType will be delegated to
												// graphModels CommandExecuter's update-method
												return («e.apiFQN») this.get«subType.commandExecuter»()
													.update«subType.name.fuEscapeJava»((«subType.restFQN») update);
											«ELSE»
												return («e.apiFQN») update«subType.name.fuEscapeJava»((«subType.restFQN») update);
											«ENDIF»
										}«
										ENDFOR»
										
									«ENDIF»
								'''
							}»
							«IF e.isAbstract»
								return null;
							«ELSE»
								«e.entityFQN» dbEntity = «e.entityFQN».findById(update.getId());
								«e.apiFQN» apiEntity = («e.apiFQN») TypeRegistry.getDBToApi(dbEntity, this);
								// handle type
								return update«e.name.fuEscapeJava»(apiEntity, update, true);
							«ENDIF»
						}
						
					«ENDIF»
					public «e.apiFQN» update«e.name.fuEscapeJava»(«IF !e.isType»«e.apiFQN» apiEntity, «ENDIF»«e.restFQN» update){
						«{
							'''
								«IF !subTypes.empty»
									// handle subTypes
									«FOR subType:subTypes SEPARATOR " else "
									»if(update.get__type().equals("«subType.typeName»")) {
										«IF subType instanceof GraphModel»
											// handling of graphModel-subType will be delegated to
											// graphModels CommandExecuter's update-method
											return («e.apiFQN») this.get«subType.commandExecuter»()
												.update«subType.name.fuEscapeJava»(«IF !e.isType»(«subType.apiFQN») apiEntity, «ENDIF»(«subType.restFQN») update, true);
										«ELSE»
											return («e.apiFQN») update«subType.name.fuEscapeJava»(«IF !e.isType»(«subType.apiFQN») apiEntity, «ENDIF»(«subType.restFQN») update, true);
										«ENDIF»
									}«
									ENDFOR»
									
								«ENDIF»
							'''
						}»
						«IF e.isAbstract»
							return null;
						«ELSE»
							// handle type
							return update«e.name.fuEscapeJava»(«IF !e.isType»apiEntity, «ENDIF»update, true);
						«ENDIF»
					}
					
					«IF !e.isAbstract»
						public «e.apiFQN» update«e.name.fuEscapeJava»(«IF !e.isType»«e.apiFQN» apiEntity, «ENDIF»«e.restFQN» update, boolean propagate){
							«{
								'''
									«IF !subTypes.empty»
										// handle subTypes
										«FOR subType:subTypes SEPARATOR " else "
										»if(update.get__type().equals("«subType.typeName»")) {
											«IF subType instanceof GraphModel»
												// handling of graphModel-subType will be delegated to
												// graphModels CommandExecuter's update-method
												return («e.apiFQN») this.get«subType.commandExecuter»()
													.update«subType.name.fuEscapeJava»(«IF !e.isType»(«subType.apiFQN») apiEntity, «ENDIF»(«subType.restFQN») update, propagate);
											«ELSE»
												return («e.apiFQN») update«subType.name.fuEscapeJava»(«IF !e.isType»(«subType.apiFQN») apiEntity, «ENDIF»(«subType.restFQN») update, propagate);
											«ENDIF»
										}«
										ENDFOR»
										
									«ENDIF»
								'''
							}»
							// handle type
							«IF e.isType»
								«e.apiFQN» apiEntity = null;
								«e.entityFQN» dbEntity = «e.entityFQN».findById(update.getId());
								«e.restFQN» prev = «e.restFQN».fromEntityProperties(
									dbEntity,
									new ObjectCache()
								);
								
								if(dbEntity == null) {
									// create new entity, if not existent (only for UserDefinedTypes)
									dbEntity = new «e.entityFQN»();
									// persist to generate associated id
									dbEntity.persist();
									apiEntity = («e.apiFQN») TypeRegistry.getDBToApi(dbEntity, this);
									«e.postCreate("apiEntity", "this", gc)»
								} else {
									apiEntity = («e.apiFQN») TypeRegistry.getDBToApi(dbEntity, this);
								}
							«ELSE»
								«e.entityFQN» dbEntity = («e.entityFQN») apiEntity.getDelegate();
								«e.restFQN» prev = «e.restFQN».fromEntityProperties(
									dbEntity,
									new ObjectCache()
								);
							«ENDIF»
							
							«IF e.isType»
								«'''dbEntity'''.setDefault(e,false)»
							«ENDIF»
						
							//for primitive prop
							«FOR attr:e.attributesExtended.filter[isPrimitive]»
								«IF attr.attributeTypeName.getEnum(modelPackage) !== null»
									//for enums
									«IF attr.list»
										if(update.get«attr.name.escapeJava»() != null) {
											List<«attr.entityFQN»> newList = update.get«attr.name.escapeJava»().stream().map( n -> {
												«{
													val en = attr.attributeTypeName.getEnum(modelPackage)
													'''
														switch (n.getliteral()){
															«en.literals.map[
																'''
																	case "«it.toUnderScoreCase.escapeJava»":
																		return «attr.entityFQN».«it.toUnderScoreCase.fuEscapeJava»;
																'''
															].join("")»
														}
														return null;
													'''
												}»
											}).collect(java.util.stream.Collectors.toList());
											
											// check if list has changed
											if(isDifferent(dbEntity.«attr.name.escapeJava», newList)) {
												dbEntity.«attr.name.escapeJava» = newList;
												«e.triggerPostAttributeChangedHook(attr)»
											}
										}
									«ELSE»
										if(
											update.get«attr.name.escapeJava»() != null
										) {
											«{
												val en = attr.attributeTypeName.getEnum(modelPackage) '''
												String e = update.get«attr.name.escapeJava»().getliteral();
												«attr.entityFQN» newValue = null;
												switch (e){
													«en.literals.map[
														'''
															case "«it.toUnderScoreCase.escapeJava»":
																newValue = «attr.entityFQN».«it.toUnderScoreCase.fuEscapeJava»;
																break;
														'''
													].join("")»
												}
												if(dbEntity.«attr.name.escapeJava» != newValue) { // value changed?
													dbEntity.«attr.name.escapeJava» = newValue;
													«e.triggerPostAttributeChangedHook(attr)»
												}
											'''}»
										}
									«ENDIF»
								«ELSE»
									if(
										«IF attr.isList»
											isDifferent(dbEntity.«attr.name.escapeJava», update.get«attr.name.escapeJava»())
										«ELSE»
											«IF attr.attributeTypeName == "EString"»
												dbEntity.«attr.name.escapeJava» != null &&
												!dbEntity.«attr.name.escapeJava».equals(update.get«attr.name.escapeJava»())
												||
												update.get«attr.name.escapeJava»() != null &&
												!update.get«attr.name.escapeJava»().equals(dbEntity.«attr.name.escapeJava»)
											«ELSE»
												dbEntity.«attr.name.escapeJava» != update.get«attr.name.escapeJava»()
											«ENDIF»
										«ENDIF»
									) { // value changed?
										apiEntity.set«attr.name.fuEscapeJava»(
											update.get«attr.name.escapeJava»()
										);
										«e.triggerPostAttributeChangedHook(attr)»
									}
								«ENDIF»
							«ENDFOR»
							
							// for complex prop
							«FOR attr:e.attributesExtended.filter[!isPrimitive]»
								«val attributeType = (attr as ComplexAttribute).type»
								«val attributeTypeName = attributeType.name.fuEscapeJava»
								«IF attr.list»
									{
										// list
										List<«attr.apiFQN»> newList = update.get«attr.name.escapeJava»().stream()
											.map((n) -> n != null ? this.update«attributeTypeName»(n) : null)
											.collect(java.util.stream.Collectors.toList());
										
										// check if list has changed
										if(isDifferent(apiEntity.get«attr.name.fuEscapeJava»(), newList)) {
											apiEntity.set«attr.name.fuEscapeJava»(newList);
											«e.triggerPostAttributeChangedHook(attr)»
										}
									}
								«ELSE»
									// type
									if(update.get«attr.name.escapeJava»() != null) {
										«IF attr.isModelElement» 
											//fetch entity and reset
											«dbTypeName» new«attr.name.fuEscapeJava» = «typeRegistryName».findAbstract(
												update.get«attr.name.escapeJava»().getId(),
												update.get«attr.name.escapeJava»().getClass()
											);
										«ELSE»
											//update user defined type
											«dbTypeName» new«attr.name.fuEscapeJava» = («dbTypeName») update«attributeTypeName»(update.get«attr.name.escapeJava»()).getDelegate();
										«ENDIF»
										
										if(!new«attr.name.fuEscapeJava».equals(dbEntity.get«attr.name.fuEscapeJava»())) {
											// update new value
											dbEntity.set«attr.name.fuEscapeJava»(new«attr.name.fuEscapeJava»«IF attributeType instanceof UserDefinedType», true«ENDIF»);
											«e.triggerPostAttributeChangedHook(attr)»
										}
									} else if(dbEntity.get«attr.name.fuEscapeJava»() != null) {
										// update new value
										dbEntity.set«attr.name.fuEscapeJava»(null«IF !attr.isModelElement», true«ENDIF»);
										«e.triggerPostAttributeChangedHook(attr)»
									}
								«ENDIF»
								
							«ENDFOR»
							
							// persist changes
							dbEntity.persist();
							
							if(propagate) {
								// collect changes for websocket-multiuser-propagation
								super.updateProperties(
									TypeRegistry.getTypeOf(dbEntity),
									«e.restFQN».fromEntityProperties(
										dbEntity,
										new ObjectCache()
									),
									prev
								);
							}
							
							return apiEntity;
						}
						
						«IF e instanceof GraphicalModelElement && (e as GraphicalModelElement).hasAppearanceProvider(styles)»
							private void updateAppearanceProvider«e.name.escapeJava»(«e.apiFQN» modelElement)
							{
								«{
									val style = (e as GraphicalModelElement).styleFor(styles)
									val cl = style.appearanceProvider.substring(1,style.appearanceProvider.length-1)
									'''
									«cl» app = new «cl»();
									String elementName = TypeRegistry.getTypeOf(modelElement);
									«IF style instanceof EdgeStyle»
										style.Appearance root = style.StyleFactory.eINSTANCE.createAppearance();
										root.setId("root");
										root.setName("«style.name»");
										«style.appearance("null","root")»
										style.Appearance rootResult = app.getAppearance(modelElement,root.getName());
										if(rootResult != null) {
											style.Appearance rootMerged = super.mergeAppearance(root,rootResult);
											super.updateAppearance(elementName,modelElement,rootMerged);
										}
										«style.collectMarkupCSSTags().values.join»
									«ENDIF»
									«IF style instanceof NodeStyle»
										«style.mainShape.collectMarkupCSSTags("",0,"null").values.join»
									«ENDIF»
									'''
								}»
							}
							
						«ENDIF»
					«ENDIF»
				«ENDFOR»
				«IF g.graphModelContainsPostAttributeValueChange»
					public <T extends graphmodel.IdentifiableElement>  void triggerPostAttributeChange(T element, String name, de.jabc.cinco.meta.runtime.action.CincoPostAttributeChangeHook<T> hook) {
						//property change hook
						{
							org.eclipse.emf.ecore.EStructuralFeature esf = new org.eclipse.emf.ecore.EStructuralFeature();
							esf.setName(name);
							hook.init(this);
							if(hook.canHandleChange(element,esf)) {
								hook.handleChange(element,esf);
							}
						}
					}
					
				«ENDIF»
				@Override
				public void updateAppearance() {
					updateAppearance(getBatch().getGraphModel());
				}
				
				@Override
				public void updateAppearance(ModelElementContainer mec) {
					getAllModelElements(mec).forEach((element)->{
						updateAppearanceOf(element);
					});
				}
				
				@Override
				public void updateAppearanceOf(IdentifiableElement element) {
					«{
						val possibleTypes = g.elements.filter[!isIsAbstract].filter[!isType].filter[hasAppearanceProvider(styles)]
						'''element'''.typeInstanceSwitchTemplate(
							possibleTypes,
							[e|
								'''
									updateAppearanceProvider«e.name.escapeJava»((«e.apiFQN») element);
								'''
							],
							[type|'''«type.apiFQN»'''],
							false
						)
					}»
				}
				«FOR e:g.elements.filter(UserDefinedType)»
					
					public void remove«e.name.escapeJava»(«e.apiFQN» apiEntity){
						«dbTypeName» entity = apiEntity.getDelegate();
						«IF e.isAbstract»
							remove«e.name.escapeJava»(entity);
						«ELSE»
							remove«e.name.escapeJava»((«e.entityFQN») entity);
						«ENDIF»
					}
					«IF e.isAbstract»
						
						public void remove«e.name.escapeJava»(«dbTypeName» entity) {
							«{
								val possibleTypes = g.elements.filter(UserDefinedType).filter[!isIsAbstract].filter(ModelElement)
								'''entity'''.typeInstanceSwitchTemplate(
									possibleTypes,
									[t|
										'''
											remove«t.name.escapeJava»((«t.entityFQN») entity);
										'''
									],
									[type|'''«type.entityFQN»'''],
									true
								)
							}»
						}
					«ELSE»
						
						public void remove«e.name.escapeJava»(«e.entityFQN» entity){
							//for enums
							«FOR attr:e.attributes.filter[isPrimitive].filter[attributeTypeName.getEnum(modelPackage)!==null]»
								if(entity.«attr.name.escapeJava»!=null){
									«IF attr.list»
										entity.«attr.name.escapeJava».clear();
									«ELSE»
										entity.«attr.name.escapeJava»= null;
									«ENDIF»
								}
							«ENDFOR»
							//remove all complex fieds
							«FOR attr:e.attributes.filter[!isPrimitive]»
								«IF attr.list»
									if(!entity.isEmpty«attr.name.fuEscapeJava»()){
										entity.get«attr.name.fuEscapeJava»().stream()
											.map(«attr.entityFQN».class::cast)
											.map((n) -> («attr.apiFQN») TypeRegistry.getDBToApi(n, this))
											.forEach(this::remove«attr.attributeTypeName.fuEscapeJava»);
									}
								«ELSE»
									if(entity.get«attr.name.fuEscapeJava»() != null) {
										«dbTypeName» cp«attr.name.escapeJava» = entity.get«attr.name.fuEscapeJava»();
										«attr.apiFQN» apiEntity = («attr.apiFQN») TypeRegistry.getDBToApi(cp«attr.name.escapeJava», this);
										remove«attr.attributeTypeName.fuEscapeJava»(apiEntity);
									}
								«ENDIF»
							«ENDFOR»
							entity.delete();
						}
					«ENDIF»
				«ENDFOR»
				
				public «g.commandExecuter» get«g.commandExecuter»() {
					return this;
				}
				«
					val otherModels = (primeReferencedModels + g.resolveSubTypesAndType).filter[!(it.equals(g))].toSet
				»
				«IF !otherModels.empty»
					/* 
					 * All primeReferenced Models or concrete SubTypes have their own CommandExecuter,
					 * that will be resolved and referenced for further handling
					 */
					«FOR pm: otherModels»
						«IF !pm.apiFQN.toString.equals(g.apiFQN.toString)»
							
							public «pm.commandExecuter» get«pm.commandExecuter»() {
								return new «pm.commandExecuter»(
									this.batch,
									this.graphModelWebSocket,
									this.highlightings,
									this.objectCache
								);
							}
						«ENDIF»
					«ENDFOR»
				«ENDIF»
				
				public <T> boolean isDifferent(java.util.Collection<T> a, java.util.Collection<T> b) {
					java.util.Set<?> aH = a.stream().collect(java.util.stream.Collectors.toSet());
					java.util.Set<?> bH = b.stream().collect(java.util.stream.Collectors.toSet());
					return !aH.equals(bH) || a.size() != b.size();
				}
				
				/**
				 * This can be used by e.g. CustomActions to synchronize to e.g. the PrimeViewer
				 * or other custom plugins a change of an element
				 */
				@Override
				public void sync(ModelElement e) {
					if(e == null) {
						return;
					}
					String messageEventType = "«websocketEventPrime»";
					String idString = null;
					if(e instanceof GraphModel) {
						idString = e.getId();
					} else {
						// element could be contained
						GraphModel root = ((ModelElement) e).getRootElement();
						idString = root.getId();
					}
					long id = Long.parseLong(idString);
					info.scce.pyro.core.graphmodel.IdentifiableElement content = TypeRegistry.getApiToRest(e);
					
					// propagate directly to used graphModel
					graphModelWebSocket.send(id, WebSocketMessage.fromEntity(userId, messageEventType, content));
					«{
						val potencialReferencees = g.referencingGraphModels(gc)
						'''
							«IF !potencialReferencees.empty»
								
								// collect ids of references
								List<Long> ids = new LinkedList<>();
								
								// propagate to potencial referencing models
								«FOR refG : potencialReferencees»
									{
										// collect ids from all «refG.typeName»
										List<«refG.entityFQN»> potencialReferencing = «refG.entityFQN».listAll();
										ids.addAll(
											potencialReferencing.stream().map((«refG.entityFQN» entity) -> entity.id).collect(Collectors.toList())
										);
									}
								«ENDFOR»
								
								if(content == null)
									return;
								// propagate to references
								for(Long refId : ids) {
									this.graphModelWebSocket.send(refId, WebSocketMessage.fromEntity(userId, messageEventType, content));
								}
							«ENDIF»
						'''
					}»
				}
				
				/**
				 * This can be used by e.g. CustomActions to synchronize to e.g. the PrimeViewer
				 * or other custom plugins a change of an element
				 */
				@Override
				public void sync(EObject e) {
					«{
						var referencableEcoreElements = g.ecorePrimeRefs.map[type].map[it.modelPackage as EPackage].map[elementsAndEnumsAndPackage].flatten.toSet
						'''
							«IF !referencableEcoreElements.empty»
								if(e == null) {
									return;
								}
								// collect ids of references
								List<Long> ids = new LinkedList<>();
								info.scce.pyro.core.graphmodel.IdentifiableElement content = TypeRegistry.getDBToRestPrime(e.getDelegate(), new ObjectCache(), false);
								
								«FOR e : referencableEcoreElements SEPARATOR " else "
								»«val ecoreElement = (e as EObject)»if("«ecoreElement.typeName»".equals(e.getType())) {
									«{	
										val potencialReferencees = ecoreElement.referencingGraphModels(gc)
										'''
											«IF !potencialReferencees.empty»
												«FOR refG : potencialReferencees»
													{
														// collect ids from all «refG.typeName»
														List<«refG.entityFQN»> potencialReferencing = «refG.entityFQN».listAll();
														ids.addAll(
															potencialReferencing.stream().map((«refG.entityFQN» entity) -> entity.id).collect(Collectors.toList())
														);
													}
												«ENDFOR»
											«ENDIF»
										'''
									}»
								}«
								ENDFOR»
								
								if(content == null)
									return;
								// propagate to references
								for(Long refId : ids) {
									this.graphModelWebSocket.send(refId, WebSocketMessage.fromEntity(userId, "«websocketEventPrime»", content));
								}
							«ENDIF»
						'''
					}»
				}
			}
		'''
	}
	
	def dispatch getMainShape(EdgeStyle style) {
		style.decorator
	}
	
	def dispatch getMainShape(NodeStyle style) {
		style.mainShape
	}
	
	def setDefault(CharSequence s,ModelElement t,boolean useExecuter) {
		return setDefault(s, t, useExecuter, '''null''')
	}
	
	def setDefault(CharSequence s, ModelElement t, boolean useExecuter, CharSequence injectParent)
	'''
		«IF t instanceof GraphModel»
			«s».scale = 1.0;
			«s».connector = "normal";
			«s».height = 600L;
			«s».width = 2000L;
			«s».router = null;
			«s».isPublic = false;

		«ENDIF»
		«IF t instanceof Node»
			«s».width = 0L;
			«s».height = 0L;
			«s».x = 0L;
			«s».y = 0L;
		«ENDIF»
		«IF t instanceof Edge»
		«ENDIF»
		«primitiveInit(s, t, useExecuter)»
	'''
	
	def primitiveInit(CharSequence s, ModelElement t, boolean useExecuter) {
		'''
			//primitive init
			«FOR attr:t.attributesExtended.filter[isPrimitive]»
				«IF attr.list»
				«ELSE»
					«IF attr.attributeTypeName.getEnum(t.modelPackage as MGLModel)!==null»
						«s».«attr.name.escapeJava» = «attr.getEnumDefault(useExecuter)»;
					«ELSE»
						«s».«attr.name.escapeJava» = «attr.attributeTypeName.getPrimitiveDefault(attr)»;
					«ENDIF»
				«ENDIF»
			«ENDFOR»
		'''
	}
	
	def getPrimitiveDefault(String string,Attribute attr) {
		if(attr.defaultValue!==null) {
			switch(string){
				case "EInt": return '''«attr.defaultValue»'''
				case "ELong": return '''«attr.defaultValue»L'''
				case "EBigInteger": return '''«attr.defaultValue»'''
				case "EByte": return '''«attr.defaultValue»'''
				case "EShort": return '''«attr.defaultValue»'''
				case "EString": return '''"«attr.defaultValue»"'''
				default: return '''«attr.defaultValue»'''
			}
		}
		switch(string){
			case "EBoolean": return '''false'''
			case "ELong": return '''0L'''
			case "EBigInteger": return '''0'''
			case "EByte": return '''0'''
			case "EShort": return '''0'''
			case "EFloat": return '''0.0'''
			case "EBigDecimal": return '''0.0'''
			case "EInt": return '''0'''
			case "EDouble": return '''0.0'''
			default: return '''null'''
		}
	}
	
	def getEnumDefault(Attribute attr, boolean useExecuter)
	'''«attr.entityFQN».«attr.attributeTypeName.getEnum(attr.modelPackage as MGLModel).literals.get(0).toUnderScoreCase»'''
	
	def Map<AbstractShape,CharSequence> collectMarkupCSSTags(AbstractShape shape,String prefix,int i,String ref){
		val l = new LinkedHashMap
		l.put(shape,shape.markupCSS(prefix,i,ref))
		if(shape instanceof ContainerShape) {
			shape.children.forEach[n,idx|l.putAll(n.collectMarkupCSSTags(i+"x",idx,'''«prefix.tagClass(i)»'''))]			
		}
		return l
	}
	
	def Map<ConnectionDecorator,CharSequence> collectMarkupCSSTags(EdgeStyle style){
		val l = new LinkedHashMap
		style.decorator.forEach[n,idx|l.put(n,n.markupCSS("x",idx,'''null'''))]			
		return l
	}
	
	def tagClass(String s,int i)'''pyro«s»«i»tag'''
	
	def markupCSS(AbstractShape shape,String s,int i,String ref)
	'''
		style.Appearance «s.tagClass(i)» = style.StyleFactory.eINSTANCE.createAppearance();
		«s.tagClass(i)».setId("«s.tagClass(i)»");
		«IF shape.name.nullOrEmpty»
			«s.tagClass(i)».setName("«s.tagClass(i)»");
		«ELSE»
			«s.tagClass(i)».setName("«shape.name.escapeJava»");
		«ENDIF»
		«shape.appearance(ref,s.tagClass(i).toString)»
		style.Appearance «s.tagClass(i)»Result = app.getAppearance(modelElement,«s.tagClass(i)».getName());
		if(«s.tagClass(i)»Result != null) {
			style.Appearance «s.tagClass(i)»Merged = super.mergeAppearance(«s.tagClass(i)»,«s.tagClass(i)»Result);
			super.updateAppearance(elementName,modelElement,«s.tagClass(i)»Merged);
		}
	'''
	
	def markupCSS(ConnectionDecorator shape,String s,int i,String ref)
	'''
		style.Appearance «s.tagClass(i)» = style.StyleFactory.eINSTANCE.createAppearance();
		«s.tagClass(i)».setId("«s.tagClass(i)»");
		«IF shape.name.nullOrEmpty»
			«s.tagClass(i)».setName("«s.tagClass(i)»");
		«ELSE»
			«s.tagClass(i)».setName("«shape.name.escapeJava»");
		«ENDIF»
		«shape.appearance(ref,s.tagClass(i).toString)»
		style.Appearance «s.tagClass(i)»Result = app.getAppearance(modelElement,«s.tagClass(i)».getName());
		if(«s.tagClass(i)»Result != null) {
			style.Appearance «s.tagClass(i)»Merged = super.mergeAppearance(«s.tagClass(i)»,«s.tagClass(i)»Result);
			super.updateAppearance(elementName,modelElement,«s.tagClass(i)»Merged);
		}
	'''
	
	def appearance(AbstractShape shape,String parent,String s){
		if(shape.referencedAppearance!==null){
			return new PyroAppearance(shape.referencedAppearance).appearancePreparing(parent,s)
		}
		if(shape.inlineAppearance!==null){
			return new PyroAppearance(shape.inlineAppearance).appearancePreparing(parent,s)
		}
		return new PyroAppearance().appearancePreparing(parent,s)
	}
	
	def appearance(EdgeStyle shape,String parent,String s){
		if(shape.referencedAppearance!==null){
			return new PyroAppearance(shape.referencedAppearance).appearancePreparing(parent,s)
		}
		if(shape.inlineAppearance!==null){
			return new PyroAppearance(shape.inlineAppearance).appearancePreparing(parent,s)
		}
		return new PyroAppearance().appearancePreparing(parent,s)
	}
	
	def appearance(ConnectionDecorator shape,String parent,String s){
		if(shape.predefinedDecorator!==null) {
			if(shape.predefinedDecorator.referencedAppearance!==null){
				return new PyroAppearance(shape.predefinedDecorator.referencedAppearance).appearancePreparing(parent,s)
			}
			if(shape.predefinedDecorator.inlineAppearance!==null){
				return new PyroAppearance(shape.predefinedDecorator.inlineAppearance).appearancePreparing(parent,s)
			}
		}
		return new PyroAppearance().appearancePreparing(parent,s)
	}
	
	def appearancePreparing(PyroAppearance app,String parent,String name)
	{
		'''
		«name».setAngle(«app.angle»F);
		«name».setFilled(style.BooleanEnum.«IF app.filled==BooleanEnum.FALSE»FALSE«ELSEIF app.filled==BooleanEnum.TRUE»TRUE«ELSE»UNDEF«ENDIF»);
		style.Color foreground«name» = style.StyleFactory.eINSTANCE.createColor();
		«IF !(app.foreground===null)»
			foreground«name».setR(«app.foreground.r»);
			foreground«name».setG(«app.foreground.g»);
			foreground«name».setB(«app.foreground.b»);
		«ENDIF»
		«name».setForeground(foreground«name»);
		style.Color background«name» = style.StyleFactory.eINSTANCE.createColor();
		«IF !(app.background===null)»
			background«name».setR(«app.background.r»);
			background«name».setG(«app.background.g»);
			background«name».setB(«app.background.b»);
		«ENDIF»
		«name».setBackground(background«name»);
		«name».setLineInVisible(«IF app.lineInVisible»true«ELSE»false«ENDIF»);
		style.Font font«name» = style.StyleFactory.eINSTANCE.createFont();
		«IF app.font!==null»
			font«name».setFontName("«app.font.fontName»");
			font«name».setSize(«app.font.size»);
			font«name».setIsBold(«IF app.font.isIsBold»true«ELSE»false«ENDIF»);
			font«name».setIsItalic(«IF app.font.isIsItalic»true«ELSE»false«ENDIF»);
		«ENDIF»
		«name».setFont(font«name»);
		«name».setLineWidth(«app.lineWidth»);
		«name».setLineStyle(style.LineStyle.«app.lineStyle.toString.toUpperCase»);
		«name».setTransparency(«app.transparency»);
		«name».setParent(«parent»);
		«name».setImagePath(«IF app.imagePath.nullOrEmpty»null«ELSE»«app.imagePath»«ENDIF»);
		'''
	}
	
	def triggerPostAttributeChangedHook(ModelElement e, Attribute attr)
	'''
		«IF e.containsPostAttributeValueChange»
			«{
				// PostAttributeValueChange - Hook
				val hooks = e.resolvePostAttributeValueChange
				'''
					// trigger hooks
					«FOR h: hooks»
						triggerPostAttributeChange(apiEntity, "«attr.name»", new «h»());
					«ENDFOR»
				'''
			}»
		«ENDIF»
	'''
}
