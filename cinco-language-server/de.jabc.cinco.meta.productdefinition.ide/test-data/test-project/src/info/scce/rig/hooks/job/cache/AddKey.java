package info.scce.rig.hooks.job.cache;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Cache;

public class AddKey extends CincoCustomAction<Cache> {
	
	@Override
	public boolean canExecute(Cache cache) {
		return 
			   cache.canNewKey() 
			&& cache.canNewPrefix()
			&& cache.getFiles().size() == 0;
	}
	
	@Override
	public String getName() {
		return "(+) Key";
	}

	@Override
	public void execute(Cache cache) {
		cache.newKey(0, 0);
	}

}
