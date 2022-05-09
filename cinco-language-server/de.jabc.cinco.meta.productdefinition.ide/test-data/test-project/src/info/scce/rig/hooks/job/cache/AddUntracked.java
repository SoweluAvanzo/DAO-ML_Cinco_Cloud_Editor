package info.scce.rig.hooks.job.cache;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Cache;

public class AddUntracked extends CincoCustomAction<Cache> {
	
	@Override
	public boolean canExecute(Cache cache) {
		return cache.canNewUntracked();
	}
	
	@Override
	public String getName() {
		return "(+) Untracked";
	}

	@Override
	public void execute(Cache cache) {
		cache.newUntracked(0, 0);
	}
}
