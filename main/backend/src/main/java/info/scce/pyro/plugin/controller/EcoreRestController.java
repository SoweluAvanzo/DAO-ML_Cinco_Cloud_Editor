package info.scce.pyro.plugin.controller;
import info.scce.pyro.externallibrary.rest.ExternalLibrary;
import info.scce.pyro.plugin.rest.TreeViewNodeRest;
import info.scce.pyro.plugin.rest.TreeViewRest;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.core.SecurityContext;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.ws.rs.WebApplicationException;

@javax.transaction.Transactional
@javax.ws.rs.Path("/ecoreview")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
public class EcoreRestController {

    @javax.inject.Inject
    private info.scce.pyro.rest.ObjectCache objectCache;
	
	@javax.inject.Inject
	private info.scce.pyro.core.ExternalLibraryController externalLibraryRestController;

    @javax.ws.rs.GET
    @javax.ws.rs.Path("read/{id}/private")
    @javax.annotation.security.RolesAllowed("user")
    public Response load(@javax.ws.rs.core.Context SecurityContext securityContext,@javax.ws.rs.PathParam("id") final long id) {
    	final entity.core.PyroProjectDB project = entity.core.PyroProjectDB.findById(id);
		
		if(project==null){
		    return Response.status(Response.Status.BAD_REQUEST).build();
		}
    	TreeViewRest tvr = new TreeViewRest();
    	tvr.setlayer(new LinkedList<>());
		final java.util.Set<entity.externallibrary.ExternalLibraryDB> listexternalLibrary = externalLibraryRestController
		        .collectProjectFiles(project);
		tvr.getlayer().addAll(buildResponseExternalLibrary(listexternalLibrary));
        return Response.ok(tvr).build();
    }
	
	private List<TreeViewNodeRest> buildResponseExternalLibrary(java.util.Set<entity.externallibrary.ExternalLibraryDB> list) {
		java.util.Map<PanacheEntity,TreeViewNodeRest> cache = new java.util.HashMap<>();
		return list.stream().map(n->buildResponseExternalLibrary(n,cache)).collect(Collectors.toList());
	}
	
	private TreeViewNodeRest buildResponseExternalLibrary(entity.externallibrary.ExternalLibraryDB entity, java.util.Map<PanacheEntity,TreeViewNodeRest> cache) {
	    if(cache.containsKey(entity)) {
	    	return cache.get(entity);
	    }
	    
	    List<TreeViewNodeRest> restChildren = new LinkedList<>();
	    TreeViewNodeRest rest = TreeViewNodeRest.fromEntity(
	            entity
	            ,objectCache,
	            entity.filename,
	            null,
	            "externallibrary.ExternalLibrary",
	            false,
	            false,
	            false,
	            restChildren
	
	    );
	    cache.put(entity,rest);
	    restChildren.addAll(
	    	entity.getExternalActivityLibrary().stream().map(n->
	    		buildResponseExternalActivityLibrary(n,cache)
	    	).collect(Collectors.toList())
	    );
	    restChildren.addAll(
	    	entity.getExternalActivityA().stream().map(n->
	    		buildResponseExternalActivityA(n,cache)
	    	).collect(Collectors.toList())
	    );
	    restChildren.addAll(
	    	entity.getExternalAbstractActivityB().stream().map(n->
	    		buildResponseExternalAbstractActivityB(n,cache)
	    	).collect(Collectors.toList())
	    );
	    restChildren.addAll(
	    	entity.getExternalAbstractActivityC().stream().map(n->
	    		buildResponseExternalAbstractActivityC(n,cache)
	    	).collect(Collectors.toList())
	    );
	    restChildren.addAll(
	    	entity.getExternalActivityD().stream().map(n->
	    		buildResponseExternalActivityD(n,cache)
	    	).collect(Collectors.toList())
	    );
	    return rest;
	}
	
