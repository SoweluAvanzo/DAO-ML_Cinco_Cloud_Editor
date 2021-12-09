package info.scce.cincocloud.exeptions;

import javax.ws.rs.core.Response.Status;

public class RestException extends RuntimeException {

  private Status status;

  public RestException() {
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
