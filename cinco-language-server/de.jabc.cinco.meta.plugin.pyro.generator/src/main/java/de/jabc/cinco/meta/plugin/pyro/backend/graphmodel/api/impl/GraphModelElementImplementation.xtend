package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.impl

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import de.jabc.cinco.meta.plugin.pyro.util.MGLExtension
import mgl.Attribute
import mgl.ContainingElement
import mgl.Edge
import mgl.GraphicalModelElement
import mgl.MGLModel
import mgl.ModelElement
import mgl.Node
import mgl.UserDefinedType
import style.NodeStyle
import style.Styles
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.ModelElementHook
import mgl.GraphModel
import mgl.NodeContainer

class GraphModelElementImplementation extends Generatable {
	
	protected extension ModelElementHook = new ModelElementHook
	
	new(GeneratorCompound gc) {
		super(gc)
	}
		
	def filename(ModelElement me)'''«me.name.fuEscapeJava»Impl.java'''
	
	def content(ModelElement me, Styles styles) {
		content(me, styles, false)
	}
	
	def content(ModelElement me, Styles styles, boolean isTransient)
	{
		val modelPackage = me.MGLModel
		val typeRegistry = '''«typeRegistryName»'''
		'''
			package «modelPackage.apiImplFQNBase»;
			
			import «dbTypeFQN»;
			«IF !isTransient»
				import «modelPackage.typeRegistryFQN»;
				import «commandExecuterFQN»;
				«FOR g: modelPackage.graphModels.filter[!isAbstract]»
					import «g.commandExecuterFQN»;
				«ENDFOR»
			«ENDIF»
			
			public class «me.name.fuEscapeJava»Impl implements «me.apiFQN» {
				
				«IF isTransient»
					final long id;
					«IF me instanceof ContainingElement || me instanceof Node»
						int width = 0;
						int height = 0;
					«ENDIF»
					«IF me instanceof Node»
						int x = 0;
						int y = 0;
						final java.util.List<graphmodel.Edge> incoming = new java.util.LinkedList<>();
						final java.util.List<graphmodel.Edge> outgoing = new java.util.LinkedList<>();
						«IF me.isPrime»
							«{
								val refElem = me.primeReference.type
								'''
									«refElem.apiFQN» «me.primeReference.name.escapeJava»;
								'''
							}»
						«ENDIF»
					«ENDIF»
					«IF me instanceof GraphModel»
						final java.util.List<graphmodel.Edge> edges = new java.util.LinkedList<>();
					«ENDIF»
					«IF me instanceof Edge»
						graphmodel.Node source;
						graphmodel.Node target;
						final java.util.List<info.scce.pyro.trans.BendingPoint> bendingPoints = new java.util.LinkedList<>();
					«ENDIF»
					«IF me instanceof GraphicalModelElement»
						graphmodel.ModelElementContainer container;
					«ENDIF»
					«IF me instanceof ContainingElement»
						final java.util.List<graphmodel.ModelElement> modelElements = new java.util.LinkedList<>();
					«ENDIF»
					«FOR attr:me.attributesExtended»
						«IF attr.isList»java.util.List<«ENDIF»«attr.javaType»«IF attr.isList»>«ENDIF» «attr.name.escapeJava»«IF attr.isList» =  new LinkedList<>()«ENDIF»;
					«ENDFOR»
				«ELSE»
					private final «me.entityFQN» delegate;
					private final CommandExecuter cmdExecuter;
					«IF me instanceof UserDefinedType»
						private final graphmodel.IdentifiableElement parent;
						private final info.scce.pyro.core.graphmodel.IdentifiableElement prev;
					«ENDIF»
				«ENDIF»
				
				public «me.name.fuEscapeJava»Impl«IF isTransient»() {
					«ELSE»
					(
						«me.entityFQN» delegate,
						CommandExecuter cmdExecuter«IF me instanceof UserDefinedType»,
						graphmodel.IdentifiableElement parent,
						info.scce.pyro.core.graphmodel.IdentifiableElement prev«ENDIF»
					) {
					«ENDIF»
					«IF isTransient»
						«IF me instanceof GraphicalModelElement»
							this.container = null;
						«ENDIF»
						this.id = this.hashCode();
						«FOR attr:me.attributesExtended.filter[isPrimitive]»
							this.«attr.name.escapeJava» = «attr.getPrimitiveDefault»;
						«ENDFOR»
					«ELSE»
						this.delegate = delegate;
						this.cmdExecuter = cmdExecuter;
						«IF me instanceof UserDefinedType»
							this.parent = parent;
							this.prev = prev;
						«ENDIF»
					«ENDIF»
				}
				«IF !(isTransient && me instanceof mgl.GraphModel)»
					
					public «me.name.fuEscapeJava»Impl(
						«IF isTransient»
							«IF me instanceof GraphicalModelElement»graphmodel.ModelElementContainer container«ENDIF»
						«ELSE»
							CommandExecuter cmdExecuter«IF me instanceof UserDefinedType»,
							graphmodel.IdentifiableElement parent,
							info.scce.pyro.core.graphmodel.IdentifiableElement prev
							«ENDIF»
						«ENDIF»
					) {
						«IF isTransient»
							«IF me instanceof GraphicalModelElement»
								this.container = container;
							«ENDIF»
							this.id = this.hashCode();
							«FOR attr:me.attributesExtended.filter[isPrimitive]»
								this.«attr.name.escapeJava» = «attr.getPrimitiveDefault»;
							«ENDFOR»
						«ELSE»
							this.delegate = new «me.entityFQN»();
							this.delegate.persist();
							this.cmdExecuter = cmdExecuter;
							«IF me instanceof UserDefinedType»
								this.parent = parent;
								this.prev = prev;
							«ENDIF»
						«ENDIF»
					}
				«ENDIF»
				
				@Override
				public boolean equals(Object obj) {
					return obj!=null
						&& obj instanceof «me.apiFQN»
						&& ((«me.apiFQN») obj).getId().equals(getId());
				}
				«IF !isTransient»
					
					@Override
					public int hashCode() {
						return (int) this.getDelegateId();
					}
				«ENDIF»
				
				@Override
				public String getId() {
					return Long.toString(this.getDelegateId());
				}
				
				@Override
				public long getDelegateId() {
					«IF isTransient»
						return this.id;
					«ELSE»
						return this.delegate.id;
					«ENDIF»
				}
				
				@Override
				public boolean isTransient() {
					return «isTransient»;
				}
				
				@Override
				public «IF isTransient»«dbTypeName»«ELSE»«me.entityFQN»«ENDIF» getDelegate() {
					«IF isTransient»
						return null;
					«ELSE»
						return this.delegate;
					«ENDIF»
				}
				«IF me instanceof GraphModel»
					
					@Override
					public String getRouter() {
						«IF isTransient»
							return null;
						«ELSE»
							return this.delegate.router;
						«ENDIF»
					}
					
					@Override
					public String getConnector() {
						«IF isTransient»
							return null;
						«ELSE»
							return this.delegate.connector;
						«ENDIF»
					}
					
					@Override
					public double getScale() {
						«IF isTransient»
							return -1;
						«ELSE»
							return this.delegate.scale;
						«ENDIF»
					}
					
					@Override
					public String getFileName() {
						«IF isTransient»
							return null;
						«ELSE»
							return this.delegate.filename;
						«ENDIF»
					}
					
					@Override
					public String getExtension() {
						«IF isTransient»
							return null;
						«ELSE»
							return this.delegate.extension;
						«ENDIF»
					}
					
					@Override
					public void deleteModelElement(graphmodel.ModelElement me) {
						«IF isTransient»
							«FOR e:me.elements SEPARATOR " else "
							»if(me instanceof «e.apiFQN») {
								((«e.apiFQN») me).delete();
							}«
							ENDFOR»
						«ELSE»
							«dbTypeName» e = «typeRegistry».getApiToDB(me);
							if(e != null) {
								e.delete();
							}
						«ENDIF»
					}
					
					@Override
					public void delete() {
						«IF isTransient»
						«ELSE»
							this.delegate.delete();
						«ENDIF»
					}
					«me.embeddedEdges»
				«ENDIF»
				«FOR t : me.resolveSuperTypesAndType»
					
					@Override
					public «t.apiFQN» get«t.name.fuEscapeJava»View() {
						return this;
					}
				«ENDFOR»
				@Override
				public «me.apiFQN» eClass() {
					return this;
				}
				«IF me instanceof GraphicalModelElement»
					
					@Override
					public «coreAPIFQN("GraphModel")» getRootElement() {
						«coreAPIFQN("ModelElementContainer")» container = this.getContainer();
						if(container instanceof «"GraphModel".coreAPIFQN»){
							return («"GraphModel".coreAPIFQN») container;
						} else if(container instanceof graphmodel.ModelElement) {
							graphmodel.ModelElement parent = (graphmodel.ModelElement) container;
							return («"GraphModel".coreAPIFQN») parent.getRootElement();
						} else {
							return null;
						}
					}
					
					@Override
					public «"ModelElementContainer".coreAPIFQN» getContainer() {
						«IF isTransient»
							return («"ModelElementContainer".coreAPIFQN») this.container;
						«ELSE»
							return («coreAPIFQN("ModelElementContainer")») «typeRegistry».getDBToApi(this.delegate.getContainer(), cmdExecuter);
						«ENDIF»
					}
				«ENDIF»
				«IF me instanceof Edge»
					
					@Override
					public void delete() {
						«IF isTransient»
							«'''
								this.source.getOutgoing().remove(this);
								this.target.getIncoming().remove(this);
								this.getRootElement().getModelElements().remove(this);
							'''.deleteHooks(me)»
						«ELSE»
							«'''
								// decouple from container
								«commandExecuterSwitch(me, 
									[cmdExecuter|
										'''
											«cmdExecuter».remove«me.name.fuEscapeJava»(this);
										'''									
									],
									[g|
										g.elements.contains(me)
									]
								)»
								this.delegate.delete();
							'''.deleteHooks(me)»
						«ENDIF»
					}
					
					@Override
					public graphmodel.Node getSourceElement() {
						«IF isTransient»
							return source;
						«ELSE»
							«IF !me.possibleSources.empty»
								return (graphmodel.Node) «typeRegistry».getDBToApi(this.delegate.getSource(), cmdExecuter);
							«ELSE»
								return null;
							«ENDIF»
						«ENDIF»
					}
					
					@Override
					public graphmodel.Node getTargetElement() {
						«IF isTransient»
							return target;
						«ELSE»
							«IF !me.possibleTargets.empty»
								return (graphmodel.Node) «typeRegistry».getDBToApi(
									this.delegate.getTarget(),
									cmdExecuter
								);
							«ELSE»
								return null;
							«ENDIF»
						«ENDIF»
					}
					
					@Override
					public void reconnectSource(graphmodel.Node node) {
						«IF isTransient»
							this.decoupleSourceOutgoing(this.source);
							this.source = node;
							this.source.getOutgoing().add(this);
						«ELSE»
							«dbTypeName» dbTarget = this.delegate.getTarget();
							if(dbTarget != null) {
								cmdExecuter.reconnectEdge(
										«typeRegistry».getTypeOf(this),
										this,
										node,
										this.getTargetElement(),
										«typeRegistry».getTypeOf(node),
										«typeRegistry».getTypeOf(this.getTargetElement()),
										«typeRegistry».getTypeOf(this.getSourceElement()),
										«typeRegistry».getTypeOf(this.getTargetElement())
									);
								
								// reconnect
								«dbTypeName» oldSource = this.delegate.getSource();
								this.decoupleSourceOutgoing(oldSource);
								«dbTypeName» newSource = «typeRegistry».getApiToDB(node);
								this.delegate.setSource(newSource);
								
								// persist
								oldSource.persist();
								newSource.persist();
								this.delegate.persist();
							}
						«ENDIF»
					}
					
					@Override
					public void reconnectTarget(graphmodel.Node node) {
						«IF isTransient»
							this.decoupleTargetIncoming(this.target);
							this.target = node;
							this.target.getIncoming().add(this);
						«ELSE»
							«dbTypeName» dbSource = this.delegate.getSource();
							if(dbSource != null) {
								// commandExecuter
								cmdExecuter.reconnectEdge(
									«typeRegistry».getTypeOf(this),
									this,
									this.getSourceElement(),
									node, 
									«typeRegistry».getTypeOf(this.getSourceElement()),
									«typeRegistry».getTypeOf(node),
									«typeRegistry».getTypeOf(this.getSourceElement()),
									«typeRegistry».getTypeOf(this.getTargetElement())
								);
								
								// reconnect
								«dbTypeName» oldTarget = this.delegate.getTarget();
								this.decoupleTargetIncoming(oldTarget);
								«dbTypeName» newTarget = «typeRegistry».getApiToDB(node);
								this.delegate.setTarget(newTarget);
								
								// persist
								oldTarget.persist();
								newTarget.persist();
								this.delegate.persist();
							}
						«ENDIF»
					}
					
					public void decoupleTargetIncoming(«IF isTransient»graphmodel.Node«ELSE»«dbTypeName»«ENDIF» node) {
						«IF isTransient»
							graphmodel.Node target = (graphmodel.Node) node;
							target.getIncoming().remove(this);
							this.target = null;
						«ELSE»
							«FOR target : me.possibleTargets.filter[!isAbstract] SEPARATOR " else "
							»if(node instanceof «target.entityFQN») {
								«target.entityFQN» target = («target.entityFQN») node;
								target.removeIncoming(this.delegate);
								this.delegate.setTarget(null);
							}«
							ENDFOR»
						«ENDIF»
					}
					
					public void decoupleSourceOutgoing(«IF isTransient»graphmodel.Node«ELSE»«dbTypeName»«ENDIF» node) {
						«IF isTransient»
							graphmodel.Node source = (graphmodel.Node) node;
							source.getOutgoing().remove(this);
							this.source = null;
						«ELSE»
							«FOR source : me.possibleSources.filter[!isAbstract] SEPARATOR " else "
							»if(node instanceof «source.entityFQN») {
								«source.entityFQN» source = («source.entityFQN») node;
								source.removeOutgoing(this.delegate);
								this.delegate.setSource(null);
							}«
							ENDFOR»
						«ENDIF»
					}
					
					@Override
					public void addBendingPoint(long x, long y) {
						«IF isTransient»
							bendingPoints.add(new info.scce.pyro.trans.BendingPoint(x,y));
						«ELSE»
							entity.core.BendingPointDB bp = new entity.core.BendingPointDB();
							bp.x = x;
							bp.y = y;
							bp.persist();
							this.delegate.bendingPoints.add(bp);
						«ENDIF»
					}
					
					@Override
					public void clearBendingPoints() {
						«IF isTransient»
							bendingPoints.clear();
						«ELSE»
							this.delegate.bendingPoints.clear();
						«ENDIF»
					}
					
					@Override
					public java.util.List<? extends graphmodel.BendingPoint> getBendingPoints() {
						«IF isTransient»
							return this.bendingPoints;
						«ELSE»
							return new java.util.LinkedList<>(this.delegate.bendingPoints);
						«ENDIF»
					}
				«ENDIF»
				«IF me instanceof Node»
					
					@Override
					public void delete() {
						«IF isTransient»
							«'''
								«IF me instanceof NodeContainer»
									getModelElements().stream().filter(n->n instanceof graphmodel.Node).forEach(n->((graphmodel.Node)n).delete());
								«ENDIF»
								java.util.Set<graphmodel.Edge> edges = new java.util.HashSet<>();
								edges.addAll(getIncoming());
								edges.addAll(getOutgoing());
								edges.forEach(graphmodel.Edge::delete);
								this.container.getModelElements().remove(this);
							'''.deleteHooks(me)»
						«ELSE»
							removeEdges();
							«IF me instanceof ContainingElement»
								removeNodes();
							«ENDIF»
							«'''
								«commandExecuterSwitch(me, 
									[cmdExecuter|
										'''
											«IF me.modelPrime»
												«cmdExecuter».remove«me.name.fuEscapeJava»(this,this.get«me.primeReference.name.fuEscapeJava»());
											«ELSEIF me.primeReference !== null»
												«cmdExecuter».remove«me.name.fuEscapeJava»(this,this.get«me.primeReference.name.fuEscapeJava»());
											«ELSE»
												«cmdExecuter».remove«me.name.fuEscapeJava»(this);
											«ENDIF»
										'''									
									],
									[g|
										g.elements.contains(me)
									]
								)»
								this.delegate.delete();
							'''.deleteHooks(me)»
						«ENDIF»
					}
					
					private void removeEdges() {
						java.util.List<graphmodel.Edge> outgoing = this.getOutgoing();
						java.util.List<graphmodel.Edge> incoming = this.getIncoming();
						
						java.util.Iterator<graphmodel.Edge> iter_outgoing = outgoing.iterator();
						while(iter_outgoing.hasNext()) {
							graphmodel.Edge e = iter_outgoing.next();
							if(e != null) {
								e.delete();
								outgoing.remove(e);
							}
							iter_outgoing = outgoing.iterator();
						}
						
						java.util.Iterator<graphmodel.Edge> iter_incoming = incoming.iterator();
						while(iter_incoming.hasNext()) {
							graphmodel.Edge e = iter_incoming.next();
							if(e != null) {
								e.delete();
								incoming.remove(e);
							}
							iter_incoming = incoming.iterator();
						}
					}
					
					@Override
					public int getX() {
						«IF isTransient»
							return x;
						«ELSE»
							return (int) this.delegate.x;
					    «ENDIF»
					}
					
					@Override
					public int getY() {
						«IF isTransient»
							return y;
						«ELSE»
							return (int) this.delegate.y;
					    «ENDIF»
					}
					
					@Override
					public void setX(int x) {
						this.move(this.getX(), x);
						
					}
					
					@Override
					public void setY(int y) {
						this.move(this.getY(), y);
						
					}
					
					@Override
					public void setWidth(int width) {
						this.move(this.getWidth(), width);
						
					}
					
					@Override
					public void setHeight(int height) {
						this.move(this.getHeight(), height);
						
					}
					
					@Override
					public java.util.List<graphmodel.Edge> getIncoming() {
						«IF isTransient»
							return incoming;
						«ELSE»
							java.util.List<graphmodel.Edge> edges = new java.util.LinkedList<>();
							java.util.Collection<«dbTypeName»> incoming = this.delegate.getIncoming();
							for(«dbTypeName» e : incoming) {
								graphmodel.Edge edge = (graphmodel.Edge) «typeRegistry».getDBToApi(e, cmdExecuter);
								edges.add(edge);
							}
							return edges;
						«ENDIF»
					}
					
					@Override
					public <T extends graphmodel.Edge> java.util.List<T> getIncoming(Class<T> clazz) {
					    return getIncoming().stream().filter(n->clazz.isInstance(n)).map(n->clazz.cast(n)).collect(java.util.stream.Collectors.toList());
					}
					
					@Override
					public java.util.List<graphmodel.Node> getPredecessors() {
					    return getIncoming().stream().map(n->n.getSourceElement()).collect(java.util.stream.Collectors.toList());
					}
					
					@Override
					public <T extends graphmodel.Node> java.util.List<T> getPredecessors(Class<T> clazz) {
					   return getPredecessors().stream().filter(n->clazz.isInstance(n)).map(n->clazz.cast(n)).collect(java.util.stream.Collectors.toList());
					}
					
					@Override
					public java.util.List<graphmodel.Edge> getOutgoing() {
						«IF isTransient»
							return outgoing;
						«ELSE»
							java.util.List<graphmodel.Edge> edges = new java.util.LinkedList<>();
							java.util.Collection<«dbTypeName»> outgoing = this.delegate.getOutgoing();
							for(«dbTypeName» e : outgoing) {
								graphmodel.Edge edge = (graphmodel.Edge) «typeRegistry».getDBToApi(e, cmdExecuter);
								edges.add(edge);
							}
							return edges;
						«ENDIF»
					}
					
					@Override
					public <T extends graphmodel.Edge> java.util.List<T> getOutgoing(Class<T> clazz) {
					   return getOutgoing().stream().filter(n->clazz.isInstance(n)).map(n->clazz.cast(n)).collect(java.util.stream.Collectors.toList());
					}
					
					@Override
					public java.util.List<graphmodel.Node> getSuccessors() {
					    return getOutgoing().stream().map(n->n.getTargetElement()).collect(java.util.stream.Collectors.toList());
					}
					
					@Override
					public <T extends graphmodel.Node> java.util.List<T> getSuccessors(Class<T> clazz) {
					    return getSuccessors().stream().filter(n->clazz.isInstance(n)).map(n->clazz.cast(n)).collect(java.util.stream.Collectors.toList());
					}
					
					@Override
					public void move(int x, int y) {
						this.moveTo(this.getContainer(), x, y);
					}
					«IF !isTransient»
						
						private void changeContainer(«dbTypeName» newContainer) {
							«dbTypeName» c = this.delegate.getContainer();
							«FOR container : me.resolvePossibleContainer SEPARATOR " else "
							»if(c instanceof «container.entityFQN») {
								«container.entityFQN» container = («container.entityFQN») c;
								container.removeModelElements(this.delegate);
								this.setContainer(newContainer);
							}«
							ENDFOR»
						}
						
						private void setContainer(«dbTypeName» c) {
							«FOR container : me.resolvePossibleContainer SEPARATOR " else "
							»if(c instanceof «container.entityFQN») {
								«container.entityFQN» newContainer = («container.entityFQN») c;
								newContainer.addModelElements(this.delegate);
								this.delegate.setContainer(newContainer);
							}«
							ENDFOR»
						}
					«ENDIF»
					
					@Override
					public void moveTo(graphmodel.ModelElementContainer container,int x, int y) {
						«IF me.containsPostMove»
							// pre move
							graphmodel.ModelElementContainer preContainer = this.getContainer();
							int preX = this.getX();
							int preY = this.getY();
							
						«ENDIF»
						
						«IF isTransient»
							this.container.getModelElements().remove(this);
							container.getModelElements().add(this);
							this.container = container;
							this.x = x;
							this.y = y;
						«ELSE»
							this.cmdExecuter.moveNode(
								«typeRegistry».getTypeOf(this),
								this,
								container,
								«typeRegistry».getTypeOf(container),
								«typeRegistry».getTypeOf(this.getContainer()),
								x,
								y
							);
							
							// changes
							«dbTypeName» oldContainer = this.delegate.getContainer();
							«dbTypeName» newContainer = «typeRegistry».getApiToDB(container);
							this.changeContainer(newContainer);
							this.delegate.x = x;
							this.delegate.y = y;
						«ENDIF»
						«{
							val postMove = me.resolvePostMove
							'''
								«IF !postMove.empty»
									
									// postMoveHooks
									«FOR pM:postMove»
										{
											//post move
											«pM» hook = new «pM»();
											hook.init(«IF isTransient»null«ELSE»cmdExecuter«ENDIF»);
											hook.postMove(this,preContainer,container,x,y,x-preX,y-preY);	
										}	
									«ENDFOR»
								«ENDIF»
							'''
						}»
						«IF !isTransient»
							// persist
							oldContainer.persist();
							newContainer.persist();
							this.delegate.persist();
						«ENDIF»
					}
					
					«IF me.isPrime»
						«{
							val ref = me.primeReference
							val refType = ref.type
							'''
								
								@Override
								public «refType.apiFQN» get«me.primeReference.name.fuEscapeJava»()
								{
									«IF isTransient»
										return «me.primeReference.name.escapeJava»;
									«ELSE»
										«dbTypeName» entity = delegate.get«ref.name.fuEscapeJava»();
										return («refType.apiFQN») «typeRegistry».getDBToApi(entity, cmdExecuter);
									«ENDIF»
								}
							'''
						}»
					«ENDIF»
					«connectedNodeMethods(me, isTransient)»
				«ENDIF»
				«IF me instanceof Node || me instanceof ContainingElement»
					
					@Override
					public int getWidth() {
						«IF isTransient»
							return this.width;
						«ELSE»
							return (int) this.delegate.width;
						«ENDIF»
					}
					
					@Override
					public int getHeight() {
						«IF isTransient»
							return this.height;
						«ELSE»
							return (int) this.delegate.height;
						«ENDIF»
					}
					«IF me instanceof Node»
						
						@Override
						public void resize(int width, int height) {
							«IF isTransient»
								this.width = width;
								this.height = height;
							«ELSE»
								String type = «typeRegistry».getTypeOf(this);
								this.cmdExecuter.resizeNode(type, this, (long) width, (long) height);
								this.delegate.width = width;
								this.delegate.height = height;
								this.delegate.persist();
							«ENDIF»
							«{
								val postResize = me.resolvePostResize
								'''
									«IF !postResize.empty»
										
										// postResizeHooks
										«FOR pR:postResize»
											{
												// post resize
												«pR» hook = new «pR»();
												hook.init(«IF isTransient»null«ELSE»cmdExecuter«ENDIF»);
												hook.postResize(this, width, height);
											}
										«ENDFOR»
									«ENDIF»
								'''
							}»
						}
					«ENDIF»
				«ENDIF»
				«IF me instanceof ContainingElement»
					
					private void removeNodes() {
						java.util.List<graphmodel.Node> nodes = this.getModelElements(graphmodel.Node.class);
						
						java.util.Iterator<graphmodel.Node> iter_nodes = nodes.iterator();
						while(iter_nodes.hasNext()) {
							graphmodel.Node e = iter_nodes.next();
							if(e != null) {
								e.delete();
								nodes.remove(e);
							}
							iter_nodes = nodes.iterator();
						}
					}
					
					@Override
					public java.util.List<graphmodel.ModelElement> getModelElements() {
						«IF !isTransient»
							java.util.List<graphmodel.ModelElement> modelElements = new java.util.LinkedList<>();
							java.util.Collection<«dbTypeName»> m = this.delegate.getModelElements();
							for(«dbTypeName» e : m) {
								graphmodel.ModelElement apiE = (graphmodel.ModelElement) «typeRegistry».getDBToApi(e, cmdExecuter);
								modelElements.add(apiE);
							}
						«ENDIF»
						return modelElements;
					}
					
					@Override
					public <T extends graphmodel.ModelElement> java.util.List<T> getModelElements(Class<T> clazz) {
						return this.getModelElements().stream().filter(n->clazz.isInstance(n)).map(n->clazz.cast(n)).collect(java.util.stream.Collectors.toList());
					}
					
					@Override
					public <T extends graphmodel.IdentifiableElement> java.util.List<T> find(Class<T> clazz) {
						if(clazz.isInstance(graphmodel.ModelElement.class) ){
							return (java.util.List<T>) this.getModelElements();	
						}
						if(clazz.isInstance(graphmodel.Container.class) ){
							return (java.util.List<T>) this.getAllContainers();
						}
						if(clazz.isInstance(graphmodel.Node.class) ){
							return (java.util.List<T>) this.getAllNodes();
						}
						if(clazz.isInstance(graphmodel.Edge.class) ){
							return (java.util.List<T>) this.getAllEdges();
						}
						return null;
					}
					
					private java.util.List<graphmodel.ModelElement> getAllModelElements(graphmodel.ModelElementContainer cmc) {
						java.util.List<graphmodel.ModelElement> cm = new java.util.LinkedList<>(cmc.getModelElements());
						cm.addAll(cmc.getModelElements().stream()
							.filter(n -> n instanceof graphmodel.ModelElementContainer)
							.flatMap(n->
								getAllModelElements((graphmodel.ModelElementContainer)n).stream()
							).collect(java.util.stream.Collectors.toList()));
						return cm;
					}
					
					@Override
					public <T extends graphmodel.Edge> java.util.List<T> getEdges(Class<T> clazz) {
						return getModelElements(clazz);
					}
					
					@Override
					public <T extends graphmodel.Node> java.util.List<T> getNodes(Class<T> clazz) {
						return getModelElements(clazz);
					}
					
					@Override
					public java.util.List<graphmodel.Node> getNodes() {
						return getModelElements(graphmodel.Node.class);
					}
						
					@Override
					public java.util.List<graphmodel.Node> getAllNodes() {
						return getAllModelElements(this).stream()
						.filter(n->n instanceof graphmodel.Node)
						.map(n->(graphmodel.Node)n)
						.collect(java.util.stream.Collectors.toList());
					}
						
					@Override
					public java.util.List<graphmodel.Edge> getAllEdges() {
						return getAllModelElements(this).stream()
							.filter(n->n instanceof graphmodel.Edge)
							.map(n->(graphmodel.Edge)n)
							.collect(java.util.stream.Collectors.toList());
					}
					
					@Override
					public java.util.List<graphmodel.Container> getAllContainers() {
						return getAllModelElements(this).stream()
							.filter(n->n instanceof graphmodel.Container)
							.map(n->(graphmodel.Container)n)
							.collect(java.util.stream.Collectors.toList());
					}
					«embeddedNodeMethods(me,styles, isTransient)»
				«ENDIF»
				«IF !me.attributesExtended.exists[it.name.fuEscapeJava == "Name"]»
					
					public String getName() {
						return "«me.name»";
					}
				«ENDIF»
				«FOR attr:me.attributesExtended»
					«val rawType = '''«attr.javaType»'''»
					«val attributeType = '''«IF attr.isList»java.util.List<«ENDIF»«rawType»«IF attr.isList»>«ENDIF»'''»«{
						if(attr.name.fuEscapeJava == "Name" && !attributeType.equals("String"))
							throw new RuntimeException("attribute \"name\" is predefined as \"String\" by cinco")
					}»
					
					@Override
					public «attributeType» «IF attr.isPrimitive && attributeType.toLowerCase.equals("boolean")»is«ELSE»get«ENDIF»«attr.name.fuEscapeJava»() {
						«IF isTransient»«»
							return «attr.name.escapeJava»;
						«ELSE»
							«IF attr.isPrimitive»
								«IF attr.attributeTypeName.getEnum(modelPackage)!==null»
									«IF attr.list»
										return this.delegate.«attr.name.escapeJava».stream().map((n)->{
											«{  
												val e = attr.attributeTypeName.getEnum(modelPackage) '''
												switch (n){
													«e.literals.map['''case «it.toUnderScoreCase.escapeJava»: return «e.apiFQN».«it.toUnderScoreCase.fuEscapeJava»;'''].join("\n")»
												}
												return null;
											'''}»
										}).collect(java.util.stream.Collectors.toList());
									«ELSE»
										«{  
											val e = attr.attributeTypeName.getEnum(modelPackage) '''
											switch (this.delegate.«attr.name.escapeJava»){
												«e.literals.map['''case «it.toUnderScoreCase.escapeJava»: return «e.apiFQN».«it.toUnderScoreCase.fuEscapeJava»;'''].join("\n")»
											}
											return null;
										'''}»
									«ENDIF»
								«ELSE»
									return «attr.primitiveGETConverter('''this.delegate.«attr.name.escapeJava»''')»;
								«ENDIF»
							«ELSE»
								«IF !me.isType»
									«me.restFQN» prev = «me.restFQN».fromEntityProperties(this.delegate,new info.scce.pyro.rest.ObjectCache());
								«ENDIF»
								«IF attr.isList»
										java.util.Collection<«dbTypeName»> entityList = this.delegate.get«attr.name.fuEscapeJava»();
										return («attributeType») entityList.stream().map(n -> {
											return («rawType») «typeRegistry».getDBToApi(n, this.cmdExecuter);
										}).collect(java.util.stream.Collectors.toList());
								«ELSE»
										«dbTypeName» attribute = this.delegate.get«attr.name.fuEscapeJava»();
										return («attributeType») «typeRegistry».getDBToApi(attribute, this.cmdExecuter);
								«ENDIF»
							«ENDIF»
						«ENDIF»
					}
					«IF attr.isPrimitive && attr.annotations.exists[name.equals("file")]»
						«IF attr.isList»
							
							@Override
							public java.util.List<java.io.File> get«attr.name.fuEscapeJava»File() {
								java.util.List<String> paths = this.get«attr.name.fuEscapeJava»();
								return paths.stream().map(
									(path) -> info.scce.pyro.core.FileController.getFile(path)
								).collect(java.util.stream.Collectors.toList());
							}
						«ELSE»
							
							@Override
							public java.io.File get«attr.name.fuEscapeJava»File() {
								String path = this.get«attr.name.fuEscapeJava»();
								java.io.File file = info.scce.pyro.core.FileController.getFile(path);
								return file;
							}
						«ENDIF»
					«ENDIF»
					
					@Override
					public void set«attr.name.fuEscapeJava»(«attributeType» attr) {
						«IF isTransient»
							this.«attr.name.escapeJava» = attr;
						«ELSE»
							«IF !me.isType»
								«me.restFQN» prev = «me.restFQN».fromEntityProperties(this.delegate,new info.scce.pyro.rest.ObjectCache());
							«ENDIF»
							«IF attr.isPrimitive»
								«IF attr.attributeTypeName.getEnum(modelPackage)!==null»
									«IF attr.list»
										this.delegate.«attr.name.escapeJava» = attr.stream().map(n -> {
											«{  
												val e = attr.attributeTypeName.getEnum(modelPackage) '''
												switch (n){
													«e.literals.map['''case «it.toUnderScoreCase.fuEscapeJava»: return «e.entityFQN».«it.toUnderScoreCase.escapeJava»;'''].join("\n")»
												}
												return null;
											'''}»
										}).collect(java.util.stream.Collectors.toList());
									«ELSE»
										«{  
											val e = attr.attributeTypeName.getEnum(modelPackage) '''
											switch (attr){
												«e.literals.map['''case «it.toUnderScoreCase.escapeJava»: this.delegate.«attr.name.escapeJava» = «e.entityFQN».«it.toUnderScoreCase.escapeJava»;break;'''].join("\n")»
											}
										'''}»
									«ENDIF»
								«ELSE»
									this.delegate.«attr.name.escapeJava» = «attr.primitiveSETConverter('''attr''')»;
								«ENDIF»
							«ELSE»
								«IF attr.isList»
									// cast values
									java.util.Collection<«dbTypeName»> newList = attr.stream().map(n -> 
											n.getDelegate()
										).collect(java.util.stream.Collectors.toList());
									«IF attr.isUserDefinedType»
										// delete values that are not present in newList
										this.delegate.get«attr.name.fuEscapeJava»().stream().filter(
												(e) -> !newList.contains(e)
											).forEach(
												(e) -> this.delegate.remove«attr.name.fuEscapeJava»(e, true)
											);
									«ENDIF»
									// set new values
									this.delegate.set«attr.name.fuEscapeJava»(newList);
								«ELSE»
									
									// cast value
									«dbTypeName» newEntity = attr != null? attr.getDelegate() : null;
									// set new value«IF attr.isUserDefinedType»/delete old value«ENDIF»
									this.delegate.set«attr.name.fuEscapeJava»(newEntity«IF attr.isUserDefinedType», true«ENDIF»);
								«ENDIF»
							«ENDIF»
							
							// commandExecuter
							«
								commandExecuterSwitch(
									me,
									[cmdExecuter|
									'''
										«cmdExecuter».update«IF me instanceof UserDefinedType»IdentifiableElement«ELSE»«me.name.escapeJava»Properties«ENDIF»(this«IF me instanceof UserDefinedType».parent«ENDIF»,prev);
									'''
									],
									[g|
										g.elementsAndTypesAndGraphModels.contains(me)
									]
								)
							»
							
							// persist
							this.delegate.persist();
						«ENDIF»
						
						«IF me.containsPostAttributeValueChange»
							
							// Trigger postAttributeValueChangeHook
							«{
								val postAttributeValueChangeHooks = me.resolvePostAttributeValueChange
								'''
									org.eclipse.emf.ecore.EStructuralFeature esf = new org.eclipse.emf.ecore.EStructuralFeature();
									esf.setName("«attr.name»");
									«FOR anno:postAttributeValueChangeHooks.indexed»
										{
											//property change hook «anno.key»
											«anno.value» hook = new «anno.value»();
											hook.init(cmdExecuter);
											if(hook.canHandleChange(this,esf)) {
												hook.handleChange(this,esf);
											}
										}
									«ENDFOR»
								'''
							}»
						«ENDIF»
					}
				«ENDFOR»
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
		'''«string»'''
		/*
		return switch(attribute.attributeTypeName) {
			case "EInt":'''«IF attribute.list»«string».stream().map(n->Long.valueOf(n)).collect(java.util.stream.Collectors.toList())«ELSE»«string»)«ENDIF»'''
			case "EBigInteger":'''«IF attribute.list»«string».stream().map(n->Long.valueOf(n)).collect(java.util.stream.Collectors.toList())«ELSE»Long.valueOf(«string»)«ENDIF»'''
			case "ELong":'''«IF attribute.list»«string».stream().map(n->Long.valueOf(n)).collect(java.util.stream.Collectors.toList())«ELSE»Long.valueOf(«string»)«ENDIF»'''
			case "EByte":'''«IF attribute.list»«string».stream().map(n->Long.valueOf(n)).collect(java.util.stream.Collectors.toList())«ELSE»Long.valueOf(«string»)«ENDIF»'''
			case "EShort":'''«IF attribute.list»«string».stream().map(n->Long.valueOf(n)).collect(java.util.stream.Collectors.toList())«ELSE»Long.valueOf(«string»)«ENDIF»'''
			default:'''«IF attribute.list»«string».stream().collect(java.util.stream.Collectors.toList())«ELSE»«string»«ENDIF»'''
		}
		*/
	}
	
	def embeddedEdges(GraphModel g)
	'''
		«FOR edge:g.edges»
			
