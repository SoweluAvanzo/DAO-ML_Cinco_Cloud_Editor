package info.scce.pyro.core;

import info.scce.pyro.core.rest.types.*;
import info.scce.pyro.sync.ticket.TicketRegistrationHandler;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import entity.core.PyroProjectDB;
import entity.core.PyroFolderDB;
import entity.core.PyroBinaryFileDB;
import entity.core.PyroURLFileDB;
import entity.core.PyroTextualFileDB;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.ws.rs.core.SecurityContext;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import javax.ws.rs.core.CacheControl;

@javax.transaction.Transactional
@javax.ws.rs.Path("/pyrofile")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
public class PyroFileController {

	@javax.inject.Inject
	info.scce.pyro.rest.ObjectCache objectCache;

    @javax.inject.Inject
    GraphModelController graphModelController;
    
    @javax.inject.Inject
    FileController fileController;

	@javax.ws.rs.POST
	@javax.ws.rs.Path("create/binary/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response createBinary(@javax.ws.rs.core.Context SecurityContext securityContext, CreatePyroBinaryFile newFile) {
		entity.core.PyroBinaryFileDB file = checkAddPersist(
				newFile.getparentId(),
				securityContext,
				() -> {
					// create file
					final entity.core.PyroBinaryFileDB newPBF = new entity.core.PyroBinaryFileDB();
					final String filename = newFile.getfile().getFileName();
					final int dot = filename.lastIndexOf(".");
					newPBF.filename = dot > -1 ? filename.substring(0,dot) : filename;
					newPBF.extension = dot > -1 ? filename.substring(dot+1) : null;
					newPBF.file = entity.core.BaseFileDB.findById(newFile.getfile().getId());
					return newPBF;
				}
			);
		if(file == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		else
			return Response.ok(info.scce.pyro.core.rest.types.PyroBinaryFile.fromEntity(file,objectCache)).build();
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("create/blob/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response createBlob(@javax.ws.rs.core.Context SecurityContext securityContext, CreatePyroBlobFile newFile) {
		entity.core.PyroBinaryFileDB file = checkAddPersist(
				newFile.getparentId(),
				securityContext,
				() -> {
					// create file
					final entity.core.PyroBinaryFileDB newPBF = new entity.core.PyroBinaryFileDB();
					final String filename = "PyroBinaryFile_"+newFile.getname();
					final int dot = filename.lastIndexOf(".");
					newPBF.filename = dot > -1 ? filename.substring(0,dot) : filename;
					newPBF.extension = dot > -1 ? filename.substring(dot+1) : null;
					// create from stream
					final entity.core.BaseFileDB reference = this.fileController
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
		if(file == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		else
			return Response.ok(info.scce.pyro.core.rest.types.PyroBinaryFile.fromEntity(file,objectCache)).build();
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("create/textual/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response createTextual(@javax.ws.rs.core.Context SecurityContext securityContext,CreatePyroTextualFile newFile) {
		entity.core.PyroTextualFileDB file = checkAddPersist(
				newFile.getparentId(),
				securityContext,
				() -> {
					// create file
					final entity.core.PyroTextualFileDB newPBF = new entity.core.PyroTextualFileDB();
					newPBF.filename = newFile.getfilename();
					newPBF.extension = newFile.getextension();
					newPBF.content = "";
					return newPBF;
				}
			);
		if(file == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		else
			return Response.ok(info.scce.pyro.core.rest.types.PyroTextualFile.fromEntity(file,objectCache)).build();
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("create/url/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response createUrl(@javax.ws.rs.core.Context SecurityContext securityContext,CreatePyroURLFile newFile) {
		entity.core.PyroURLFileDB file = checkAddPersist(
				newFile.getparentId(),
				securityContext,
				() -> {
					// create file
					final entity.core.PyroURLFileDB newPBF = new entity.core.PyroURLFileDB();
					newPBF.filename = newFile.getfilename();
					newPBF.extension = newFile.getextension();
					newPBF.url = newFile.geturl();
					return newPBF;
				}
			);
		if(file == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		else
			return Response.ok(info.scce.pyro.core.rest.types.PyroURLFile.fromEntity(file,objectCache)).build();
	}
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("export/empty/{id}/{path:.+}")
	@javax.annotation.security.RolesAllowed("user")
	public Response exportEmpty(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id,@javax.ws.rs.PathParam("path") final String path) {
		final entity.empty.EmptyDB graph = entity.empty.EmptyDB.findById(id);
		if (graph == null) {
		    return Response.status(Response.Status.NOT_FOUND).build();
		}
		checkPermission(id, securityContext);
		
		info.scce.pyro.core.export.EmptyExporter exporter = new info.scce.pyro.core.export.EmptyExporter();
		
		InputStream input = new ByteArrayInputStream(exporter.getContent(graph).getBytes(StandardCharsets.UTF_8));
		final byte[] result;
		try {
		    result = org.apache.commons.io.IOUtils.toByteArray(input);
		} catch (IOException e) {
		    throw new WebApplicationException(e);
		}
		
		CacheControl cc = new CacheControl();
		        cc.setMustRevalidate(true);
		        cc.setNoStore(true);
		        cc.setNoCache(true);
		return Response
		        .ok(result,"text/plain")
		        .cacheControl(cc)
		        .header("Content-Disposition", "attachment; filename=" + path)
		        .build();
	}
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("export/pr/{id}/{path:.+}")
	@javax.annotation.security.RolesAllowed("user")
	public Response exportPrimeRefs(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id,@javax.ws.rs.PathParam("path") final String path) {
		final entity.primerefs.PrimeRefsDB graph = entity.primerefs.PrimeRefsDB.findById(id);
		if (graph == null) {
		    return Response.status(Response.Status.NOT_FOUND).build();
		}
		checkPermission(id, securityContext);
		
		info.scce.pyro.core.export.PrimeRefsExporter exporter = new info.scce.pyro.core.export.PrimeRefsExporter();
		
		InputStream input = new ByteArrayInputStream(exporter.getContent(graph).getBytes(StandardCharsets.UTF_8));
		final byte[] result;
		try {
		    result = org.apache.commons.io.IOUtils.toByteArray(input);
		} catch (IOException e) {
		    throw new WebApplicationException(e);
		}
		
		CacheControl cc = new CacheControl();
		        cc.setMustRevalidate(true);
		        cc.setNoStore(true);
		        cc.setNoCache(true);
		return Response
		        .ok(result,"text/plain")
		        .cacheControl(cc)
		        .header("Content-Disposition", "attachment; filename=" + path)
		        .build();
	}
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("export/hierarchy/{id}/{path:.+}")
	@javax.annotation.security.RolesAllowed("user")
	public Response exportHierarchy(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id,@javax.ws.rs.PathParam("path") final String path) {
		final entity.hierarchy.HierarchyDB graph = entity.hierarchy.HierarchyDB.findById(id);
		if (graph == null) {
		    return Response.status(Response.Status.NOT_FOUND).build();
		}
		checkPermission(id, securityContext);
		
		info.scce.pyro.core.export.HierarchyExporter exporter = new info.scce.pyro.core.export.HierarchyExporter();
		
		InputStream input = new ByteArrayInputStream(exporter.getContent(graph).getBytes(StandardCharsets.UTF_8));
		final byte[] result;
		try {
		    result = org.apache.commons.io.IOUtils.toByteArray(input);
		} catch (IOException e) {
		    throw new WebApplicationException(e);
		}
		
		CacheControl cc = new CacheControl();
		        cc.setMustRevalidate(true);
		        cc.setNoStore(true);
		        cc.setNoCache(true);
		return Response
		        .ok(result,"text/plain")
		        .cacheControl(cc)
		        .header("Content-Disposition", "attachment; filename=" + path)
		        .build();
	}
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("export/ha/{id}/{path:.+}")
	@javax.annotation.security.RolesAllowed("user")
	public Response exportHooksAndActions(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id,@javax.ws.rs.PathParam("path") final String path) {
		final entity.hooksandactions.HooksAndActionsDB graph = entity.hooksandactions.HooksAndActionsDB.findById(id);
		if (graph == null) {
		    return Response.status(Response.Status.NOT_FOUND).build();
		}
		checkPermission(id, securityContext);
		
		info.scce.pyro.core.export.HooksAndActionsExporter exporter = new info.scce.pyro.core.export.HooksAndActionsExporter();
		
		InputStream input = new ByteArrayInputStream(exporter.getContent(graph).getBytes(StandardCharsets.UTF_8));
		final byte[] result;
		try {
		    result = org.apache.commons.io.IOUtils.toByteArray(input);
		} catch (IOException e) {
		    throw new WebApplicationException(e);
		}
		
		CacheControl cc = new CacheControl();
		        cc.setMustRevalidate(true);
		        cc.setNoStore(true);
		        cc.setNoCache(true);
		return Response
		        .ok(result,"text/plain")
		        .cacheControl(cc)
		        .header("Content-Disposition", "attachment; filename=" + path)
		        .build();
	}
	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("export/flowgraph/{id}/{path:.+}")
	@javax.annotation.security.RolesAllowed("user")
	public Response exportFlowGraph(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id,@javax.ws.rs.PathParam("path") final String path) {
		final entity.flowgraph.FlowGraphDB graph = entity.flowgraph.FlowGraphDB.findById(id);
		if (graph == null) {
		    return Response.status(Response.Status.NOT_FOUND).build();
		}
		checkPermission(id, securityContext);
		
		info.scce.pyro.core.export.FlowGraphExporter exporter = new info.scce.pyro.core.export.FlowGraphExporter();
		
		InputStream input = new ByteArrayInputStream(exporter.getContent(graph).getBytes(StandardCharsets.UTF_8));
		final byte[] result;
		try {
		    result = org.apache.commons.io.IOUtils.toByteArray(input);
		} catch (IOException e) {
		    throw new WebApplicationException(e);
		}
		
		CacheControl cc = new CacheControl();
		        cc.setMustRevalidate(true);
		        cc.setNoStore(true);
		        cc.setNoCache(true);
		return Response
		        .ok(result,"text/plain")
		        .cacheControl(cc)
		        .header("Content-Disposition", "attachment; filename=" + path)
		        .build();
	}
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("update/file/private")
	@javax.annotation.security.RolesAllowed("user")
	public Response updateFile(@javax.ws.rs.core.Context SecurityContext securityContext, UpdatePyroFile file) {
	
	    io.quarkus.hibernate.orm.panache.PanacheEntity pf = null;
	    io.quarkus.hibernate.orm.panache.PanacheEntity update = null;
	    
	    final Optional<entity.core.PyroBinaryFileDB> optPP = entity.core.PyroBinaryFileDB.findByIdOptional(file.getId());
	    if(optPP.isPresent()) {
	        entity.core.PyroBinaryFileDB f = optPP.get();
	        f.filename = file.getfilename();
	        update = f;
	        pf = graphModelController.getParent(f);
	    }
	    final Optional<entity.core.PyroURLFileDB> optPf = entity.core.PyroURLFileDB.findByIdOptional(file.getId());
	    if(optPf.isPresent()) {
	        entity.core.PyroURLFileDB f = optPf.get();
	        f.filename = file.getfilename();
	        update = f;
	        pf = graphModelController.getParent(f);
	    }
	    final Optional<entity.core.PyroTextualFileDB> optPT = entity.core.PyroTextualFileDB.findByIdOptional(file.getId());
	    if(optPT.isPresent()) {
	        entity.core.PyroTextualFileDB f = optPT.get();
	        f.filename = file.getfilename();
	        update = f;
	        pf = graphModelController.getParent(f);
	    }
	    {
	    	final Optional<entity.empty.EmptyDB> optG = entity.empty.EmptyDB.findByIdOptional(file.getId());
	    	if(optG.isPresent()) {
	    		entity.empty.EmptyDB f = optG.get();
	    		f.filename = file.getfilename();
	    		update = f;
	    		pf = graphModelController.getParent(f);
	    	}
	    }
	    {
	    	final Optional<entity.primerefs.PrimeRefsDB> optG = entity.primerefs.PrimeRefsDB.findByIdOptional(file.getId());
	    	if(optG.isPresent()) {
	    		entity.primerefs.PrimeRefsDB f = optG.get();
	    		f.filename = file.getfilename();
	    		update = f;
	    		pf = graphModelController.getParent(f);
	    	}
	    }
	    {
	    	final Optional<entity.hierarchy.HierarchyDB> optG = entity.hierarchy.HierarchyDB.findByIdOptional(file.getId());
	    	if(optG.isPresent()) {
	    		entity.hierarchy.HierarchyDB f = optG.get();
	    		f.filename = file.getfilename();
	    		update = f;
	    		pf = graphModelController.getParent(f);
	    	}
	    }
	    {
	    	final Optional<entity.hooksandactions.HooksAndActionsDB> optG = entity.hooksandactions.HooksAndActionsDB.findByIdOptional(file.getId());
	    	if(optG.isPresent()) {
	    		entity.hooksandactions.HooksAndActionsDB f = optG.get();
	    		f.filename = file.getfilename();
	    		update = f;
	    		pf = graphModelController.getParent(f);
	    	}
	    }
	    {
	    	final Optional<entity.flowgraph.FlowGraphDB> optG = entity.flowgraph.FlowGraphDB.findByIdOptional(file.getId());
	    	if(optG.isPresent()) {
	    		entity.flowgraph.FlowGraphDB f = optG.get();
	    		f.filename = file.getfilename();
	    		update = f;
	    		pf = graphModelController.getParent(f);
	    	}
	    }
	    {
	    	final Optional<entity.externallibrary.ExternalLibraryDB> optG = entity.externallibrary.ExternalLibraryDB.findByIdOptional(file.getId());
	    	if(optG.isPresent()) {
	    		entity.externallibrary.ExternalLibraryDB f = optG.get();
	    		f.filename = file.getfilename();
	    		update = f;
	    		pf = graphModelController.getParent(f);
	    	}
	    }
	
	    if(pf instanceof entity.core.PyroFolderDB) {
	        graphModelController.checkPermission((entity.core.PyroFolderDB)pf,securityContext);
	        update.persist();
	        graphModelController.sendProjectUpdate((entity.core.PyroFolderDB)pf,securityContext);
	    } else if(pf instanceof entity.core.PyroProjectDB) {
	        graphModelController.checkPermission((entity.core.PyroProjectDB)pf,securityContext);
	        update.persist();
	        graphModelController.sendProjectUpdate((entity.core.PyroProjectDB)pf,securityContext);
	    }
	    
	    return Response.ok(file).build();
	}

    @javax.ws.rs.GET
	@javax.ws.rs.Path("read/projectresource/{id}/{path:.+}")
	// TODO: needs security... @javax.annotation.security.RolesAllowed("user")
	public Response readFile(@javax.ws.rs.core.Context SecurityContext securityContext, @Context UriInfo ui, @javax.ws.rs.PathParam("id") final long id, @javax.ws.rs.PathParam("path") final String path, @javax.ws.rs.PathParam("ticket") final String ticket) {
		//find parent
		final entity.core.PyroProjectDB pf = entity.core.PyroProjectDB.findById(id);
		if(pf==null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		//find folder
		String[] folders = path.split("/");
		Object current = pf;
		for(int i=0;i<folders.length;i++) {
			//is last part, e.g. the file
			final int fin_i = i;
			if(i>=(folders.length-1)) {
				if(current instanceof entity.core.PyroFolderDB) {
					entity.core.PyroBinaryFileDB ppf = ((entity.core.PyroFolderDB)current).binaryFiles.stream().filter(n->(n.filename + (n.extension == null ? "" : "."+n.extension)).equals(folders[fin_i])).findAny().orElse(null);
					if(ppf == null) {
						entity.core.PyroTextualFileDB ptf = ((entity.core.PyroFolderDB)current).textualFiles.stream().filter(n->(n.filename + (n.extension == null ? "" : "."+n.extension)).equals(folders[fin_i])).findAny().orElse(null);
						if(ptf == null) {
							entity.core.PyroURLFileDB puf = ((entity.core.PyroFolderDB)current).urlFiles.stream().filter(n->(n.filename + (n.extension == null ? "" : "."+n.extension)).equals(folders[fin_i])).findAny().orElse(null);
							if(puf == null) {
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
				
				else if(current instanceof entity.core.PyroProjectDB) {
					entity.core.PyroBinaryFileDB ppf = ((entity.core.PyroProjectDB)current).binaryFiles.stream().filter(n->(n.filename + (n.extension == null ? "" : "."+n.extension)).equals(folders[fin_i])).findAny().orElse(null);
					if(ppf == null) {
						entity.core.PyroTextualFileDB ptf = ((entity.core.PyroProjectDB)current).textualFiles.stream().filter(n->(n.filename + (n.extension == null ? "" : "."+n.extension)).equals(folders[fin_i])).findAny().orElse(null);
						if(ptf == null) {
							entity.core.PyroURLFileDB puf = ((entity.core.PyroProjectDB)current).urlFiles.stream().filter(n->(n.filename + (n.extension == null ? "" : "."+n.extension)).equals(folders[fin_i])).findAny().orElse(null);
							if(puf == null) {
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
				if(current instanceof entity.core.PyroProjectDB) {
					
					Optional<entity.core.PyroFolderDB> optFolder = ((entity.core.PyroProjectDB)current).innerFolders.stream().filter(n->n.name.equals(folders[fin_i])).findAny();
					if(optFolder.isPresent()) {
						current = optFolder.get();
					} else {
						return Response.status(Response.Status.NOT_FOUND).build();
					}
				} else if(current instanceof entity.core.PyroFolderDB) {
					Optional<entity.core.PyroFolderDB> optFolder = ((entity.core.PyroFolderDB)current).innerFolders.stream().filter(n->n.name.equals(folders[fin_i])).findAny();
					if(optFolder.isPresent()) {
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
		entity.core.PyroFileContainerDB target = entity.core.PyroFileContainerDB.findById(targetId);
		checkPermission(target, securityContext);
		
		{
			final entity.core.PyroTextualFileDB file = entity.core.PyroTextualFileDB.findById(id);
			if(file != null) {
				final PanacheEntity source = graphModelController.getParent(file);
				if(source instanceof entity.core.PyroFolderDB) {
					((entity.core.PyroFolderDB)source).textualFiles.remove(file);
				} else if(source instanceof entity.core.PyroProjectDB) {
					((entity.core.PyroProjectDB)source).textualFiles.remove(file);
				}
				if(target instanceof entity.core.PyroFolderDB) {
					if( ((entity.core.PyroFolderDB)target).textualFiles.stream().filter(n->n.filename.equals(file.filename)).findAny().isPresent()) {
						return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
					}
					((entity.core.PyroFolderDB)target).textualFiles.add(file);
					file.parent = target;
				} else if(target instanceof entity.core.PyroProjectDB) {
					if( ((entity.core.PyroProjectDB)target).textualFiles.stream().filter(n->n.filename.equals(file.filename)).findAny().isPresent()) {
						return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
					}
					((entity.core.PyroProjectDB)target).textualFiles.add(file);
					file.parent = target;
				}
				source.persist();
				target.persist();
				file.persist();
				graphModelController.sendProjectUpdate(target,securityContext);
				graphModelController.sendProjectUpdate(source,securityContext);
				
				return Response.ok().build();
			}
		}
		{
			final entity.core.PyroBinaryFileDB file = entity.core.PyroBinaryFileDB.findById(id);
			if(file != null) {
				final PanacheEntity source = graphModelController.getParent(file);
				if(source instanceof entity.core.PyroFolderDB) {
					((entity.core.PyroFolderDB)source).binaryFiles.remove(file);
				} else if(source instanceof entity.core.PyroProjectDB) {
					((entity.core.PyroProjectDB)source).binaryFiles.remove(file);
				}
				if(target instanceof entity.core.PyroFolderDB) {
					if( ((entity.core.PyroFolderDB)target).binaryFiles.stream().filter(n->n.filename.equals(file.filename)).findAny().isPresent()) {
						return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
					}
					((entity.core.PyroFolderDB)target).binaryFiles.add(file);
					file.parent = target;
				} else if(target instanceof entity.core.PyroProjectDB) {
					if( ((entity.core.PyroProjectDB)target).binaryFiles.stream().filter(n->n.filename.equals(file.filename)).findAny().isPresent()) {
						return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
					}
					((entity.core.PyroProjectDB)target).binaryFiles.add(file);
					file.parent = target;
				}
				source.persist();
				target.persist();
				file.persist();
				graphModelController.sendProjectUpdate(target,securityContext);
				graphModelController.sendProjectUpdate(source,securityContext);
				
				return Response.ok().build();
			}
		}
		{
			final entity.core.PyroURLFileDB file = entity.core.PyroURLFileDB.findById(id);
			if(file != null) {
				final PanacheEntity source = graphModelController.getParent(file);
				if(source instanceof entity.core.PyroFolderDB) {
					((entity.core.PyroFolderDB)source).urlFiles.remove(file);
				} else if(source instanceof entity.core.PyroProjectDB) {
					((entity.core.PyroProjectDB)source).urlFiles.remove(file);
				}
				if(target instanceof entity.core.PyroFolderDB) {
					if( ((entity.core.PyroFolderDB)target).urlFiles.stream().filter(n->n.filename.equals(file.filename)).findAny().isPresent()) {
						return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
					}
					((entity.core.PyroFolderDB)target).urlFiles.add(file);
					file.parent = target;
				} else if(target instanceof entity.core.PyroProjectDB) {
					if( ((entity.core.PyroProjectDB)target).urlFiles.stream().filter(n->n.filename.equals(file.filename)).findAny().isPresent()) {
						return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
					}
					((entity.core.PyroProjectDB)target).urlFiles.add(file);
					file.parent = target;
				}
				source.persist();
				target.persist();
				file.persist();
				graphModelController.sendProjectUpdate(target,securityContext);
				graphModelController.sendProjectUpdate(source,securityContext);
				
				return Response.ok().build();
			}
		}
		{
		    final entity.empty.EmptyDB file = entity.empty.EmptyDB.findById(id);
		    if(file != null) {
		        final PanacheEntity source = graphModelController.getParent(file);
		        if(source instanceof entity.core.PyroFolderDB) {
		            ((entity.core.PyroFolderDB)source).files_Empty.remove(file);
		        } else if(source instanceof entity.core.PyroProjectDB) {
		            ((entity.core.PyroProjectDB)source).files_Empty.remove(file);
		        }
		        if(target instanceof entity.core.PyroFolderDB) {
		            if( ((entity.core.PyroFolderDB)target).files_Empty.stream().filter(n->n.filename.equals(file.filename)).findAny().isPresent()) {
		                return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
		            }
		            ((entity.core.PyroFolderDB)target).files_Empty.add(file);
		            file.parent = target;
		        } else if(target instanceof entity.core.PyroProjectDB) {
		            if( ((entity.core.PyroProjectDB)target).files_Empty.stream().filter(n->n.filename.equals(file.filename)).findAny().isPresent()) {
		                return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
		            }
		            ((entity.core.PyroProjectDB)target).files_Empty.add(file);
		            file.parent = target;
		        }
		        source.persist();
		        target.persist();
		        file.persist();
		        graphModelController.sendProjectUpdate(target,securityContext);
		        graphModelController.sendProjectUpdate(source,securityContext);
		        
		        return Response.ok().build();
		    }
		}
		{
		    final entity.primerefs.PrimeRefsDB file = entity.primerefs.PrimeRefsDB.findById(id);
		    if(file != null) {
		        final PanacheEntity source = graphModelController.getParent(file);
		        if(source instanceof entity.core.PyroFolderDB) {
		            ((entity.core.PyroFolderDB)source).files_PrimeRefs.remove(file);
		        } else if(source instanceof entity.core.PyroProjectDB) {
		            ((entity.core.PyroProjectDB)source).files_PrimeRefs.remove(file);
		        }
		        if(target instanceof entity.core.PyroFolderDB) {
		            if( ((entity.core.PyroFolderDB)target).files_PrimeRefs.stream().filter(n->n.filename.equals(file.filename)).findAny().isPresent()) {
		                return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
		            }
		            ((entity.core.PyroFolderDB)target).files_PrimeRefs.add(file);
		            file.parent = target;
		        } else if(target instanceof entity.core.PyroProjectDB) {
		            if( ((entity.core.PyroProjectDB)target).files_PrimeRefs.stream().filter(n->n.filename.equals(file.filename)).findAny().isPresent()) {
		                return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
		            }
		            ((entity.core.PyroProjectDB)target).files_PrimeRefs.add(file);
		            file.parent = target;
		        }
		        source.persist();
		        target.persist();
		        file.persist();
		        graphModelController.sendProjectUpdate(target,securityContext);
		        graphModelController.sendProjectUpdate(source,securityContext);
		        
		        return Response.ok().build();
		    }
		}
		{
		    final entity.hierarchy.HierarchyDB file = entity.hierarchy.HierarchyDB.findById(id);
		    if(file != null) {
		        final PanacheEntity source = graphModelController.getParent(file);
		        if(source instanceof entity.core.PyroFolderDB) {
		            ((entity.core.PyroFolderDB)source).files_Hierarchy.remove(file);
		        } else if(source instanceof entity.core.PyroProjectDB) {
		            ((entity.core.PyroProjectDB)source).files_Hierarchy.remove(file);
		        }
		        if(target instanceof entity.core.PyroFolderDB) {
		            if( ((entity.core.PyroFolderDB)target).files_Hierarchy.stream().filter(n->n.filename.equals(file.filename)).findAny().isPresent()) {
		                return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
		            }
		            ((entity.core.PyroFolderDB)target).files_Hierarchy.add(file);
		            file.parent = target;
		        } else if(target instanceof entity.core.PyroProjectDB) {
		            if( ((entity.core.PyroProjectDB)target).files_Hierarchy.stream().filter(n->n.filename.equals(file.filename)).findAny().isPresent()) {
		                return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
		            }
		            ((entity.core.PyroProjectDB)target).files_Hierarchy.add(file);
		            file.parent = target;
		        }
		        source.persist();
		        target.persist();
		        file.persist();
		        graphModelController.sendProjectUpdate(target,securityContext);
		        graphModelController.sendProjectUpdate(source,securityContext);
		        
		        return Response.ok().build();
		    }
		}
		{
		    final entity.hooksandactions.HooksAndActionsDB file = entity.hooksandactions.HooksAndActionsDB.findById(id);
		    if(file != null) {
		        final PanacheEntity source = graphModelController.getParent(file);
		        if(source instanceof entity.core.PyroFolderDB) {
		            ((entity.core.PyroFolderDB)source).files_HooksAndActions.remove(file);
		        } else if(source instanceof entity.core.PyroProjectDB) {
		            ((entity.core.PyroProjectDB)source).files_HooksAndActions.remove(file);
		        }
		        if(target instanceof entity.core.PyroFolderDB) {
		            if( ((entity.core.PyroFolderDB)target).files_HooksAndActions.stream().filter(n->n.filename.equals(file.filename)).findAny().isPresent()) {
		                return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
		            }
		            ((entity.core.PyroFolderDB)target).files_HooksAndActions.add(file);
		            file.parent = target;
		        } else if(target instanceof entity.core.PyroProjectDB) {
		            if( ((entity.core.PyroProjectDB)target).files_HooksAndActions.stream().filter(n->n.filename.equals(file.filename)).findAny().isPresent()) {
		                return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
		            }
		            ((entity.core.PyroProjectDB)target).files_HooksAndActions.add(file);
		            file.parent = target;
		        }
		        source.persist();
		        target.persist();
		        file.persist();
		        graphModelController.sendProjectUpdate(target,securityContext);
		        graphModelController.sendProjectUpdate(source,securityContext);
		        
		        return Response.ok().build();
		    }
		}
		{
		    final entity.flowgraph.FlowGraphDB file = entity.flowgraph.FlowGraphDB.findById(id);
		    if(file != null) {
		        final PanacheEntity source = graphModelController.getParent(file);
		        if(source instanceof entity.core.PyroFolderDB) {
		            ((entity.core.PyroFolderDB)source).files_FlowGraph.remove(file);
		        } else if(source instanceof entity.core.PyroProjectDB) {
		            ((entity.core.PyroProjectDB)source).files_FlowGraph.remove(file);
		        }
		        if(target instanceof entity.core.PyroFolderDB) {
		            if( ((entity.core.PyroFolderDB)target).files_FlowGraph.stream().filter(n->n.filename.equals(file.filename)).findAny().isPresent()) {
		                return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
		            }
		            ((entity.core.PyroFolderDB)target).files_FlowGraph.add(file);
		            file.parent = target;
		        } else if(target instanceof entity.core.PyroProjectDB) {
		            if( ((entity.core.PyroProjectDB)target).files_FlowGraph.stream().filter(n->n.filename.equals(file.filename)).findAny().isPresent()) {
		                return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
		            }
		            ((entity.core.PyroProjectDB)target).files_FlowGraph.add(file);
		            file.parent = target;
		        }
		        source.persist();
		        target.persist();
		        file.persist();
		        graphModelController.sendProjectUpdate(target,securityContext);
		        graphModelController.sendProjectUpdate(source,securityContext);
		        
		        return Response.ok().build();
		    }
		}
		{
		    final entity.externallibrary.ExternalLibraryDB file = entity.externallibrary.ExternalLibraryDB.findById(id);
		    if(file != null) {
		        final PanacheEntity source = graphModelController.getParent(file);
		        if(source instanceof entity.core.PyroFolderDB) {
		            ((entity.core.PyroFolderDB)source).files_ExternalLibrary.remove(file);
		        } else if(source instanceof entity.core.PyroProjectDB) {
		            ((entity.core.PyroProjectDB)source).files_ExternalLibrary.remove(file);
		        }
		        if(target instanceof entity.core.PyroFolderDB) {
		            if( ((entity.core.PyroFolderDB)target).files_ExternalLibrary.stream().filter(n->n.filename.equals(file.filename)).findAny().isPresent()) {
		                return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
		            }
		            ((entity.core.PyroFolderDB)target).files_ExternalLibrary.add(file);
		            file.parent = target;
		        } else if(target instanceof entity.core.PyroProjectDB) {
		            if( ((entity.core.PyroProjectDB)target).files_ExternalLibrary.stream().filter(n->n.filename.equals(file.filename)).findAny().isPresent()) {
		                return Response.status(Response.Status.BAD_REQUEST).entity("Name already exists").build();
		            }
		            ((entity.core.PyroProjectDB)target).files_ExternalLibrary.add(file);
		            file.parent = target;
		        }
		        source.persist();
		        target.persist();
		        file.persist();
		        graphModelController.sendProjectUpdate(target,securityContext);
		        graphModelController.sendProjectUpdate(source,securityContext);
		        
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
		entity.core.PyroFolderDB folder = null;
		entity.core.PyroProjectDB project = null;
		PanacheEntity file = null;
        
		//find parent
        folder = entity.core.PyroFolderDB.findById(parentId);
        if(folder == null) {
        	project = entity.core.PyroProjectDB.findById(parentId);
        	if(project == null)
                return Response.status(Response.Status.BAD_REQUEST).entity("Parent not found").build();
        }
        
        // security
        graphModelController.checkPermission(folder != null? folder : project, securityContext);
		
		// find file
		file = entity.core.PyroBinaryFileDB.findById(id);
		if(file == null) {
			file = entity.core.PyroURLFileDB.findById(id);
			if(file == null) {
				file = entity.core.PyroTextualFileDB.findById(id);
				
				if(file == null) {
					file = entity.externallibrary.ExternalLibraryDB.findById(id);
				if(file == null) { // file couldn't be found
				    return Response.status(Response.Status.BAD_REQUEST).entity("File not found").build();
				}
				}
		    }
		}
		
		// delete + persist
		if(folder != null) {
			folder.removeFile(file, true);
			folder.persist();
        } else {
        	project.removeFile(file, true);
        	project.persist();
        }
        
		graphModelController.sendProjectUpdate(folder != null? folder : project, securityContext);
		return Response.ok().build();
    }
    
	public <T extends PanacheEntity> T checkAddPersist(
			long folderId,
			SecurityContext securityContext,
			java.util.concurrent.Callable<T> foo
	) {
		//find parent
		io.quarkus.hibernate.orm.panache.PanacheEntity folder = getFileContainer(folderId);
		if(folder == null) {
			return null;
		}
		// permission check
		checkPermission(folder, securityContext);
		
		T file = null;
		try {
			file = foo.call();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		// persist and chain
		addAndPersist(file, folder, securityContext);
		return file;
	}
	
	public io.quarkus.hibernate.orm.panache.PanacheEntity getFileContainer(long folderId) {
		io.quarkus.hibernate.orm.panache.PanacheEntity folder = entity.core.PyroFolderDB.findById(folderId);
		if(folder != null) {
			return folder;
		}
		io.quarkus.hibernate.orm.panache.PanacheEntity project = entity.core.PyroProjectDB.findById(folderId);
		if(project != null) {
			return project;
		}
		return null;
	}
	
	public void checkPermission(io.quarkus.hibernate.orm.panache.PanacheEntity entity, SecurityContext securityContext) {
		graphModelController.checkPermission(entity,securityContext);
	}
	
	public void checkPermission(long folderId, SecurityContext securityContext) {
		//find parent
		io.quarkus.hibernate.orm.panache.PanacheEntity folder = getFileContainer(folderId);
		// permission check
		checkPermission(folder, securityContext);
	}
	
	public void addAndPersist(io.quarkus.hibernate.orm.panache.PanacheEntity f, io.quarkus.hibernate.orm.panache.PanacheEntity fC, SecurityContext securityContext) {
		if(fC instanceof PyroFolderDB) {
			PyroFolderDB folder = (PyroFolderDB) fC;
			if(f instanceof PyroBinaryFileDB) {
				PyroBinaryFileDB file = (PyroBinaryFileDB) f;
				file.parent = folder;
				file.persist();
				folder.binaryFiles.add(file);
				folder.persist();
			} else if(f instanceof PyroURLFileDB) {
				PyroURLFileDB file = (PyroURLFileDB) f;
				file.parent = folder;
				file.persist();
				folder.urlFiles.add(file);
				folder.persist();
			} else if(f instanceof PyroTextualFileDB) {
				PyroTextualFileDB file = (PyroTextualFileDB) f;
				file.parent = folder;
				file.persist();
				folder.textualFiles.add(file);
				folder.persist();
			}
	
		} else if(fC instanceof PyroProjectDB) {
			PyroProjectDB folder = (PyroProjectDB) fC;
			if(f instanceof PyroBinaryFileDB) {
				PyroBinaryFileDB file = (PyroBinaryFileDB) f;
				file.parent = folder;
				file.persist();
				folder.binaryFiles.add(file);
				folder.persist();
			} else if(f instanceof PyroURLFileDB) {
				PyroURLFileDB file = (PyroURLFileDB) f;
				file.parent = folder;
				file.persist();
				folder.urlFiles.add(file);
				folder.persist();
			} else if(f instanceof PyroTextualFileDB) {
				PyroTextualFileDB file = (PyroTextualFileDB) f;
				file.parent = folder;
				file.persist();
				folder.textualFiles.add(file);
				folder.persist();
			}
		}
		graphModelController.sendProjectUpdate(fC,securityContext);
	}
	
    private Response readFileInner(io.quarkus.hibernate.orm.panache.PanacheEntity pe, UriInfo ui)
    {
        final byte[] result;
        String fileName = getFileName(pe);
        InputStream input1 = getInputStream(pe, ui);
        InputStream input2 = getInputStream(pe, ui);
        
        if(fileName == null || input1 == null || input2 == null)
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        
		try {
		    result = org.apache.commons.io.IOUtils.toByteArray(input1);
		} catch (IOException e) {
		    throw new WebApplicationException(e);
		}
		
		return buildResponse(fileName, input2, result);
    }
    
    private InputStream getInputStream(io.quarkus.hibernate.orm.panache.PanacheEntity f, UriInfo ui) {
    	if(f instanceof PyroBinaryFileDB) {
    		PyroBinaryFileDB file = (PyroBinaryFileDB) f;
			entity.core.BaseFileDB reference = file.file;
			InputStream stream = this.fileController.loadFile(reference);
			java.io.BufferedInputStream bstream = new java.io.BufferedInputStream(stream);
			return bstream;
    	} else if(f instanceof PyroURLFileDB) {
    		PyroURLFileDB file = (PyroURLFileDB) f;
			String url = file.url;
			if(!url.startsWith("http")){
				String uri = ui.getBaseUri().toString();
			    url = uri.substring(0,uri.lastIndexOf("/rest"))+"/"+url;
			}
			try {
				InputStream stream =  new URL(url).openStream();
				java.io.BufferedInputStream bstream = new java.io.BufferedInputStream(stream);
				return bstream;
			} catch(java.net.MalformedURLException e) {
				throw new WebApplicationException(e);
			} catch(java.io.IOException e) {
				throw new WebApplicationException(e);
			}
    	} else if(f instanceof PyroTextualFileDB) {
    		PyroTextualFileDB file = (PyroTextualFileDB) f;
    		return new ByteArrayInputStream(file.content.getBytes(StandardCharsets.UTF_8));
    	}
    	return null;
    }
    
    private String getFileName(io.quarkus.hibernate.orm.panache.PanacheEntity f) {
    	if(f instanceof PyroBinaryFileDB) {
    		PyroBinaryFileDB file = (PyroBinaryFileDB) f;
    		return file.extension != null?
    				file.filename+"."+file.extension
    				: file.filename;
    	} else if(f instanceof PyroURLFileDB) {
    		PyroURLFileDB file = (PyroURLFileDB) f;
    		return file.extension != null?
    				file.filename+"."+file.extension
    				: file.filename;
    	} else if(f instanceof PyroTextualFileDB) {
    		PyroTextualFileDB file = (PyroTextualFileDB) f;
    		return file.extension != null?
    				file.filename+"."+file.extension
    				: file.filename;
    	}
    	return null;
    }
    
    private MediaType getMime(String fileName, InputStream content) {
    	TikaConfig config = TikaConfig.getDefaultConfig();
	    Metadata md = new Metadata();
	    md.set(Metadata.RESOURCE_NAME_KEY, fileName);
	    try {
	    	return config.getMimeRepository().detect(content,md);
	    } catch(java.io.IOException e) {
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
		        .ok(result,mime)
		        .cacheControl(cc)
				.build();
    }
}
