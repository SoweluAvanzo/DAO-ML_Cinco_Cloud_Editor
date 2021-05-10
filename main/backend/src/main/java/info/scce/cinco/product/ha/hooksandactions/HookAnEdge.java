package info.scce.cinco.product.ha.hooksandactions;

public interface HookAnEdge extends graphmodel.Edge {
	
	public HooksAndActions getRootElement();
	public info.scce.cinco.product.ha.hooksandactions.HooksAndActions getContainer();
	String getAttribute();
	void setAttribute(String attribute);
}
