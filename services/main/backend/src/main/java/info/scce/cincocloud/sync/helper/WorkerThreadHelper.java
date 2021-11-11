package info.scce.cincocloud.sync.helper;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;

public class WorkerThreadHelper {

  // NOTE: https://stackoverflow.com/questions/58534957/how-to-execute-jpa-entity-manager-operations-inside-quarkus-kafka-consumer-metho
  public static void runWorkerThread(Runnable work) {
    ManagedExecutor executor = ManagedExecutor.builder()
        .maxAsync(5)
        .propagated(ThreadContext.CDI,
            ThreadContext.TRANSACTION)
        .build();
    ThreadContext threadContext = ThreadContext.builder()
        .propagated(ThreadContext.CDI,
            ThreadContext.TRANSACTION)
        .build();
    executor.runAsync(threadContext.contextualRunnable(work));
  }

  public static void runAsync(Runnable work) {
    ManagedExecutor executor = ManagedExecutor.builder()
        .cleared(ThreadContext.ALL_REMAINING)
        .maxAsync(5)
        .build();
    ThreadContext threadContext = ThreadContext.builder()
        .cleared(ThreadContext.ALL_REMAINING)
        .build();
    executor.runAsync(threadContext.contextualRunnable(work));
  }
}
