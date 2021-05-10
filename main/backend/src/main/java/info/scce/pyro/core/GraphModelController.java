package info.scce.pyro.core;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.util.Optional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import entity.core.PyroFileContainerDB;
import info.scce.pyro.core.rest.types.CreatePyroFolder;
import info.scce.pyro.core.rest.types.PyroProjectStructure;
import info.scce.pyro.core.rest.types.UpdatePyroFolder;
import info.scce.pyro.sync.GraphModelWebSocket;
import info.scce.pyro.sync.ProjectWebSocket;
import info.scce.pyro.sync.WebSocketMessage;

@javax.transaction.Transactional
@javax.ws.rs.Path("/graph")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
public class GraphModelController {

	@javax.inject.Inject
	info.scce.pyro.rest.ObjectCache objectCache;

    @javax.inject.Inject
    ProjectWebSocket projectWebSocket;

    @javax.inject.Inject
    GraphModelWebSocket graphModelWebSocket;
	
	@javax.ws.rs.POST
    @javax.ws.rs.Path("create/folder/private")
    @javax.annotation.security.RolesAllowed("user")
	public Response createFolder(@javax.ws.rs.core.Context SecurityContext securityContext, CreatePyroFolder newFolder) {
        //find parent
		final entity.core.PyroFolderDB pf = entity.core.PyroFolderDB.findById(newFolder.getparentId());
		if(pf != null) {
			checkPermission(pf,securityContext);
			final entity.core.PyroFolderDB newPF = new entity.core.PyroFolderDB();
			newPF.name = newFolder.getname();
			newPF.parent = pf;
			newPF.persist();
			pf.innerFolders.add(newPF);
			pf.persist();
			sendProjectUpdate(pf,securityContext);
			return Response.ok(info.scce.pyro.core.rest.types.PyroFolder.fromEntity(newPF,objectCache)).build();
		}
		final entity.core.PyroProjectDB pp = entity.core.PyroProjectDB.findById(newFolder.getparentId());
		if(pp!=null){
			checkPermission(pp,securityContext);
			final entity.core.PyroFolderDB newPF = new entity.core.PyroFolderDB();
			newPF.name = newFolder.getname();
			newPF.parent = pp;
			newPF.persist();
			pp.innerFolders.add(newPF);
			pp.persist();
			sendProjectUpdate(pp,securityContext);
        	return Response.ok(info.scce.pyro.core.rest.types.PyroFolder.fromEntity(newPF,objectCache)).build();
        }
    	return Response.status(Response.Status.NOT_FOUND).build();
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("move/folder/{id}/{targetId}/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response moveFolder(
		@javax.ws.rs.core.Context SecurityContext securityContext,
		@javax.ws.rs.PathParam("id") final long id, 
		@javax.ws.rs.PathParam("targetId") final long targetId
	) {
		if (id == targetId) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Cannot move folder to itself").build();
		}
		
		final entity.core.PyroFolderDB sourceFolder = entity.core.PyroFolderDB.findById(id);
		if (sourceFolder != null) {
			checkPermission(sourceFolder,securityContext);
		} else {
			return Response.status(Response.Status.BAD_REQUEST).entity("Source Folder not found").build();
		}
		
		entity.core.PyroFileContainerDB pfc = entity.core.PyroFileContainerDB.findById(targetId);
		if (pfc != null) {
			checkPermission(pfc,securityContext);
			
			if(pfc instanceof entity.core.PyroFolderDB) {
				entity.core.PyroFolderDB targetFolder = (entity.core.PyroFolderDB) pfc;
				for (final entity.core.PyroFolderDB f: targetFolder.innerFolders) {
					if (sourceFolder.name.equals(f.name)) 
						return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
				}
				
				if (isAscendantFolderOf(sourceFolder, targetFolder)) {
					return Response.status(Response.Status.BAD_REQUEST).entity("Cannot move folder to ascendant").build();
				}
				
				Object obj = getParent(sourceFolder);
				if(obj instanceof entity.core.PyroFolderDB) {
					final entity.core.PyroFolderDB parentFolder = (entity.core.PyroFolderDB)obj;
					parentFolder.innerFolders.remove(sourceFolder);
					targetFolder.innerFolders.add(sourceFolder);
					sourceFolder.parent = targetFolder;
					
					parentFolder.persist();
					targetFolder.persist();
					sourceFolder.persist();
					
				sendProjectUpdate(targetFolder,securityContext);
					sendProjectUpdate(parentFolder,securityContext);
				}
				if(obj instanceof entity.core.PyroProjectDB) {
					final entity.core.PyroProjectDB parentFolder = (entity.core.PyroProjectDB)obj;
					parentFolder.innerFolders.remove(sourceFolder);
					targetFolder.innerFolders.add(sourceFolder);
					sourceFolder.parent = targetFolder;
					
					parentFolder.persist();
					targetFolder.persist();
					sourceFolder.persist();
					
					sendProjectUpdate(targetFolder,securityContext);
					sendProjectUpdate(parentFolder,securityContext);
				}
			} else if(pfc instanceof entity.core.PyroProjectDB) {
				entity.core.PyroProjectDB targetFolder = (entity.core.PyroProjectDB) pfc;
				for (final entity.core.PyroFolderDB f: targetFolder.innerFolders) {
					if (sourceFolder.name.equals(f.name)) 
						return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
				}
				
				Object obj = getParent(sourceFolder);
				if(obj instanceof entity.core.PyroFolderDB) {
					final entity.core.PyroFolderDB parentFolder = (entity.core.PyroFolderDB)obj;
					parentFolder.innerFolders.remove(sourceFolder);
					targetFolder.innerFolders.add(sourceFolder);
					sourceFolder.parent = targetFolder;
					
					parentFolder.persist();
					targetFolder.persist();
					sourceFolder.persist();
					
					sendProjectUpdate(targetFolder,securityContext);
					sendProjectUpdate(parentFolder,securityContext);
				}
				if(obj instanceof entity.core.PyroProjectDB) {
					final entity.core.PyroProjectDB parentFolder = (entity.core.PyroProjectDB)obj;
					parentFolder.innerFolders.remove(sourceFolder);
					targetFolder.innerFolders.add(sourceFolder);
					sourceFolder.parent = targetFolder;
					
					parentFolder.persist();
					targetFolder.persist();
					sourceFolder.persist();
					
					sendProjectUpdate(targetFolder,securityContext);
					sendProjectUpdate(parentFolder,securityContext);
				}
			}
		}
		return Response.ok().build();
	}
	
