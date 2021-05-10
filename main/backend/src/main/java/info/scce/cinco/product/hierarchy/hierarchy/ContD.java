package info.scce.cinco.product.hierarchy.hierarchy;

public interface ContD extends graphmodel.Container {
	
	public Hierarchy getRootElement();
	public info.scce.cinco.product.hierarchy.hierarchy.Hierarchy getContainer();
	public D newD(int x, int y, int width, int height);
	public D newD(int x, int y);
	public java.util.List<D> getDs();
	public java.util.List<C> getCs();
	public java.util.List<B> getBs();
	public A newA(int x, int y, int width, int height);
	public A newA(int x, int y);
	public java.util.List<A> getAs();
	String getOfContD();
	void setOfContD(String ofcontd);
	TD getTd();
	void setTd(TD td);
	java.util.List<TD> getTdList();
	void setTdList(java.util.List<TD> tdlist);
}
