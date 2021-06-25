  package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.impl

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import de.jabc.cinco.meta.plugin.pyro.util.MGLExtension
import mgl.Attribute
import mgl.ContainingElement
import mgl.Edge
import mgl.GraphModel
import mgl.GraphicalModelElement
import mgl.MGLModel
import mgl.ModelElement
import mgl.Node
import mgl.NodeContainer
import style.NodeStyle
import style.Styles
import de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.ModelElementHook

class GraphModelElementTransientImplementation extends Generatable {
	
	protected extension ModelElementHook = new ModelElementHook
	
	new(GeneratorCompound gc) {
		super(gc)
	}
		
	def filename(ModelElement me)'''«me.name.fuEscapeJava»Impl.java'''
	
	def content(ModelElement me, Styles styles)
	{
		val g = me.modelPackage as MGLModel
		'''
			package «g.apiImplFQNBase»;
			import «dbTypeFQN»;
			
			public class «me.name.fuEscapeJava»Impl implements «me.apiFQN» {
				
				final String id;
				«IF me instanceof GraphModel»
					final java.util.List<graphmodel.Edge> edges = new java.util.LinkedList<>();
				«ENDIF»
				«IF me instanceof ContainingElement»
					final java.util.List<graphmodel.ModelElement> modelElements = new java.util.LinkedList<>();
				«ENDIF»
				«IF me instanceof GraphicalModelElement»
					graphmodel.ModelElementContainer container;
				«ENDIF»
				«IF me instanceof Edge»
					graphmodel.Node source;
					graphmodel.Node target;
					final java.util.List<info.scce.pyro.trans.BendingPoint> bendingPoints = new java.util.LinkedList<>();
				«ENDIF»
				«IF me instanceof Node»
					int x = 0;
					int y = 0;
					int width = 0;
					int height = 0;
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
				
				«FOR attr:me.attributesExtended»
					«IF attr.isList»java.util.List<«ENDIF»«IF !attr.isPrimitive»«g.apiFQN».«ENDIF»«attr.javaType(g)»«IF attr.isList»>«ENDIF» «attr.name.escapeJava»«IF attr.isList» =  new LinkedList<>()«ENDIF»;
				«ENDFOR»
				
				
				public «me.name.fuEscapeJava»Impl(«IF me instanceof GraphicalModelElement»graphmodel.ModelElementContainer container«ENDIF») {
					«IF me instanceof GraphicalModelElement»
						this.container = container;
					«ENDIF»
					this.id = org.apache.commons.lang3.RandomStringUtils.random(10, true, false);
					«FOR attr:me.attributesExtended.filter[isPrimitive]»
						«IF attr.type.getEnum(g)!==null»
							this.«attr.name.escapeJava» = «attr.type.getEnum(g).literals.get(0).toUnderScoreCase»;
						«ELSE»
							this.«attr.name.escapeJava» = «attr.getPrimitiveDefault»;
						«ENDIF»
					«ENDFOR»
				}
			    
				@Override
				public String getId() {
					return id;
				}
				
				@Override
				public «dbTypeName» getDelegate() {
					return null;
				}
				
				/**
				 * Since this is a transient class, that has no
				 * delegate, this method should not be used!
				 */
				@Override
				@Deprecated
				public long getDelegateId() {
					return -1;
				}
				
				«IF me instanceof MGLModel»
					«me.embeddedEdges»
							
					@Override
					public void deleteModelElement(graphmodel.ModelElement cme) {
						«FOR e:g.elements SEPARATOR " else "
						»if(cme instanceof «e.apiFQN») {
								((«e.apiFQN») cme).delete();
						}«
						ENDFOR»
					}
					
					@Override
					public void delete() {
						// TODO: NOT IMPLEMENTED
					}
				«ENDIF»
				«IF me instanceof GraphicalModelElement»
					
					@Override
					public «coreAPIFQN("GraphModel")» getRootElement() {
						if(this.getContainer() instanceof «g.apiFQN»){
							return «coreAPIFQN("GraphModel")») this.getContainer();
						}
						return («coreAPIFQN("GraphModel")») ((«coreAPIFQN("ModelElement")») this.getContainer()).getRootElement();
					}
					
					@Override
					public «coreAPIFQN("ModelElementContainer")» getContainer() {
						return («coreAPIFQN("ModelElementContainer")») this.container;
					}
				«ENDIF»
				«IF me instanceof Edge»
					
					@Override
					public void delete() {
						«'''
							this.source.getOutgoing().remove(this);
							this.target.getIncoming().remove(this);
							this.getRootElement().getModelElements().remove(this);
						'''.deleteHooks(me, g)»
					}
					
					@Override
					public graphmodel.Node getSourceElement() {
						return source;
					}
					
					@Override
					public graphmodel.Node getTargetElement() {
						return target;
					}
					
					@Override
					public void reconnectSource(graphmodel.Node node) {
						this.source.getOutgoing().remove(this);
						this.source = node;
						this.source.getOutgoing().add(this);
					}
					
					@Override
					public void reconnectTarget(graphmodel.Node node) {
						this.target.getIncoming().remove(this);
						this.target = node;
						this.target.getIncoming().add(this);
					}
					
					@Override
					public void addBendingPoint(long x, long y) {
						bendingPoints.add(new info.scce.pyro.trans.BendingPoint(x,y));
					}
					
					@Override
					public void clearBendingPoints() {
						bendingPoints.clear();
					}
					
					@Override
					public java.util.List<? extends graphmodel.BendingPoint> getBendingPoints() {
						return this.bendingPoints;
					}
				«ENDIF»
				«IF me instanceof Node»
					
					@Override
					public void delete() {
						«'''
							«IF me instanceof NodeContainer»
								getModelElements().stream().filter(n->n instanceof graphmodel.Node).forEach(n->((graphmodel.Node)n).delete());
							«ENDIF»
							java.util.Set<graphmodel.Edge> edges = new java.util.HashSet<>();
							edges.addAll(getIncoming());
							edges.addAll(getOutgoing());
							edges.forEach(graphmodel.Edge::delete);
							this.container.getModelElements().remove(this);
						'''.deleteHooks(me, g)»
					}
					
					@Override
					public int getX() {
					    return x;
					}
					
					@Override
					public int getY() {
					    return y;
					}
					
					@Override
					public int getWidth() {
					    return width;
					}
					
					@Override
					public int getHeight() {
					    return height;
					}
					
					@Override
					public java.util.List<graphmodel.Edge> getIncoming() {
						return incoming;
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
						return outgoing;
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
					
					@Override
					public void moveTo(graphmodel.ModelElementContainer container,int x, int y) {
						«IF me.hasPostMove»
							graphmodel.ModelElementContainer preContainer = this.getContainer();
							int preX = this.getX();
							int preY = this.getY();
							
						«ENDIF»
						this.container.getModelElements().remove(this);
						container.getModelElements().add(this);
						this.container = container;
						this.x = x;
						this.y = y;
						«{
							val postMove = me.resolvePostMove
							'''
								«IF !postMove.empty»
									
									// postMoveHooks
									«FOR pM:postMove»
										{
											//post move
											«pM» hook = new «pM»();
											hook.init(null);
											hook.postMove(this,preContainer,container,x,y,x-preX,y-preY);	
										}	
									«ENDFOR»
								«ENDIF»
							'''
						}»
					}
					
					@Override
					public void resize(int width, int height) {
						this.width = width;
						this.height = height;
						«{
							val postResize = me.resolvePostResize
							'''
								«IF !postResize.empty»
									
									// postResizeHooks
									«FOR pR:postResize»
										{
											// post resize
											«pR» hook = new «pR»();
											hook.init(null);
											hook.postResize(this,width,height);
										}
									«ENDFOR»
								«ENDIF»
							'''
						}»
					}
					
					«IF me.isPrime || me.isEcorePrime»
						«{
							val refElem = me.primeReference.type
							'''
								@Override
								public «refElem.apiFQN» get«me.primeReference.name.fuEscapeJava»()
								{
									return «me.primeReference.name.escapeJava»;
								}
							'''
						}»
					«ENDIF»
					«connectedNodeMethods(me,g)»
				«ENDIF»
				«IF me instanceof ContainingElement»
					
					@Override
					public java.util.List<graphmodel.ModelElement> getModelElements() {
						return modelElements;
					}
						
					@Override
					public <T extends graphmodel.ModelElement> java.util.List<T> getModelElements(Class<T> clazz) {
						return this.getModelElements().stream().filter(n->clazz.isInstance(n)).map(n->clazz.cast(n)).collect(java.util.stream.Collectors.toList());
					}
							
					private java.util.List<graphmodel.ModelElement> getAllModelElements(graphmodel.ModelElementContainer cmc) {
						java.util.List<graphmodel.ModelElement> cm = new java.util.LinkedList<>(cmc.getModelElements());
						cm.addAll(cmc.getModelElements().stream().filter(n->n instanceof graphmodel.ModelElementContainer).flatMap(n->getAllModelElements((graphmodel.ModelElementContainer)n).stream()).collect(java.util.stream.Collectors.toList()));
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
					«embeddedNodeMethods(me,g,styles)»
				«ENDIF»
				«FOR attr:me.attributesExtended»
					
					@Override
					public «IF attr.isList»java.util.List<«ENDIF»«IF !attr.isPrimitive»«g.apiFQN».«ENDIF»«attr.javaType(g)»«IF attr.isList»>«ENDIF» «IF attr.type.equals("EBoolean")»is«ELSE»get«ENDIF»«attr.name.fuEscapeJava»() {
						return «attr.name.escapeJava»;
					}
					
					@Override
					public void set«attr.name.fuEscapeJava»(«IF attr.isList»java.util.List<«ENDIF»«IF !attr.isPrimitive»«g.apiFQN».«ENDIF»«attr.javaType(g)»«IF attr.isList»>«ENDIF» attr) {
						this.«attr.name.escapeJava» = attr;
						«{
							val postAttributeValueChange = me.resolvePostAttributeValueChange
							'''
								«IF !postAttributeValueChange.empty»
									
									// postAttributeValueChangeHooks
									«FOR pV:postAttributeValueChange»
										{
											org.eclipse.emf.ecore.EStructuralFeature esf = new org.eclipse.emf.ecore.EStructuralFeature();
											esf.setName("«attr.name»");
											«pV» hook = new «pV»();
											hook.init(null);
											if(hook.canHandleChange(this,esf)) {
												hook.handleChange(this,esf);
											}
										}
									«ENDFOR»
								«ENDIF»
							'''
						}»
					}
				«ENDFOR»
			}
		'''
	}
	
