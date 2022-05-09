package info.scce.rig.hooks.tar;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Target;

public class AddBoolean extends CincoCustomAction<Target> {
	
	@Override
	public boolean canExecute(Target target) {
		return true;
	}
	
	@Override
	public String getName() {
		return "(+) Boolean";
	}

	@Override
	public void execute(Target target) {
		target.newBooleanParameter(0, 0);
		
	}
}
