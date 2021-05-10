package info.scce.cinco.product.primerefs.primerefs;

public interface PrimeToEdgeFlow extends graphmodel.Node {
	
	public PrimeRefs getRootElement();
	public info.scce.cinco.product.primerefs.primerefs.PrimeRefs getContainer();
	public info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition getPr();
}
