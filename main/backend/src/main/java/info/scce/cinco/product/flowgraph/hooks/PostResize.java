package info.scce.cinco.product.flowgraph.hooks;

import de.jabc.cinco.meta.runtime.hook.CincoPostResizeHook;
import graphmodel.ModelElement;

/**
 * Example post-create hook that randomly sets the name of the activity. Possible
 * names are inspired by the action verbs of old-school point&click adventure games :)
 */
public class PostResize extends CincoPostResizeHook<ModelElement> {
	public void postResize(ModelElement modelElement, int deltaWidth, int deltaHeight) {
		System.out.println("I am PostResize!");
	}
}

