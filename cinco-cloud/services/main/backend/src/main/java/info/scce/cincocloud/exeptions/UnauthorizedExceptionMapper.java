package info.scce.cincocloud.exeptions;

import io.quarkus.security.UnauthorizedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {

  @Override
  public Response toResponse(UnauthorizedException exception) {
    return Response.status(Status.UNAUTHORIZED)
        .entity(new RestErrorResponse(exception.getMessage(), Status.UNAUTHORIZED.getStatusCode()))
        .build();
  }
}
