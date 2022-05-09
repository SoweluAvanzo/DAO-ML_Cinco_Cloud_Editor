package info.scce.rig.hooks.slot;

import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook;
import info.scce.rig.pipeline.Slot;
import info.scce.rig.pipeline.Slottable;
import info.scce.rig.graphmodel.controller.SlottableLayout;

public class PostCreateSlot extends CincoPostCreateHook<Slot>{

	@Override
	public void postCreate(Slot slot) {
		((Slottable) slot.getContainer()).setMinimized(false);
		SlottableLayout.layout((Slottable) slot.getContainer(), null);
	}

}
