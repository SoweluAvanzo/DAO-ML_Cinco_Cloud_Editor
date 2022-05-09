package info.scce.rig.hooks.tar;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Target;

public class AddString extends CincoCustomAction<Target> {
	
	@Override
	public boolean canExecute(Target target) {
		return true;
	}
	
	@Override
	public String getName() {
		return "(+) String";
	}

	@Override
	public void execute(Target target) {
		target.newStringParameter(0, 0);
	}
}
