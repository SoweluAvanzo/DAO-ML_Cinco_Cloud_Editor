package info.scce.rig.hooks.tar;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Target;

public class AddInt extends CincoCustomAction<Target> {
	
	@Override
	public boolean canExecute(Target target) {
		return true;
	}
	
	@Override
	public String getName() {
		return "(+) Integer";
	}

	@Override
	public void execute(Target target) {
		target.newIntParameter(0, 0);
		
	}
}
