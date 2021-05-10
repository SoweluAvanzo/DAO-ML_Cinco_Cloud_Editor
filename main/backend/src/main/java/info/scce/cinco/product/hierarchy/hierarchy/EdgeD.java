package info.scce.cinco.product.hierarchy.hierarchy;

public interface EdgeD extends graphmodel.Edge {
	
	public Hierarchy getRootElement();
	public info.scce.cinco.product.hierarchy.hierarchy.Hierarchy getContainer();
	String getOfD();
	void setOfD(String ofd);
	TD getTd();
	void setTd(TD td);
	java.util.List<TD> getTdList();
	void setTdList(java.util.List<TD> tdlist);
}
