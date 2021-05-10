package info.scce.cinco.product.hierarchy.hierarchy;

public interface EdgeC extends EdgeD {
	
	public Hierarchy getRootElement();
	public info.scce.cinco.product.hierarchy.hierarchy.Hierarchy getContainer();
	String getOfC();
	void setOfC(String ofc);
	TC getTc();
	void setTc(TC tc);
	java.util.List<TC> getTcList();
	void setTcList(java.util.List<TC> tclist);
}
