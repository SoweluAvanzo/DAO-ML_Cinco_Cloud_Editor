package info.scce.rig.hooks.job;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Job;


public class AddAllowFailure extends CincoCustomAction<Job> {
	
	@Override
	public boolean canExecute(Job job) {
		return job.canNewAllowFailure();
	}
	
	@Override
	public String getName() {
		return "(+) Allow Failure";
	}

	@Override
	public void execute(Job job) {
		job.newAllowFailure(0, 0);
	}

}
