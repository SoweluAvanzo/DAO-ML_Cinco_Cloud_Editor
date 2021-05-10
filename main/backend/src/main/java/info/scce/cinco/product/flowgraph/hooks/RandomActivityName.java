package info.scce.cinco.product.flowgraph.hooks;

import graphmodel.IdentifiableElement;

import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook;

/**
 * Example post-create hook that randomly sets the name of the activity. Possible
 * names are inspired by the action verbs of old-school point&click adventure games :)
 */
public class RandomActivityName extends CincoPostCreateHook<IdentifiableElement> {

	@Override
	public void postCreate(IdentifiableElement activity) {


	}
	
}

