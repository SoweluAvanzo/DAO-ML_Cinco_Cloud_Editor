package info.scce.cinco.product.hierarchy.hierarchy;

public interface EdgeA extends EdgeB {
	
	public Hierarchy getRootElement();
	public info.scce.cinco.product.hierarchy.hierarchy.Hierarchy getContainer();
	String getOfA();
	void setOfA(String ofa);
	TA getTa();
	void setTa(TA ta);
	java.util.List<TA> getTaList();
	void setTaList(java.util.List<TA> talist);
}
