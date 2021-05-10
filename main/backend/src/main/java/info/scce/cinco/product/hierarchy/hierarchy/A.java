package info.scce.cinco.product.hierarchy.hierarchy;

public interface A extends B {
	
	public Hierarchy getRootElement();
	public info.scce.cinco.product.hierarchy.hierarchy.ContD getContainer();
	String getOfA();
	void setOfA(String ofa);
	TA getTa();
	void setTa(TA ta);
	java.util.List<TA> getTaList();
	void setTaList(java.util.List<TA> talist);
}
