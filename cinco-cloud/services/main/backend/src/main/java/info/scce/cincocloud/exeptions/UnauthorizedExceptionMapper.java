package info.scce.cincocloud.exeptions;

import io.quarkus.security.UnauthorizedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {

  @Override
  public Response toResponse(UnauthorizedException exception) {
    return Response.status(Status.UNAUTHORIZED)
        .entity(new RestErrorResponse(exception.getMessage(), Status.UNAUTHORIZED.getStatusCode()))
        .build();
  }
}
