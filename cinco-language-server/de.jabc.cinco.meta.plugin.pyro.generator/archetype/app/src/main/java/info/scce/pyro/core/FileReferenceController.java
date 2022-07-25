package info.scce.pyro.core;

import org.jboss.resteasy.plugins.providers.multipart.*;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.MultivaluedMap;
import info.scce.pyro.core.rest.types.FileReference;

@javax.transaction.Transactional
@Path("/files")
@javax.enterprise.context.RequestScoped
public class FileReferenceController {
	
	@POST
	@Path("/create")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@javax.annotation.security.RolesAllowed("user")
	public Response create(final MultipartFormDataInput input) throws java.io.IOException {
 
		final java.util.List<InputPart> inputParts = input
				.getFormDataMap().get("file");

		if (inputParts == null || inputParts.isEmpty()) {
			throw new WebApplicationException("invalid request");
		}

		final InputPart inputPart = inputParts.get(0);
		final MultivaluedMap<String, String> header = inputPart.getHeaders();

		String fileName = "unknown";
		final String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

		for (String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {
				final String[] name = filename.split("=");
				fileName = name[1].trim().replaceAll("\"", "");
				break;
			}
		}

		final entity.core.BaseFileDB reference = FileController.storeFile(fileName, inputPart.getBody(java.io.InputStream.class, null));
		reference.contentType = inputPart.getMediaType().toString();
		reference.persist();
		return Response.ok(new FileReference(reference)).build();
	}

	@GET
	@Path("/download/id/{id}/private")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response download(@PathParam("id") final long id) {
		final entity.core.BaseFileDB reference = FileController.getFileReference(id);
		final java.io.InputStream stream = FileController.loadFile(reference);
		final byte[] result;
		try {
			result = IOUtils.toByteArray(stream);
		} catch (java.io.IOException e) {
			throw new WebApplicationException(e);
		}
		return Response.ok(result, reference.contentType)
				.header("Content-Disposition", "attachment; filename=" + (reference.filename+"."+reference.fileExtension))
				.build();
	}
	
	@GET
	@Path("/download/path/{path}/private")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response download(@PathParam("path") final String relativeFilePath) {
		final java.io.InputStream stream = FileController.loadFile(relativeFilePath);
		final String fileName = FileController.getFileName(relativeFilePath);
		final byte[] result;
		try {
			result = IOUtils.toByteArray(stream);
		} catch (java.io.IOException e) {
			throw new WebApplicationException(e);
		}
		return Response.ok(result, MediaType.APPLICATION_OCTET_STREAM)
				.header("Content-Disposition", "attachment; filename=" + fileName)
				.build();
	}

	@GET
	@Path("/read/{id}/private")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@javax.annotation.security.RolesAllowed("user")
	public Response read(@PathParam("id") final long id) {
		final entity.core.BaseFileDB reference = FileController.getFileReference(id);
		final java.io.InputStream stream = FileController.loadFile(reference);
		final byte[] result;
		try {
			result = IOUtils.toByteArray(stream);
		} catch (java.io.IOException e) {
			throw new WebApplicationException(e);
		}
		return Response.ok(result, reference.contentType).build();
	}

	@GET
	@Path("/read/root/private")
	@Produces(MediaType.TEXT_PLAIN)
	@javax.annotation.security.RolesAllowed("user")
	public Response getWorkspaceRoot() {
		final String root = FileController.getWorkspaceRoot();
		return Response.ok(root).build();
	}
	
	@GET
	@Path("/read/upload_folder/private")
	@Produces(MediaType.TEXT_PLAIN)
	@javax.annotation.security.RolesAllowed("user")
	public Response getUploadFolder() {
		return Response.ok(FileController.UPLOAD_FOLDER).build();
	}	
}