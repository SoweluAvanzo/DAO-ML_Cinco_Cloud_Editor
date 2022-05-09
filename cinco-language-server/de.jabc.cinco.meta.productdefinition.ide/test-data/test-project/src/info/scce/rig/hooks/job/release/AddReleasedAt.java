package info.scce.rig.hooks.job.release;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Release;

public class AddReleasedAt extends CincoCustomAction<Release> {
	
	@Override
	public boolean canExecute(Release release) {
		return release.canNewReleasedAt();
	}
	
	@Override
	public String getName() {
		return "(+)  Released at";
	}
	
	@Override
	public void execute (Release release) {
		release.newReleasedAt(0, 0);
	}
}
