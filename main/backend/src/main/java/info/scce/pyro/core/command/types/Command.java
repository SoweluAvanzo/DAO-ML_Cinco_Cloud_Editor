package info.scce.pyro.core.command.types;

/**
 * Author zweihoff
 */

@com.fasterxml.jackson.annotation.JsonFilter("PYRO_Selective_Filter")
@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include=com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY, property = info.scce.pyro.util.Constants.PYRO_RUNTIME_TYPE)
@com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(name = "info.scce.pyro.core.command.types.AppearanceCommand", value = AppearanceCommand.class),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(name = "info.scce.pyro.core.command.types.CheckResultCommand", value = CheckResultCommand.class),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(name = "info.scce.pyro.core.command.types.CreateNodeCommand", value = CreateNodeCommand.class),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(name = "info.scce.pyro.core.command.types.MoveNodeCommand", value = MoveNodeCommand.class),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(name = "info.scce.pyro.core.command.types.ReconnectEdgeCommand", value = ReconnectEdgeCommand.class),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(name = "info.scce.pyro.core.command.types.RemoveEdgeCommand", value = RemoveEdgeCommand.class),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(name = "info.scce.pyro.core.command.types.UpdateBendPointCommand", value = UpdateBendPointCommand.class),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(name = "info.scce.pyro.core.command.types.CreateEdgeCommand", value = CreateEdgeCommand.class),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(name = "info.scce.pyro.core.command.types.RemoveNodeCommand", value = RemoveNodeCommand.class),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(name = "info.scce.pyro.core.command.types.ResizeNodeCommand", value = ResizeNodeCommand.class),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(name = "info.scce.pyro.core.command.types.UpdateCommand", value = UpdateCommand.class)
})
public abstract class Command {
	
    @com.fasterxml.jackson.annotation.JsonProperty("delegateId")
    long delegateId;
    
    @com.fasterxml.jackson.annotation.JsonProperty("type")
    String type;

    public long getDelegateId() {
        return delegateId;
    }

    public void setDelegateId(long delegateId) {
        this.delegateId = delegateId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public final void rewriteId(long oldId,long newId) {
        if (delegateId==oldId) {
            delegateId = newId;
        }
    }
    
    abstract protected void rewrite(long oldId,long newId);
}