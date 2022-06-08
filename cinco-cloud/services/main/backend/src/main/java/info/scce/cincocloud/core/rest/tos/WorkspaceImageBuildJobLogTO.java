package info.scce.cincocloud.core.rest.tos;

import info.scce.cincocloud.rest.RESTBaseImpl;

public class WorkspaceImageBuildJobLogTO extends RESTBaseImpl {

  private long jobId;
  private Status logStatus;
  private String log;

  public Status getLogStatus() {
    return logStatus;
  }

  public void setLogStatus(Status logStatus) {
    this.logStatus = logStatus;
  }

  public long getJobId() {
    return jobId;
  }

  public void setJobId(long jobId) {
    this.jobId = jobId;
  }

  public String getLog() {
    return log;
  }

  public void setLog(String log) {
    this.log = log;
  }

  public enum Status {
    COMPLETE,
    PARTIAL,
    MISSING
  }
}
