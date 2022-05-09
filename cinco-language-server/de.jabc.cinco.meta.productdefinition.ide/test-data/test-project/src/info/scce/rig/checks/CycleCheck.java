package info.scce.rig.checks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import info.scce.rig.pipeline.Pipeline;
import info.scce.rig.pipeline.Job;
import info.scce.rig.mcam.modules.checks.PipelineCheck;

public class CycleCheck extends PipelineCheck {

	@Override
	public void check(Pipeline graph) {
		if (isCyclic(graph))
			addError(graph, Pipeline.class.getSimpleName() + " must not be cyclic");
	}

	public boolean isCyclic(Pipeline graph) {

		/* Start with an empty path and cache */
		Set<Job> path = new HashSet<Job>();
		Map<Job, Boolean> cache = new HashMap<Job, Boolean>();

		/* A graph is cyclic if there is any cycle (any node lies on a cycle) */
		for (Job j : graph.getJobs()) {
			if (isCyclic(j, path, cache))
				return true;
		}
		return false;
	}

	private boolean isCyclic(Job j0, Set<Job> path, Map<Job, Boolean> cache) {

		/* Take advantage of cache where possible */
		if (!cache.containsKey(j0)) {

			/* Check if cycle closed */
			boolean cycleFound = path.contains(j0);

			/* If not closed search recursively */
			if (!cycleFound) {

				/* Extend path by one step */
				path.add(j0);

				for (Job j1 : j0.getJobSuccessors()) {
					if (isCyclic(j1, path, cache)) {
						cycleFound = true;
						break;
					}
				}

				/* Restore path state */
				path.remove(j0);
			}

			/* Feed cache */
			cache.put(j0, cycleFound);
		}
		return cache.get(j0);
	}
}
