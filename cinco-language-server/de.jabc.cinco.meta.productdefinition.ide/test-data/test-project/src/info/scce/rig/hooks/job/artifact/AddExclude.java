package info.scce.rig.hooks.job.artifact;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Artifact;

public class AddExclude extends CincoCustomAction<Artifact> {
	
	@Override
	public boolean canExecute(Artifact artifact) {
		return artifact.canNewExclude();
	}
	
	@Override
	public String getName() {
		return "(+) Exclude";
	}

	@Override
	public void execute(Artifact artifact) {
		artifact.newExclude(0, 0);
	}

}
