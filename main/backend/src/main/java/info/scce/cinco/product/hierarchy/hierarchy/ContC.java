package info.scce.cinco.product.hierarchy.hierarchy;

public interface ContC extends ContD {
	
	public Hierarchy getRootElement();
	public info.scce.cinco.product.hierarchy.hierarchy.Hierarchy getContainer();
	public java.util.List<C> getCs();
	public java.util.List<B> getBs();
	public A newA(int x, int y, int width, int height);
	public A newA(int x, int y);
	public java.util.List<A> getAs();
	public D newD(int x, int y, int width, int height);
	public D newD(int x, int y);
	public java.util.List<D> getDs();
	String getOfContC();
	void setOfContC(String ofcontc);
	TC getTc();
	void setTc(TC tc);
	java.util.List<TC> getTcList();
	void setTcList(java.util.List<TC> tclist);
}