	def primitiveGETConverter(Attribute attribute, String string) {
		return switch(attribute.type) {
			case "EInt":'''«IF attribute.list»«string».stream().map(n->Math.toIntExact(n)).collect(java.util.stream.Collectors.toList())«ELSE»Math.toIntExact(«string»)«ENDIF»'''
			case "EBigInteger":'''«IF attribute.list»«string».stream().map(n->Math.toIntExact(n)).collect(java.util.stream.Collectors.toList())«ELSE»Math.toIntExact(«string»)«ENDIF»'''
			case "ELong":'''«IF attribute.list»«string».stream().map(n->Math.toIntExact(n)).collect(java.util.stream.Collectors.toList())«ELSE»Math.toIntExact(«string»)«ENDIF»'''
			case "EByte":'''«IF attribute.list»«string».stream().map(n->Math.toIntExact(n)).collect(java.util.stream.Collectors.toList())«ELSE»Math.toIntExact(«string»)«ENDIF»'''
			case "EShort":'''«IF attribute.list»«string».stream().map(n->Math.toIntExact(n)).collect(java.util.stream.Collectors.toList())«ELSE»Math.toIntExact(«string»)«ENDIF»'''
			default:string
		}
	}
	
	def primitiveSETConverter(Attribute attribute, String string) {
		return switch(attribute.type) {
			case "EInt":'''«IF attribute.list»«string».stream().map(n->Long.valueOf(n)).collect(java.util.stream.Collectors.toList())«ELSE»Long.valueOf(«string»)«ENDIF»'''
			case "EBigInteger":'''«IF attribute.list»«string».stream().map(n->Long.valueOf(n)).collect(java.util.stream.Collectors.toList())«ELSE»Long.valueOf(«string»)«ENDIF»'''
			case "ELong":'''«IF attribute.list»«string».stream().map(n->Long.valueOf(n)).collect(java.util.stream.Collectors.toList())«ELSE»Long.valueOf(«string»)«ENDIF»'''
			case "EByte":'''«IF attribute.list»«string».stream().map(n->Long.valueOf(n)).collect(java.util.stream.Collectors.toList())«ELSE»Long.valueOf(«string»)«ENDIF»'''
			case "EShort":'''«IF attribute.list»«string».stream().map(n->Long.valueOf(n)).collect(java.util.stream.Collectors.toList())«ELSE»Long.valueOf(«string»)«ENDIF»'''
			default:string
		}
	}
	
