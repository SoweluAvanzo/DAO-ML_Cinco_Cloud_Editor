package info.scce.cinco.product.primerefs.primerefs;

public interface PrimeToGraphModelFlow extends graphmodel.Node {
	
	public PrimeRefs getRootElement();
	public info.scce.cinco.product.primerefs.primerefs.PrimeRefs getContainer();
	public info.scce.cinco.product.flowgraph.flowgraph.FlowGraph getPr();
}
