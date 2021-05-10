package info.scce.cinco.product.hierarchy.hierarchy;

public interface ContB extends ContC {
	
	public Hierarchy getRootElement();
	public info.scce.cinco.product.hierarchy.hierarchy.Hierarchy getContainer();
	public java.util.List<B> getBs();
	public A newA(int x, int y, int width, int height);
	public A newA(int x, int y);
	public java.util.List<A> getAs();
	public java.util.List<C> getCs();
	public D newD(int x, int y, int width, int height);
	public D newD(int x, int y);
	public java.util.List<D> getDs();
	String getOfContB();
	void setOfContB(String ofcontb);
	TB getTb();
	void setTb(TB tb);
	java.util.List<TB> getTbList();
	void setTbList(java.util.List<TB> tblist);
}
