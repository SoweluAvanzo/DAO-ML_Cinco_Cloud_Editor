package info.scce.cincocloud.core.services;

import info.scce.cincocloud.core.rest.tos.PageTO;
import info.scce.cincocloud.core.rest.tos.WorkspaceImageBuildJobTO;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.WorkspaceImageBuildJobDB;
import info.scce.cincocloud.rest.ObjectCache;
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
  WorkspaceImageBuildJobLogFileService workspaceImageBuildJobLogFileService;

  public WorkspaceImageBuildJobDB getOrThrow(long jobId) {
    return (WorkspaceImageBuildJobDB) WorkspaceImageBuildJobDB.findByIdOptional(jobId)
        .orElseThrow(() -> new EntityNotFoundException("Build Job could not be found."));
  }

  public PageTO<WorkspaceImageBuildJobTO> getAllPaged(ProjectDB project, int index, int size) {
    final var query = WorkspaceImageBuildJobDB.findByProjectIdOrderByStartedAtDesc(project.id);
    return PageTO.ofQuery(query, index, size, j -> WorkspaceImageBuildJobTO.fromEntity(j, objectCache));
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

    // TODO: implement with rabbitmq if the time comes

    return job;
  }
}
