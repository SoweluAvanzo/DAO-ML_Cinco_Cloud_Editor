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

class GraphModelElementImplementation extends Generatable {
	
	protected extension ModelElementHook = new ModelElementHook
	
	new(GeneratorCompound gc) {
		super(gc)
	}
		
	def filename(ModelElement me)'''«me.name.fuEscapeJava»Impl.java'''
	
	def content(ModelElement me, MGLModel modelPackage, Styles styles)
	{
		val typeRegistry = '''«typeRegistryName»'''
		//var bestContainerSuperTypeName = null as String
		//if(me instanceof GraphicalModelElement) 
		//	bestContainerSuperTypeName = me.getBestContainerSuperTypeNameAPI.escapeJava
		'''
			package «modelPackage.apiImplFQNBase»;
			
			import «modelPackage.typeRegistryFQN»;
			import «dbTypeFQN»;
			import «commandExecuterFQN»;
			«FOR g: modelPackage.graphModels.filter[!isAbstract]»
				import «g.commandExecuterFQN»;
			«ENDFOR»
			
			public class «me.name.fuEscapeJava»Impl implements «me.apiFQN» {
				
				private final «me.entityFQN» delegate;
				
				private final CommandExecuter cmdExecuter;
				
				«IF me instanceof UserDefinedType»
					private final graphmodel.IdentifiableElement parent;
					private final info.scce.pyro.core.graphmodel.IdentifiableElement prev;
				«ENDIF»
				
				public «me.name.fuEscapeJava»Impl(
					«me.entityFQN» delegate,
					CommandExecuter cmdExecuter«IF me instanceof UserDefinedType»,
						graphmodel.IdentifiableElement parent,
						info.scce.pyro.core.graphmodel.IdentifiableElement prev
					«ENDIF»
				) {
					this.delegate = delegate;
					this.cmdExecuter = cmdExecuter;
					«IF me instanceof UserDefinedType»
						this.parent = parent;
						this.prev = prev;
					«ENDIF»
				}
				
				public «me.name.fuEscapeJava»Impl(
					CommandExecuter cmdExecuter«IF me instanceof UserDefinedType»,
						graphmodel.IdentifiableElement parent,
						info.scce.pyro.core.graphmodel.IdentifiableElement prev
					«ENDIF»
				) {
					this.delegate = new «me.entityFQN»();
					this.delegate.persist();
					this.cmdExecuter = cmdExecuter;
					«IF me instanceof UserDefinedType»
						this.parent = parent;
						this.prev = prev;
					«ENDIF»
				}
				
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
				
				«IF me instanceof GraphModel»
					
					@Override
					public long getWidth() {
						return this.delegate.width;
					}
					
					@Override
					public long getHeight() {
						return this.delegate.height;
					}
					
					@Override
					public String getRouter() {
						return this.delegate.router;
					}
					
					@Override
					public String getConnector() {
						return this.delegate.connector;
					}
					
					@Override
					public double getScale() {
						return this.delegate.scale;
					}
					
					@Override
					public String getFileName() {
						return this.delegate.filename;
					}
					
					@Override
					public String getExtension() {
						return this.delegate.extension;
					}
					
					@Override
					public void deleteModelElement(graphmodel.ModelElement me) {
						«dbTypeName» e = «typeRegistry».getApiToDB(me);
						if(e != null) {
							e.delete();
						}
					}
					
					@Override
					public void delete() {
						this.delegate.delete();
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
						if(container instanceof «coreAPIFQN("GraphModel")»){
							return («coreAPIFQN("GraphModel")») container;
						} else if(container instanceof graphmodel.ModelElement) {
							graphmodel.ModelElement parent = (graphmodel.ModelElement) container;
							return («coreAPIFQN("GraphModel")») parent.getRootElement();
						} else {
							return null;
						}
					}
					
					@Override
					public «coreAPIFQN("ModelElementContainer")» getContainer() {
						return («coreAPIFQN("ModelElementContainer")») «typeRegistry».getDBToApi(this.delegate.getContainer(), cmdExecuter);
					}
				«ENDIF»
				«IF me instanceof Edge»
					
					@Override
					public void delete() {
						«'''
							// decouple from container
							«commandExecuterSwitch(me, 
								[cmdExecuter|
									'''
										«cmdExecuter».remove«me.name.fuEscapeJava»(this);
									'''									
								]
							)»
							this.delegate.delete();
						'''.deleteHooks(me)»
					}
					
					@Override
					public graphmodel.Node getSourceElement() {
						«IF !me.possibleSources.empty»
							return (graphmodel.Node) «typeRegistry».getDBToApi(this.delegate.getSource(), cmdExecuter);
						«ELSE»
							return null;
						«ENDIF»
					}
					
					@Override
					public graphmodel.Node getTargetElement() {
						«IF !me.possibleTargets.empty»
							return (graphmodel.Node) «typeRegistry».getDBToApi(this.delegate.getTarget(), cmdExecuter);
						«ELSE»
							return null;
						«ENDIF»
					}
					
					@Override
					public void reconnectSource(graphmodel.Node node) {
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
					}
					
					@Override
					public void reconnectTarget(graphmodel.Node node) {
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
					}
					
					public void decoupleTargetIncoming(«dbTypeName» node) {
						«FOR target : me.possibleTargets.filter[!isAbstract] SEPARATOR " else "
						»if(node instanceof «target.entityFQN») {
							«target.entityFQN» target = («target.entityFQN») node;
							target.removeIncoming(this.delegate);
							this.delegate.setTarget(null);
						}«
						ENDFOR»
					}
						
					public void decoupleSourceOutgoing(«dbTypeName» node) {
						«FOR source : me.possibleSources.filter[!isAbstract] SEPARATOR " else "
						»if(node instanceof «source.entityFQN») {
							«source.entityFQN» source = («source.entityFQN») node;
							source.removeOutgoing(this.delegate);
							this.delegate.setSource(null);
						}«
						ENDFOR»
					}
					
					@Override
					public void addBendingPoint(long x, long y) {
						entity.core.BendingPointDB bp = new entity.core.BendingPointDB();
						bp.x = x;
						bp.y = y;
						bp.persist();
						this.delegate.bendingPoints.add(bp);
					}
					
					@Override
					public void clearBendingPoints() {
						this.delegate.bendingPoints.clear();
					}
					
					@Override
					public java.util.List<? extends graphmodel.BendingPoint> getBendingPoints() {
						return new java.util.LinkedList<>(this.delegate.bendingPoints);
					}
				«ENDIF»
				«IF me instanceof Node»
					
					@Override
					public void delete() {
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
								]
							)»
							this.delegate.delete();
						'''.deleteHooks(me)»
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
					    return (int)this.delegate.x;
					}
					
