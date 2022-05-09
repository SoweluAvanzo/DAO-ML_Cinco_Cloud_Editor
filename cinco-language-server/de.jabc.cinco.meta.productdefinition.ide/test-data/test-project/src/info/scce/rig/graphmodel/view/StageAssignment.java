package info.scce.rig.graphmodel.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import info.scce.rig.pipeline.Job;
import info.scce.rig.pipeline.Next;
import info.scce.rig.pipeline.Pipeline;
import info.scce.rig.pipeline.StageAssignmentStrategy;

public class StageAssignment {

	private Pipeline model;
	private Next toBeIgnored;
	private Map<Job, Integer> assignment;
	private List<Set<Job>> stages;

	public StageAssignment(Pipeline model) {
		this(model, null);
	}

	public StageAssignment(Pipeline model, Next toBeIgnored) {
		this.model = model;
		this.toBeIgnored = toBeIgnored;
		this.assignment = null;
		this.stages = null;
	}

	public Map<Job, Integer> getAssignment() {
		if (assignment == null) {
			assignment = new HashMap<Job, Integer>();
			assignJobs(assignment, model);
		}
		return assignment;
	}

	public int getStageIdx(Job job) {
		return getAssignment().get(job);
	}

	public List<Set<Job>> getStages() {
		if (stages == null)
			stages = deriveStages(assignment);
		return stages;
	}

	public int countStages() {
		return getStages().size();
	}

	public Set<Job> getStage(int idx) {
		return getStages().get(idx);
	}

	public Set<Job> getStage(Job job) {
		return getStage(getStageIdx(job));
	}

	private void assignJobs(Map<Job, Integer> assignment, Pipeline model) {
		StageAssignmentStrategy stageAssignmentStrategy = model.getStageAssignmentStrategy();
		if (stageAssignmentStrategy == StageAssignmentStrategy.AS_EARLY_AS_POSSIBLE)
			assignJobsAsEarlyAsPossible(assignment, model);
		else if (stageAssignmentStrategy == StageAssignmentStrategy.AS_LATE_AS_POSSIBLE)
			assignJobsAsLateAsPossible(assignment, model);
	}

	private void assignJobsAsEarlyAsPossible(Map<Job, Integer> assignment, Pipeline model) {
		model.getJobs().forEach((j) -> assignJobAsEarlyAsPossible(assignment, j));
	}

	private void assignJobsAsLateAsPossible(Map<Job, Integer> assignment, Pipeline model) {
		Map<Job, Integer> preliminaryAssignment = new HashMap<Job, Integer>();
		Optional<Integer> firstStage = model.getJobs().stream()
				.map((j) -> assignJobPreliminarilyAsLateAsPossible(preliminaryAssignment, j))
				.min(Integer::compare);

		/* Shift indices such that the first stage is associated with 0 */
		if (firstStage.isPresent()) {
			for (Job j : preliminaryAssignment.keySet())
				assignment.put(j, preliminaryAssignment.get(j) - firstStage.get());
		}
	}

	private int assignJobAsEarlyAsPossible(Map<Job, Integer> assignment, Job job) {
		if (!assignment.containsKey(job)) {
			int firstStageIdx = 0;

			/* Assign anything to guarantee termination in case of cycles */
			assignment.put(job, firstStageIdx);

			/* Stage must be greater than that of all preceding jobs */
			
			int stageIdx = job.getIncoming(Next.class).stream()
					.filter((n) -> n != toBeIgnored)
					.map((n) -> (Job) n.getSourceElement())
					.map((j) -> assignJobAsEarlyAsPossible(assignment, j) + 1)
					.max(Integer::compare).orElse(firstStageIdx);
			assignment.put(job, stageIdx);
		}
		return assignment.get(job);
	}

	private int assignJobPreliminarilyAsLateAsPossible(Map<Job, Integer> preliminaryAssignment, Job job) {
		if (!preliminaryAssignment.containsKey(job)) {
			int lastStageIdx = -1;

			/* Assign anything to guarantee termination in case of cycles */
			preliminaryAssignment.put(job, lastStageIdx);

			/* Stage must be smaller than that of all succeeding jobs */
			int stageIdx = job.getOutgoing(Next.class).stream()
					.filter((n) -> n != toBeIgnored)
					.filter((n) -> n.getTargetElement() instanceof Job)
					.map((n) -> (Job) n.getTargetElement())
					.map((j) -> assignJobPreliminarilyAsLateAsPossible(preliminaryAssignment, j) - 1)
					.min(Integer::compare)
					.orElse(lastStageIdx);
			preliminaryAssignment.put(job, stageIdx);
		}
		return preliminaryAssignment.get(job);
	}

	private List<Set<Job>> deriveStages(Map<Job, Integer> assignment) {
		List<Set<Job>> stages = new ArrayList<Set<Job>>();
		for (Entry<Job, Integer> js : assignment.entrySet()) {
			Job job = js.getKey();
			Integer stageIdx = js.getValue();
			while (stages.size() < stageIdx)
				stages.add(new HashSet<Job>());
			stages.get(stageIdx).add(job);
		}
		return stages;
	}
}
