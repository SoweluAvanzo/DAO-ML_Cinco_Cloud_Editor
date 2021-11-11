package info.scce.cincocloud.config;

import io.vertx.mutiny.core.Vertx;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class VertxService {

  @Inject
  Vertx vertx;

  public Vertx getVertx() {
    return vertx;
  }
}