					@Override
					public int getY() {
					    return (int)this.delegate.y;
					}
					
					@Override
					public int getWidth() {
					    return (int)this.delegate.width;
					}
					
					@Override
					public int getHeight() {
					    return (int)this.delegate.height;
					}
					
					@Override
					public java.util.List<graphmodel.Edge> getIncoming() {
						java.util.List<graphmodel.Edge> edges = new java.util.LinkedList<>();
						java.util.Collection<«dbTypeName»> incoming = this.delegate.getIncoming();
						for(«dbTypeName» e : incoming) {
							graphmodel.Edge edge = (graphmodel.Edge) «typeRegistry».getDBToApi(e, cmdExecuter);
							edges.add(edge);
						}
						return edges;
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
						java.util.List<graphmodel.Edge> edges = new java.util.LinkedList<>();
						java.util.Collection<«dbTypeName»> outgoing = this.delegate.getOutgoing();
						for(«dbTypeName» e : outgoing) {
							graphmodel.Edge edge = (graphmodel.Edge) «typeRegistry».getDBToApi(e, cmdExecuter);
							edges.add(edge);
						}
						return edges;
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
						this.moveTo(this.getContainer(),x,y);
					}
					
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
					
					@Override
					public void moveTo(graphmodel.ModelElementContainer container,int x, int y) {
						«IF me.hasPostMove»
							// pre move
							graphmodel.ModelElementContainer preContainer = this.getContainer();
							int preX = this.getX();
							int preY = this.getY();
							
						«ENDIF»
						// command executer
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
						
						// postMove
						«FOR pM:me.resolvePostMove»
							{
								«pM» hook = new «pM»();
								hook.init(cmdExecuter);
								hook.postMove(this,preContainer,container,x,y,x-preX,y-preY);	
							}	
						«ENDFOR»
						
						// persist
						oldContainer.persist();
						newContainer.persist();
						this.delegate.persist();
					}
					
