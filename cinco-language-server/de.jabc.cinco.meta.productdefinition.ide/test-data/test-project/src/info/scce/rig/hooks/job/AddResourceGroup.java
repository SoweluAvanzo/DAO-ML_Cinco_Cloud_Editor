package info.scce.rig.hooks.job;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Job;

public class AddResourceGroup extends CincoCustomAction<Job> {
	
	@Override
	public boolean canExecute(Job job) {
		return job.canNewResourceGroup();
	}
	
	@Override
	public String getName() {
		return "(+) Resource Group";
	}

	@Override
	public void execute(Job job) {
		job.newResourceGroup(0, 0);
	}
}
