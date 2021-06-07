package info.scce.cincocloud.util;

import io.vertx.mutiny.core.Vertx;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

public class WaitUtils {

    public interface Callback {
        void execute();
    }

    public static void asyncWaitUntil(Vertx vertx, BooleanSupplier condition, Callback thenFn, Callback catchFn, Duration timeout, Duration delay) {
        final var time = new AtomicLong(0);
        vertx.setPeriodic(delay.toMillis(), (timer) -> {
            if (time.get() > timeout.toMillis()) {
                vertx.cancelTimer(timer);
                catchFn.execute();
            } else {
                if (condition.getAsBoolean()) {
                    vertx.cancelTimer(timer);
                    thenFn.execute();
                } else {
                    time.getAndAdd(delay.toMillis());
                }
            }
        });
    }
}
