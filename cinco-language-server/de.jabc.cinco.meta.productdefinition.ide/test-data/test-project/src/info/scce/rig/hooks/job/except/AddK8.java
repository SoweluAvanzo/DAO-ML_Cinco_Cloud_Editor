package info.scce.rig.hooks.job.except;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Except;

public class AddK8 extends CincoCustomAction<Except> {

	@Override
	public boolean canExecute(Except except) {
		return except.canNewKubernetes();
	}
	
	@Override
	public String getName() {
		return "(+) Kubernetes";
	}
	
	@Override
	public void execute(Except except) {
		except.newKubernetes(0, 0);
	}
}
