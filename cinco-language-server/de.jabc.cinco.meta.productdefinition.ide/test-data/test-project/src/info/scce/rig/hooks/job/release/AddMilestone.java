package info.scce.rig.hooks.job.release;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Release;

public class AddMilestone extends CincoCustomAction<Release> {
	
	@Override
	public boolean canExecute(Release release) {
		return release.canNewMilestone();
	}
	
	@Override
	public String getName() {
		return "(+)  Milestone";
	}
	
	@Override
	public void execute (Release release) {
		release.newMilestone(0, 0);
	}
}
