package info.scce.rig.hooks.job;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Job;

public class AddExcept extends CincoCustomAction<Job> {
	
	@Override
	public boolean canExecute(Job job) {
		return job.canNewSimpleExcept() && job.canNewSimpleExcept();
	}
	
	@Override
	public String getName() {
		return "(+) Except";
	}

	@Override
	public void execute(Job job) {
		job.newSimpleExcept(0, 0);
	}
}