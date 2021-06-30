package info.scce.cinco.product.flowgraph.action;

import graphmodel.Node;
import info.scce.cinco.product.flowgraph.flowgraph.End;
import info.scce.cinco.product.flowgraph.flowgraph.Start;

//import org.eclipse.jface.dialogs.MessageDialog;

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
		int length = getShortest(start, 100);
		String message = 
			length >= 0
				? String.format("The shortest End node is %d steps away.", length)
				: "No End node could be found within the search range.";
		//MessageDialog.openInformation(null, "Shortest Path Search Result", message);
	}

	/**
	 * Find the shortest distance to a node of type {@link End}
	 * 
	 * @param node 
	 * 		The node to search from.
	 * @param maxSearchDepth 
	 * 		poor man's solution to prevent endless recursion ;)
	 * @return 
	 * 		The nearest distance to an End node or 0 if node is an End node 
	 * 		or some value < 0 in case no End node is within the range of maxSearchDepth
	 */
	private int getShortest(Node node, int maxSearchDepth) {
		if (node instanceof End)
			return 0;
		if (maxSearchDepth == 0)
			return Integer.MIN_VALUE;
		int shortestPath = Integer.MAX_VALUE;
		for (Node successor : node.getSuccessors(Node.class)) {
			int	currentSuccDistance = getShortest(successor, maxSearchDepth -1);
			if (currentSuccDistance < shortestPath && currentSuccDistance >= 0)
				shortestPath = currentSuccDistance;
		}
		return shortestPath + 1;
	}

}
