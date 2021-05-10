package info.scce.cinco.product.flowgraph.hooks;

import de.jabc.cinco.meta.runtime.hook.CincoPostDeleteHook;
import graphmodel.ModelElement;

/**
 * Example post-create hook that randomly sets the name of the activity. Possible
 * names are inspired by the action verbs of old-school point&click adventure games :)
 */
public class PostDelete extends CincoPostDeleteHook<ModelElement> {
	@Override
	public Runnable getPostDeleteFunction(ModelElement modelElement) {
		System.out.println("I am PostDelete!");
		return new Runnable() {
			@Override
			public void run() {
				System.out.println("I am PostDeleteRunnable!");
			}
		};
	}
	
}

