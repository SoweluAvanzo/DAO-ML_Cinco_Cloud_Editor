package info.scce.rig.hooks.job.retry;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Retry;

public class AddMax extends CincoCustomAction<Retry> {

	@Override
	public boolean canExecute(Retry retry) {
		return retry.canNewSimpleRetry();
	}
	
	@Override
	public String getName() {
		return "(+) Max";
	}
	
	@Override
	public void execute(Retry retry) {
		retry.newSimpleRetry(0, 0);
	}
}
