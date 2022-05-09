package info.scce.rig.hooks.job.service;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Service;

public class AddCommand extends CincoCustomAction<Service> {
	
	@Override
	public boolean canExecute(Service service) {
		return service.canNewCommand();
	}
	
	@Override
	public String getName() {
		return "(+) Command";
	}

	@Override
	public void execute(Service service) {
		service.newCommand(0, 0);
	}
}
