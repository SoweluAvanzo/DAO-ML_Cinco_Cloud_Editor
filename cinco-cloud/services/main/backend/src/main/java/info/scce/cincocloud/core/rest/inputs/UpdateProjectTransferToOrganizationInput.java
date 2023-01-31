package info.scce.cincocloud.core.rest.inputs;

import javax.validation.constraints.NotNull;

public class UpdateProjectTransferToOrganizationInput {
  @NotNull(message = "Field 'orgId' may not be null")
  private long orgId;

  public void setOrgId(long orgId) {
    this.orgId = orgId;
  }

  public long getOrgId() {
    return orgId;
  }
}
