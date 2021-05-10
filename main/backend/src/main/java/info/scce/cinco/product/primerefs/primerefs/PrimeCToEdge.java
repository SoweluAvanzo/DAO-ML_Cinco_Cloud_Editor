package info.scce.cinco.product.primerefs.primerefs;

public interface PrimeCToEdge extends graphmodel.Container {
	
	public PrimeRefs getRootElement();
	public info.scce.cinco.product.primerefs.primerefs.PrimeRefs getContainer();
	public info.scce.cinco.product.primerefs.primerefs.SourceEdge getPr();
	public SourceNode newSourceNode(int x, int y, int width, int height);
	public SourceNode newSourceNode(int x, int y);
	public java.util.List<SourceNode> getSourceNodes();
	public SourceContainer newSourceContainer(int x, int y, int width, int height);
	public SourceContainer newSourceContainer(int x, int y);
	public java.util.List<SourceContainer> getSourceContainers();
	public PrimeToNode newPrimeToNode(
		long primeId,
		int x,
		int y
	);
	public PrimeToNode newPrimeToNode(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<PrimeToNode> getPrimeToNodes();
	public PrimeToEdge newPrimeToEdge(
		long primeId,
		int x,
		int y
	);
	public PrimeToEdge newPrimeToEdge(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<PrimeToEdge> getPrimeToEdges();
	public PrimeToContainer newPrimeToContainer(
		long primeId,
		int x,
		int y
	);
	public PrimeToContainer newPrimeToContainer(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<PrimeToContainer> getPrimeToContainers();
	public PrimeToGraphModel newPrimeToGraphModel(
		long primeId,
		int x,
		int y
	);
	public PrimeToGraphModel newPrimeToGraphModel(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<PrimeToGraphModel> getPrimeToGraphModels();
	public PrimeCToNode newPrimeCToNode(
		long primeId,
		int x,
		int y
	);
	public PrimeCToNode newPrimeCToNode(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<PrimeCToNode> getPrimeCToNodes();
	public PrimeCToEdge newPrimeCToEdge(
		long primeId,
		int x,
		int y
	);
	public PrimeCToEdge newPrimeCToEdge(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<PrimeCToEdge> getPrimeCToEdges();
	public PrimeCToContainer newPrimeCToContainer(
		long primeId,
		int x,
		int y
	);
	public PrimeCToContainer newPrimeCToContainer(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<PrimeCToContainer> getPrimeCToContainers();
	public PrimeCToGraphModel newPrimeCToGraphModel(
		long primeId,
		int x,
		int y
	);
	public PrimeCToGraphModel newPrimeCToGraphModel(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<PrimeCToGraphModel> getPrimeCToGraphModels();
	public PrimeToNodeHierarchy newPrimeToNodeHierarchy(
		long primeId,
		int x,
		int y
	);
	public PrimeToNodeHierarchy newPrimeToNodeHierarchy(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<PrimeToNodeHierarchy> getPrimeToNodeHierarchys();
	public PrimeToAbstractNodeHierarchy newPrimeToAbstractNodeHierarchy(
		long primeId,
		int x,
		int y
	);
	public PrimeToAbstractNodeHierarchy newPrimeToAbstractNodeHierarchy(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<PrimeToAbstractNodeHierarchy> getPrimeToAbstractNodeHierarchys();
	public PrimeToNodeFlow newPrimeToNodeFlow(
		long primeId,
		int x,
		int y
	);
	public PrimeToNodeFlow newPrimeToNodeFlow(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<PrimeToNodeFlow> getPrimeToNodeFlows();
	public PrimeToEdgeFlow newPrimeToEdgeFlow(
		long primeId,
		int x,
		int y
	);
	public PrimeToEdgeFlow newPrimeToEdgeFlow(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<PrimeToEdgeFlow> getPrimeToEdgeFlows();
	public PrimeToContainerFlow newPrimeToContainerFlow(
		long primeId,
		int x,
		int y
	);
	public PrimeToContainerFlow newPrimeToContainerFlow(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<PrimeToContainerFlow> getPrimeToContainerFlows();
	public PrimeToGraphModelFlow newPrimeToGraphModelFlow(
		long primeId,
		int x,
		int y
	);
	public PrimeToGraphModelFlow newPrimeToGraphModelFlow(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<PrimeToGraphModelFlow> getPrimeToGraphModelFlows();
	public PrimeCToNodeFlow newPrimeCToNodeFlow(
		long primeId,
		int x,
		int y
	);
	public PrimeCToNodeFlow newPrimeCToNodeFlow(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<PrimeCToNodeFlow> getPrimeCToNodeFlows();
	public PrimeCToEdgeFlow newPrimeCToEdgeFlow(
		long primeId,
		int x,
		int y
	);
	public PrimeCToEdgeFlow newPrimeCToEdgeFlow(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<PrimeCToEdgeFlow> getPrimeCToEdgeFlows();
	public PrimeCToContainerFlow newPrimeCToContainerFlow(
		long primeId,
		int x,
		int y
	);
	public PrimeCToContainerFlow newPrimeCToContainerFlow(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<PrimeCToContainerFlow> getPrimeCToContainerFlows();
	public PrimeCToGraphModelFlow newPrimeCToGraphModelFlow(
		long primeId,
		int x,
		int y
	);
	public PrimeCToGraphModelFlow newPrimeCToGraphModelFlow(
		long primeId,
		int x,
		int y,
		int width,
		int height
	);
	public java.util.List<PrimeCToGraphModelFlow> getPrimeCToGraphModelFlows();
}
