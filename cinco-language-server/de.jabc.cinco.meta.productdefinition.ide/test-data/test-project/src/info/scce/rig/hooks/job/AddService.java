package info.scce.rig.hooks.job;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Job;

public class AddService  extends CincoCustomAction<Job> {
	
	@Override
	public boolean canExecute(Job job) {
		return job.canNewSimpleService();
	}
	
	@Override
	public String getName() {
		return "(+) Service";
	}

	@Override
	public void execute(Job job) {
		job.newSimpleService(0, 0);
	}
}
