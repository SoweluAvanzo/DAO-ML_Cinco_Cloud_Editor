package info.scce.rig.hooks.job.only;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Only;

public class AddVar extends CincoCustomAction<Only> {

	@Override
	public boolean canExecute(Only only) {
		return only.canNewVarExp();
	}
	
	@Override
	public String getName() {
		return "(+) Variable";
	}
	
	@Override
	public void execute(Only only) {
		only.newVarExp(0, 0);
	}
}
