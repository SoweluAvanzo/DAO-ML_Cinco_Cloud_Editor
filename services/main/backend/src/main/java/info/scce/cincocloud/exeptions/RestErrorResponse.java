package info.scce.cincocloud.exeptions;

public class RestErrorResponse {

  /**
   * The message to send to the client.
   */
  public String message;

  /**
   * The HTTP status code.
   */
  public Integer statusCode;

  public RestErrorResponse() {
  }

  public RestErrorResponse(String message, Integer statusCode) {
    this.message = message;
    this.statusCode = statusCode;
  }
}
