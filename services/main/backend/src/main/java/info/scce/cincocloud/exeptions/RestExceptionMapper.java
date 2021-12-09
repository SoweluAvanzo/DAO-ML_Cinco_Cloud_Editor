package info.scce.cincocloud.exeptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RestExceptionMapper implements ExceptionMapper<RestException> {

  @Override
  public Response toResponse(RestException exception) {
    return Response.status(exception.getStatus())
        .entity(new RestErrorResponse(exception.getMessage(), exception.getStatus().getStatusCode()))
        .build();
  }
}
