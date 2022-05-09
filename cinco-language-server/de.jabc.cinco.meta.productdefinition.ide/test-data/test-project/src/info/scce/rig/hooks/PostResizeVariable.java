package info.scce.rig.hooks;

import de.jabc.cinco.meta.runtime.hook.CincoPostResizeHook;
import info.scce.rig.pipeline.Variable;

public class PostResizeVariable extends CincoPostResizeHook<Variable> {
	@Override
	public void postResize(Variable variable, int dW, int dH) {
		variable.resize(variable.getWidth(), 24);
	}

	
	public void postResize(Variable variable, int arg1, int arg2, int arg3, int arg4) {
		postResize(variable, 0, 0);
	}
}