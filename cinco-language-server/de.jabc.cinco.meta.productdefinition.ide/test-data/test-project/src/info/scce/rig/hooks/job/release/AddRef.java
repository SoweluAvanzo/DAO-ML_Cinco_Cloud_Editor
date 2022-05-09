package info.scce.rig.hooks.job.release;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Release;

public class AddRef extends CincoCustomAction<Release> {
	
	@Override
	public boolean canExecute(Release release) {
		return release.canNewRawRef();
	}
	
	@Override
	public String getName() {
		return "(+)  Ref";
	}
	
	@Override
	public void execute (Release release) {
		release.newRawRef(0, 0);
	}
}
