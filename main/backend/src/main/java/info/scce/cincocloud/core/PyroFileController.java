package info.scce.cincocloud.core;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import info.scce.cincocloud.core.rest.types.CreatePyroBinaryFile;
import info.scce.cincocloud.core.rest.types.CreatePyroBlobFile;
import info.scce.cincocloud.core.rest.types.CreatePyroTextualFile;
import info.scce.cincocloud.core.rest.types.CreatePyroURLFile;
import info.scce.cincocloud.core.rest.types.UpdatePyroFile;
import info.scce.cincocloud.db.BaseFileDB;
import info.scce.cincocloud.db.PyroBinaryFileDB;
import info.scce.cincocloud.db.PyroFileContainerDB;
import info.scce.cincocloud.db.PyroFolderDB;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.db.PyroTextualFileDB;
import info.scce.cincocloud.db.PyroURLFileDB;

@javax.transaction.Transactional
@javax.ws.rs.Path("/pyrofile")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
public class PyroFileController {

    @javax.inject.Inject
    info.scce.cincocloud.rest.ObjectCache objectCache;

    @javax.inject.Inject
    GraphModelController graphModelController;

    @javax.inject.Inject
    FileController fileController;

    @javax.ws.rs.POST
    @javax.ws.rs.Path("create/binary/private")
    @javax.annotation.security.RolesAllowed("user")
    public Response createBinary(@javax.ws.rs.core.Context SecurityContext securityContext, CreatePyroBinaryFile newFile) {
        PyroBinaryFileDB file = checkAddPersist(
                newFile.getparentId(),
                securityContext,
                () -> {
                    // create file
                    final PyroBinaryFileDB newPBF = new PyroBinaryFileDB();
                    final String filename = newFile.getfile().getFileName();
                    final int dot = filename.lastIndexOf(".");
                    newPBF.filename = dot > -1 ? filename.substring(0, dot) : filename;
                    newPBF.extension = dot > -1 ? filename.substring(dot + 1) : null;
                    newPBF.file = BaseFileDB.findById(newFile.getfile().getId());
                    return newPBF;
                }
        );
        if (file == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(info.scce.cincocloud.core.rest.types.PyroBinaryFile.fromEntity(file, objectCache)).build();
        }
    }

    @javax.ws.rs.POST
    @javax.ws.rs.Path("create/blob/private")
    @javax.annotation.security.RolesAllowed("user")
    public Response createBlob(@javax.ws.rs.core.Context SecurityContext securityContext, CreatePyroBlobFile newFile) {
        PyroBinaryFileDB file = checkAddPersist(
                newFile.getparentId(),
                securityContext,
                () -> {
                    // create file
                    final PyroBinaryFileDB newPBF = new PyroBinaryFileDB();
                    final String filename = "PyroBinaryFile_" + newFile.getname();
                    final int dot = filename.lastIndexOf(".");
                    newPBF.filename = dot > -1 ? filename.substring(0, dot) : filename;
                    newPBF.extension = dot > -1 ? filename.substring(dot + 1) : null;
                    // create from stream
                    final BaseFileDB reference = this.fileController
                            .storeFile(
                                    newFile.getname(),
                                    new ByteArrayInputStream(
                                            newFile.getfile().getBytes(StandardCharsets.UTF_8)
                                    )
                            );
                    newPBF.file = reference;
                    return newPBF;
                }
        );
        if (file == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(info.scce.cincocloud.core.rest.types.PyroBinaryFile.fromEntity(file, objectCache)).build();
        }
    }

