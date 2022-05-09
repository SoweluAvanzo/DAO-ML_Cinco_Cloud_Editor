package info.scce.rig.hooks.job.retry;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Retry;

public class AddWhen extends CincoCustomAction<Retry> {

	@Override
	public boolean canExecute(Retry retry) {
		return retry.canNewRetryWhen();
	}
	
	@Override
	public String getName() {
		return "(+) When";
	}
	
	@Override
	public void execute(Retry retry) {
		retry.newRetryWhen(0, 0);
	}
}
