package info.scce.rig.hooks.job.artifact;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Artifact;

public class AddWhen extends CincoCustomAction<Artifact> {
	
	@Override
	public boolean canExecute(Artifact artifact) {
		return artifact.canNewWhen();
	}
	
	@Override
	public String getName() {
		return "(+) When";
	}

	@Override
	public void execute(Artifact artifact) {
		artifact.newWhen(0, 0);
	}
}
