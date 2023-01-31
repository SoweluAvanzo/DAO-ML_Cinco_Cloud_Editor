package info.scce.cincocloud.core.services;

import info.scce.cincocloud.core.rest.tos.PageTO;
import info.scce.cincocloud.core.rest.tos.WorkspaceImageBuildJobTO;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.WorkspaceImageBuildJobDB;
import info.scce.cincocloud.mq.WorkspaceImageAbortBuildJobMessage;
import info.scce.cincocloud.mq.WorkspaceMQProducer;
import info.scce.cincocloud.rest.ObjectCache;
import io.quarkus.panache.common.Page;
import java.util.List;
import java.util.Optional;
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

  public List<WorkspaceImageBuildJobDB> getAll(ProjectDB project, Optional<Integer> page, Optional<Integer> size) {
    return WorkspaceImageBuildJobDB.findByProjectId(project.id).list();
  }

  public PageTO<WorkspaceImageBuildJobTO> getAllPaged(ProjectDB project, int page, int size) {
    final var query = WorkspaceImageBuildJobDB
        .findByProjectIdOrderByStartedAtDesc(project.id);

    final var amountOfPages = (long) Math.ceil((double) query.count() / size);

    final var items = query
        .page(Page.of(page, size)).stream()
        .map(j -> WorkspaceImageBuildJobTO.fromEntity(j, objectCache))
        .collect(Collectors.toList());

    return new PageTO<>(items, page, size, amountOfPages);
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
