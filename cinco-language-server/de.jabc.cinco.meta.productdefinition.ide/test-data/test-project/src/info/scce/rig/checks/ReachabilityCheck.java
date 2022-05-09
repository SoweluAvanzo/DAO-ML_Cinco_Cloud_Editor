package info.scce.rig.checks;

import static info.scce.rig.graphmodel.view.PipelineView.getReachableTargets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import info.scce.rig.pipeline.Pipeline;
import info.scce.rig.pipeline.Job;
import info.scce.rig.pipeline.Target;
import info.scce.rig.mcam.modules.checks.PipelineCheck;

public class ReachabilityCheck extends PipelineCheck {

	@Override
	public void check(Pipeline model) {
		Map<Job, Set<Target>> cache = new HashMap<Job, Set<Target>>();
		for (Job j : model.getJobs()) {
			if (getReachableTargets(j, cache).isEmpty())
				addWarning(j, Job.class.getSimpleName() + " is never used");
		}
	}
}
