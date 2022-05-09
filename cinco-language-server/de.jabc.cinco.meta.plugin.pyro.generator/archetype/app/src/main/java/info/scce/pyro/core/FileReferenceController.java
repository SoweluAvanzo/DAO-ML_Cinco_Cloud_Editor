package info.scce.pyro.core;

@javax.transaction.Transactional
@javax.ws.rs.Path("/files")
@javax.enterprise.context.RequestScoped
public class FileReferenceController {
	
	@javax.ws.rs.POST
	@javax.ws.rs.Path("/create")
	@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA)
	@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response create(
			final org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput input)
			throws java.io.IOException {
 
		final java.util.List<org.jboss.resteasy.plugins.providers.multipart.InputPart> inputParts = input
				.getFormDataMap().get("file");

		if (inputParts == null || inputParts.isEmpty()) {
			throw new javax.ws.rs.WebApplicationException("invalid request");
		}

		final org.jboss.resteasy.plugins.providers.multipart.InputPart inputPart = inputParts
				.get(0);
		final javax.ws.rs.core.MultivaluedMap<java.lang.String, java.lang.String> header = inputPart
				.getHeaders();

		java.lang.String fileName = "unknown";
		final java.lang.String[] contentDisposition = header.getFirst(
				"Content-Disposition").split(";");

		for (java.lang.String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {
				final java.lang.String[] name = filename.split("=");
				fileName = name[1].trim().replaceAll("\"", "");
				break;
			}
		}

		final entity.core.BaseFileDB reference = FileController
				.storeFile(fileName,
						inputPart.getBody(java.io.InputStream.class, null));
		reference.contentType = inputPart.getMediaType().toString();
		reference.persist();
		return javax.ws.rs.core.Response.ok(
				new info.scce.pyro.core.rest.types.FileReference(reference))
				.build();
	}

	@javax.ws.rs.GET
	@javax.ws.rs.Path("/download/{id}/private")
	@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM)
	public javax.ws.rs.core.Response download(@javax.ws.rs.PathParam("id") final long id) {
		final entity.core.BaseFileDB reference = FileController.getFileReference(id);
		final java.io.InputStream stream = FileController.loadFile(reference);

		final byte[] result;

		try {
			result = org.apache.commons.io.IOUtils.toByteArray(stream);
		} catch (java.io.IOException e) {
			throw new javax.ws.rs.WebApplicationException(e);
		}

		return javax.ws.rs.core.Response
				.ok(result, reference.contentType)
				.header("Content-Disposition", "attachment; filename=" + (reference.filename+"."+reference.fileExtension))
				.build();
	}

	@javax.ws.rs.GET
	@javax.ws.rs.Path("/read/{id}/private")
	@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM)
	@javax.annotation.security.RolesAllowed("user")
	public javax.ws.rs.core.Response read(@javax.ws.rs.PathParam("id") final long id) {

		final entity.core.BaseFileDB reference = FileController.getFileReference(id);
		final java.io.InputStream stream = FileController.loadFile(reference);

		final byte[] result;

		try {
			result = org.apache.commons.io.IOUtils.toByteArray(stream);
		} catch (java.io.IOException e) {
			throw new javax.ws.rs.WebApplicationException(e);
		}

		return javax.ws.rs.core.Response
				.ok(result, reference.contentType)
				.build();
	}	
}