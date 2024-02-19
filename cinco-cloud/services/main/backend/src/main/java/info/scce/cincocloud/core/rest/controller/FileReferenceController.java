package info.scce.cincocloud.core.rest.controller;

import info.scce.cincocloud.core.rest.tos.FileReferenceTO;
import info.scce.cincocloud.core.services.FileService;
import info.scce.cincocloud.db.BaseFileDB;
import jakarta.annotation.security.RolesAllowed;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Transactional
@Path("/files")
@RequestScoped
public class FileReferenceController {

  @Inject
  FileService fileService;

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("user")
  public Response create(final MultipartFormDataInput input) throws java.io.IOException {

    final List<InputPart> inputParts = input.getFormDataMap().get("file");

    if (inputParts == null || inputParts.isEmpty()) {
      throw new WebApplicationException("invalid request");
    }

    final InputPart inputPart = inputParts.get(0);
    final MultivaluedMap<String, String> header = inputPart.getHeaders();

    String fileName = "unknown";
    final String[] contentDisposition = header
        .getFirst("Content-Disposition")
        .split(";");

    for (String filename : contentDisposition) {
      if ((filename.trim().startsWith("filename"))) {
        final String[] name = filename.split("=");
        fileName = name[1].trim().replaceAll("\"", "");
        break;
      }
    }

    final BaseFileDB reference = this.fileService
        .storeFile(fileName, inputPart.getBody(InputStream.class, null), inputPart.getMediaType().toString());
    reference.contentType = inputPart.getMediaType().toString();
    reference.persist();

    return Response.ok(new FileReferenceTO(reference)).build();
  }
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  //TODO: security
  public Response read(@PathParam("id") final long id,
      @QueryParam("download") String download) {
    final BaseFileDB reference = this.fileService.getFileReference(id);
    final InputStream stream = this.fileService.loadFile(reference);

    final byte[] result;

    try {
      result = IOUtils.toByteArray(stream);
    } catch (IOException e) {
      throw new WebApplicationException(e);
    }

    var response = Response.ok(result, reference.contentType);

    if (Boolean.parseBoolean(download)) {
      response = response.header("Content-Disposition",
          "attachment; filename=" + (reference.filename + "." + reference.fileExtension));
    }

    return response.build();
  }
}
