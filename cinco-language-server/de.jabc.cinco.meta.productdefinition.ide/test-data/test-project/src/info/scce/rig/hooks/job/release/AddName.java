package info.scce.rig.hooks.job.release;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Release;

public class AddName extends CincoCustomAction<Release> {
	
	@Override
	public boolean canExecute(Release release) {
		return release.canNewName();
	}
	
	@Override
	public String getName() {
		return "(+)  Name";
	}
	@Override
	public void execute (Release release) {
		release.newName(0, 0);
	}
}
