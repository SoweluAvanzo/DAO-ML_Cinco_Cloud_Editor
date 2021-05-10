package info.scce.cinco.product.ha.hooksandactions;

public interface HookAContainer extends graphmodel.Container {
	
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
	public java.util.List<AbstractHookANode> getAbstractHookANodes();
	public HookAContainer newHookAContainer(int x, int y, int width, int height);
	public HookAContainer newHookAContainer(int x, int y);
	public java.util.List<HookAContainer> getHookAContainers();
	public HookANode newHookANode(int x, int y, int width, int height);
	public HookANode newHookANode(int x, int y);
	public java.util.List<HookANode> getHookANodes();
	String getAttribute();
	void setAttribute(String attribute);
}
