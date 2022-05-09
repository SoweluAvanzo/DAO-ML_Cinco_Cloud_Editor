package info.scce.rig.hooks.job.artifact;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Artifact;

public class AddPath extends CincoCustomAction<Artifact> {
	
	@Override
	public boolean canExecute(Artifact artifact) {
		return artifact.canNewPath();
	}
	
	@Override
	public String getName() {
		return "(+) Path";
	}

	@Override
	public void execute(Artifact artifact) {
		artifact.newPath(0, 0);
	}

}