			@Override
			public java.util.List<«edge.apiFQN»> get«edge.name.fuEscapeJava»s() {
				return this.getModelElements(«edge.apiFQN».class);
			}
		«ENDFOR»
	'''
	
	def connectedNodeMethods(Node node, boolean isTransient) {
		val possibleIncoming = node.resolveSuperTypesAndType.filter(Node).map[possibleIncoming].flatten.toSet
		val possibleSources = possibleIncoming.map[possibleSources].flatten.toSet
		val possibleOutgoing = node.resolveSuperTypesAndType.filter(Node).map[possibleOutgoing].flatten.toSet
		val possibleTargets = possibleOutgoing.map[possibleTargets].flatten.toSet
		val supportedOutgoing = node.possibleOutgoing.filter[!isAbstract]
		'''
			«FOR incoming:possibleIncoming»
				
				@Override
				public java.util.List<«incoming.apiFQN»> getIncoming«incoming.name.fuEscapeJava»s() {
					return getIncoming(«incoming.apiFQN».class);
				}
			«ENDFOR»
			«FOR outgoing:possibleOutgoing»
				
				@Override
				public java.util.List<«outgoing.apiFQN»> getOutgoing«outgoing.name.fuEscapeJava»s() {
					return getOutgoing(«outgoing.apiFQN».class);
				}
			«ENDFOR»
			«FOR source:possibleSources»
				
