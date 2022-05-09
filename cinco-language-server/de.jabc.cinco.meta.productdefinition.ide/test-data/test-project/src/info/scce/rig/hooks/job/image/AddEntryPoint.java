package info.scce.rig.hooks.job.image;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Image;

public class AddEntryPoint extends CincoCustomAction<Image> {
	
	@Override
	public boolean canExecute(Image image) {
		return image.canNewEntryPoint();
	}
	
	@Override
	public String getName() {
		return "(+) Entrypoint";
	}

	@Override
	public void execute(Image image) {
		image.newEntryPoint(0, 0);
	}
}
