package info.scce.cinco.product.flowgraph.hooks;

import info.scce.cinco.product.flowgraph.flowgraph.FlowGraphDiagram;
import info.scce.cinco.product.flowgraph.flowgraph.Start;
import info.scce.cinco.product.flowgraph.flowgraph.End;
import info.scce.cinco.product.flowgraph.flowgraph.Activity;
import info.scce.cinco.product.flowgraph.flowgraph.LabeledTransition;
import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook;

/**
 *  This post-create hook is part of the transformation API feature showcase. As it is defined
 *  for the root model FlowGraph, it will be called by the "New FlowGraph" wizard after creating
 *  the empty model.
 *  
 *  It will just insert a Start node, an Activity, and an End node to every newly created model.
 *
 */
public class InitializeFlowGraphModel extends CincoPostCreateHook<FlowGraphDiagram> {

	@Override
	public void postCreate(FlowGraphDiagram flowGraph) {
		try {
			// Create the three nodes
			Start start = flowGraph.newStart(50, 50);
			Activity activity = flowGraph.newActivity(150, 50);
			End end = flowGraph.newEnd(310, 50);

			// Connect the nodes with edges
			start.newTransition(activity);
			LabeledTransition labeledTransition = activity.newLabeledTransition(end);
			labeledTransition.getLabeledTransitionView().setLabel("success");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
