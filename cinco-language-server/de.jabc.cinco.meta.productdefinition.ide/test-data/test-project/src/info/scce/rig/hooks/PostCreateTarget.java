package info.scce.rig.hooks;

import java.util.Random;

import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook;
import info.scce.rig.pipeline.Target;

public class PostCreateTarget extends CincoPostCreateHook<Target> {
	
	// use platforms for now, its enough as an example
	public static final String[] PLATFORM = {"OSX", "Windows", "Android", "XboxOne", "iOS", "Linux"};
	
	public static final Random random = new Random();

	@Override
	public void postCreate (Target target) {
		target.setName(PLATFORM[random.nextInt(PLATFORM.length)]);
	}
}
