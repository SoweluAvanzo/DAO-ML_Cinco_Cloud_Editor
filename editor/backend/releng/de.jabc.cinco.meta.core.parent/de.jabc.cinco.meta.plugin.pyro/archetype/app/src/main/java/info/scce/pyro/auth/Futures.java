package info.scce.pyro.auth;

/**
 * Author zweihoff
 */
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

class Futures {

    //transforms Future<T> to CompletableFuture<T>
    static <T> CompletableFuture<T> toCompletable(Future<T> future) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }
}