				@Override
				public java.util.List<«source.apiFQN»> get«source.name.fuEscapeJava»Predecessors() {
					return getPredecessors(«source.apiFQN».class);
				}
			«ENDFOR»
			«FOR target:possibleTargets»
				
				@Override
				public java.util.List<«target.apiFQN»> get«target.name.fuEscapeJava»Successors() {
					return getSuccessors(«target.apiFQN».class);
				}
			«ENDFOR»
			«FOR outgoing:supportedOutgoing»
				
				@Override
				public «outgoing.apiFQN» new«outgoing.name.fuEscapeJava»(graphmodel.Node target) {
					«IF isTransient»
						«outgoing.name.fuEscapeJava»Impl cn = new «outgoing.name.fuEscapeJava»Impl(getRootElement());
						cn.source = this;
						this.outgoing.add(cn);
						cn.target = target;
						target.getIncoming().add(cn);
						this.getRootElement().getModelElements().add(cn);
					«ELSE»
						«outgoing.apiFQN» cn = null;
						«commandExecuterSwitch(
							outgoing,
							[cmdExecuter|
								'''
									cn = «cmdExecuter».create«outgoing.name.fuEscapeJava»(this, target, java.util.Collections.emptyList(), null);
								'''
							],
							[g|
								g.elements.contains(outgoing)
							]
						)»
					«ENDIF»
					«outgoing.postCreate("cn",gc)»
					return cn;
				}
			«ENDFOR»
			«FOR outgoing:possibleOutgoing.filter[!isAbstract && !supportedOutgoing.contains(it)]»
				
