package info.scce.rig.hooks.job.only;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Only;

public class AddK8 extends CincoCustomAction<Only> {

	@Override
	public boolean canExecute(Only only) {
		return only.canNewKubernetes();
	}
	
	@Override
	public String getName() {
		return "(+) Kubernetes";
	}
	
	@Override
	public void execute(Only only) {
		only.newKubernetes(0, 0);
	}
}
