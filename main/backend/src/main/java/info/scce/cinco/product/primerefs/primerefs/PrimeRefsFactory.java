package info.scce.cinco.product.primerefs.primerefs;
import entity.core.PyroProjectDB;
import info.scce.pyro.sync.ProjectWebSocket;

/**
 * Author zweihoff
 */
public interface PrimeRefsFactory {
	public PrimeRefsFactory eINSTANCE = info.scce.cinco.product.primerefs.primerefs.impl.PrimeRefsFactoryImpl.init();
	public PrimeRefs createPrimeRefs(String projectRelativePath, String filename);
	public void warmup(PyroProjectDB project,
		ProjectWebSocket projectWebSocket,
		entity.core.PyroUserDB subject,
		info.scce.pyro.core.command.PrimeRefsCommandExecuter executer
	);
}