					@Override
					public void resize(int width, int height) {
						String type = «typeRegistry».getTypeOf(this);
						this.cmdExecuter.resizeNode(type, this, width, height);
						this.delegate.width = width;
						this.delegate.height = height;
						this.delegate.persist();
						«FOR pR:me.resolvePostResize BEFORE "\n"»
							{
								«pR» hook = new «pR»();
								hook.init(cmdExecuter);
								hook.postResize(this,width,height);
							}
						«ENDFOR»
					}
					
					«IF me.isPrime»
						«{
							val ref = me.primeReference
							val refType = ref.type
							'''
								@Override
								public «refType.apiFQN» get«me.primeReference.name.fuEscapeJava»()
								{
									«dbTypeName» entity = delegate.get«ref.name.fuEscapeJava»();
									return («refType.apiFQN») «typeRegistry».getDBToApi(entity, cmdExecuter);
								}
							'''
						}»
					«ENDIF»
					«connectedNodeMethods(me)»
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
						java.util.List<graphmodel.ModelElement> modelElements = new java.util.LinkedList<>();
						java.util.Collection<«dbTypeName»> m = this.delegate.getModelElements();
						for(«dbTypeName» e : m) {
							graphmodel.ModelElement apiE = (graphmodel.ModelElement) «typeRegistry».getDBToApi(e, cmdExecuter);
							modelElements.add(apiE);
						}
						return modelElements;
					}
						
					@Override
					public <T extends graphmodel.ModelElement> java.util.List<T> getModelElements(Class<T> clazz) {
						return this.getModelElements().stream().filter(n->clazz.isInstance(n)).map(n->clazz.cast(n)).collect(java.util.stream.Collectors.toList());
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
					«embeddedNodeMethods(me,styles)»
				«ENDIF»
				«IF !me.attributes.exists[it.name == "name"]»
					
					public String getName() {
						return "«me.name»";
					}
				«ENDIF»
				«FOR attr:me.attributesExtended»
					«val rawType = '''«attr.javaType(modelPackage)»'''»
					«val attributeType = '''«IF attr.isList»java.util.List<«ENDIF»«rawType»«IF attr.isList»>«ENDIF»'''»«{
						if(attr.name.fuEscapeJava == "Name" && !attributeType.equals("String"))
							throw new RuntimeException("attribute \"name\" is predefined as \"String\" by cinco")
					}»
					@Override
					public «attributeType» «IF attr.isPrimitive && attr.attributeTypeName.equals("EBoolean")»is«ELSE»get«ENDIF»«attr.name.fuEscapeJava»() {
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
					}
					«IF attr.isPrimitive && attr.annotations.exists[name.equals("file")]»
						«IF attr.isList»
							
							@Override
							public java.util.List<java.io.File> get«attr.name.fuEscapeJava»File() {
								java.util.List<String> paths = this.get«attr.name.fuEscapeJava»();
								return paths.stream().map(
									(path) -> getFile(path)
								).collect(java.util.stream.Collectors.toList());
							}
						«ELSE»
							
							@Override
							public java.io.File get«attr.name.fuEscapeJava»File() {
								String path = this.get«attr.name.fuEscapeJava»();
								java.io.File file = getFile(path);
								return file;
							}
						«ENDIF»
					«ENDIF»
					
					@Override
					public void set«attr.name.fuEscapeJava»(«attributeType» attr) {
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
						«commandExecuterSwitch(me,
						[cmdExecuter|
						'''
							«cmdExecuter».update«IF me instanceof UserDefinedType»IdentifiableElement«ELSE»«me.name.escapeJava»Properties«ENDIF»(this«IF me instanceof UserDefinedType».parent«ENDIF»,prev);
						'''
						])»
						
						// persist
						this.delegate.persist();
						«IF me.hasPostAttributeValueChange»
							
							//property change hook
							org.eclipse.emf.ecore.EStructuralFeature esf = new org.eclipse.emf.ecore.EStructuralFeature();
							esf.setName("«attr.name»");
							«me.postAttributeValueChange» hook = new «me.postAttributeValueChange»();
							hook.init(cmdExecuter);
							if(hook.canHandleChange(this,esf)) {
								hook.handleChange(this,esf);
							}
						«ENDIF»
					}
				«ENDFOR»
				«IF me.attributesExtended.exists[it.annotations.exists[name.equals("file")]]»«/* TODO: SAMI: move into a controller */»
					
					@Override
					public java.io.File getFile(String baseFilePath) {
						entity.core.BaseFileDB fr = this.getBaseFile(baseFilePath);
						return getFile(fr);
					}
					
					@Override
					public java.io.File getFile(entity.core.BaseFileDB fr) {
						if(fr == null) {
							return null;
						}
						final java.io.InputStream stream = cmdExecuter.loadFile(fr);
						try {
							java.io.File tempFile = java.io.File.createTempFile(
									org.apache.commons.io.FilenameUtils.getBaseName(fr.filename),
									"."+fr.fileExtension
								);
							org.apache.commons.io.FileUtils.copyInputStreamToFile(stream, tempFile);
							return tempFile;
						}catch( java.io.IOException e) {
							e.printStackTrace();
						}
						return null;
					}
					
					@Override
					public entity.core.BaseFileDB getBaseFile(String baseFilePath) {
						try {
							java.net.URI uri = new java.net.URI(baseFilePath);
							String[] segments = uri.getPath().split("/");
							String idStr = segments[segments.length-2];
							long id = Long.parseLong(idStr);
							return entity.core.BaseFileDB.findById(id);
						} catch (java.net.URISyntaxException e) {
							e.printStackTrace();
							return null;
						}
					}
				«ENDIF»
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
	
	def embeddedEdges(GraphModel g)
	'''
		«FOR edge:g.edges»
			
			@Override
			public java.util.List<«edge.apiFQN»> get«edge.name.fuEscapeJava»s() {
				return this.getModelElements(«edge.apiFQN».class);
			}
		«ENDFOR»
	'''
	
	def connectedNodeMethods(Node node) {
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
					«outgoing.apiFQN» cn = null;
					«commandExecuterSwitch(
						outgoing,
						[cmdExecuter|
							'''
								cn = «cmdExecuter».create«outgoing.name.fuEscapeJava»(this, target, java.util.Collections.emptyList(), null);
							'''
						]
					)»
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
	
	
	def embeddedNodeMethods(ModelElement ce,Styles styles) {
		val g = ce.modelPackage as MGLModel
		val superTypes = ce.resolveSuperTypesAndType
		val containedTypes = new java.util.HashSet
		
		for(s:superTypes) {
			val  directContainedTypes = (s as ContainingElement).possibleEmbeddingTypes(g)
			containedTypes += directContainedTypes.map[it.resolveAllSubTypesAndType].flatten.toSet	
		}
		
		'''
			«FOR em:containedTypes»
				«IF !em.isIsAbstract»
					«IF em.isPrime»
						
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
											new Long(width),
											new Long(height),
											this,
											null,
											primeId
										);
									'''									
								]
							)» 
							«em.postCreate("cn",gc)»
							return cn;
						}
					«ELSE»
						
						@Override
						public «em.apiFQN» new«em.name.fuEscapeJava»(int x, int y, int width, int height) {
							«em.apiFQN» cn = null;
							«commandExecuterSwitch(em, 
								[cmdExecuter|
									'''
										cn = «cmdExecuter».create«em.name.fuEscapeJava»(x,y,new Long(width),new Long(height),this,null);
									'''									
								]
							)» 
							«em.postCreate("cn",gc)»
							return cn;
						}
						
						@Override
						public «em.apiFQN» new«em.name.fuEscapeJava»(int x, int y) {
								return this.new«em.name.fuEscapeJava»(x,y,«{
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
