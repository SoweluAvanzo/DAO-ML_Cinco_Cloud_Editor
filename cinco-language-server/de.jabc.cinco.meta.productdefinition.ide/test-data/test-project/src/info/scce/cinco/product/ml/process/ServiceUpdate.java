package info.scce.cinco.product.ml.process;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.cinco.product.ml.process.mlprocess.DataService;

public class ServiceUpdate extends CincoCustomAction<DataService> {

	@Override
	public void execute(DataService element) {
		element.getNodes().forEach(n->n.delete());
		new ServicePostCreate().postCreate(element);
	}
	
	public String getName() {
		return "Synchronize";
	}
}