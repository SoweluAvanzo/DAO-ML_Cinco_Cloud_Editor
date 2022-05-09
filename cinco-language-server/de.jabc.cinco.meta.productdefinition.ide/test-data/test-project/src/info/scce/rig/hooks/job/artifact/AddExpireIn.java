package info.scce.rig.hooks.job.artifact;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Artifact;


public class AddExpireIn extends CincoCustomAction<Artifact> {
	
	@Override
	public boolean canExecute(Artifact artifact) {
		return artifact.canNewExpireIn();
	}
	
	@Override
	public String getName() {
		return "(+) Expire In";
	}

	@Override
	public void execute(Artifact artifact) {
		artifact.newExpireIn(0, 0);
	}
}