				@Override
				public «outgoing.apiFQN» new«outgoing.name.fuEscapeJava»(graphmodel.Node target) {
					throw new RuntimeException("The edge-type '«outgoing.typeName»' is not supported by this type '«node.typeName»'.");
				}
			«ENDFOR»
		'''
	}
	
	
	def embeddedNodeMethods(ModelElement ce, Styles styles, boolean isTransient) {
		val g = ce.modelPackage as MGLModel
		val superTypes = ce.resolveSuperTypesAndType
		val containedTypes = new java.util.HashSet
		val containingElementsOfSuperTypes = superTypes.filter(ContainingElement)
		for(s:containingElementsOfSuperTypes) {
			if(s instanceof ContainingElement) {
				val  directContainedTypes = s.possibleEmbeddingTypes(g)
				containedTypes += directContainedTypes.map[it.resolveAllSubTypesAndType].flatten.toSet	
			}
		}
		//TODO: JOEL fill the return result of canNew... method properly 
		'''
			«FOR em:containedTypes»
				«IF !em.isIsAbstract»
					
					@Override
					public boolean canNew«em.name.fuEscapeJava»(){
						return true;
					};

					«IF em.isPrime || em.isEcorePrime»
						«IF isTransient»
							«{
								val refElem = (em as Node).primeReference.type
								val refName = (em as Node).primeReference.name
								'''
									
									@Override
									public «em.apiFQN» new«em.name.fuEscapeJava»(
										«refElem.apiFQN» object,
										int x,
										int y
									) {
										return new«em.name.fuEscapeJava»(object,x,y,«{
											val nodeStyle = styling(em as Node,styles) as NodeStyle
											val size = nodeStyle.mainShape.size
											'''
												«IF size!==null»
													«size.width»,
													«size.height»
												«ELSE»
													«MGLExtension.DEFAULT_WIDTH»,
													«MGLExtension.DEFAULT_HEIGHT»
												«ENDIF»
										  	'''
										}»);
									}
									
									@Override
									public «em.apiFQN» new«em.name.fuEscapeJava»(
										«refElem.apiFQN» object,
										int x,
										int y,
										int width,
										int height
									) {
										«em.name.fuEscapeJava»Impl cn = new «em.name.fuEscapeJava»Impl(this);
										modelElements.add(cn);
										cn.x = x;
										cn.y = y;
										cn.width = width;
										cn.height = height;
										cn.«refName.escapeJava» = object;
										«em.postCreate("cn", gc, isTransient)»
										return cn;
									}
								'''
							}»
						«ELSE»
							
