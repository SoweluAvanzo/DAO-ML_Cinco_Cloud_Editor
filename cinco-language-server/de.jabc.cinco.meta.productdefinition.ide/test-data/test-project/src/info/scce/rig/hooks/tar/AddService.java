package info.scce.rig.hooks.tar;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Target;

public class AddService  extends CincoCustomAction<Target> {
	
	@Override
	public boolean canExecute(Target target) {
		return target.canNewSimpleService();
	}
	
	@Override
	public String getName() {
		return "(+) Service";
	}

	@Override
	public void execute(Target target) {
		target.newSimpleService(0, 0);
	}
}