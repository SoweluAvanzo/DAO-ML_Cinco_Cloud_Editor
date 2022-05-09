package info.scce.rig.hooks.slot;

import de.jabc.cinco.meta.runtime.hook.CincoPreDeleteHook;
import info.scce.rig.pipeline.Slot;
import info.scce.rig.pipeline.Slottable;
import info.scce.rig.graphmodel.controller.SlottableLayout;

public class PreDeleteSlot extends CincoPreDeleteHook<Slot> {

	@Override
	public void preDelete(Slot slot) {
		SlottableLayout.layout((Slottable) slot.getContainer(), slot);
	}
}
