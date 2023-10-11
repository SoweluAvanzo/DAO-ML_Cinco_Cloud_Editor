package info.scce.cincocloud.core.services;

import info.scce.cincocloud.core.rest.tos.PageTO;
import info.scce.cincocloud.core.rest.tos.WorkspaceImageBuildJobTO;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.WorkspaceImageBuildJobDB;
import info.scce.cincocloud.mq.WorkspaceImageAbortBuildJobMessage;
import info.scce.cincocloud.mq.WorkspaceMQProducer;
import info.scce.cincocloud.rest.ObjectCache;
import io.quarkus.panache.common.Page;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@ApplicationScoped
@Transactional
public class WorkspaceImageBuildJobService {

  @Inject
  ObjectCache objectCache;

  @Inject
  WorkspaceMQProducer workspaceMQProducer;

  @Inject
  WorkspaceImageBuildJobLogFileService workspaceImageBuildJobLogFileService;

  public WorkspaceImageBuildJobDB getOrThrow(long jobId) {
    return (WorkspaceImageBuildJobDB) WorkspaceImageBuildJobDB.findByIdOptional(jobId)
        .orElseThrow(() -> new EntityNotFoundException("Build Job could not be found."));
  }

  public PageTO<WorkspaceImageBuildJobTO> getAllPaged(ProjectDB project, int index, int size) {
    final var query = WorkspaceImageBuildJobDB
        .findByProjectIdOrderByStartedAtDesc(project.id);

    final var page = query.page(Page.of(index, size));

    final var items = page.stream()
        .map(j -> WorkspaceImageBuildJobTO.fromEntity(j, objectCache))
        .collect(Collectors.toList());

    return new PageTO<>(items, index, size, page.pageCount(), page.hasPreviousPage(), page.hasNextPage());
  }

  public void delete(long jobId) {
    final var job = getOrThrow(jobId);

    if (!job.isTerminated()) {
      throw new IllegalArgumentException("The job (id: " + job.id + ") is still running");
    }

    job.delete();
    workspaceImageBuildJobLogFileService.deleteLogFile(job.project.id, jobId);
  }

  public WorkspaceImageBuildJobDB abort(long jobId) {
    final var job = getOrThrow(jobId);

    if (job.isTerminated()) {
      throw new IllegalArgumentException("The job (id: " + job.id + ") has already been terminated");
    }

    job.status = WorkspaceImageBuildJobDB.Status.ABORTED;
    job.persist();

    workspaceMQProducer.send(new WorkspaceImageAbortBuildJobMessage(job.id));

    return job;
  }
}