    @javax.ws.rs.POST
    @javax.ws.rs.Path("create/textual/private")
    @javax.annotation.security.RolesAllowed("user")
    public Response createTextual(@javax.ws.rs.core.Context SecurityContext securityContext, CreatePyroTextualFile newFile) {
        PyroTextualFileDB file = checkAddPersist(
                newFile.getparentId(),
                securityContext,
                () -> {
                    // create file
                    final PyroTextualFileDB newPBF = new PyroTextualFileDB();
                    newPBF.filename = newFile.getfilename();
                    newPBF.extension = newFile.getextension();
                    newPBF.content = "";
                    return newPBF;
                }
        );
        if (file == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(info.scce.cincocloud.core.rest.types.PyroTextualFile.fromEntity(file, objectCache)).build();
        }
    }

    @javax.ws.rs.POST
    @javax.ws.rs.Path("create/url/private")
    @javax.annotation.security.RolesAllowed("user")
    public Response createUrl(@javax.ws.rs.core.Context SecurityContext securityContext, CreatePyroURLFile newFile) {
        PyroURLFileDB file = checkAddPersist(
                newFile.getparentId(),
                securityContext,
                () -> {
                    // create file
                    final PyroURLFileDB newPBF = new PyroURLFileDB();
                    newPBF.filename = newFile.getfilename();
                    newPBF.extension = newFile.getextension();
                    newPBF.url = newFile.geturl();
                    return newPBF;
                }
        );
        if (file == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(info.scce.cincocloud.core.rest.types.PyroURLFile.fromEntity(file, objectCache)).build();
        }
    }

    @javax.ws.rs.POST
    @javax.ws.rs.Path("update/file/private")
    @javax.annotation.security.RolesAllowed("user")
    public Response updateFile(@javax.ws.rs.core.Context SecurityContext securityContext, UpdatePyroFile file) {

        io.quarkus.hibernate.orm.panache.PanacheEntity pf = null;
        io.quarkus.hibernate.orm.panache.PanacheEntity update = null;

        final Optional<PyroBinaryFileDB> optPP = PyroBinaryFileDB.findByIdOptional(file.getId());
        if (optPP.isPresent()) {
            PyroBinaryFileDB f = optPP.get();
            f.filename = file.getfilename();
            update = f;
            pf = graphModelController.getParent(f);
        }
        final Optional<PyroURLFileDB> optPf = PyroURLFileDB.findByIdOptional(file.getId());
        if (optPf.isPresent()) {
            PyroURLFileDB f = optPf.get();
            f.filename = file.getfilename();
            update = f;
            pf = graphModelController.getParent(f);
        }
        final Optional<PyroTextualFileDB> optPT = PyroTextualFileDB.findByIdOptional(file.getId());
        if (optPT.isPresent()) {
            PyroTextualFileDB f = optPT.get();
            f.filename = file.getfilename();
            update = f;
            pf = graphModelController.getParent(f);
        }

        if (pf instanceof PyroFolderDB) {
            graphModelController.checkPermission(pf, securityContext);
            update.persist();
            graphModelController.sendProjectUpdate((PyroFolderDB) pf, securityContext);
        } else if (pf instanceof PyroProjectDB) {
            graphModelController.checkPermission(pf, securityContext);
            update.persist();
            graphModelController.sendProjectUpdate((PyroProjectDB) pf, securityContext);
        }

        return Response.ok(file).build();
    }

