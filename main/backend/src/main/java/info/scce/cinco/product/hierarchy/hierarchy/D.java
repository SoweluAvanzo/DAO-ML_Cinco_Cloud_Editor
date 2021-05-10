package info.scce.cinco.product.hierarchy.hierarchy;

public interface D extends graphmodel.Node {
	
	public Hierarchy getRootElement();
	public info.scce.cinco.product.hierarchy.hierarchy.ContD getContainer();
	String getOfD();
	void setOfD(String ofd);
	TD getTd();
	void setTd(TD td);
	java.util.List<TD> getTdList();
	void setTdList(java.util.List<TD> tdlist);
}
