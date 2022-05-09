package info.scce.rig.hooks.job.cache;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Cache;

public class AddFile extends CincoCustomAction<Cache> {
	
	@Override
	public boolean canExecute(Cache cache) {
		return cache.canNewKey() && cache.canNewFile();
	}
	
	@Override
	public String getName() {
		return "(+) [Key] File";
	}

	@Override
	public void execute(Cache cache) {
		cache.newFile(0, 0);
	}
}
