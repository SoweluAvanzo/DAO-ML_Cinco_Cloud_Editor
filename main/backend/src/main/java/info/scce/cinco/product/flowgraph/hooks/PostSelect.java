package info.scce.cinco.product.flowgraph.hooks;

import de.jabc.cinco.meta.runtime.hook.CincoPostSelectHook;
import graphmodel.ModelElement;

/**
 * Example post-create hook that randomly sets the name of the activity. Possible
 * names are inspired by the action verbs of old-school point&click adventure games :)
 */
public class PostSelect extends CincoPostSelectHook<ModelElement> {

	@Override
	public void postSelect(ModelElement activity) {
		System.out.println("I am PostSelect!");
	}
	
}

