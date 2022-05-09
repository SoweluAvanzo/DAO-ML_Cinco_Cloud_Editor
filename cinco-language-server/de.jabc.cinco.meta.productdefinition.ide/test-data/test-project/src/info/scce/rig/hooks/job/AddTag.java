package info.scce.rig.hooks.job;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Job;

public class AddTag extends CincoCustomAction<Job> {
	
	@Override
	public boolean canExecute(Job job) {
		return job.canNewTag();
	}
	
	@Override
	public String getName() {
		return "(+) Tag";
	}

	@Override
	public void execute(Job job) {
		job.newTag(0, 0);
	}
}
