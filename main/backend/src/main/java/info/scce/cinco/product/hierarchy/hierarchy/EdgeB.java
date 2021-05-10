package info.scce.cinco.product.hierarchy.hierarchy;

public interface EdgeB extends EdgeC {
	
	public Hierarchy getRootElement();
	public info.scce.cinco.product.hierarchy.hierarchy.Hierarchy getContainer();
	String getOfB();
	void setOfB(String ofb);
	TB getTb();
	void setTb(TB tb);
	java.util.List<TB> getTbList();
	void setTbList(java.util.List<TB> tblist);
}
