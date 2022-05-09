package info.scce.rig.hooks.job.release;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Release;

public class AddDescription extends CincoCustomAction<Release> {
	
	@Override
	public boolean canExecute(Release release) {
		return release.canNewDescription();
	}
	
	@Override
	public String getName() {
		return "(+)  Description";
	}
	@Override
	public void execute (Release release) {
		release.newDescription(0,0);
	}
}
