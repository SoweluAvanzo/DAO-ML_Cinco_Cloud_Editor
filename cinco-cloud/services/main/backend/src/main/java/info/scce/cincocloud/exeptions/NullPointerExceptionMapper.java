package info.scce.cincocloud.exeptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NullPointerExceptionMapper implements ExceptionMapper<NullPointerException> {

  @Override
  public Response toResponse(NullPointerException nullPointerException) {
    return Response.status(Status.INTERNAL_SERVER_ERROR)
        .entity(new RestErrorResponse(nullPointerException.getMessage(), Status.INTERNAL_SERVER_ERROR.getStatusCode()))
        .build();
  }
}