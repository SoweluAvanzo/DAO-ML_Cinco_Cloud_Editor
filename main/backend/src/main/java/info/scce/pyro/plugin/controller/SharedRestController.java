package info.scce.pyro.plugin.controller;

import javax.ws.rs.core.Response;
import info.scce.pyro.plugin.rest.TreeViewRest;
import info.scce.pyro.plugin.rest.TreeViewNodeRest;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
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
        
        final java.util.List<PanacheEntity> list = new LinkedList<>();
        list.addAll(
        	entity.empty.EmptyDB.find("isPublic", true).list()
        );
        list.addAll(
        	entity.primerefs.PrimeRefsDB.find("isPublic", true).list()
        );
        list.addAll(
        	entity.hierarchy.HierarchyDB.find("isPublic", true).list()
        );
        list.addAll(
        	entity.hooksandactions.HooksAndActionsDB.find("isPublic", true).list()
        );
        list.addAll(
        	entity.flowgraph.FlowGraphDB.find("isPublic", true).list()
        );
        
        //find all projects
        List<TreeViewNodeRest> roots = new LinkedList<>();
        for(PanacheEntity g:list) {
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
			if(g instanceof entity.empty.EmptyDB) {
				entity.empty.EmptyDB gc = (entity.empty.EmptyDB)g;
				projectLevel.getchildren().add(
					TreeViewNodeRest.fromEntity(
				        gc,
				        objectCache,
				        gc.filename,
				        null,
				        "empty.Empty",
				        false,
				        false,
				        true,
				        Collections.EMPTY_LIST
					)
				);
			}
			if(g instanceof entity.primerefs.PrimeRefsDB) {
				entity.primerefs.PrimeRefsDB gc = (entity.primerefs.PrimeRefsDB)g;
				projectLevel.getchildren().add(
					TreeViewNodeRest.fromEntity(
				        gc,
				        objectCache,
				        gc.filename,
				        null,
				        "primerefs.PrimeRefs",
				        false,
				        false,
				        true,
				        Collections.EMPTY_LIST
					)
				);
			}
			if(g instanceof entity.hierarchy.HierarchyDB) {
				entity.hierarchy.HierarchyDB gc = (entity.hierarchy.HierarchyDB)g;
				projectLevel.getchildren().add(
					TreeViewNodeRest.fromEntity(
				        gc,
				        objectCache,
				        gc.filename,
				        null,
				        "hierarchy.Hierarchy",
				        false,
				        false,
				        true,
				        Collections.EMPTY_LIST
					)
				);
			}
			if(g instanceof entity.hooksandactions.HooksAndActionsDB) {
				entity.hooksandactions.HooksAndActionsDB gc = (entity.hooksandactions.HooksAndActionsDB)g;
				projectLevel.getchildren().add(
					TreeViewNodeRest.fromEntity(
				        gc,
				        objectCache,
				        gc.filename,
				        null,
				        "hooksandactions.HooksAndActions",
				        false,
				        false,
				        true,
				        Collections.EMPTY_LIST
					)
				);
			}
			if(g instanceof entity.flowgraph.FlowGraphDB) {
				entity.flowgraph.FlowGraphDB gc = (entity.flowgraph.FlowGraphDB)g;
				projectLevel.getchildren().add(
					TreeViewNodeRest.fromEntity(
				        gc,
				        objectCache,
				        gc.filename,
				        null,
				        "flowgraph.FlowGraph",
				        false,
				        false,
				        true,
				        Collections.EMPTY_LIST
					)
				);
			}
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
	
	PyroProjectDB getProject(PanacheEntity entity) {
		if(
			entity instanceof entity.empty.EmptyDB
			|| entity instanceof entity.primerefs.PrimeRefsDB
			|| entity instanceof entity.hierarchy.HierarchyDB
			|| entity instanceof entity.hooksandactions.HooksAndActionsDB
			|| entity instanceof entity.flowgraph.FlowGraphDB
		) {
			return getProjectOf(entity);
		}
		else if(entity instanceof PyroProjectDB) {
			return (PyroProjectDB) entity;
		} else if(entity instanceof PyroFolderDB) {
			return getProject(((PyroFolderDB) entity).parent);
		}
		throw new IllegalStateException("entity is neither a PyroFolderDB nor a PyroProjectDB nor a GraphModel!");
	}
    
    PyroProjectDB getProjectOf(PanacheEntity graph){
    	if(graph == null) {
	        throw new IllegalStateException("Graph is null!");
    	}
    	
    	// derive project
    	PanacheEntity parent = null;
    	if(graph instanceof entity.empty.EmptyDB) {
    		parent = ((entity.empty.EmptyDB) graph).parent;
    	} else if(graph instanceof entity.primerefs.PrimeRefsDB) {
    		parent = ((entity.primerefs.PrimeRefsDB) graph).parent;
    	} else if(graph instanceof entity.hierarchy.HierarchyDB) {
    		parent = ((entity.hierarchy.HierarchyDB) graph).parent;
    	} else if(graph instanceof entity.hooksandactions.HooksAndActionsDB) {
    		parent = ((entity.hooksandactions.HooksAndActionsDB) graph).parent;
    	} else if(graph instanceof entity.flowgraph.FlowGraphDB) {
    		parent = ((entity.flowgraph.FlowGraphDB) graph).parent;
    	}
		
		if(parent == null){
		    throw new IllegalStateException("Graph without parent detected");
		}
		return getProject(parent);
	}
}
