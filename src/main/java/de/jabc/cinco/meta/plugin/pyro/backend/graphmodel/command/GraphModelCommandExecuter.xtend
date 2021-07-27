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
import mgl.NodeContainer
import mgl.UserDefinedType
import style.AbstractShape
import style.BooleanEnum
import style.ConnectionDecorator
import style.ContainerShape
import style.EdgeStyle
import style.NodeStyle
import style.Styles
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.ModelElementHook

class GraphModelCommandExecuter extends Generatable {
	
	protected extension ModelElementHook = new ModelElementHook
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(GraphModel g)'''«g.commandExecuter».java'''
	
	def content(GraphModel g,Styles styles) {
		val modelPackage = g.modelPackage as MGLModel
		val primeReferencedModels = g.getPrimeReferencedGraphModels
		
		'''
			package info.scce.pyro.core.command;
			
			import «modelPackage.typeRegistryFQN»;
			import info.scce.pyro.core.graphmodel.BendingPoint;
			import graphmodel.*;
			import entity.core.PyroUserDB;
			import info.scce.pyro.sync.GraphModelWebSocket;
			import «dbTypeFQN»;
			
			/**
			 * Author zweihoff
			 */
			public class «g.commandExecuter» extends CommandExecuter {
				
				private info.scce.pyro.rest.ObjectCache objectCache;
				private GraphModelWebSocket graphModelWebSocket;
				
				public «g.commandExecuter»(
					PyroUserDB user,
					info.scce.pyro.rest.ObjectCache objectCache,
					GraphModelWebSocket graphModelWebSocket,
					«g.entityFQN» graph,
					java.util.List<info.scce.pyro.core.command.types.HighlightCommand> highlightings
				) {
					super(
						graphModelWebSocket,
						highlightings
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
					java.util.List<info.scce.pyro.core.command.types.HighlightCommand> highlightings,
					info.scce.pyro.rest.ObjectCache objectCache
				) {
					super(
						graphModelWebSocket,
						highlightings
					);
					super.batch = batch;
					this.objectCache = objectCache;
				}
				
				public void remove«g.name.escapeJava»(«g.apiFQN» entity){
					//for complex props
					entity.delete();
				}
				«FOR e:g.nodesTopologically.filter[!isIsAbstract]»
					
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
											new info.scce.pyro.rest.ObjectCache()
										),
									'''
								}»
							«ENDIF»
							«e.restFQN».fromEntityProperties(
								node,
								new info.scce.pyro.rest.ObjectCache()
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
								new info.scce.pyro.rest.ObjectCache()
							)
						);
					}
				«ENDFOR»
				«FOR e:g.edgesTopologically.filter[!isIsAbstract]»
					
					public «e.apiFQN» create«e.name.escapeJava»(Node source, Node target, java.util.List<BendingPoint> positions, «e.restFQN» prev){
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
								new info.scce.pyro.rest.ObjectCache()
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
						java.util.List<BendingPoint> bps = edge.getBendingPoints().stream()
							.map(n->BendingPoint.fromEntity((entity.core.BendingPointDB) n))
							.collect(java.util.stream.Collectors.toList());
						bps.add(BendingPoint.fromEntity(bp));
						bp.persist();
						super.updateBendingPoints(TypeRegistry.getTypeOf(edge),edge,bps);
					}
					
					public void update«e.name.fuEscapeJava»(«e.apiFQN» edge, java.util.List<BendingPoint> points){
						super.updateBendingPoints(TypeRegistry.getTypeOf(edge),edge,points);
					}
					
					public void remove«e.name.escapeJava»(«e.apiFQN» entity){
						super.removeEdge(
							TypeRegistry.getTypeOf(entity),
							entity,
							«e.restFQN».fromEntityProperties(
								(«e.entityFQN») entity.getDelegate(),
								new info.scce.pyro.rest.ObjectCache()
							),
							TypeRegistry.getTypeOf(entity.getSourceElement()),
							TypeRegistry.getTypeOf(entity.getTargetElement())
						);
					}
					
					public void set«e.name.fuEscapeJava»Container(«e.entityFQN» edge, «dbTypeName» container) {
						«{
							val possibleContainer = e.resolvePossibleContainer + #[g]
							'''
								«FOR container:possibleContainer SEPARATOR " else "
								»if(container instanceof «container.entityFQN») {
									«container.entityFQN» containerDB = («container.entityFQN») container;
									containerDB.addModelElements(edge);
									edge.setContainer(container);
									container.persist();
								}«
								ENDFOR»
							'''
						}»
					}
					
					public void set«e.name.fuEscapeJava»DBSource(«e.entityFQN» edge, Node source) {
						«FOR source:e.resolvePossibleSources SEPARATOR " else "
						»if(source instanceof «source.apiFQN») {
							«source.entityFQN» o = («source.entityFQN») ((«source.apiFQN») source).getDelegate();
							edge.setSource(o);
							o.addOutgoing(edge);
							o.persist();
						}«
						ENDFOR»
					}
					
					public void set«e.name.fuEscapeJava»DBTarget(«e.entityFQN» edge, Node target) {
						«FOR target:e.resolvePossibleTargets SEPARATOR " else "
						»if(target instanceof «target.apiFQN») {
							«target.entityFQN» o = («target.entityFQN») ((«target.apiFQN») target).getDelegate();
							edge.setTarget(o);
							o.addIncoming(edge);
							o.persist();
						}«
						ENDFOR»
					}
				«ENDFOR»
		
			    public void setEdgeDBComponents(«dbTypeName» edge, Node source, Node target, java.util.List<BendingPoint> bendingPoints) {
			    	graphmodel.GraphModel graphModel = source.getRootElement();
			    	«dbTypeName» e = TypeRegistry.getApiToDB(graphModel);
			    	
			    	// switch edge types
			    	«FOR e:g.edgesTopologically.filter[!isIsAbstract] SEPARATOR " else "
			    	»if(edge instanceof «e.entityFQN») {
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
			    	}«
			    	ENDFOR»
			    }
			    
			    public void updateIdentifiableElement(IdentifiableElement entity, info.scce.pyro.core.graphmodel.IdentifiableElement prev) {
				    «FOR e:g.elements.filter[!isIsAbstract]+#[g]»
				    	if(entity instanceof «e.apiFQN») {
				    		update«e.name.fuEscapeJava»Properties((«e.apiFQN») entity,(«e.restFQN»)prev);
				    		return;
				    	}
				    «ENDFOR»
			    }
		
				«FOR e:g.elements.filter[!isIsAbstract]+#[g]»
					public void update«e.name.fuEscapeJava»Properties(«e.apiFQN» entity, «e.restFQN» prev) {
						super.updatePropertiesReNew(
							TypeRegistry.getTypeOf(entity),
							«e.restFQN».fromEntityProperties(
								(«e.entityFQN») entity.getDelegate(),
								new info.scce.pyro.rest.ObjectCache()
							),
							prev
						);
					}
					
				«ENDFOR»
				//FOR NODE EDGE GRAPHMODEL TYPE
				«FOR e:g.elementsAndTypesAndGraphModels»
					«IF !e.isType»
						public «e.apiFQN» update«e.name.fuEscapeJava»(«e.restFQN» update){
							«{
								val superTypes = e.resolveConcreteSuperTypes
								'''
									«IF !superTypes.empty»
										// handle subTypes
										«FOR superType:superTypes SEPARATOR " else "
										»if(update.get__type().equals("«superType.typeName»")) {
											return («e.apiFQN») update«superType.name.fuEscapeJava»((«superType.restFQN») update);
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
							val superTypes = e.resolveConcreteSuperTypes
							'''
								«IF !superTypes.empty»
									// handle subTypes
									«FOR superType:superTypes SEPARATOR " else "
									»if(update.get__type().equals("«superType.typeName»")) {
										return («e.apiFQN») update«superType.name.fuEscapeJava»(«IF !e.isType»(«superType.apiFQN») apiEntity, «ENDIF»(«superType.restFQN») update, true);
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
								val superTypes = e.resolveConcreteSuperTypes
								'''
									«IF !superTypes.empty»
										// handle subTypes
										«FOR superType:superTypes SEPARATOR " else "
										»if(update.get__type().equals("«superType.typeName»")) {
											return («e.apiFQN») update«superType.name.fuEscapeJava»(«IF !e.isType»(«superType.apiFQN») apiEntity, «ENDIF»(«superType.restFQN») update, propagate);
										}«
										ENDFOR»
										
									«ENDIF»
								'''
							}»
							// handle type
							«IF e.isType»
								«e.apiFQN» apiEntity = null;
								«e.entityFQN» dbEntity = «e.entityFQN».findById(update.getId());
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
							«ENDIF»
							«e.restFQN» prev = «e.restFQN».fromEntityProperties(
								dbEntity,
								new info.scce.pyro.rest.ObjectCache()
							);
							
							«IF e.isType»
								«'''dbEntity'''.setDefault(e,false)»
							«ENDIF»
						
							//for primitive prop
							«FOR attr:e.attributesExtended.filter[isPrimitive]»
								«IF attr.type.getEnum(modelPackage) !== null»
									//for enums
									«IF attr.list»
										if(update.get«attr.name.escapeJava»() != null) {
											java.util.List<«attr.entityFQN»> newList = update.get«attr.name.escapeJava»().stream().map( n -> {
												«{
													val en = attr.type.getEnum(modelPackage)
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
												val en = attr.type.getEnum(modelPackage) '''
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
											«IF attr.type == "EString"»
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
										dbEntity.«attr.name.escapeJava» = update.get«attr.name.escapeJava»();
										«e.triggerPostAttributeChangedHook(attr)»
									}
								«ENDIF»
							«ENDFOR»
							
							//for complex prop
							«FOR attr:e.attributesExtended.filter[!isPrimitive]»
								«IF attr.list»
									{
										// list
										java.util.List<«attr.apiFQN»> newList = update.get«attr.name.escapeJava»().stream()
											.map(this::update«attr.type.fuEscapeJava»)
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
											«dbTypeName» new«attr.name.fuEscapeJava» = («dbTypeName») update«attr.type.fuEscapeJava»(update.get«attr.name.escapeJava»()).getDelegate();
										«ENDIF»
										
										if(!new«attr.name.fuEscapeJava».equals(dbEntity.get«attr.name.fuEscapeJava»())) {
											// update new value
											dbEntity.set«attr.name.fuEscapeJava»(new«attr.name.fuEscapeJava»«IF (attr as ComplexAttribute).type instanceof UserDefinedType», true«ENDIF»);
											«e.triggerPostAttributeChangedHook(attr)»
										}
									} else if(dbEntity.get«attr.name.fuEscapeJava»() != null) {
										// update new value
										dbEntity.set«attr.name.fuEscapeJava»(null«IF !attr.isModelElement», true«ENDIF»);
										«e.triggerPostAttributeChangedHook(attr)»
									}
								«ENDIF»
								
							«ENDFOR»
							
							dbEntity.persist();
							
							«IF !e.isType»
								if(propagate) {
									super.updateProperties(
										TypeRegistry.getTypeOf(dbEntity),
										«e.restFQN».fromEntityProperties(
											dbEntity,
											new info.scce.pyro.rest.ObjectCache()
										),
										prev
									);
								}
							«ENDIF»
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
				«IF g.containsPostAttributeValueChange»
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
					super.getAllModelElements().forEach((element)->{
						«FOR e:g.elements.filter[!isIsAbstract].filter[hasAppearanceProvider(styles)] SEPARATOR "else "
						»if(element instanceof «e.apiFQN») {
							updateAppearanceProvider«e.name.escapeJava»((«e.apiFQN») element);
						}«
						ENDFOR»
					});
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
							«FOR t:g.elements.filter(UserDefinedType).filter[!isIsAbstract] SEPARATOR " else "
							»if(entity instanceof «t.entityFQN») {
								remove«t.name.escapeJava»((«t.entityFQN») entity);
							}«
							ENDFOR»
						}
					«ELSE»
						public void remove«e.name.escapeJava»(«e.entityFQN» entity){
							//for enums
							«FOR attr:e.attributes.filter[isPrimitive].filter[type.getEnum(modelPackage)!==null]»
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
											.forEach(this::remove«attr.type.fuEscapeJava»);
									}
								«ELSE»
									if(entity.get«attr.name.fuEscapeJava»() != null) {
										«dbTypeName» cp«attr.name.escapeJava» = entity.get«attr.name.fuEscapeJava»();
										«g.apiFQN».«attr.type.fuEscapeJava» apiEntity = («g.apiFQN».«attr.type.fuEscapeJava») TypeRegistry.getDBToApi(cp«attr.name.escapeJava», this);
										remove«attr.type.fuEscapeJava»(apiEntity);
									}
								«ENDIF»
							«ENDFOR»
							entity.delete();
						}
					«ENDIF»
				«ENDFOR»
				«FOR pm:primeReferencedModels»
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
				public «g.commandExecuter» get«g.commandExecuter»() {
					return this;
				}
				
				public <T> boolean isDifferent(java.util.Collection<T> a, java.util.Collection<T> b) {
					java.util.Set<?> aH = a.stream().collect(java.util.stream.Collectors.toSet());
					java.util.Set<?> bH = b.stream().collect(java.util.stream.Collectors.toSet());
					return !aH.equals(bH) || a.size() != b.size();
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
					«IF attr.type.getEnum(t.modelPackage as MGLModel)!==null»
						«s».«attr.name.escapeJava» = «attr.getEnumDefault(useExecuter)»;
					«ELSE»
						«s».«attr.name.escapeJava» = «attr.type.getPrimitiveDefault(attr)»;
					«ENDIF»
				«ENDIF»
			«ENDFOR»
		'''
	}
	
	def getPrimitiveDefault(String string,Attribute attr) {
		if(attr.defaultValue!==null) {
			switch(string){
				case "EInt": return '''«attr.defaultValue»L'''
				case "ELong": return '''«attr.defaultValue»L'''
				case "EBigInteger": return '''«attr.defaultValue»L'''
				case "EByte": return '''«attr.defaultValue»L'''
				case "EShort": return '''«attr.defaultValue»L'''
				case "EString": return '''"«attr.defaultValue»"'''
				default: return '''«attr.defaultValue»'''
			}
		}
		switch(string){
			case "EBoolean": return '''false'''
			case "ELong": return '''0L'''
			case "EBigInteger": return '''0L'''
			case "EByte": return '''0L'''
			case "EShort": return '''0L'''
			case "EFloat": return '''0.0'''
			case "EBigDecimal": return '''0.0'''
			case "EInt": return '''0L'''
			case "EDouble": return '''0.0'''
			default: return '''null'''
		}
	}
	
	def getEnumDefault(Attribute attr, boolean useExecuter)
	'''«attr.entityFQN».«attr.type.getEnum(attr.modelPackage as MGLModel).literals.get(0).toUnderScoreCase»'''
	
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
		«IF e.hasPostAttributeValueChange»
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
