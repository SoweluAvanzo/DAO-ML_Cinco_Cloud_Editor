package info.scce.cinco.product.ml.process;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import de.jabc.cinco.meta.runtime.hook.CincoPreDeleteHook;
import graphmodel.Container;
import graphmodel.Node;
import info.scce.cinco.product.ml.process.mlprocess.InferredPort;

public class IOPortPreDelete extends CincoPreDeleteHook<InferredPort> {

	@Override
	public void preDelete(InferredPort modelElement) {
		Container c = (Container) modelElement.getContainer();
		int y = 0;
		List<Node> nodes = new LinkedList<>(c.getNodes());
		nodes.sort(new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				return Integer.compare(o1.getY(), o2.getY());
			}
		});
		for(Node n:nodes) {
			n.move(Definitions.X_OFF, Definitions.Y_OFF + (y * Definitions.PORT_HEIGHT));
			y++;
		}
		int yMax = Definitions.Y_OFF+((y-1)*Definitions.PORT_HEIGHT);
		c.resize(c.getWidth(), yMax + 20);
	}
}