package info.scce.cincocloud.exeptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

  @Override
  public Response toResponse(RuntimeException runtimeException) {
    runtimeException.printStackTrace();
    return Response.status(Status.INTERNAL_SERVER_ERROR)
        .entity(new RestErrorResponse(runtimeException.getMessage(), Status.INTERNAL_SERVER_ERROR.getStatusCode()))
        .build();
  }
}
