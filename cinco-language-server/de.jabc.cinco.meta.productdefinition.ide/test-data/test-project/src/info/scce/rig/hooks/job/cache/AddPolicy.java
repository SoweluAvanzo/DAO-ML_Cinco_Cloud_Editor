package info.scce.rig.hooks.job.cache;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Cache;

public class AddPolicy extends CincoCustomAction<Cache> {
	
	@Override
	public boolean canExecute(Cache cache) {
		return cache.canNewPolicy();
	}
	
	@Override
	public String getName() {
		return "(+) Policy";
	}

	@Override
	public void execute(Cache cache) {
		cache.newPolicy(0, 0);
	}
}
