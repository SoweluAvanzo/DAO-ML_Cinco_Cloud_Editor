package info.scce.rig.hooks.job;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Job;

public class AddInterruptible extends CincoCustomAction<Job> {
	
	@Override
	public boolean canExecute(Job job) {
		return job.canNewInterruptible();
	}
	
	@Override
	public String getName() {
		return "(+) Interruptible";
	}

	@Override
	public void execute(Job job) {
		job.newInterruptible(0, 0);
	}
}
