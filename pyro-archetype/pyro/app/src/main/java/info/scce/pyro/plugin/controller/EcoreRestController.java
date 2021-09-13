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
    public Response load(@javax.ws.rs.core.Context SecurityContext securityContext) {
		
    	TreeViewRest tvr = new TreeViewRest();
    	tvr.setlayer(new LinkedList<>());
		final java.util.List<entity.externallibrary.ExternalLibraryDB> listexternalLibrary = entity.externallibrary.ExternalLibraryDB.listAll();
		tvr.getlayer().addAll(buildResponseExternalLibrary(listexternalLibrary));
        return Response.ok(tvr).build();
    }
	
	private List<TreeViewNodeRest> buildResponseExternalLibrary(java.util.List<entity.externallibrary.ExternalLibraryDB> list) {
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
	    	entity.getExternalActivity().stream().map(n->
	    		buildResponseExternalActivity(n,cache)
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
			        entity,
			        objectCache,
			        entity.id.toString(),
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
					buildResponseExternalActivity(n,cache)
				).collect(Collectors.toList())
			);
			return rest;
		}
	    throw new WebApplicationException("An unknown type-error occured, while building TreeViewNodeRest for ExternalActivityLibrary!");
	}
	
	private TreeViewNodeRest buildResponseExternalActivity(PanacheEntity e, java.util.Map<PanacheEntity,TreeViewNodeRest> cache) {
		if(e instanceof entity.externallibrary.ExternalActivityDB) {
			entity.externallibrary.ExternalActivityDB entity = (entity.externallibrary.ExternalActivityDB) e;
			if(cache.containsKey(entity)) {
				return cache.get(entity);
			}
			List<TreeViewNodeRest> restChildren = new LinkedList<>();
			
			TreeViewNodeRest rest = TreeViewNodeRest.fromEntity(
			        entity,
			        objectCache,
			        entity.name,
			        null,
			        "externallibrary.ExternalActivity",
			        false,
			        false,
			        true,
			        restChildren
			
			);
			cache.put(entity,rest);
			return rest;
		}
	    throw new WebApplicationException("An unknown type-error occured, while building TreeViewNodeRest for ExternalActivity!");
	}
}

