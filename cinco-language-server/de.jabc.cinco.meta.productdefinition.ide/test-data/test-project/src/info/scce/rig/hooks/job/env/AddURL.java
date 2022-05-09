package info.scce.rig.hooks.job.env;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Environment;

public class AddURL extends CincoCustomAction<Environment> {
	
	@Override
	public boolean canExecute(Environment env) {
		return env.canNewURL();
	}
	
	@Override
	public String getName() {
		return "(+)  URL";
	}
	@Override
	public void execute (Environment env) {
		env.newURL(0,0);
	}
}
