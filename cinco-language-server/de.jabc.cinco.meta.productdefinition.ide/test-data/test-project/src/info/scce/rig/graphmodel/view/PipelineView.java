package info.scce.rig.graphmodel.view;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import info.scce.rig.pipeline.Job;
import info.scce.rig.pipeline.Target;

public class PipelineView {

	public static Set<Target> getReachableTargets(Job job, Map<Job, Set<Target>> cache) {
		if (!cache.containsKey(job)) {

			/* Under-approximation to prevent cyclic invocations */
			Set<Target> reachableTargets = new HashSet<Target>();
			cache.put(job, reachableTargets);

			/* Add directly reachable targets */
			reachableTargets.addAll(job.getTargetSuccessors());

			/* Add indirectly reachable targets */
			for (Job j : job.getJobSuccessors())
				reachableTargets.addAll(getReachableTargets(j, cache));
		}
		return cache.get(job);
	}
	
	/**
	 * Recursively gets all predecessors in the ancestry tree of this job
	 * @param job
	 * @return
	 */
	public static List<Job> getJobAncestors (Job job) {
		return 
			Stream.concat(
					Stream.of(job),
					job.getJobPredecessors().stream()
						.flatMap(j -> getJobAncestors(j).stream()))
			.collect(Collectors.toList());
	}
}
