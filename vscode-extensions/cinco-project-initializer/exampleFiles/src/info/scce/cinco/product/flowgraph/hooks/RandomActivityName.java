package info.scce.cinco.product.flowgraph.hooks;

import info.scce.cinco.product.flowgraph.flowgraph.Activity;

import java.util.Random;

import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook;

/**
 * Example post-create hook that randomly sets the name of the activity. Possible
 * names are inspired by the action verbs of old-school point&click adventure games :)
 */
public class RandomActivityName extends CincoPostCreateHook<Activity> {

	@Override
	public void postCreate(Activity activity) {
		
		String[] names = new String[] {
	            "Close",
	            "Fix",
	            "Give",
	            "Look at",
	            "Open",
	            "Pick up",
	            "Pull",
	            "Push",
	            "Put on",
	            "Read",
	            "Take off",
	            "Talk to",
	            "Turn off",
	            "Turn on",
	            "Unlock",
	            "Use",
	            "Walk to"
		};
		
		int randomIndex = new Random().nextInt(names.length);

		activity.getActivityView().setName(names[randomIndex]);

	}

}
