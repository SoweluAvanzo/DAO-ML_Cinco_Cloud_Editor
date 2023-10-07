package info.scce.cincocloud.util;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import java.time.Duration;

public class WaitUtils {

  public static void asyncWaitUntil(Uni<Boolean> condition, Callback thenFn,
      Callback catchFn, Duration timeout, Duration delay) {
    final var vertx = CDIUtils.getBean(Vertx.class);
    final var startTime = System.currentTimeMillis();
    asyncWaitUntil(vertx, condition, thenFn, catchFn, timeout, delay, startTime);
  }

  private static void asyncWaitUntil(Vertx vertx, Uni<Boolean> condition, Callback thenFn,
      Callback catchFn, Duration timeout, Duration delay, Long startTime) {
    condition.subscribe()
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

  private static void retry(Vertx vertx, Uni<Boolean> condition, Callback thenFn,
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
