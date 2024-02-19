package info.scce.cincocloud.exeptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RestExceptionMapper implements ExceptionMapper<RestException> {

  @Override
  public Response toResponse(RestException exception) {
    return Response.status(exception.getStatus())
        .entity(new RestErrorResponse(exception.getMessage(), exception.getStatus().getStatusCode()))
        .build();
  }
}