	def embeddedEdges(MGLModel g)
	'''
		«FOR edge:g.edgesTopologically»
			
			@Override
			public java.util.List<«edge.apiFQN»> get«edge.name.fuEscapeJava»s() {
				return this.getModelElements(«edge.apiFQN».class);
			}
		«ENDFOR»
	'''
	
	def connectedNodeMethods(Node node,MGLModel g)
	'''
		«FOR incoming:node.possibleIncoming»
			
			@Override
			public java.util.List<«incoming.apiFQN»> getIncoming«incoming.name.fuEscapeJava»s() {
				return getIncoming(«incoming.apiFQN».class);
			}
		«ENDFOR»
		«FOR source:node.possibleIncoming.map[possibleSources].flatten.toSet»
			
			@Override
			public java.util.List<«source.apiFQN»> get«source.name.fuEscapeJava»Predecessors() {
				return getPredecessors(«source.apiFQN».class);
			}
		«ENDFOR»
		«FOR outgoing:node.possibleOutgoing»
			
			@Override
			public java.util.List<«outgoing.apiFQN»> getOutgoing«outgoing.name.fuEscapeJava»s() {
				return getOutgoing(«outgoing.apiFQN».class);
			}
			«IF !outgoing.isIsAbstract»
				«FOR target:outgoing.possibleTargets»
					
					@Override
					public «outgoing.apiFQN» new«outgoing.name.fuEscapeJava»(«target.apiFQN» target) {
						«outgoing.name.fuEscapeJava»Impl cn = new «outgoing.name.fuEscapeJava»Impl(getRootElement());
						cn.source = this;
						this.outgoing.add(cn);
						cn.target = target;
						target.getIncoming().add(cn);
						this.getRootElement().getModelElements().add(cn);
						«outgoing.postCreate("cn",gc,true)»
						return cn;
					}
				«ENDFOR»
			«ENDIF»
		«ENDFOR»
		«FOR target:node.possibleOutgoing.map[possibleTargets].flatten.toSet»
			
			@Override
			public java.util.List<«target.apiFQN»> get«target.name.fuEscapeJava»Successors() {
				return getSuccessors(«target.apiFQN».class);
			}
		«ENDFOR»
	'''
	
