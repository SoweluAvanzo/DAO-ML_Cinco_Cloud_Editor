package info.scce.cincocloud.exeptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

  @Override
  public Response toResponse(IllegalArgumentException illegalArgumentException) {
    return Response.status(Status.BAD_REQUEST)
        .entity(new RestErrorResponse(illegalArgumentException.getMessage(), Status.BAD_REQUEST.getStatusCode()))
        .build();
  }
}
