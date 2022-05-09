package info.scce.rig.hooks.job.except;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Except;

public class AddChange extends CincoCustomAction<Except> {

	@Override
	public boolean canExecute(Except except) {
		return except.canNewChange();
	}
	
	@Override
	public String getName() {
		return "(+) Change";
	}
	
	@Override
	public void execute(Except except) {
		except.newChange(0, 0);
	}
}
