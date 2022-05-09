package info.scce.rig.hooks.job;

import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook;
import info.scce.rig.pipeline.Image;

public class PostCreateImage extends CincoPostCreateHook<Image> {

	@Override
	public void postCreate(Image image) {
		image.newName(0, 0);
		//image.newEntryPoint(0, 0);
	}
}
