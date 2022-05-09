package info.scce.rig.hooks.job;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Job;

public class AddCoverage extends CincoCustomAction<Job> {
	
	@Override
	public boolean canExecute(Job job) {
		return job.canNewCoverage();
	}
	
	@Override
	public String getName() {
		return "(+) Coverage";
	}

	@Override
	public void execute(Job job) {
		job.newCoverage(0, 0);
	}
}
