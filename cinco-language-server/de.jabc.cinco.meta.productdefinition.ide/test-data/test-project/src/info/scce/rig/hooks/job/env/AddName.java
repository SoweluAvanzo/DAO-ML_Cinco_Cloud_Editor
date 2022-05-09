package info.scce.rig.hooks.job.env;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Environment;

public class AddName extends CincoCustomAction<Environment> {
	
	@Override
	public boolean canExecute(Environment env) {
		return env.canNewName();
	}
	
	@Override
	public String getName() {
		return "(+)  Name";
	}
	@Override
	public void execute (Environment env) {
		env.newName(0, 0);
	}
}
