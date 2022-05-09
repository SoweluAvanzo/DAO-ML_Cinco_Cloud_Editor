package info.scce.rig.hooks.slot;

import de.jabc.cinco.meta.runtime.hook.CincoPostResizeHook;
import info.scce.rig.pipeline.Slottable;
import info.scce.rig.graphmodel.controller.SlottableLayout;

public class PostResizeSlottable extends CincoPostResizeHook<Slottable> {
	@Override
	public void postResize(Slottable slottable, int deltaWidth, int deltaHeight) {
		SlottableLayout.layout(slottable, null);
	}

	
	public void postResize (Slottable slottable, int arg1, int arg2, int arg3, int arg4) {
		postResize(slottable, 0, 0);
		
	}
}