	private boolean isAscendantFolderOf(entity.core.PyroFolderDB parent, entity.core.PyroFolderDB possibleAcendant) {
		final java.util.Queue<entity.core.PyroFolderDB> queue = new java.util.ArrayDeque<>();
		queue.offer(parent);
		while (!queue.isEmpty()) {
			final entity.core.PyroFolderDB p = queue.poll();
			for (entity.core.PyroFolderDB f: p.innerFolders) {
				if (f.equals(possibleAcendant)) return true;
				queue.offer(f);
			}
		}
		return false;
	}
	
	public void sendProjectUpdate(PanacheEntity f,SecurityContext securityContext){
        if(f instanceof entity.core.PyroFolderDB) {
            sendProjectUpdate((entity.core.PyroFolderDB)f, securityContext);
        }
        if(f instanceof entity.core.PyroProjectDB) {
            sendProjectUpdate((entity.core.PyroProjectDB)f, securityContext);
        }
    }
	
	public void sendProjectUpdate(entity.core.PyroFolderDB folder,SecurityContext securityContext){
        final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);

        final entity.core.PyroProjectDB parent = getProject(folder);
        projectWebSocket.send(parent.id, WebSocketMessage.fromEntity(subject.id,PyroProjectStructure.fromEntity(parent,objectCache)));

    }

    public void sendProjectUpdate(entity.core.PyroProjectDB project,SecurityContext securityContext){
        final entity.core.PyroUserDB subject = entity.core.PyroUserDB.getCurrentUser(securityContext);

        projectWebSocket.send(project.id, WebSocketMessage.fromEntity(subject.id,PyroProjectStructure.fromEntity(project,objectCache)));

    }

    public void checkPermission(PanacheEntity peb,SecurityContext securityContext) {
        final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
		entity.core.PyroProjectDB project = null;
		if(peb instanceof entity.core.PyroFolderDB) {
	        project = getProject((entity.core.PyroFolderDB)peb);
		}
		else if(peb instanceof entity.core.PyroProjectDB) {
	        project = (entity.core.PyroProjectDB)peb;
		}
		// has relation to Organization as parent of the related project
		boolean isOwner = project.organization.owners.contains(user);
		boolean isMember = project.organization.members.contains(user);
		if(project != null && (isOwner || isMember)){
            return;
        }
        throw new WebApplicationException(Response.Status.FORBIDDEN);
    }

    public entity.core.PyroProjectDB getProject(entity.core.PyroFolderDB folder){
    	PyroFileContainerDB parent = folder.parent;
    	if(parent != null){
    		if(parent instanceof entity.core.PyroFolderDB)
    			return getProject((entity.core.PyroFolderDB) parent);
    		else if(parent instanceof entity.core.PyroProjectDB)
    			return (entity.core.PyroProjectDB) parent;
    	}
    	throw new WebApplicationException(Response.Status.EXPECTATION_FAILED);
    }
    
    public entity.core.PyroProjectDB getProject(entity.core.PyroFileContainerDB container) {
    	if(container instanceof entity.core.PyroFolderDB) {
    		return getProject((entity.core.PyroFolderDB) container);
    	} else if (container instanceof entity.core.PyroProjectDB) {
    		return (entity.core.PyroProjectDB) container;
    	}
    	throw new WebApplicationException(Response.Status.EXPECTATION_FAILED);
    }

    public PanacheEntity getParent(entity.core.PyroFolderDB f){
        return f.parent;
    }
    
    public PanacheEntity getParent(entity.core.PyroBinaryFileDB f){
        return f.parent;
    }
    
    public PanacheEntity getParent(entity.core.PyroURLFileDB f){
        return f.parent;
    }
    
    public PanacheEntity getParent(entity.core.PyroTextualFileDB f){
        return f.parent;
    }

    @javax.ws.rs.POST
    @javax.ws.rs.Path("update/folder/private")
    @javax.annotation.security.RolesAllowed("user")
    public Response updateFolder(@javax.ws.rs.core.Context SecurityContext securityContext, UpdatePyroFolder folder) {
        //find folder
        final Optional<entity.core.PyroFolderDB> pf = entity.core.PyroFolderDB.findByIdOptional(folder.getId());
        if(pf.isPresent()) {
            entity.core.PyroFolderDB f = pf.get();
            checkPermission(f,securityContext);
            f.name = folder.getname();
            f.persist();
            sendProjectUpdate(f,securityContext);
            return Response.ok(info.scce.pyro.core.rest.types.PyroFolder.fromEntity(f,objectCache)).build();
        }
        final Optional<entity.core.PyroProjectDB> pp = entity.core.PyroProjectDB.findByIdOptional(folder.getId());
        if(pp.isPresent()) {
            entity.core.PyroProjectDB p = pp.get();
            checkPermission(p,securityContext);
            p.name = folder.getname();
            p.persist();
            sendProjectUpdate(p,securityContext);
            return Response.ok(info.scce.pyro.core.rest.types.PyroProject.fromEntity(p,objectCache)).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

	@javax.ws.rs.GET
	@javax.ws.rs.Path("remove/folder/{id}/{parentId}/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response removeFolder(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id,@javax.ws.rs.PathParam("parentId") final long parentId) {
        // find user
        final entity.core.PyroUserDB user = entity.core.PyroUserDB.getCurrentUser(securityContext);
		
		//find parent
		final entity.core.PyroFolderDB folder = entity.core.PyroFolderDB.findById(id);
		checkPermission(folder,securityContext);
		
        final entity.core.PyroProjectDB pyroProject = getProject(folder);
		final Object parent = getParent(folder);
		
		if(parent instanceof entity.core.PyroFolderDB) {
			entity.core.PyroFolderDB parentFolder = (entity.core.PyroFolderDB) parent;
			deleteFolder(user, pyroProject, parentFolder, folder);
			sendProjectUpdate(parentFolder,securityContext);
		} else if(parent instanceof entity.core.PyroProjectDB) {
			entity.core.PyroProjectDB parentFolder = (entity.core.PyroProjectDB) parent;
			deleteFolder(user, pyroProject, parentFolder, folder);
			sendProjectUpdate(parentFolder,securityContext);
		} else {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		return Response.ok("OK").build();
	}
	
	public void deleteFolder(entity.core.PyroUserDB subject, entity.core.PyroProjectDB project, entity.core.PyroProjectDB parent, entity.core.PyroFolderDB pf) {
		deleteChildren(subject, project, pf);
		if(pf.getFiles().isEmpty() && pf.innerFolders.isEmpty()) {
			pf.parent = null;
			parent.innerFolders.remove(pf);
			pf.delete();
		}
		parent.persist();
	}
	
	public void deleteFolder(entity.core.PyroUserDB subject, entity.core.PyroProjectDB project, entity.core.PyroFolderDB parent, entity.core.PyroFolderDB pf) {
		deleteChildren(subject, project, pf);
		if(pf.getFiles().isEmpty() && pf.innerFolders.isEmpty()) {
			pf.parent = null;
			parent.innerFolders.remove(pf);
			pf.delete();
		}
		parent.persist();
	}
	
	public void deleteChildren(entity.core.PyroUserDB subject, entity.core.PyroProjectDB project, entity.core.PyroFolderDB pf) {
		removeFolders(subject, project, pf, pf.innerFolders);
		removeFiles(pf, pf.binaryFiles);
		removeFiles(pf, pf.textualFiles);
		removeFiles(pf, pf.urlFiles);
		pf.persist();
	}
	
	public void removeFolders(entity.core.PyroUserDB subject, entity.core.PyroProjectDB project, entity.core.PyroFolderDB pf, java.util.Collection<entity.core.PyroFolderDB> folders) {
		java.util.ArrayList<entity.core.PyroFolderDB> entities = new java.util.ArrayList(folders);
		java.util.Iterator<entity.core.PyroFolderDB> iter =  entities.iterator();
		while(iter.hasNext()) {
			entity.core.PyroFolderDB folder = iter.next();
			deleteFolder(subject, project, pf, folder);
			entities.remove(folder);
			iter =  entities.iterator();
		}
	}
	
	public <T extends io.quarkus.hibernate.orm.panache.PanacheEntity> void removeFiles(entity.core.PyroFolderDB pf, java.util.Collection<T> files) {
		java.util.ArrayList<T> entities = new java.util.ArrayList(files);
		java.util.Iterator<T> iter =  entities.iterator();
		while(iter.hasNext()) {
			T file = iter.next();
			file.delete();
			pf.removeFile(file, true);
			entities.remove(file);
			iter =  entities.iterator();
		}
	}
	
	public <T> void removeGraphModels(entity.core.PyroUserDB subject, entity.core.PyroProjectDB project, entity.core.PyroFolderDB pf, java.util.Collection<T> files) {
		java.util.ArrayList<T> entities = new java.util.ArrayList(files);
		java.util.Iterator<T> iter =  entities.iterator();
		while(iter.hasNext()) {
			T file = iter.next();
			entities.remove(file);
			iter =  entities.iterator();
		}
	}
}

