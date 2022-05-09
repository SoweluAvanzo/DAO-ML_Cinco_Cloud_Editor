package info.scce.rig.hooks.job;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Job;

public class AddParallel extends CincoCustomAction<Job> {
	
	@Override
	public boolean canExecute(Job job) {
		return job.canNewParallel();
	}
	
	@Override
	public String getName() {
		return "(+) Parallel";
	}

	@Override
	public void execute(Job job) {
		job.newParallel(0, 0);
	}
}
