package info.scce.rig.hooks.job;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Job;

public class AddRetry extends CincoCustomAction<Job> {
	
	@Override
	public boolean canExecute(Job job) {
		return job.canNewSimpleRetry(); // && job.canNewRetry();
	}
	
	@Override
	public String getName() {
		return "(+) Retry";
	}

	@Override
	public void execute(Job job) {
		job.newSimpleRetry(0, 0);
	}
}
