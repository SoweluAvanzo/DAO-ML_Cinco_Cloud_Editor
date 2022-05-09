package info.scce.rig.hooks.job.release;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Release;

public class AddTagName extends CincoCustomAction<Release> {
	
	@Override
	public boolean canExecute(Release release) {
		return release.canNewTagName();
	}
	
	@Override
	public String getName() {
		return "(+)  Tag Name";
	}
	@Override
	public void execute (Release release) {
		release.newTagName(0, 0);
	}
}