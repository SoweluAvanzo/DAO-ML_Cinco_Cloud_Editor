package info.scce.rig.hooks.job.cache;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Cache;

public class AddPrefix extends CincoCustomAction<Cache> {
	
	@Override
	public boolean canExecute(Cache cache) {
		return cache.canNewKey() && cache.canNewPrefix();
	}
	
	@Override
	public String getName() {
		return "(+) [Key] Prefix";
	}

	@Override
	public void execute(Cache cache) {
		cache.newPrefix(0, 0);
	}

}
