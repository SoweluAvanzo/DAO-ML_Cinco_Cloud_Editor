package info.scce.cinco.product.flowgraph.hooks;

import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook;
import graphmodel.IdentifiableElement;

/**
 * Example post-create hook that randomly sets the name of the activity. Possible
 * names are inspired by the action verbs of old-school point&click adventure games :)
 */
public class PostCreate extends CincoPostCreateHook<IdentifiableElement> {

	@Override
	public void postCreate(IdentifiableElement activity) {
		System.out.println("I am PostCreate!");
	}
	
}

