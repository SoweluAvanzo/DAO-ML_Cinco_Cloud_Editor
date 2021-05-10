package info.scce.cinco.product.hierarchy.hierarchy;
import entity.core.PyroProjectDB;
import info.scce.pyro.sync.ProjectWebSocket;

/**
 * Author zweihoff
 */
public interface HierarchyFactory {
	public HierarchyFactory eINSTANCE = info.scce.cinco.product.hierarchy.hierarchy.impl.HierarchyFactoryImpl.init();
	public Hierarchy createHierarchy(String projectRelativePath, String filename);
	public void warmup(PyroProjectDB project,
		ProjectWebSocket projectWebSocket,
		entity.core.PyroUserDB subject,
		info.scce.pyro.core.command.HierarchyCommandExecuter executer
	);
	info.scce.cinco.product.hierarchy.hierarchy.TA createTA();
	info.scce.cinco.product.hierarchy.hierarchy.TD createTD();
}
