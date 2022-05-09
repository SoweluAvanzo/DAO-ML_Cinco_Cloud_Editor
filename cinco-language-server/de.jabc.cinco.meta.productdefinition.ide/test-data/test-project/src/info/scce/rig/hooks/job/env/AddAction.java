package info.scce.rig.hooks.job.env;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Environment;

public class AddAction extends CincoCustomAction<Environment>{

	@Override
	public boolean canExecute(Environment env) {
		return env.canNewEnvAction();
	}
	
	@Override
	public String getName() {
		return "(+)  Action";
	}
	@Override
	public void execute(Environment env) {
		env.newEnvAction(0, 0);
		
	}
}
