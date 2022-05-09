package info.scce.rig.hooks.job;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Job;

public class AddOnly extends CincoCustomAction<Job> {
	
	@Override
	public boolean canExecute(Job job) {
		return job.canNewSimpleOnly() && job.canNewSimpleOnly();
	}
	
	@Override
	public String getName() {
		return "(+) Only";
	}

	@Override
	public void execute(Job job) {
		job.newSimpleOnly(0, 0);
	}
}