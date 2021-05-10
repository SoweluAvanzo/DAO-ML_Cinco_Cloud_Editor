package info.scce.cinco.product.empty.empty;
import entity.core.PyroProjectDB;
import info.scce.pyro.sync.ProjectWebSocket;

/**
 * Author zweihoff
 */
public interface EmptyFactory {
	public EmptyFactory eINSTANCE = info.scce.cinco.product.empty.empty.impl.EmptyFactoryImpl.init();
	public Empty createEmpty(String projectRelativePath, String filename);
	public void warmup(PyroProjectDB project,
		ProjectWebSocket projectWebSocket,
		entity.core.PyroUserDB subject,
		info.scce.pyro.core.command.EmptyCommandExecuter executer
	);
}
