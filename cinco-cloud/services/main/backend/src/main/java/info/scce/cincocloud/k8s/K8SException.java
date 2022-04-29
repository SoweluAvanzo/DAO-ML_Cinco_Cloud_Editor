package info.scce.cincocloud.k8s;

public class K8SException extends RuntimeException {

  public K8SException(String message) {
    super(message);
  }

  public K8SException(String message, Throwable cause) {
    super(message, cause);
  }
}
