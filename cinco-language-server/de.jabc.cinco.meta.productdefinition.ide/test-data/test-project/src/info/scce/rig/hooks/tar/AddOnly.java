package info.scce.rig.hooks.tar;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Target;

public class AddOnly extends CincoCustomAction<Target> {
	
	@Override
	public boolean canExecute(Target target) {
		return target.canNewSimpleOnly();
	}
	
	@Override
	public String getName() {
		return "(+) Only";
	}

	@Override
	public void execute(Target target) {
		target.newSimpleOnly(0, 0);
	}
}