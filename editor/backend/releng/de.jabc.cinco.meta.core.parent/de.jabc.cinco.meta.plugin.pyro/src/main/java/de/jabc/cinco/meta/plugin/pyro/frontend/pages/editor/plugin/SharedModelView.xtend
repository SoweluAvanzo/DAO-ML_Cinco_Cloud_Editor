package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.plugin

import de.jabc.cinco.meta.plugin.pyro.util.EditorViewPlugin
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import de.jabc.cinco.meta.plugin.pyro.util.PluginComponent
import de.jabc.cinco.meta.plugin.pyro.util.EditorViewPluginRestController

class SharedModelView extends EditorViewPlugin {
	
	PluginComponent pc
	
	new(GeneratorCompound gc) {
		super(gc)
		pc = new PluginComponent
		pc.tab = "Shared"
		pc.key = "plugin_shared"
		pc.fetchURL = "sharedview/read/private"	
	}
	
	override getPluginComponent() {
		pc
	}
	
	override getRestController(){
		
	val rc = new EditorViewPluginRestController()
	rc.filename="SharedRestController.java"
	rc.content = '''
		package info.scce.pyro.plugin.controller;
		
		import javax.ws.rs.core.Response;
		import info.scce.pyro.plugin.rest.TreeViewRest;
		import info.scce.pyro.plugin.rest.TreeViewNodeRest;
		import java.util.Collections;
		import java.util.LinkedList;
		import java.util.List;
		import java.util.Optional;
		import «dbTypeFQN»;
		import entity.core.PyroFolderDB;
		import entity.core.PyroProjectDB;
		import entity.core.PyroOrganizationDB;
		
		@javax.transaction.Transactional
		@javax.ws.rs.Path("/sharedview")
		public class SharedRestController {
		
		    @javax.inject.Inject
		    private info.scce.pyro.rest.ObjectCache objectCache;
		    	    
		    @javax.ws.rs.GET
		    @javax.ws.rs.Path("read/private")
		    @javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
		    @org.jboss.resteasy.annotations.GZIP
		    public Response load() {
		    	TreeViewRest tvr = new TreeViewRest();
		    	tvr.setlayer(new LinkedList<>());
		        
		        final java.util.List<«dbTypeName»> list = new LinkedList<>();
		        «FOR g:gc.graphMopdels»
		        	list.addAll(
		        		«g.entityFQN».find("isPublic", true).list()
		        	);
		        «ENDFOR»
		        
		        //find all projects
		        List<TreeViewNodeRest> roots = new LinkedList<>();
		        for(«dbTypeName» g:list) {
		        	//check for known organization
		        	PyroProjectDB project = getProject(g);
		        	PyroOrganizationDB org = getOrganization(project);
		        	//check root is known
		        	Optional<TreeViewNodeRest> optOrg = roots.stream().filter(n->n.getId() == org.id).findFirst();
		        	TreeViewNodeRest orgLevel = null;
		        	if(optOrg.isPresent()) {
		        		orgLevel = optOrg.get();
		        	} else {
		        		orgLevel = TreeViewNodeRest.fromEntity(
							org,
							objectCache,
							org.name,
							null,
							"core.PyroOrganization",
							false,
							false,
							false,
							new LinkedList<>()
		        			
		        		);
		        		roots.add(orgLevel);
		        	}
		        	//check if project is known
		        	Optional<TreeViewNodeRest> optProject = orgLevel.getchildren().stream().filter(n->n.getId() == project.id).findFirst();
					TreeViewNodeRest projectLevel = null;
					if(optProject.isPresent()) {
						projectLevel = optProject.get();
					} else {
						projectLevel = TreeViewNodeRest.fromEntity(
							project,
							objectCache,
							project.name,
							null,
							"core.PyroProject",
							false,
							false,
							false,
							new LinkedList<>()
						);
						orgLevel.getchildren().add(projectLevel);
					}
					«FOR g:gc.graphMopdels»
						if(g instanceof «g.entityFQN») {
							«g.entityFQN» gc = («g.entityFQN»)g;
							projectLevel.getchildren().add(
								TreeViewNodeRest.fromEntity(
							        gc,
							        objectCache,
							        gc.filename,
							        null,
							        "«g.typeName»",
							        false,
							        false,
							        true,
							        Collections.EMPTY_LIST
								)
							);
						}
			        «ENDFOR»
		        }
		        tvr.getlayer().addAll(roots);
		        return Response.ok(tvr).build();
		    }
			
			PyroOrganizationDB getOrganization(PyroProjectDB project) {
			    PyroOrganizationDB parent = project.organization;
			    if(parent == null){
			        throw new IllegalStateException("Project without parent detected");
			    }
			    return parent;
			}
			
			PyroProjectDB getProject(«dbTypeName» entity) {
				«IF !gc.mglModels.empty»
				if(
					«FOR g:gc.graphMopdels SEPARATOR "\n|| "
					»entity instanceof «g.entityFQN»«
					ENDFOR»
				) {
					return getProjectOf(entity);
				}
				else «ENDIF»if(entity instanceof PyroProjectDB) {
					return (PyroProjectDB) entity;
				} else if(entity instanceof PyroFolderDB) {
					return getProject(((PyroFolderDB) entity).parent);
				}
				throw new IllegalStateException("entity is neither a PyroFolderDB nor a PyroProjectDB nor a GraphModel!");
			}
		    
		    PyroProjectDB getProjectOf(«dbTypeName» graph){
		    	if(graph == null) {
			        throw new IllegalStateException("Graph is null!");
		    	}
		    	
		    	// derive project
		    	«dbTypeName» parent = null;
		    	«FOR g:gc.graphMopdels SEPARATOR " else "
		    	»if(graph instanceof «g.entityFQN») {
		    		parent = ((«g.entityFQN») graph).parent;
		    	}«
	    		ENDFOR»
				
				if(parent == null){
				    throw new IllegalStateException("Graph without parent detected");
				}
				return getProject(parent);
			}
		}
	'''
	rc
	}
	
	
	
}