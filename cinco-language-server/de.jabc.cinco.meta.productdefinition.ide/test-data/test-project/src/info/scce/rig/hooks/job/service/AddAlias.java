package info.scce.rig.hooks.job.service;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Service;

public class AddAlias extends CincoCustomAction<Service> {
	
	@Override
	public boolean canExecute(Service service) {
		return service.canNewAlias();
	}
	
	@Override
	public String getName() {
		return "(+) Alias";
	}

	@Override
	public void execute(Service service) {
		service.newAlias(0, 0);
	}
}
