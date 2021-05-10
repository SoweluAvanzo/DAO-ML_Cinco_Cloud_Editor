package info.scce.cinco.product.ha.hooksandactions;

public interface HookANode extends AbstractHookANode {
	
	public HooksAndActions getRootElement();
	public info.scce.cinco.product.ha.hooksandactions.HooksAndActions getContainer();
	java.util.List<HookAnEdge> getIncomingHookAnEdges();
	java.util.List<AbstractHookANode> getAbstractHookANodePredecessors();
	java.util.List<HookAContainer> getHookAContainerPredecessors();
	java.util.List<HookANode> getHookANodePredecessors();
	java.util.List<HookAnEdge> getOutgoingHookAnEdges();
	public HookAnEdge newHookAnEdge(AbstractHookANode target);
	public HookAnEdge newHookAnEdge(HookAContainer target);
	public HookAnEdge newHookAnEdge(HookANode target);
	java.util.List<AbstractHookANode> getAbstractHookANodeSuccessors();
	java.util.List<HookAContainer> getHookAContainerSuccessors();
	java.util.List<HookANode> getHookANodeSuccessors();
}
