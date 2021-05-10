package info.scce.cinco.product.hierarchy.hierarchy;

public interface ContA extends ContB {
	
	public Hierarchy getRootElement();
	public info.scce.cinco.product.hierarchy.hierarchy.Hierarchy getContainer();
	public A newA(int x, int y, int width, int height);
	public A newA(int x, int y);
	public java.util.List<A> getAs();
	public java.util.List<B> getBs();
	public java.util.List<C> getCs();
	public D newD(int x, int y, int width, int height);
	public D newD(int x, int y);
	public java.util.List<D> getDs();
	String getOfContA();
	void setOfContA(String ofconta);
	TA getTa();
	void setTa(TA ta);
	java.util.List<TA> getTaList();
	void setTaList(java.util.List<TA> talist);
}