    @javax.ws.rs.GET
    @javax.ws.rs.Path("read/projectresource/{id}/{path:.+}")
    @javax.annotation.security.RolesAllowed("user")
    public Response readFile(@javax.ws.rs.core.Context SecurityContext securityContext, @Context UriInfo ui, @javax.ws.rs.PathParam("id") final long id, @javax.ws.rs.PathParam("path") final String path,
                             @javax.ws.rs.PathParam("ticket") final String ticket) {
        //find parent
        final PyroProjectDB pf = PyroProjectDB.findById(id);
        if (pf == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        //find folder
        String[] folders = path.split("/");
        Object current = pf;
        for (int i = 0; i < folders.length; i++) {
            //is last part, e.g. the file
            final int fin_i = i;
            if (i >= (folders.length - 1)) {
                if (current instanceof PyroFolderDB) {
                    PyroBinaryFileDB ppf = ((PyroFolderDB) current).binaryFiles.stream().filter(n -> (n.filename + (n.extension == null ? "" : "." + n.extension)).equals(folders[fin_i])).findAny().orElse(null);
                    if (ppf == null) {
                        PyroTextualFileDB ptf = ((PyroFolderDB) current).textualFiles.stream().filter(n -> (n.filename + (n.extension == null ? "" : "." + n.extension)).equals(folders[fin_i])).findAny().orElse(null);
                        if (ptf == null) {
                            PyroURLFileDB puf = ((PyroFolderDB) current).urlFiles.stream().filter(n -> (n.filename + (n.extension == null ? "" : "." + n.extension)).equals(folders[fin_i])).findAny().orElse(null);
                            if (puf == null) {
                                return Response.status(Response.Status.NOT_FOUND).build();
                            } else {
                                return readFileInner(puf, ui);
                            }
                        } else {
                            return readFileInner(ptf, ui);
                        }
                    } else {
                        return readFileInner(ppf, ui);
                    }
                } else if (current instanceof PyroProjectDB) {
                    PyroBinaryFileDB ppf = ((PyroProjectDB) current).binaryFiles.stream().filter(n -> (n.filename + (n.extension == null ? "" : "." + n.extension)).equals(folders[fin_i])).findAny().orElse(null);
                    if (ppf == null) {
                        PyroTextualFileDB ptf = ((PyroProjectDB) current).textualFiles.stream().filter(n -> (n.filename + (n.extension == null ? "" : "." + n.extension)).equals(folders[fin_i])).findAny().orElse(null);
                        if (ptf == null) {
                            PyroURLFileDB puf = ((PyroProjectDB) current).urlFiles.stream().filter(n -> (n.filename + (n.extension == null ? "" : "." + n.extension)).equals(folders[fin_i])).findAny().orElse(null);
                            if (puf == null) {
                                return Response.status(Response.Status.NOT_FOUND).build();
                            } else {
                                return readFileInner(puf, ui);
                            }
                        } else {
                            return readFileInner(ptf, ui);
                        }
                    } else {
                        return readFileInner(ppf, ui);
                    }
                }

            } else {
                if (current instanceof PyroProjectDB) {

                    Optional<PyroFolderDB> optFolder = ((PyroProjectDB) current).innerFolders.stream().filter(n -> n.name.equals(folders[fin_i])).findAny();
                    if (optFolder.isPresent()) {
                        current = optFolder.get();
                    } else {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                } else if (current instanceof PyroFolderDB) {
                    Optional<PyroFolderDB> optFolder = ((PyroFolderDB) current).innerFolders.stream().filter(n -> n.name.equals(folders[fin_i])).findAny();
                    if (optFolder.isPresent()) {
                        current = optFolder.get();
                    } else {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                }
            }
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @javax.ws.rs.POST
    @javax.ws.rs.Path("move/{id}/{targetId}/private")
    @javax.annotation.security.RolesAllowed("user")
    public Response moveFile(
            @javax.ws.rs.core.Context SecurityContext securityContext,
            @javax.ws.rs.PathParam("id") final long id,
            @javax.ws.rs.PathParam("targetId") final long targetId
    ) {
        PyroFileContainerDB target = PyroFileContainerDB.findById(targetId);
        checkPermission(target, securityContext);

        {
            final PyroTextualFileDB file = PyroTextualFileDB.findById(id);
            if (file != null) {
                final PanacheEntity source = graphModelController.getParent(file);
                if (source instanceof PyroFolderDB) {
                    ((PyroFolderDB) source).textualFiles.remove(file);
                } else if (source instanceof PyroProjectDB) {
                    ((PyroProjectDB) source).textualFiles.remove(file);
                }
                if (target instanceof PyroFolderDB) {
                    if (((PyroFolderDB) target).textualFiles.stream().filter(n -> n.filename.equals(file.filename)).findAny().isPresent()) {
                        return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
                    }
                    ((PyroFolderDB) target).textualFiles.add(file);
                    file.parent = target;
                } else if (target instanceof PyroProjectDB) {
                    if (((PyroProjectDB) target).textualFiles.stream().filter(n -> n.filename.equals(file.filename)).findAny().isPresent()) {
                        return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
                    }
                    ((PyroProjectDB) target).textualFiles.add(file);
                    file.parent = target;
                }
                source.persist();
                target.persist();
                file.persist();
                graphModelController.sendProjectUpdate(target, securityContext);
                graphModelController.sendProjectUpdate(source, securityContext);

                return Response.ok().build();
            }
        }
        {
            final PyroBinaryFileDB file = PyroBinaryFileDB.findById(id);
            if (file != null) {
                final PanacheEntity source = graphModelController.getParent(file);
                if (source instanceof PyroFolderDB) {
                    ((PyroFolderDB) source).binaryFiles.remove(file);
                } else if (source instanceof PyroProjectDB) {
                    ((PyroProjectDB) source).binaryFiles.remove(file);
                }
                if (target instanceof PyroFolderDB) {
                    if (((PyroFolderDB) target).binaryFiles.stream().filter(n -> n.filename.equals(file.filename)).findAny().isPresent()) {
                        return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
                    }
                    ((PyroFolderDB) target).binaryFiles.add(file);
                    file.parent = target;
                } else if (target instanceof PyroProjectDB) {
                    if (((PyroProjectDB) target).binaryFiles.stream().filter(n -> n.filename.equals(file.filename)).findAny().isPresent()) {
                        return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
                    }
                    ((PyroProjectDB) target).binaryFiles.add(file);
                    file.parent = target;
                }
                source.persist();
                target.persist();
                file.persist();
                graphModelController.sendProjectUpdate(target, securityContext);
                graphModelController.sendProjectUpdate(source, securityContext);

                return Response.ok().build();
            }
        }
        {
            final PyroURLFileDB file = PyroURLFileDB.findById(id);
            if (file != null) {
                final PanacheEntity source = graphModelController.getParent(file);
                if (source instanceof PyroFolderDB) {
                    ((PyroFolderDB) source).urlFiles.remove(file);
                } else if (source instanceof PyroProjectDB) {
                    ((PyroProjectDB) source).urlFiles.remove(file);
                }
                if (target instanceof PyroFolderDB) {
                    if (((PyroFolderDB) target).urlFiles.stream().filter(n -> n.filename.equals(file.filename)).findAny().isPresent()) {
                        return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
                    }
                    ((PyroFolderDB) target).urlFiles.add(file);
                    file.parent = target;
                } else if (target instanceof PyroProjectDB) {
                    if (((PyroProjectDB) target).urlFiles.stream().filter(n -> n.filename.equals(file.filename)).findAny().isPresent()) {
                        return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
                    }
                    ((PyroProjectDB) target).urlFiles.add(file);
                    file.parent = target;
                }
                source.persist();
                target.persist();
                file.persist();
                graphModelController.sendProjectUpdate(target, securityContext);
                graphModelController.sendProjectUpdate(source, securityContext);

                return Response.ok().build();
            }
        }

        return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }

    /*
     * NOTE: GraphModelFiles are not handled here, since they need
     * special permissions to be deleted. Such functionality is handled
     * by their typed Controller.
     */
    @javax.ws.rs.GET
    @javax.ws.rs.Path("remove/{id}/{parentId}/private")
    @javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    @org.jboss.resteasy.annotations.GZIP
    public Response removeFile(
            @javax.ws.rs.core.Context SecurityContext securityContext,
            @javax.ws.rs.PathParam("id") final long id,
            @javax.ws.rs.PathParam("parentId") final long parentId
    ) {
        PyroFolderDB folder = null;
        PyroProjectDB project = null;
        PanacheEntity file = null;

        //find parent
        folder = PyroFolderDB.findById(parentId);
        if (folder == null) {
            project = PyroProjectDB.findById(parentId);
            if (project == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Parent not found").build();
            }
        }

        // security
        graphModelController.checkPermission(folder != null ? folder : project, securityContext);

        // find file
        file = PyroBinaryFileDB.findById(id);
        if (file == null) {
            file = PyroURLFileDB.findById(id);
            if (file == null) {
                file = PyroTextualFileDB.findById(id);
                if (file == null) { // file couldn't be found
                    return Response.status(Response.Status.BAD_REQUEST).entity("File not found").build();
                }
            }
        }

        // delete + persist
        if (folder != null) {
            folder.removeFile(file, true);
            folder.persist();
        } else {
            project.removeFile(file, true);
            project.persist();
        }

        graphModelController.sendProjectUpdate(folder != null ? folder : project, securityContext);
        return Response.ok().build();
    }

    public <T extends PanacheEntity> T checkAddPersist(
            long folderId,
            SecurityContext securityContext,
            java.util.concurrent.Callable<T> foo
    ) {
        //find parent
        io.quarkus.hibernate.orm.panache.PanacheEntity folder = getFileContainer(folderId);
        if (folder == null) {
            return null;
        }
        // permission check
        checkPermission(folder, securityContext);

        T file = null;
        try {
            file = foo.call();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // persist and chain
        addAndPersist(file, folder, securityContext);
        return file;
    }

    public io.quarkus.hibernate.orm.panache.PanacheEntity getFileContainer(long folderId) {
        io.quarkus.hibernate.orm.panache.PanacheEntity folder = PyroFolderDB.findById(folderId);
        if (folder != null) {
            return folder;
        }
        io.quarkus.hibernate.orm.panache.PanacheEntity project = PyroProjectDB.findById(folderId);
        return project;
    }

    public void checkPermission(io.quarkus.hibernate.orm.panache.PanacheEntity entity, SecurityContext securityContext) {
        graphModelController.checkPermission(entity, securityContext);
    }

    public void checkPermission(long folderId, SecurityContext securityContext) {
        //find parent
        io.quarkus.hibernate.orm.panache.PanacheEntity folder = getFileContainer(folderId);
        // permission check
        checkPermission(folder, securityContext);
    }

    public void addAndPersist(io.quarkus.hibernate.orm.panache.PanacheEntity f, io.quarkus.hibernate.orm.panache.PanacheEntity fC, SecurityContext securityContext) {
        if (fC instanceof PyroFolderDB) {
            PyroFolderDB folder = (PyroFolderDB) fC;
            if (f instanceof PyroBinaryFileDB) {
                PyroBinaryFileDB file = (PyroBinaryFileDB) f;
                file.parent = folder;
                file.persist();
                folder.binaryFiles.add(file);
                folder.persist();
            } else if (f instanceof PyroURLFileDB) {
                PyroURLFileDB file = (PyroURLFileDB) f;
                file.parent = folder;
                file.persist();
                folder.urlFiles.add(file);
                folder.persist();
            } else if (f instanceof PyroTextualFileDB) {
                PyroTextualFileDB file = (PyroTextualFileDB) f;
                file.parent = folder;
                file.persist();
                folder.textualFiles.add(file);
                folder.persist();
            }

        } else if (fC instanceof PyroProjectDB) {
            PyroProjectDB folder = (PyroProjectDB) fC;
            if (f instanceof PyroBinaryFileDB) {
                PyroBinaryFileDB file = (PyroBinaryFileDB) f;
                file.parent = folder;
                file.persist();
                folder.binaryFiles.add(file);
                folder.persist();
            } else if (f instanceof PyroURLFileDB) {
                PyroURLFileDB file = (PyroURLFileDB) f;
                file.parent = folder;
                file.persist();
                folder.urlFiles.add(file);
                folder.persist();
            } else if (f instanceof PyroTextualFileDB) {
                PyroTextualFileDB file = (PyroTextualFileDB) f;
                file.parent = folder;
                file.persist();
                folder.textualFiles.add(file);
                folder.persist();
            }
        }
        graphModelController.sendProjectUpdate(fC, securityContext);
    }

    private Response readFileInner(io.quarkus.hibernate.orm.panache.PanacheEntity pe, UriInfo ui) {
        final byte[] result;
        String fileName = getFileName(pe);
        InputStream input1 = getInputStream(pe, ui);
        InputStream input2 = getInputStream(pe, ui);

        if (fileName == null || input1 == null || input2 == null) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        try {
            result = org.apache.commons.io.IOUtils.toByteArray(input1);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }

        return buildResponse(fileName, input2, result);
    }

    private InputStream getInputStream(io.quarkus.hibernate.orm.panache.PanacheEntity f, UriInfo ui) {
        if (f instanceof PyroBinaryFileDB) {
            PyroBinaryFileDB file = (PyroBinaryFileDB) f;
            BaseFileDB reference = file.file;
            InputStream stream = this.fileController.loadFile(reference);
            java.io.BufferedInputStream bstream = new java.io.BufferedInputStream(stream);
            return bstream;
        } else if (f instanceof PyroURLFileDB) {
            PyroURLFileDB file = (PyroURLFileDB) f;
            String url = file.url;
            if (!url.startsWith("http")) {
                String uri = ui.getBaseUri().toString();
                url = uri.substring(0, uri.lastIndexOf("/rest")) + "/" + url;
            }
            try {
                InputStream stream = new URL(url).openStream();
                java.io.BufferedInputStream bstream = new java.io.BufferedInputStream(stream);
                return bstream;
            } catch (java.net.MalformedURLException e) {
                throw new WebApplicationException(e);
            } catch (java.io.IOException e) {
                throw new WebApplicationException(e);
            }
        } else if (f instanceof PyroTextualFileDB) {
            PyroTextualFileDB file = (PyroTextualFileDB) f;
            return new ByteArrayInputStream(file.content.getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }

    private String getFileName(io.quarkus.hibernate.orm.panache.PanacheEntity f) {
        if (f instanceof PyroBinaryFileDB) {
            PyroBinaryFileDB file = (PyroBinaryFileDB) f;
            return file.extension != null ?
                    file.filename + "." + file.extension
                    : file.filename;
        } else if (f instanceof PyroURLFileDB) {
            PyroURLFileDB file = (PyroURLFileDB) f;
            return file.extension != null ?
                    file.filename + "." + file.extension
                    : file.filename;
        } else if (f instanceof PyroTextualFileDB) {
            PyroTextualFileDB file = (PyroTextualFileDB) f;
            return file.extension != null ?
                    file.filename + "." + file.extension
                    : file.filename;
        }
        return null;
    }

    private MediaType getMime(String fileName, InputStream content) {
        TikaConfig config = TikaConfig.getDefaultConfig();
        Metadata md = new Metadata();
        md.set(Metadata.RESOURCE_NAME_KEY, fileName);
        try {
            return config.getMimeRepository().detect(content, md);
        } catch (java.io.IOException e) {
            throw new WebApplicationException(e);
        }
    }

    private CacheControl getCacheControl() {
        CacheControl cc = new CacheControl();
        cc.setMustRevalidate(true);
        cc.setNoStore(true);
        cc.setNoCache(true);
        return cc;
    }

    private Response buildResponse(String fileName, InputStream inputStream, byte[] result) {
        String mime = getMime(fileName, inputStream).toString();
        CacheControl cc = getCacheControl();
        return Response
                .ok(result, mime)
                .cacheControl(cc)
                .build();
    }
}
