package info.scce.rig.hooks;

import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook;
import graphmodel.ModelElementContainer;
import info.scce.rig.pipeline.Pipeline;
import info.scce.rig.pipeline.Next;
import info.scce.rig.graphmodel.controller.UpdateStageIdx;

public class PostCreateNext extends CincoPostCreateHook<Next> {

	@Override
	public void postCreate(Next next) {
		ModelElementContainer container = next.getContainer();
		if (container instanceof Pipeline) {
			Pipeline model = (Pipeline) container;
			UpdateStageIdx.updateStageIdxPerJob(model);
		}
	}
}
