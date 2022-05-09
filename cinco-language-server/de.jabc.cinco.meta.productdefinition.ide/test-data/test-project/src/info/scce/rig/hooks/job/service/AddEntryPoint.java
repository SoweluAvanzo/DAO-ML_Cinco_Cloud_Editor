package info.scce.rig.hooks.job.service;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Service;

public class AddEntryPoint extends CincoCustomAction<Service> {
	
	@Override
	public boolean canExecute(Service service) {
		return service.canNewEntryPoint();
	}
	
	@Override
	public String getName() {
		return "(+) Entrypoint";
	}

	@Override
	public void execute(Service service) {
		service.newEntryPoint(0, 0);
	}
}
