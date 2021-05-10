package info.scce.cinco.product.flowgraph.action;

import graphmodel.Node;
import info.scce.cinco.product.flowgraph.flowgraph.End;
import info.scce.cinco.product.flowgraph.flowgraph.Start;
import de.jabc.cinco.meta.runtime.action.CincoCustomAction;

public class ShortestPathToEnd extends CincoCustomAction<Start> {
	
	@Override
	public String getName() {
		return "Calculate Distance to End";
	}

	@Override
	/**
	 * @return always <code>true</code>, as the action can
	 * 		be executed for any Start node.
	 */
	public boolean canExecute(Start start) {
		return true;
	}

	@Override
	public void execute(Start start) {
	}
}	
