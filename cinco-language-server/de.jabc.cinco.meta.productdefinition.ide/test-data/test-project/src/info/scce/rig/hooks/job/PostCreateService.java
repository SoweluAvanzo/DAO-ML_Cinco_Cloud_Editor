package info.scce.rig.hooks.job;

import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook;
import info.scce.rig.pipeline.Service;

public class PostCreateService extends CincoPostCreateHook<Service> {

	@Override
	public void postCreate(Service service) {
		service.newName(0, 0);
	}
}
