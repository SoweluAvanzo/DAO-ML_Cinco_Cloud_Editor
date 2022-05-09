package info.scce.rig.hooks.job.env;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Environment;

public class AddAutoStopIn extends CincoCustomAction<Environment>{

	@Override
	public boolean canExecute(Environment env) {
		return env.canNewAutoStopIn();
	}
	
	@Override
	public String getName() {
		return "(+)  Auto Stop In";
	}
	@Override
	public void execute(Environment env) {
		env.newAutoStopIn(0, 0);
		
	}
}
