package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.plugin

import de.jabc.cinco.meta.plugin.pyro.util.EditorViewPlugin
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import de.jabc.cinco.meta.plugin.pyro.util.PluginComponent
import de.jabc.cinco.meta.plugin.pyro.util.EditorViewPluginRestController
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EReference

class EcoreModelView extends EditorViewPlugin {
	
	PluginComponent pc
	
	new(GeneratorCompound gc) {
		super(gc)
		pc = new PluginComponent
		pc.tab = "Ecore"
		pc.key = "plugin_ecore"
		pc.fetchURL = "ecoreview/read/private"	
	}
	
	override getPluginComponent() {
		pc
	}
	
	override getRestController(){

		
	val rc = new EditorViewPluginRestController()
	rc.filename="EcoreRestController.java"
	rc.content = '''
	package info.scce.pyro.plugin.controller;
	import info.scce.pyro.plugin.rest.TreeViewNodeRest;
	import info.scce.pyro.plugin.rest.TreeViewRest;
	import javax.ws.rs.core.Response;
	import java.util.LinkedList;
	import java.util.List;
	import java.util.stream.Collectors;
	import javax.ws.rs.core.SecurityContext;
	import «dbTypeFQN»;
	import javax.ws.rs.WebApplicationException;
	
	@javax.transaction.Transactional
	@javax.ws.rs.Path("/ecoreview")
	@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public class EcoreRestController {
	
	    @javax.inject.Inject
	    private info.scce.pyro.rest.ObjectCache objectCache;
		
		«FOR lib:gc.ecores»
			@javax.inject.Inject
			private info.scce.pyro.core.«lib.name.fuEscapeJava»Controller «lib.name.escapeJava»RestController;
		«ENDFOR»

	    @javax.ws.rs.GET
	    @javax.ws.rs.Path("read/{id}/private")
	    @javax.annotation.security.RolesAllowed("user")
	    public Response load(@javax.ws.rs.core.Context SecurityContext securityContext) {
			
	    	TreeViewRest tvr = new TreeViewRest();
	    	tvr.setlayer(new LinkedList<>());
			«FOR lib:gc.ecores»
				final java.util.Set<«lib.entityFQN»> list«lib.name.escapeJava» = «lib.name.escapeJava»RestController
				        .collectFiles();
				tvr.getlayer().addAll(buildResponse«lib.name.fuEscapeJava»(list«lib.name.escapeJava»));
	        «ENDFOR»
	        return Response.ok(tvr).build();
	    }
		«FOR lib:gc.ecores»
			
			private List<TreeViewNodeRest> buildResponse«lib.name.fuEscapeJava»(java.util.Set<«lib.entityFQN»> list) {
				java.util.Map<«dbTypeName»,TreeViewNodeRest> cache = new java.util.HashMap<>();
				return list.stream().map(n->buildResponse«lib.name.fuEscapeJava»(n,cache)).collect(Collectors.toList());
			}
			
			private TreeViewNodeRest buildResponse«lib.name.fuEscapeJava»(«lib.entityFQN» entity, java.util.Map<«dbTypeName»,TreeViewNodeRest> cache) {
			    if(cache.containsKey(entity)) {
			    	return cache.get(entity);
			    }
			    
			    List<TreeViewNodeRest> restChildren = new LinkedList<>();
			    TreeViewNodeRest rest = TreeViewNodeRest.fromEntity(
			            entity
			            ,objectCache,
			            entity.filename,
			            null,
			            "«lib.typeName»",
			            false,
			            false,
			            false,
			            restChildren
			
			    );
			    cache.put(entity,rest);
			    «FOR cl:lib.EClassifiers.filter(EClass).filter[isReferenceable]»
			    	restChildren.addAll(
			    		entity.get«cl.name.fuEscapeJava»().stream().map(n->
			    			buildResponse«cl.name.fuEscapeJava»(n,cache)
			    		).collect(Collectors.toList())
			    	);
			    «ENDFOR»
			    return rest;
			}
			«FOR cl:lib.EClassifiers.filter(EClass).filter[!abstract]»
				
				private TreeViewNodeRest buildResponse«cl.name.fuEscapeJava»(«dbTypeName» e, java.util.Map<«dbTypeName»,TreeViewNodeRest> cache) {
					if(e instanceof «cl.entityFQN») {
						«cl.entityFQN» entity = («cl.entityFQN») e;
						if(cache.containsKey(entity)) {
							return cache.get(entity);
						}
						List<TreeViewNodeRest> restChildren = new LinkedList<>();
						
						TreeViewNodeRest rest = TreeViewNodeRest.fromEntity(
						        entity,
						        objectCache,
						        «IF cl.attributesExtended.exists[a|a.name == 'name']»
						        	entity.name,
						        «ELSE»
						        	entity.id.toString(),
						        «ENDIF»
						        null,
						        "«cl.typeName»",
						        false,
						        false,
						        true,
						        restChildren
						
						);
						cache.put(entity,rest);
						«FOR er:cl.EReferences.filter[isReferenceable]»
							«IF er.list»
								restChildren.addAll(
									entity.get«er.name.fuEscapeJava»().stream().map(n->
										buildResponse«er.EType.name.fuEscapeJava»(n,cache)
									).collect(Collectors.toList())
								);
							«ELSE»
								if(entity.get«er.name.fuEscapeJava»()!=null) {
									restChildren.add(
										buildResponse«er.EType.name.fuEscapeJava»(entity.get«er.name.fuEscapeJava»(),cache)
									);
								}
							«ENDIF»
						«ENDFOR»
						return rest;
					}
					«{
						val subTypes = cl.resolveSubTypesAndType.filter[!equals(cl)]
						'''
							«IF !subTypes.empty»// switching to discrete subTypes of «cl.name.fuEscapeJava»«ENDIF»
							«FOR type : subTypes»
								else if(e instanceof «type.entityFQN») {
									return buildResponse«type.name.fuEscapeJava»(e, cache);
								}
							«ENDFOR»
						'''
					}»
				    throw new WebApplicationException("An unknown type-error occured, while building TreeViewNodeRest for «cl.name.fuEscapeJava»!");
				}
		    «ENDFOR»
			«FOR cl:lib.EClassifiers.filter(EClass).filter[abstract]»
				
				private TreeViewNodeRest buildResponse«cl.name.fuEscapeJava»(«dbTypeName» entity, java.util.Map<«dbTypeName»,TreeViewNodeRest> cache) {
					«{
						val subTypes = cl.resolveSubTypesAndType
						'''
							«FOR type:subTypes SEPARATOR " else "
							»if(entity instanceof «type.entityFQN») {
								return buildResponse«type.name.fuEscapeJava»(entity, cache);
							}«
							ENDFOR»
						'''
					}»
				    throw new WebApplicationException("An unknown type-error occured, while building TreeViewNodeRest for «cl.name.fuEscapeJava»!");
				}
			«ENDFOR»
		«ENDFOR»
	}
	
	'''
	
	rc
	}
	
	def isReferenceable(EClass c) {
		val r = gc.mglModels.map[annotations].flatten.filter[name.equals("pyroEcoreRootType")]
		r.exists[value.contains(c.name)] || r.empty
	}
	
	def isReferenceable(EReference c) {
		val r = gc.mglModels.map[annotations].flatten.filter[name.equals("pyroEcoreExcludeType")]
		!r.exists[value.contains(c.EType.name)]
	}
	
	
	
}