	private TreeViewNodeRest buildResponseExternalActivityLibrary(PanacheEntity e, java.util.Map<PanacheEntity,TreeViewNodeRest> cache) {
		if(e instanceof entity.externallibrary.ExternalActivityLibraryDB) {
			entity.externallibrary.ExternalActivityLibraryDB entity = (entity.externallibrary.ExternalActivityLibraryDB) e;
			if(cache.containsKey(entity)) {
				return cache.get(entity);
			}
			List<TreeViewNodeRest> restChildren = new LinkedList<>();
			
			TreeViewNodeRest rest = TreeViewNodeRest.fromEntity(
			        entity
			        ,objectCache,
			        entity.name,
			        null,
			        "externallibrary.ExternalActivityLibrary",
			        false,
			        false,
			        true,
			        restChildren
			
			);
			cache.put(entity,rest);
			restChildren.addAll(
				entity.getActivities().stream().map(n->
					buildResponseExternalActivityA(n,cache)
				).collect(Collectors.toList())
			);
			restChildren.addAll(
				entity.getRepresentsA().stream().map(n->
					buildResponseExternalActivityA(n,cache)
				).collect(Collectors.toList())
			);
			restChildren.addAll(
				entity.getRepresentsB().stream().map(n->
					buildResponseExternalAbstractActivityB(n,cache)
				).collect(Collectors.toList())
			);
			restChildren.addAll(
				entity.getRepresentsC().stream().map(n->
					buildResponseExternalAbstractActivityC(n,cache)
				).collect(Collectors.toList())
			);
			restChildren.addAll(
				entity.getRepresentsD().stream().map(n->
					buildResponseExternalActivityD(n,cache)
				).collect(Collectors.toList())
			);
			return rest;
		}
	    throw new WebApplicationException("An unknown type-error occured, while building TreeViewNodeRest for ExternalActivityLibrary!");
	}
	
	private TreeViewNodeRest buildResponseExternalActivityA(PanacheEntity e, java.util.Map<PanacheEntity,TreeViewNodeRest> cache) {
		if(e instanceof entity.externallibrary.ExternalActivityADB) {
			entity.externallibrary.ExternalActivityADB entity = (entity.externallibrary.ExternalActivityADB) e;
			if(cache.containsKey(entity)) {
				return cache.get(entity);
			}
			List<TreeViewNodeRest> restChildren = new LinkedList<>();
			
			TreeViewNodeRest rest = TreeViewNodeRest.fromEntity(
			        entity
			        ,objectCache,
			        entity.name,
			        null,
			        "externallibrary.ExternalActivityA",
			        false,
			        false,
			        true,
			        restChildren
			
			);
			cache.put(entity,rest);
			return rest;
		}
	    throw new WebApplicationException("An unknown type-error occured, while building TreeViewNodeRest for ExternalActivityA!");
	}
	
	private TreeViewNodeRest buildResponseExternalActivityD(PanacheEntity e, java.util.Map<PanacheEntity,TreeViewNodeRest> cache) {
		if(e instanceof entity.externallibrary.ExternalActivityDDB) {
			entity.externallibrary.ExternalActivityDDB entity = (entity.externallibrary.ExternalActivityDDB) e;
			if(cache.containsKey(entity)) {
				return cache.get(entity);
			}
			List<TreeViewNodeRest> restChildren = new LinkedList<>();
			
			TreeViewNodeRest rest = TreeViewNodeRest.fromEntity(
			        entity
			        ,objectCache,
			        entity.name,
			        null,
			        "externallibrary.ExternalActivityD",
			        false,
			        false,
			        true,
			        restChildren
			
			);
			cache.put(entity,rest);
			if(entity.getReferencedOfD()!=null) {
				restChildren.add(
					buildResponseExternalActivityD(entity.getReferencedOfD(),cache)
				);
			}
			restChildren.addAll(
				entity.getRecerencingAbstractList().stream().map(n->
					buildResponseExternalActivityD(n,cache)
				).collect(Collectors.toList())
			);
			restChildren.addAll(
				entity.getReferencingList().stream().map(n->
					buildResponseExternalAbstractActivityB(n,cache)
				).collect(Collectors.toList())
			);
			return rest;
		}
		// switching to discrete subTypes of ExternalActivityD
		else if(e instanceof entity.externallibrary.ExternalActivityADB) {
			return buildResponseExternalActivityA(e, cache);
		}
	    throw new WebApplicationException("An unknown type-error occured, while building TreeViewNodeRest for ExternalActivityD!");
	}
	
	private TreeViewNodeRest buildResponseExternalAbstractActivityB(PanacheEntity entity, java.util.Map<PanacheEntity,TreeViewNodeRest> cache) {
		if(entity instanceof entity.externallibrary.ExternalActivityADB) {
			return buildResponseExternalActivityA(entity, cache);
		}
	    throw new WebApplicationException("An unknown type-error occured, while building TreeViewNodeRest for ExternalAbstractActivityB!");
	}
	
	private TreeViewNodeRest buildResponseExternalAbstractActivityC(PanacheEntity entity, java.util.Map<PanacheEntity,TreeViewNodeRest> cache) {
		if(entity instanceof entity.externallibrary.ExternalActivityADB) {
			return buildResponseExternalActivityA(entity, cache);
		}
	    throw new WebApplicationException("An unknown type-error occured, while building TreeViewNodeRest for ExternalAbstractActivityC!");
	}
}

