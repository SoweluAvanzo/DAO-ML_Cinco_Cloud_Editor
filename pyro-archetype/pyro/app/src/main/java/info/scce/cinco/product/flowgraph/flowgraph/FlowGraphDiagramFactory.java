package info.scce.cinco.product.flowgraph.flowgraph;

/**
 * Author zweihoff
 */
public interface FlowGraphDiagramFactory {
	public FlowGraphDiagramFactory eINSTANCE = info.scce.cinco.product.flowgraph.flowgraph.impl.FlowGraphDiagramFactoryImpl.init();
	public FlowGraphDiagram createFlowGraphDiagram(String projectRelativePath, String filename);
	public void warmup(
		info.scce.pyro.core.command.FlowGraphDiagramCommandExecuter executer
	);
}
