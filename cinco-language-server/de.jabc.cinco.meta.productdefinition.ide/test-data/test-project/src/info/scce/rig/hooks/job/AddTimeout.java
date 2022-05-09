package info.scce.rig.hooks.job;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Job;

public class AddTimeout extends CincoCustomAction<Job> {
	
	@Override
	public boolean canExecute(Job job) {
		return job.canNewTimeout();
	}
	
	@Override
	public String getName() {
		return "(+) Timeout";
	}

	@Override
	public void execute(Job job) {
		job.newTimeout(0, 0);
	}
}
