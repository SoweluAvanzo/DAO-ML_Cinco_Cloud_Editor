package info.scce.rig.hooks.job.artifact;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Artifact;

public class AddName extends CincoCustomAction<Artifact> {
	
	@Override
	public boolean canExecute(Artifact artifact) {
		return artifact.canNewName();
	}
	
	@Override
	public String getName() {
		return "(+) Name";
	}

	@Override
	public void execute(Artifact artifact) {
		artifact.newName(0, 0);
	}
}
