package info.scce.rig.hooks.tar;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Target;

public class AddExcept extends CincoCustomAction<Target> {
	
	@Override
	public boolean canExecute(Target target) {
		return target.canNewSimpleExcept();
	}
	
	@Override
	public String getName() {
		return "(+) Except";
	}

	@Override
	public void execute(Target target) {
		target.newSimpleExcept(0, 0);
	}
}