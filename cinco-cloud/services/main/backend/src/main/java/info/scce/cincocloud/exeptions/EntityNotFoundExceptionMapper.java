package info.scce.cincocloud.exeptions;

import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class EntityNotFoundExceptionMapper implements ExceptionMapper<EntityNotFoundException> {

  @Override
  public Response toResponse(EntityNotFoundException exception) {
    return Response.status(Status.NOT_FOUND)
        .entity(new RestErrorResponse(exception.getMessage(), Status.NOT_FOUND.getStatusCode()))
        .build();
  }
}
