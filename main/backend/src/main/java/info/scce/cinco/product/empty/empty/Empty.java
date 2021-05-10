package info.scce.cinco.product.empty.empty;

public interface Empty extends graphmodel.GraphModel {
	
	public String getRouter();
	public String getConnector();
	public long getWidth();
	public long getHeight();
	public double getScale();
	public String getFileName();
	public String getExtension();
	String getModelName();
	void setModelName(String modelname);
}
