package info.scce.cinco.product.flowgraph.hooks;

import de.jabc.cinco.meta.runtime.hook.CincoPostMoveHook;
import graphmodel.ModelElement;
import graphmodel.ModelElementContainer;

/**
 * Example post-create hook that randomly sets the name of the activity. Possible
 * names are inspired by the action verbs of old-school point&click adventure games :)
 */
public class PostMove2 extends CincoPostMoveHook<ModelElement> {
	@Override
	public void postMove(ModelElement modelElement, ModelElementContainer source, ModelElementContainer target, int x,
			int y, int deltaX, int deltaY) {
		System.out.println("I am PostMove2!");
	}
}