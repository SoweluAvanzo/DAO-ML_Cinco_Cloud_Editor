package info.scce.cincocloud.util;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import java.time.Duration;
import java.util.function.BooleanSupplier;

public class WaitUtils {

  public static void asyncWaitUntil(Vertx vertx, BooleanSupplier condition, Callback thenFn,
      Callback catchFn, Duration timeout, Duration delay) {
    asyncWaitUntil(vertx, Uni.createFrom().item(condition).map(BooleanSupplier::getAsBoolean),
        thenFn, catchFn, timeout, delay);
  }

  public static void asyncWaitUntil(Vertx vertx, Uni<Boolean> condition, Callback thenFn,
      Callback catchFn, Duration timeout, Duration delay) {
    final var startTime = System.currentTimeMillis();
    asyncWaitUntil(vertx, condition, thenFn, catchFn, timeout, delay, startTime);
  }

  private static void asyncWaitUntil(Vertx vertx, Uni<Boolean> condition, Callback thenFn,
      Callback catchFn, Duration timeout, Duration delay, Long startTime) {
    condition.subscribe()
        .with(
            res -> thenFn.execute(),
            err -> {
              final var timePassed = System.currentTimeMillis() - startTime;
              if (timePassed > timeout.toMillis()) {
                catchFn.execute();
              } else {
                vertx.setTimer(delay.toMillis(),
                    t -> asyncWaitUntil(vertx, condition, thenFn, catchFn, timeout, delay,
                        startTime));
              }
            });
  }

  public interface Callback {

    void execute();
  }
}
