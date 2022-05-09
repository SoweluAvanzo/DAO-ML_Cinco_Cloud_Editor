package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.plugin

import de.jabc.cinco.meta.plugin.pyro.util.EditorViewPlugin
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import de.jabc.cinco.meta.plugin.pyro.util.PluginComponent
import de.jabc.cinco.meta.plugin.pyro.util.EditorViewPluginRestController
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EReference
import mgl.GraphModel
import org.eclipse.emf.ecore.EPackage

class EcoreModelView extends EditorViewPlugin {
	
	PluginComponent pc
	
	new(GeneratorCompound gc) {
		super(gc)
		pc = new PluginComponent
		pc.tab = "Ecore"
		pc.key = "plugin_ecore"
		pc.fetchURL = "ecoreview/read/'+currentFile.$type()+'/private"
	}
	
	override getPluginComponent() {
		pc
	}
	
	override getRestController(){	
    	
		val rc = new EditorViewPluginRestController()
		rc.filename="EcoreRestController.java"
		rc.content =
		'''
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
			    info.scce.pyro.rest.ObjectCache objectCache;
				
			    @javax.ws.rs.GET
			    @javax.ws.rs.Path("read/{typeName}/private")
			    @javax.annotation.security.RolesAllowed("user")
			    public Response load(@javax.ws.rs.core.Context SecurityContext securityContext,  @javax.ws.rs.PathParam("typeName") final String graphModelType) {
					
			    	TreeViewRest tvr = new TreeViewRest();
			    	tvr.setlayer(new LinkedList<>());
					«FOR lib:gc.ecores»
						«{
							
							'''
								«IF !lib.allReferringGraphModels.empty»
									if(
										«FOR g : lib.allReferringGraphModels SEPARATOR " ||"»
											graphModelType.equals("«g.typeName»")
										«ENDFOR»
									) {
										final java.util.List<«lib.entityFQN»> list«lib.name.escapeJava» = «lib.entityFQN».listAll();
										tvr.getlayer().addAll(
											buildResponse«lib.name.fuEscapeJava»(list«lib.name.escapeJava», graphModelType)
										);
									}
								«ENDIF»
							'''
						}»
			        «ENDFOR»
			        return Response.ok(tvr).build();
			    }
				«FOR lib:gc.ecores»
					
					private List<TreeViewNodeRest> buildResponse«lib.name.fuEscapeJava»(java.util.List<«lib.entityFQN»> list, String graphModelType) {
						java.util.Map<«dbTypeName», TreeViewNodeRest> cache = new java.util.HashMap<>();
						return list.stream().map(n->
							buildResponse«lib.name.fuEscapeJava»(n, graphModelType, cache)
						).collect(Collectors.toList());
					}
					
					private TreeViewNodeRest buildResponse«lib.name.fuEscapeJava»(«lib.entityFQN» entity, String graphModelType, java.util.Map<«dbTypeName»,TreeViewNodeRest> cache) {
					    if(cache.containsKey(entity)) {
					    	return cache.get(entity);
					    }
					    
					    List<TreeViewNodeRest> restChildren = new LinkedList<>();
					    TreeViewNodeRest rest = TreeViewNodeRest.fromEntity(
					            entity,
					            objectCache,
					            entity.filename,
					            null,
					            "«lib.typeName»",
					            false,
					            false,
					            false,
					            restChildren
					
					    );
					    cache.put(entity,rest);
						«FOR cl:lib.EClassifiers.filter(EClass)»
							«{
								// all referenting graphmodel-types that apply to "pyroEcoreRootType",
								// don't exclude this type with "pyroEcoreExcludeType",
								// or do not have such an annotation
								val referringGraphModels = lib.allReferringGraphModels.filter[isReferenceable(cl,it)]
								'''
									«IF !referringGraphModels.empty»
										if(
											«FOR g:referringGraphModels SEPARATOR " ||"»
												graphModelType.equals("«g.typeName»")
											«ENDFOR»
										) {
											restChildren.addAll(
												entity.get«cl.name.fuEscapeJava»().stream().map(n->
													buildResponse«cl.name.fuEscapeJava»(n, graphModelType, cache)
												).collect(Collectors.toList())
											);
										}
										
									«ENDIF»
								'''
							}»
						«ENDFOR»
					    return rest;
					}
					«FOR cl:lib.EClassifiers.filter(EClass).filter[!abstract]»
						
						private TreeViewNodeRest buildResponse«cl.name.fuEscapeJava»(«dbTypeName» e, String graphModelType, java.util.Map<«dbTypeName»,TreeViewNodeRest> cache) {
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
								«FOR er:cl.EReferences»
									«{
										// all referenting graphmodel-types that apply to "pyroEcoreRootType",
										// don't exclude this type with "pyroEcoreExcludeType",
										// or do not have such an annotation
										val referringGraphModels = lib.allReferringGraphModels.filter[isReferenceable(cl,it)]
										'''
											«IF !referringGraphModels.empty»
												if( 
													«FOR g:referringGraphModels SEPARATOR " ||"»
														graphModelType.equals("«g.typeName»")
													«ENDFOR»
												) {
													«IF er.list»
														restChildren.addAll(
															entity.get«er.name.fuEscapeJava»().stream().map(n->
																buildResponse«er.EType.name.fuEscapeJava»(n, graphModelType, cache)
															).collect(Collectors.toList())
														);
													«ELSE»
														if(entity.get«er.name.fuEscapeJava»()!=null) {
															restChildren.add(
																buildResponse«er.EType.name.fuEscapeJava»(entity.get«er.name.fuEscapeJava»(), graphModelType, cache)
															);
														}
													«ENDIF»
												}
												
											«ENDIF»
										'''
									}»
								«ENDFOR»
								return rest;
							}
							«{
								val subTypes = cl.resolveSubTypesAndType.filter[!equals(cl)]
								'''
									«IF !subTypes.empty»// switching to concrete subTypes of «cl.name.fuEscapeJava»«ENDIF»
									«FOR type : subTypes»
										else if(e instanceof «type.entityFQN») {
											return buildResponse«type.name.fuEscapeJava»(e, graphModelType, cache);
										}
									«ENDFOR»
								'''
							}»
						    throw new WebApplicationException("An unknown type-error occured, while building TreeViewNodeRest for «cl.name.fuEscapeJava»!");
						}
				    «ENDFOR»
					«FOR cl:lib.EClassifiers.filter(EClass).filter[abstract]»
						
						private TreeViewNodeRest buildResponse«cl.name.fuEscapeJava»(«dbTypeName» entity, String graphModelType, java.util.Map<«dbTypeName»,TreeViewNodeRest> cache) {
							«{
								val subTypes = cl.resolveSubTypesAndType
								'''
									«FOR type:subTypes SEPARATOR " else "
									»if(entity instanceof «type.entityFQN») {
										return buildResponse«type.name.fuEscapeJava»(entity, graphModelType, cache);
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
	
	def allReferringGraphModels(EPackage lib) {
		val allPrimeNodes = gc.mglModels.map[nodes].flatten.toSet.filter[isPrime].filter[!(primeReference.type instanceof mgl.ModelElement)]
		val containedElements = lib.elements.map[resolveSubTypesAndType].flatten.toSet
		val allReferencees = allPrimeNodes.filter[p|
			val primeType = p.primeReference.type
			val resolvedTypes = primeType.resolveSubTypesAndType
			!containedElements.filter[c|
				!resolvedTypes.filter[r|
					r.typeName.toString == c.typeName.toString
				].empty
			].empty
		]
		allReferencees.map[graphModels].flatten.toSet.filter[!isAbstract]
	}
	
	def isReferenceable(EClass c, GraphModel g) {
		val r = g.annotations.filter[name.equals("pyroEcoreRootType")]
		r.exists[value.contains(c.name)] || r.empty
	}
	
	def isReferenceable(EReference c, GraphModel g) {
		val r = g.annotations.filter[name.equals("pyroEcoreExcludeType")]
		!r.exists[value.contains(c.EType.name)]
	}
}
