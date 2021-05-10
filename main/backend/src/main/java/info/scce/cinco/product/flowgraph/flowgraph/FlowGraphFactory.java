package info.scce.cinco.product.flowgraph.flowgraph;
import entity.core.PyroProjectDB;
import info.scce.pyro.sync.ProjectWebSocket;

/**
 * Author zweihoff
 */
public interface FlowGraphFactory {
	public FlowGraphFactory eINSTANCE = info.scce.cinco.product.flowgraph.flowgraph.impl.FlowGraphFactoryImpl.init();
	public FlowGraph createFlowGraph(String projectRelativePath, String filename);
	public void warmup(PyroProjectDB project,
		ProjectWebSocket projectWebSocket,
		entity.core.PyroUserDB subject,
		info.scce.pyro.core.command.FlowGraphCommandExecuter executer
	);
}
