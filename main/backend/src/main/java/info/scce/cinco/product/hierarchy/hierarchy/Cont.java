package info.scce.cinco.product.hierarchy.hierarchy;

public interface Cont extends ContA {
	
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
	String getOfCont();
	void setOfCont(String ofcont);
}
