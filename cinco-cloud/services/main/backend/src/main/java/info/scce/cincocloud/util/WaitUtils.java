package info.scce.cincocloud.util;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import java.time.Duration;
import java.util.function.BooleanSupplier;

public class WaitUtils {

  public static void asyncWaitUntil(Vertx vertx, BooleanSupplier condition, Callback thenFn,
      Callback catchFn, Duration timeout, Duration delay) {
    final var result = Uni.createFrom().item(condition);
    final var startTime = System.currentTimeMillis();
    asyncWaitUntil(vertx, result, thenFn, catchFn, timeout, delay, startTime);
  }

  public static void asyncWaitUntil(Vertx vertx, Uni<Boolean> condition, Callback thenFn,
      Callback catchFn, Duration timeout, Duration delay) {
    final var result = condition.map(v -> (BooleanSupplier) () -> v);
    final var startTime = System.currentTimeMillis();
    asyncWaitUntil(vertx, result, thenFn, catchFn, timeout, delay, startTime);
  }

  private static void asyncWaitUntil(Vertx vertx, Uni<BooleanSupplier> condition, Callback thenFn,
      Callback catchFn, Duration timeout, Duration delay, Long startTime) {
    condition.map(BooleanSupplier::getAsBoolean).subscribe()
        .with(
            conditionFulfilled -> {
              if (conditionFulfilled) {
                thenFn.execute();
              } else {
                retry(vertx, condition, thenFn, catchFn, timeout, delay, startTime);
              }
            },
            err -> retry(vertx, condition, thenFn, catchFn, timeout, delay, startTime));
  }

  private static void retry(Vertx vertx, Uni<BooleanSupplier> condition, Callback thenFn,
      Callback catchFn, Duration timeout, Duration delay, Long startTime) {
    final var timePassed = System.currentTimeMillis() - startTime;
    if (timePassed > timeout.toMillis()) {
      catchFn.execute();
    } else {
      vertx.setTimer(delay.toMillis(),
          t -> asyncWaitUntil(vertx, condition, thenFn, catchFn, timeout, delay, startTime));
    }
  }

  public interface Callback {

    void execute();
  }
}
