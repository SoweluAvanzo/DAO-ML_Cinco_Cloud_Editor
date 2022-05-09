package info.scce.rig.hooks.job.artifact;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Artifact;

public class AddReport extends CincoCustomAction<Artifact> {
	
	@Override
	public boolean canExecute(Artifact artifact) {
		return artifact.canNewReport();
	}
	
	@Override
	public String getName() {
		return "(+) Report";
	}

	@Override
	public void execute(Artifact artifact) {
		artifact.newReport(0,0);
	}
}
