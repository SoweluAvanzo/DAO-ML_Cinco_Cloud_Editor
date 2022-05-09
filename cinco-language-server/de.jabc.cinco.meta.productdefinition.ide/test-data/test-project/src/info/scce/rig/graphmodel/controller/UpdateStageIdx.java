package info.scce.rig.graphmodel.controller;

import info.scce.rig.pipeline.Pipeline;
import info.scce.rig.pipeline.Job;
import info.scce.rig.pipeline.Next;
import info.scce.rig.graphmodel.view.StageAssignment;

public class UpdateStageIdx {

	public static void updateStageIdxPerJob(Pipeline model) {
		Next toBeIgnored = null;
		updateStageIdxPerJob(model, toBeIgnored);
	}

	public static void updateStageIdxPerJob(Pipeline model, Next toBeIgnored) {
		StageAssignment stageAssignment = new StageAssignment(model, toBeIgnored);
		for (Job j : model.getJobs())
			j.setStageIdx(stageAssignment.getStageIdx(j));
	}
}