package info.scce.rig.hooks.job.cache;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Cache;

public class AddWhen extends CincoCustomAction<Cache> {
	
	@Override
	public boolean canExecute(Cache cache) {
		return cache.canNewWhen();
	}
	
	@Override
	public String getName() {
		return "(+) When";
	}

	@Override
	public void execute(Cache cache) {
		cache.newWhen(0, 0);
	}
}