	def embeddedNodeMethods(ContainingElement ce,MGLModel g,Styles styles)
	'''
		«FOR em:ce.possibleEmbeddingTypes(g)»
			
			«IF !em.isIsAbstract»
				«IF em.isPrime || em.isEcorePrime»
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
								«em.postCreate("cn",gc,true)»
								return cn;
							}
						'''
					}»
				«ELSE»
					@Override
					public «em.apiFQN» new«em.name.fuEscapeJava»(int x, int y, int width, int height) {
						«em.name.fuEscapeJava»Impl cn = new «em.name.fuEscapeJava»Impl(this);
						modelElements.add(cn);
						cn.x = x;
						cn.y = y;
						cn.width = width;
						cn.height = height;
						«em.postCreate("cn",gc,true)»
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
	
	def deleteHooks(CharSequence inner, ModelElement me, MGLModel g) {
		val preDeletes = me.resolvePreDelete
		val postDeletes = me.resolvePostDelete
		'''
			«IF !preDeletes.empty»
				// preDeleteHooks
				«FOR preD:preDeletes»
					{
						«preD» prehook = new «preD»();
						prehook.init(null);
						prehook.preDelete(this);
					}
				«ENDFOR»
				
			«ENDIF»
			«inner»
			«IF !postDeletes.empty»
				
				// postDeleteHooks
				«FOR postD:postDeletes»
					{
						«postD» posthook = new «postD»();
						Runnable runnable = posthook.getPostDeleteFunction(this);
						runnable.run();
					}
				«ENDFOR»
			«ENDIF»
		'''
	}
	
	
}