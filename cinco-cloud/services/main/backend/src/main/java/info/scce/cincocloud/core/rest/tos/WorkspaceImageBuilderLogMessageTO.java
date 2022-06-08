package info.scce.cincocloud.core.rest.tos;

import info.scce.cincocloud.rest.RESTBaseImpl;
import java.util.List;

public class WorkspaceImageBuilderLogMessageTO extends RESTBaseImpl {

  private long jobId;
  private List<String> logMessages;

  public long getJobId() {
    return jobId;
  }

  public void setJobId(long jobId) {
    this.jobId = jobId;
  }

  public List<String> getLogMessages() {
    return logMessages;
  }

  public void setLogMessages(List<String> logMessages) {
    this.logMessages = logMessages;
  }
}
