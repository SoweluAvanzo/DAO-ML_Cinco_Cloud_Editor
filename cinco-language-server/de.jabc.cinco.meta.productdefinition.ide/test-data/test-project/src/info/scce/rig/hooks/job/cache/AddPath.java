package info.scce.rig.hooks.job.cache;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Cache;

public class AddPath extends CincoCustomAction<Cache> {
	
	@Override
	public boolean canExecute(Cache cache) {
		return cache.canNewPath();
	}
	
	@Override
	public String getName() {
		return "(+) Path";
	}

	@Override
	public void execute(Cache cache) {
		cache.newPath(0, 0);
	}
}
