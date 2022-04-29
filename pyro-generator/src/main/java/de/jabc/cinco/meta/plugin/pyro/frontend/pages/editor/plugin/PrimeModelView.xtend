package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.plugin

import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import de.jabc.cinco.meta.plugin.pyro.util.EditorViewPlugin
import de.jabc.cinco.meta.plugin.pyro.util.EditorViewPluginRestController
import de.jabc.cinco.meta.plugin.pyro.util.PluginComponent

class PrimeModelView extends EditorViewPlugin {
	
	PluginComponent pc
	
	new(GeneratorCompound gc) {
		super(gc)
		pc = new PluginComponent
		pc.tab = "Prime"
		pc.key = "plugin_prime"
		pc.fetchURL = "primeview/read/'+currentFile.$type()+'/private"
	}
	
	override getPluginComponent() {	
		pc
	}
	
	override getRestController() {
		val rc = new EditorViewPluginRestController()
		rc.filename = filename
		rc.content = content
		return rc
	}
	
	def filename()'''PrimeRestController.java'''
	
	def content() {
		val graphModels = gc.concreteGraphModels
		val elements = gc.concreteGraphModels.map[elementsAndTypesAndGraphModels].flatten.toSet.filter[!isAbstract]
		'''
			package info.scce.pyro.plugin.controller;
			import info.scce.pyro.plugin.rest.TreeViewNodeRest;
			import info.scce.pyro.plugin.rest.TreeViewRest;
			import javax.ws.rs.core.Response;
			import java.util.LinkedList;
			import java.util.List;
			import java.util.stream.Collectors;
			import javax.ws.rs.core.SecurityContext;
			import io.quarkus.hibernate.orm.panache.PanacheEntity;
			import javax.ws.rs.WebApplicationException;
			
			@javax.transaction.Transactional
			@javax.ws.rs.Path("/primeview")
			@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
			@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
			@javax.enterprise.context.RequestScoped
			public class PrimeRestController {
			
				@javax.inject.Inject
				info.scce.pyro.rest.ObjectCache objectCache;
				
				private static final String LIST_TYPE = "[]";
				
				@javax.ws.rs.GET
				@javax.ws.rs.Path("/read/{typeName}/private")
			    public Response load(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("typeName") final String graphModelType) {
			    	TreeViewRest tvr = new TreeViewRest();
					tvr.setlayer(new LinkedList<>());
			    	String[] typeNames = getPrimeTypes(graphModelType);
					
					for(String typeName : typeNames) {
						«FOR g:graphModels SEPARATOR " else "
						»if("«g.typeName»".equals(typeName)) {
							final java.util.List<«g.entityFQN»> modelList = «g.entityFQN».listAll();
							tvr.getlayer().addAll(
								buildResponse(modelList)
							);
						}«
						ENDFOR»«IF !graphModels.empty» else «ENDIF»{
							throw new WebApplicationException("An unknown type-error occured, while building TreeViewNodeRest!");
						}
					}
					«IF !graphModels.empty»
						return Response.ok(tvr).build();
					«ENDIF»
				}
				
				private <T extends PanacheEntity> List<TreeViewNodeRest> buildResponse(java.util.List<T> list) {
					java.util.Map<PanacheEntity,TreeViewNodeRest> cache = new java.util.HashMap<>();
					return list.stream().map(n -> 
						{
							return buildResponse(n,cache);
						}
					).collect(Collectors.toList());
				}
				
				private TreeViewNodeRest buildResponse(PanacheEntity e, java.util.Map<PanacheEntity,TreeViewNodeRest> cache) {
					return buildResponse(e, cache, "");
				}
				
				private TreeViewNodeRest buildResponse(PanacheEntity e, java.util.Map<PanacheEntity,TreeViewNodeRest> cache, String labelPrefix) {
					if(cache.containsKey(e)) {
				    	return cache.get(e);
				    }
					List<TreeViewNodeRest> restChildren = new LinkedList<>();
					ElementInfo info = getInfo(e);
					TreeViewNodeRest rest = TreeViewNodeRest.fromEntity(
				            e,
				            objectCache,
				            labelPrefix + info.label,
				            info.iconPath,
				            info.typeName,
				            true,
				            true,
				            true,
				            restChildren
				    );
					cache.put(e,rest);
					resolveChildren(e, restChildren, cache);
				    return rest;
				}
				
				private TreeViewNodeRest buildResponse(
					java.util.Collection<PanacheEntity> elementList,
					String label,
					String iconPath,
					String typeName,
					java.util.Map<PanacheEntity,TreeViewNodeRest> cache
				) {
					List<TreeViewNodeRest> restChildren = new LinkedList<>();
					TreeViewNodeRest rest = TreeViewNodeRest.fromEntity(
				            null,
				            objectCache,
				            label,
				            iconPath,
				            typeName,
				            true,
				            false,
				            false,
				            restChildren
				    );
					for(PanacheEntity e : elementList) {
						restChildren.add(
							buildResponse(e, cache)
						);
					}
				    return rest;
				}
				
				private <T extends PanacheEntity> void resolveChildren(T e, List<TreeViewNodeRest> restChildren, java.util.Map<PanacheEntity,TreeViewNodeRest> cache) {
					«FOR e:elements SEPARATOR " else "
					»if(e instanceof «e.entityFQN») {
						«IF e instanceof mgl.ContainingElement»
							// resolve all contained modelElements
							
							java.util.Collection<PanacheEntity> modelElements = ((«e.entityFQN») e).getModelElements();
							restChildren.add(
								buildResponse(
									modelElements,
									"modelelements",
									null,
									LIST_TYPE,
									cache
								)
							);
						«ENDIF»
						«{
							val containedTypes = e.attributesExtended.filter(mgl.ComplexAttribute).filter[!(it.type instanceof mgl.Enumeration)]
							'''
								«IF !containedTypes.empty»
									// resolve all contained types
									«FOR t:containedTypes»
										«IF !t.list»
											PanacheEntity type_«t.name.escapeJava» = ((«e.entityFQN») e).get«t.name.fuEscapeJava»();
											if(type_«t.name.escapeJava» != null) {
												restChildren.add(
													buildResponse(type_«t.name.escapeJava», cache, "«t.name.escapeDart»: ")
												);
											}
										«ELSE»
											java.util.Collection<PanacheEntity> type_«t.name.escapeJava» = ((«e.entityFQN») e).get«t.name.fuEscapeJava»();
											restChildren.add(
												buildResponse(
													type_«t.name.escapeJava»,
													"«t.name.escapeDart»",
													null,
													LIST_TYPE,
													cache
												)
											);
										«ENDIF»
									«ENDFOR»
								«ENDIF»
							'''
						}»
					}«
					ENDFOR»
					«IF !elements.empty» else «ENDIF»{
					    throw new WebApplicationException("An unknown type-error occured, while building TreeViewNodeRest!");
					}
				}
				
				private ElementInfo getInfo(PanacheEntity e) {
					String label = null;
					String typeName = null;
					String iconPath = null;
					«FOR e:elements SEPARATOR " else "
						»if(e instanceof «e.entityFQN») {
							«IF e instanceof mgl.GraphModel»
								label = ((«e.entityFQN») e).filename;
							«ELSE»
								«{
									val hasName = e.attributesExtended.filter(mgl.PrimitiveAttribute).exists[it.name.equals("name")];
									'''
										«IF hasName»
											label = ((«e.entityFQN») e).name;
											label = (label == null || label.isEmpty()) ?
												"[" + ((«e.entityFQN») e).id.toString() + "]" // ID
												: label;
										«ELSE»
											label = " [" + ((«e.entityFQN») e).id.toString() + "]";
										«ENDIF»
									'''
								}»
							«ENDIF»
							label += " («e.typeName»)";
							typeName = "«e.typeName»";
							iconPath = null;
						}«
					ENDFOR»
					«IF !elements.empty»else «ENDIF»{
						throw new WebApplicationException("An unknown type-error occured, while building TreeViewNodeRest!");
					}
					«IF !elements.empty»
						return new ElementInfo(typeName, label, iconPath);
					«ENDIF»
				}
				
				private class ElementInfo {
					final String typeName;
					final String label;
					final String iconPath;
					
					public ElementInfo(String typeName, String label, String iconPath) {
						this.typeName = typeName;
						this.label = label;
						this.iconPath = iconPath;
					}
				}
				
				public String[] getPrimeTypes(String graphModelType) {
			    	«FOR g:graphModels SEPARATOR " else "
			    	»if("«g.typeName»".equals(graphModelType)) {
			    		return «g.MGLModel.typeRegistryFQN».getPrimeModels(graphModelType);
			    	}«
			    	ENDFOR»
			    	return new String[0];
			    }
			}
		'''	
	}

}
