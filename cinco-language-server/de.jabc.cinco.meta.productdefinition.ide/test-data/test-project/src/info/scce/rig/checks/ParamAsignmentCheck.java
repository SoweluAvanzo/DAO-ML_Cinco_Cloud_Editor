package info.scce.rig.checks;

//import static info.scce.rig.graphmodelview.BuildMapView.getReachableTargets;
//import static java.util.stream.Collectors.joining;
//import static java.util.stream.Collectors.toList;
//import static java.util.stream.Collectors.toSet;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.eclipse.emf.common.util.EList;

import info.scce.rig.pipeline.Pipeline;
import info.scce.rig.mcam.modules.checks.PipelineCheck;

public class ParamAsignmentCheck extends PipelineCheck {


	@Override
	public void check(Pipeline model) {
//		Map<Job, Set<Target>> reachableTargetsCache = new HashMap<Job, Set<Target>>();
//
//		/* Checks jobs separately and take advantage of global cache */
//		for (Job j : model.getJobs()) {
//			checkJobHasItsParamKeys(j);
//			Set<Target> reachableTargets = getReachableTargets(j, reachableTargetsCache);
//			checkReachableTargetsAssignRequiredParamKeys(j, reachableTargets);
//		}
//
//		/* Check targets separately */
//		for (Target t : model.getTargets())
//			checkParamAssignmentIsUnambiguous(t);
	}
//
//	private void checkJobHasItsParamKeys(Job job) {
//		Set<String> expectedParamKeys = getRequiredParamKeys(job);
//		Set<String> actualParamKeys = job.getParamKeyPredecessors().stream().map((paramKey) -> paramKey.getKey())
//				.collect(toSet());
//
//		/* Ensure parameter keys are unique */
//		if (job.getParamKeyPredecessors().size() != actualParamKeys.size())
//			addError(job, Job.class.getSimpleName() + " must must be associated unique parameter keys");
//
//		if (!expectedParamKeys.equals(actualParamKeys)) {
//			String readableExpectedParamKeys = expectedParamKeys.stream().map((name) -> "\"" + name + "\"")
//					.collect(joining(", "));
//			addError(job, Job.class.getSimpleName() + " must must be associated with parameter keys "
//					+ readableExpectedParamKeys);
//		}
//	}
//
//	private void checkReachableTargetsAssignRequiredParamKeys(Job job, Set<Target> targets) {
//		EList<ParamKey> requiredParamKeys = job.getParamKeyPredecessors();
//		for (Target t : targets) {
//
//			/* Collect assigned parameter keys */
//			Set<ParamKey> assignedParamKeys = new HashSet<ParamKey>();
//			for (ParamValue pv : t.getParamValues())
//				assignedParamKeys.addAll(pv.getParamKeySuccessors());
//
//			if (!assignedParamKeys.containsAll(requiredParamKeys))
//				addError(job,
//						Job.class.getSimpleName() + " must have its parameter keys assigned by every reachable target");
//		}
//	}
//
//	private void checkParamAssignmentIsUnambiguous(Target target) {
//
//		/* Find sets of parameter keys per parameter value */
//		List<Set<ParamKey>> paramKeysPerParamValue = target.getParamValues().stream()
//				.map((paramValue) -> new HashSet<ParamKey>(paramValue.getParamKeySuccessors())).collect(toList());
//
//		/* Assignment is unambiguous if sets do not overlap */
//		Set<ParamKey> pksSeen = new HashSet<ParamKey>();
//
//		for (Set<ParamKey> pks : paramKeysPerParamValue) {
//			if (!Collections.disjoint(pksSeen, pks))
//				addError(target, Target.class.getSimpleName() + " must assign parameter values unambiguously");
//			pksSeen.addAll(pks);
//		}
//	}
}
