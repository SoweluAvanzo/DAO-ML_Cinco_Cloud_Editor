package info.scce.cincocloud.exeptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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