							@Override
							public «em.apiFQN» new«em.name.fuEscapeJava»(
								long primeId,
								int x,
								int y
							) {
								«em.apiFQN» cn = null;
								«commandExecuterSwitch(em, 
									[cmdExecuter|
										'''
											cn = «cmdExecuter».create«em.name.fuEscapeJava»(x,y,
												«{
													val nodeStyle = styling(em as Node,styles) as NodeStyle
													val size = nodeStyle.mainShape.size
													'''
													«IF size!==null»
														«size.width»,
														«size.height»,
													«ELSE»
														«MGLExtension.DEFAULT_WIDTH»,
														«MGLExtension.DEFAULT_HEIGHT»,
													«ENDIF»
													'''
												}»
												this,
												null,
												primeId
											);
										'''									
									],
									[gm|
										gm.elements.contains(em)
									]
								)»
								«em.postCreate("cn",gc)»
								return cn;
							}
							
							@Override
							public «em.apiFQN» new«em.name.fuEscapeJava»(
								long primeId,
								int x,
								int y,
								int width,
								int height
							) {
								«em.apiFQN» cn = null;
								«commandExecuterSwitch(em, 
									[cmdExecuter|
										'''
											cn = «cmdExecuter».create«em.name.fuEscapeJava»(
												x,
												y,
												width,
												height,
												this,
												null,
												primeId
											);
										'''									
									],
									[gm|
										gm.elements.contains(em)
									]
								)» 
								«em.postCreate("cn",gc)»
								return cn;
							}
						«ENDIF»
					«ELSE»
						
