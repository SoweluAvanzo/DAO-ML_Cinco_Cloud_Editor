package info.scce.cincocloud.config;

import io.vertx.mutiny.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VertxService {

  @Inject
  Vertx vertx;

  public Vertx getVertx() {
    return vertx;
  }
}
