package info.scce.cincocloud.exeptions;

import jakarta.ws.rs.core.Response.Status;

public class RestException extends RuntimeException {

  private final Status status;

  public RestException() {
    this(Status.BAD_REQUEST);
  }

  public RestException(String message) {
    this(Status.BAD_REQUEST, message);
  }

  public RestException(Status status) {
    this(status, "");
  }

  public RestException(Status status, String message) {
    this(status, message, null);
  }

  public RestException(Status status, String message, Throwable e) {
    super(message, e);
    this.status = status;
  }

  public Status getStatus() {
    return status;
  }
}
