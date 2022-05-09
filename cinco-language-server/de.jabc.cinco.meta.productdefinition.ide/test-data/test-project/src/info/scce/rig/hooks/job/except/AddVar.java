package info.scce.rig.hooks.job.except;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Except;

public class AddVar extends CincoCustomAction<Except> {

	@Override
	public boolean canExecute(Except except) {
		return except.canNewVarExp();
	}
	
	@Override
	public String getName() {
		return "(+) Variable";
	}
	
	@Override
	public void execute(Except except) {
		except.newVarExp(0, 0);
	}
}
