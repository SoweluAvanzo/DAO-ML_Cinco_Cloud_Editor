package info.scce.cincocloud.exeptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NullPointerExceptionMapper implements ExceptionMapper<NullPointerException> {

  @Override
  public Response toResponse(NullPointerException nullPointerException) {
    return Response.status(Status.INTERNAL_SERVER_ERROR)
        .entity(new RestErrorResponse(nullPointerException.getMessage(), Status.INTERNAL_SERVER_ERROR.getStatusCode()))
        .build();
  }
}