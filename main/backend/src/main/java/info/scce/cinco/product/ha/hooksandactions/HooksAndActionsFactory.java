package info.scce.cinco.product.ha.hooksandactions;
import entity.core.PyroProjectDB;
import info.scce.pyro.sync.ProjectWebSocket;

/**
 * Author zweihoff
 */
public interface HooksAndActionsFactory {
	public HooksAndActionsFactory eINSTANCE = info.scce.cinco.product.ha.hooksandactions.impl.HooksAndActionsFactoryImpl.init();
	public HooksAndActions createHooksAndActions(String projectRelativePath, String filename);
	public void warmup(PyroProjectDB project,
		ProjectWebSocket projectWebSocket,
		entity.core.PyroUserDB subject,
		info.scce.pyro.core.command.HooksAndActionsCommandExecuter executer
	);
	info.scce.cinco.product.ha.hooksandactions.HookAType createHookAType();
}
