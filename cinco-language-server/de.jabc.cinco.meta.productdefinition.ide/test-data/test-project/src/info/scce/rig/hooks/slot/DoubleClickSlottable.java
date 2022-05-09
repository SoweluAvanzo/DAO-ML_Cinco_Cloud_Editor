package info.scce.rig.hooks.slot;

import de.jabc.cinco.meta.runtime.action.CincoDoubleClickAction;
import info.scce.rig.pipeline.Slottable;
import info.scce.rig.graphmodel.controller.SlottableLayout;

public class DoubleClickSlottable extends CincoDoubleClickAction<Slottable>{

	@Override
	public void execute (Slottable slottable) {
		slottable.setMinimized(!slottable.isMinimized());
		SlottableLayout.layout(slottable, null);
	}
}
