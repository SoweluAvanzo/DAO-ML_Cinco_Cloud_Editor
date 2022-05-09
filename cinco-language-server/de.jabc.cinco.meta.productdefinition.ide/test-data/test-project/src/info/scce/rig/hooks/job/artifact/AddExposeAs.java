package info.scce.rig.hooks.job.artifact;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Artifact;

public class AddExposeAs extends CincoCustomAction<Artifact> {
	
	@Override
	public boolean canExecute(Artifact artifact) {
		return artifact.canNewExposeAs();
	}
	
	@Override
	public String getName() {
		return "(+) Expose as";
	}

	@Override
	public void execute(Artifact artifact) {
		artifact.newExposeAs(0, 0);
	}
}