						@Override
						public «em.apiFQN» new«em.name.fuEscapeJava»(int x, int y, int width, int height) {
							«IF isTransient»
								«em.name.fuEscapeJava»Impl cn = new «em.name.fuEscapeJava»Impl(this);
								modelElements.add(cn);
								cn.x = x;
								cn.y = y;
								cn.width = width;
								cn.height = height;
							«ELSE»
								«em.apiFQN» cn = null;
								«commandExecuterSwitch(em, 
									[cmdExecuter|
										'''
											cn = «cmdExecuter».create«em.name.fuEscapeJava»(x, y, width, height, this, null);
										'''						
									],
									[gm|
										gm.elements.contains(em)
									]
								)»
							«ENDIF»
							«em.postCreate("cn",gc, isTransient)»
							return cn;
						}
						
						@Override
						public «em.apiFQN» new«em.name.fuEscapeJava»(int x, int y) {
							return this.new«em.name.fuEscapeJava»(x, y, «{
								val nodeStyle = styling(em as Node,styles) as NodeStyle
								val size = nodeStyle.mainShape.size
								 '''
								  	 «IF size!==null»
									  	 «size.width»,
									  	 «size.height»
								  	 «ELSE»
									  	 «MGLExtension.DEFAULT_WIDTH»,
									  	 «MGLExtension.DEFAULT_HEIGHT»
								  	 «ENDIF»
							  	 '''
							}»);
						}
					«ENDIF»
				«ENDIF»
				
				@Override
				public java.util.List<«em.apiFQN»> get«em.name.fuEscapeJava»s() {
					return getModelElements(«em.apiFQN».class);
				}
			«ENDFOR»
		'''
	}
	
	def deleteHooks(CharSequence inner, ModelElement me) {
		val preDeletes = me.resolvePreDelete
		val postDeletes = me.resolvePostDelete
		'''
			«IF !preDeletes.empty»
				// preDeleteHooks
				«FOR preD:preDeletes»
					{
						«preD» hook_preDelete = new «preD»();
						hook_preDelete.init(cmdExecuter);
						hook_preDelete.preDelete(this);
					}
				«ENDFOR»
				
			«ENDIF»
			«inner»
			«IF !postDeletes.empty»
				
				// postDeleteHooks
				«FOR postD:postDeletes»
					{
						«postD» hook_postDelete = new «postD»();
						Runnable runnable = hook_postDelete.getPostDeleteFunction(this);
						runnable.run();
					}
				«ENDFOR»
			«ENDIF»
		'''
	}
}
