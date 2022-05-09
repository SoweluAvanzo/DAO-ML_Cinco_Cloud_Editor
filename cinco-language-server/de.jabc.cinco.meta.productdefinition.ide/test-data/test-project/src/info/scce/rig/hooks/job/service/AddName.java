package info.scce.rig.hooks.job.service;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Service;

public class AddName extends CincoCustomAction<Service> {
	
	@Override
	public boolean canExecute(Service service) {
		return service.canNewName();
	}
	
	@Override
	public String getName() {
		return "(+) Name";
	}

	@Override
	public void execute(Service service) {
		service.newName(0, 0);
	}
}
