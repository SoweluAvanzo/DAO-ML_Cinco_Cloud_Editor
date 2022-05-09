package info.scce.rig.hooks.job;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Job;

public class AddImage extends CincoCustomAction<Job> {
	
	@Override
	public boolean canExecute(Job job) {
		return job.canNewSimpleImage(); //&& job.canNewImage();
	}
	
	@Override
	public String getName() {
		return "(+) Image";
	}

	@Override
	public void execute(Job job) {
		job.newSimpleImage(0, 0);
	}
}
