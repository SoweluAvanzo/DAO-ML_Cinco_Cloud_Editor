package info.scce.rig.hooks;

import de.jabc.cinco.meta.runtime.hook.CincoPreDeleteHook;
import graphmodel.ModelElementContainer;
import info.scce.rig.pipeline.Pipeline;
import info.scce.rig.pipeline.Next;
import info.scce.rig.graphmodel.controller.UpdateStageIdx;

public class PreDeleteNext extends CincoPreDeleteHook<Next> {

	@Override
	public void preDelete(Next next) {
		ModelElementContainer container = next.getContainer();
		if (container instanceof Pipeline) {
			Pipeline model = (Pipeline) container;
			Next toBeIgnored = next;
			UpdateStageIdx.updateStageIdxPerJob(model, toBeIgnored);
		}
	}
}
