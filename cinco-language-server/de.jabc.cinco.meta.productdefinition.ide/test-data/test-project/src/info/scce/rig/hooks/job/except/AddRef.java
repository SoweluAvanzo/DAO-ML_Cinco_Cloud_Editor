package info.scce.rig.hooks.job.except;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Except;

public class AddRef extends CincoCustomAction<Except> {

	@Override
	public boolean canExecute(Except except) {
		return except.canNewRef();
	}
	
	@Override
	public String getName() {
		return "(+) Ref";
	}
	
	@Override
	public void execute(Except except) {
		except.newRef(0, 0);
	}
}
