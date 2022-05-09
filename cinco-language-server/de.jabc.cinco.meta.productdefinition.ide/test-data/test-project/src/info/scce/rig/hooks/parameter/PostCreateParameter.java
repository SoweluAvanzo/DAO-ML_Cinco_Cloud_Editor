package info.scce.rig.hooks.parameter;

import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook;
import info.scce.rig.pipeline.Parameter;
import info.scce.rig.pipeline.Slottable;
import info.scce.rig.graphmodel.controller.SlottableLayout;

public class PostCreateParameter extends CincoPostCreateHook<Parameter> {
	
	private static int nextIdx = 0;
	
	@Override
	public void postCreate (Parameter parameter) {
		parameter.setIdx(nextIdx++);
		SlottableLayout.layout((Slottable)parameter.getContainer(), null);
	}
}