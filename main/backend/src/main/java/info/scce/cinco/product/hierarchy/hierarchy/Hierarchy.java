package info.scce.cinco.product.hierarchy.hierarchy;

public interface Hierarchy extends graphmodel.GraphModel {
	
	public String getRouter();
	public String getConnector();
	public long getWidth();
	public long getHeight();
	public double getScale();
	public String getFileName();
	public String getExtension();
	public java.util.List<EdgeA> getEdgeAs();
	public java.util.List<EdgeC> getEdgeCs();
	public java.util.List<EdgeB> getEdgeBs();
	public java.util.List<EdgeD> getEdgeDs();
	public java.util.List<ContB> getContBs();
	public ContA newContA(int x, int y, int width, int height);
	public ContA newContA(int x, int y);
	public java.util.List<ContA> getContAs();
	public Cont newCont(int x, int y, int width, int height);
	public Cont newCont(int x, int y);
	public java.util.List<Cont> getConts();
	TA getTa();
	void setTa(TA ta);
	String getModelName();
	void setModelName(String modelname);
}
