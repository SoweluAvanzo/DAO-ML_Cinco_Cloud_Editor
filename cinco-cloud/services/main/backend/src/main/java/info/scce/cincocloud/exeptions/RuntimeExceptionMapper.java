package info.scce.cincocloud.exeptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

  @Override
  public Response toResponse(RuntimeException runtimeException) {
    return Response.status(Status.BAD_REQUEST)
        .entity(new RestErrorResponse(runtimeException.getMessage(), Status.BAD_REQUEST.getStatusCode()))
        .build();
  }
}
