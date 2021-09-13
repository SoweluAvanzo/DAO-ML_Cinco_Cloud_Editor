package info.scce.pyro.core;

import info.scce.pyro.core.rest.types.*;
import info.scce.pyro.sync.ticket.TicketRegistrationHandler;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

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


	
	@javax.ws.rs.GET
	@javax.ws.rs.Path("export/flowgraph/{id}")
	@javax.annotation.security.RolesAllowed("user")
	public Response exportFlowGraphDiagram(@javax.ws.rs.core.Context SecurityContext securityContext, @javax.ws.rs.PathParam("id") final long id) {
		final entity.flowgraph.FlowGraphDiagramDB graph = entity.flowgraph.FlowGraphDiagramDB.findById(id);
		if (graph == null) {
		    return Response.status(Response.Status.NOT_FOUND).build();
		}
		checkPermission(securityContext);
		
		info.scce.pyro.core.export.FlowGraphDiagramExporter exporter = new info.scce.pyro.core.export.FlowGraphDiagramExporter();
		
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
		        .header("Content-Disposition", "attachment; filename=" + graph.filename + "." + graph.extension)
		        .build();
	}
	
    
	
	public void checkPermission(SecurityContext securityContext) {
		graphModelController.checkPermission(securityContext);
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
