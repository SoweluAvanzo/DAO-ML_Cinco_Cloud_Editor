package info.scce.cinco.product.ha.hooksandactions;

public interface HooksAndActions extends graphmodel.GraphModel {
	
	public String getRouter();
	public String getConnector();
	public long getWidth();
	public long getHeight();
	public double getScale();
	public String getFileName();
	public String getExtension();
	public java.util.List<HookAnEdge> getHookAnEdges();
	public java.util.List<AbstractHookANode> getAbstractHookANodes();
	public HookAContainer newHookAContainer(int x, int y, int width, int height);
	public HookAContainer newHookAContainer(int x, int y);
	public java.util.List<HookAContainer> getHookAContainers();
	public HookANode newHookANode(int x, int y, int width, int height);
	public HookANode newHookANode(int x, int y);
	public java.util.List<HookANode> getHookANodes();
	HookAType getAtype();
	void setAtype(HookAType atype);
	String getAttribute();
	void setAttribute(String attribute);
}
