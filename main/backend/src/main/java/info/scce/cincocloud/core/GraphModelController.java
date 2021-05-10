package info.scce.cincocloud.core;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.util.Optional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import info.scce.cincocloud.core.rest.types.CreatePyroFolder;
import info.scce.cincocloud.core.rest.types.PyroProjectStructure;
import info.scce.cincocloud.core.rest.types.UpdatePyroFolder;
import info.scce.cincocloud.db.PyroBinaryFileDB;
import info.scce.cincocloud.db.PyroFileContainerDB;
import info.scce.cincocloud.db.PyroFolderDB;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.db.PyroTextualFileDB;
import info.scce.cincocloud.db.PyroURLFileDB;
import info.scce.cincocloud.db.PyroUserDB;
import info.scce.cincocloud.sync.ProjectWebSocket;
import info.scce.cincocloud.sync.WebSocketMessage;

@javax.transaction.Transactional
@javax.ws.rs.Path("/graph")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
public class GraphModelController {

    @javax.inject.Inject
    info.scce.cincocloud.rest.ObjectCache objectCache;

    @javax.inject.Inject
    ProjectWebSocket projectWebSocket;

    @javax.ws.rs.POST
    @javax.ws.rs.Path("create/folder/private")
    @javax.annotation.security.RolesAllowed("user")
    public Response createFolder(@javax.ws.rs.core.Context SecurityContext securityContext, CreatePyroFolder newFolder) {
        //find parent
        final PyroFolderDB pf = PyroFolderDB.findById(newFolder.getparentId());
        if (pf != null) {
            checkPermission(pf, securityContext);
            final PyroFolderDB newPF = new PyroFolderDB();
            newPF.name = newFolder.getname();
            newPF.parent = pf;
            newPF.persist();
            pf.innerFolders.add(newPF);
            pf.persist();
            sendProjectUpdate(pf, securityContext);
            return Response.ok(info.scce.cincocloud.core.rest.types.PyroFolder.fromEntity(newPF, objectCache)).build();
        }
        final PyroProjectDB pp = PyroProjectDB.findById(newFolder.getparentId());
        if (pp != null) {
            checkPermission(pp, securityContext);
            final PyroFolderDB newPF = new PyroFolderDB();
            newPF.name = newFolder.getname();
            newPF.parent = pp;
            newPF.persist();
            pp.innerFolders.add(newPF);
            pp.persist();
            sendProjectUpdate(pp, securityContext);
            return Response.ok(info.scce.cincocloud.core.rest.types.PyroFolder.fromEntity(newPF, objectCache)).build();
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

        final PyroFolderDB sourceFolder = PyroFolderDB.findById(id);
        if (sourceFolder != null) {
            checkPermission(sourceFolder, securityContext);
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Source Folder not found").build();
        }

        PyroFileContainerDB pfc = PyroFileContainerDB.findById(targetId);
        if (pfc != null) {
            checkPermission(pfc, securityContext);

            if (pfc instanceof PyroFolderDB) {
                PyroFolderDB targetFolder = (PyroFolderDB) pfc;
                for (final PyroFolderDB f : targetFolder.innerFolders) {
                    if (sourceFolder.name.equals(f.name)) {
                        return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
                    }
                }

                if (isAscendantFolderOf(sourceFolder, targetFolder)) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("Cannot move folder to ascendant").build();
                }

                Object obj = getParent(sourceFolder);
                if (obj instanceof PyroFolderDB) {
                    final PyroFolderDB parentFolder = (PyroFolderDB) obj;
                    parentFolder.innerFolders.remove(sourceFolder);
                    targetFolder.innerFolders.add(sourceFolder);
                    sourceFolder.parent = targetFolder;

                    parentFolder.persist();
                    targetFolder.persist();
                    sourceFolder.persist();

                    sendProjectUpdate(targetFolder, securityContext);
                    sendProjectUpdate(parentFolder, securityContext);
                }
                if (obj instanceof PyroProjectDB) {
                    final PyroProjectDB parentFolder = (PyroProjectDB) obj;
                    parentFolder.innerFolders.remove(sourceFolder);
                    targetFolder.innerFolders.add(sourceFolder);
                    sourceFolder.parent = targetFolder;

                    parentFolder.persist();
                    targetFolder.persist();
                    sourceFolder.persist();

                    sendProjectUpdate(targetFolder, securityContext);
                    sendProjectUpdate(parentFolder, securityContext);
                }
            } else if (pfc instanceof PyroProjectDB) {
                PyroProjectDB targetFolder = (PyroProjectDB) pfc;
                for (final PyroFolderDB f : targetFolder.innerFolders) {
                    if (sourceFolder.name.equals(f.name)) {
                        return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
                    }
                }

                Object obj = getParent(sourceFolder);
                if (obj instanceof PyroFolderDB) {
                    final PyroFolderDB parentFolder = (PyroFolderDB) obj;
                    parentFolder.innerFolders.remove(sourceFolder);
                    targetFolder.innerFolders.add(sourceFolder);
                    sourceFolder.parent = targetFolder;

                    parentFolder.persist();
                    targetFolder.persist();
                    sourceFolder.persist();

                    sendProjectUpdate(targetFolder, securityContext);
                    sendProjectUpdate(parentFolder, securityContext);
                }
                if (obj instanceof PyroProjectDB) {
                    final PyroProjectDB parentFolder = (PyroProjectDB) obj;
                    parentFolder.innerFolders.remove(sourceFolder);
                    targetFolder.innerFolders.add(sourceFolder);
                    sourceFolder.parent = targetFolder;

                    parentFolder.persist();
                    targetFolder.persist();
                    sourceFolder.persist();

                    sendProjectUpdate(targetFolder, securityContext);
                    sendProjectUpdate(parentFolder, securityContext);
                }
            }
        }
        return Response.ok().build();
    }

