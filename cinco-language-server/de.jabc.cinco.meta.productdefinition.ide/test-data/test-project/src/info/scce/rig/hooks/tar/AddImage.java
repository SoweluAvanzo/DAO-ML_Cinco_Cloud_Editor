package info.scce.rig.hooks.tar;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Target;

public class AddImage extends CincoCustomAction<Target> {
	
	@Override
	public boolean canExecute(Target target) {
		return target.canNewSimpleImage();
	}
	
	@Override
	public String getName() {
		return "(+) Image";
	}

	@Override
	public void execute(Target target) {
		target.newSimpleImage(0, 0);
	}
}
