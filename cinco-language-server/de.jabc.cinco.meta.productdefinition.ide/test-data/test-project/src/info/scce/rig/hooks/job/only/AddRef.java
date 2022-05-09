package info.scce.rig.hooks.job.only;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Only;

public class AddRef extends CincoCustomAction<Only> {

	@Override
	public boolean canExecute(Only only) {
		return only.canNewRef();
	}
	
	@Override
	public String getName() {
		return "(+) Ref";
	}
	
	@Override
	public void execute(Only only) {
		only.newRef(0, 0);
	}
}