    private boolean isAscendantFolderOf(PyroFolderDB parent, PyroFolderDB possibleAcendant) {
        final java.util.Queue<PyroFolderDB> queue = new java.util.ArrayDeque<>();
        queue.offer(parent);
        while (!queue.isEmpty()) {
            final PyroFolderDB p = queue.poll();
            for (PyroFolderDB f : p.innerFolders) {
                if (f.equals(possibleAcendant)) {
                    return true;
                }
                queue.offer(f);
            }
        }
        return false;
    }

    public void sendProjectUpdate(PanacheEntity f, SecurityContext securityContext) {
        if (f instanceof PyroFolderDB) {
            sendProjectUpdate((PyroFolderDB) f, securityContext);
        }
        if (f instanceof PyroProjectDB) {
            sendProjectUpdate((PyroProjectDB) f, securityContext);
        }
    }

    public void sendProjectUpdate(PyroFolderDB folder, SecurityContext securityContext) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        final PyroProjectDB parent = getProject(folder);
        projectWebSocket.send(parent.id, WebSocketMessage.fromEntity(subject.id, PyroProjectStructure.fromEntity(parent, objectCache)));

    }

    public void sendProjectUpdate(PyroProjectDB project, SecurityContext securityContext) {
        final PyroUserDB subject = PyroUserDB.getCurrentUser(securityContext);

        projectWebSocket.send(project.id, WebSocketMessage.fromEntity(subject.id, PyroProjectStructure.fromEntity(project, objectCache)));

    }

    public void checkPermission(PanacheEntity peb, SecurityContext securityContext) {
        final PyroUserDB user = PyroUserDB.getCurrentUser(securityContext);
        PyroProjectDB project = null;
        if (peb instanceof PyroFolderDB) {
            project = getProject((PyroFolderDB) peb);
        } else if (peb instanceof PyroProjectDB) {
            project = (PyroProjectDB) peb;
        }
        // has relation to Organization as parent of the related project
        boolean isOwner = project.organization.owners.contains(user);
        boolean isMember = project.organization.members.contains(user);
        if (project != null && (isOwner || isMember)) {
            return;
        }
        throw new WebApplicationException(Response.Status.FORBIDDEN);
    }

    public PyroProjectDB getProject(PyroFolderDB folder) {
        PyroFileContainerDB parent = folder.parent;
        if (parent != null) {
            if (parent instanceof PyroFolderDB) {
                return getProject((PyroFolderDB) parent);
            } else if (parent instanceof PyroProjectDB) {
                return (PyroProjectDB) parent;
            }
        }
        throw new WebApplicationException(Response.Status.EXPECTATION_FAILED);
    }

    public PyroProjectDB getProject(PyroFileContainerDB container) {
        if (container instanceof PyroFolderDB) {
            return getProject((PyroFolderDB) container);
        } else if (container instanceof PyroProjectDB) {
            return (PyroProjectDB) container;
        }
        throw new WebApplicationException(Response.Status.EXPECTATION_FAILED);
    }

    public PanacheEntity getParent(PyroFolderDB f) {
        return f.parent;
    }

    public PanacheEntity getParent(PyroBinaryFileDB f) {
        return f.parent;
    }

    public PanacheEntity getParent(PyroURLFileDB f) {
        return f.parent;
    }

    public PanacheEntity getParent(PyroTextualFileDB f) {
        return f.parent;
    }

    @javax.ws.rs.POST
    @javax.ws.rs.Path("update/folder/private")
    @javax.annotation.security.RolesAllowed("user")
    public Response updateFolder(@javax.ws.rs.core.Context SecurityContext securityContext, UpdatePyroFolder folder) {
        //find folder
        final Optional<PyroFolderDB> pf = PyroFolderDB.findByIdOptional(folder.getId());
        if (pf.isPresent()) {
            PyroFolderDB f = pf.get();
            checkPermission(f, securityContext);
            f.name = folder.getname();
            f.persist();
            sendProjectUpdate(f, securityContext);
            return Response.ok(info.scce.cincocloud.core.rest.types.PyroFolder.fromEntity(f, objectCache)).build();
        }
        final Optional<PyroProjectDB> pp = PyroProjectDB.findByIdOptional(folder.getId());
        if (pp.isPresent()) {
            PyroProjectDB p = pp.get();
            checkPermission(p, securityContext);
            p.name = folder.getname();
            p.persist();
            sendProjectUpdate(p, securityContext);
            return Response.ok(info.scce.cincocloud.core.rest.types.PyroProject.fromEntity(p, objectCache)).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @javax.ws.rs.GET
    @javax.ws.rs.Path("remove/folder/{id}/{parentId}/private")
    @javax.annotation.security.RolesAllowed("user")
    public Response removeFolder(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id, @javax.ws.rs.PathParam("parentId") final long parentId) {
        // find user
        final PyroUserDB user = PyroUserDB.getCurrentUser(securityContext);

        //find parent
        final PyroFolderDB folder = PyroFolderDB.findById(id);
        checkPermission(folder, securityContext);

        final PyroProjectDB pyroProject = getProject(folder);
        final Object parent = getParent(folder);

        if (parent instanceof PyroFolderDB) {
            PyroFolderDB parentFolder = (PyroFolderDB) parent;
            deleteFolder(user, pyroProject, parentFolder, folder);
            sendProjectUpdate(parentFolder, securityContext);
        } else if (parent instanceof PyroProjectDB) {
            PyroProjectDB parentFolder = (PyroProjectDB) parent;
            deleteFolder(user, pyroProject, parentFolder, folder);
            sendProjectUpdate(parentFolder, securityContext);
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok("OK").build();
    }

    public void deleteFolder(PyroUserDB subject, PyroProjectDB project, PyroProjectDB parent, PyroFolderDB pf) {
        deleteChildren(subject, project, pf);
        if (pf.getFiles().isEmpty() && pf.innerFolders.isEmpty()) {
            pf.parent = null;
            parent.innerFolders.remove(pf);
            pf.delete();
        }
        parent.persist();
    }

    public void deleteFolder(PyroUserDB subject, PyroProjectDB project, PyroFolderDB parent, PyroFolderDB pf) {
        deleteChildren(subject, project, pf);
        if (pf.getFiles().isEmpty() && pf.innerFolders.isEmpty()) {
            pf.parent = null;
            parent.innerFolders.remove(pf);
            pf.delete();
        }
        parent.persist();
    }

    public void deleteChildren(PyroUserDB subject, PyroProjectDB project, PyroFolderDB pf) {
        removeFolders(subject, project, pf, pf.innerFolders);
        removeFiles(pf, pf.binaryFiles);
        removeFiles(pf, pf.textualFiles);
        removeFiles(pf, pf.urlFiles);
        pf.persist();
    }

    public void removeFolders(PyroUserDB subject, PyroProjectDB project, PyroFolderDB pf, java.util.Collection<PyroFolderDB> folders) {
        java.util.ArrayList<PyroFolderDB> entities = new java.util.ArrayList(folders);
        java.util.Iterator<PyroFolderDB> iter = entities.iterator();
        while (iter.hasNext()) {
            PyroFolderDB folder = iter.next();
            deleteFolder(subject, project, pf, folder);
            entities.remove(folder);
            iter = entities.iterator();
        }
    }

    public <T extends io.quarkus.hibernate.orm.panache.PanacheEntity> void removeFiles(PyroFolderDB pf, java.util.Collection<T> files) {
        java.util.ArrayList<T> entities = new java.util.ArrayList(files);
        java.util.Iterator<T> iter = entities.iterator();
        while (iter.hasNext()) {
            T file = iter.next();
            file.delete();
            pf.removeFile(file, true);
            entities.remove(file);
            iter = entities.iterator();
        }
    }

    public <T> void removeGraphModels(PyroUserDB subject, PyroProjectDB project, PyroFolderDB pf, java.util.Collection<T> files) {
        java.util.ArrayList<T> entities = new java.util.ArrayList(files);
        java.util.Iterator<T> iter = entities.iterator();
        while (iter.hasNext()) {
            T file = iter.next();
            entities.remove(file);
            iter = entities.iterator();
        }
    }
}

