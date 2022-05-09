package info.scce.rig.hooks.job.only;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Only;

public class AddChange extends CincoCustomAction<Only> {

	@Override
	public boolean canExecute(Only only) {
		return only.canNewChange();
	}
	
	@Override
	public String getName() {
		return "(+) Change";
	}
	
	@Override
	public void execute(Only only) {
		only.newChange(0, 0);
	}
}
