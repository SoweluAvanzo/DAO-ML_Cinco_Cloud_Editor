package info.scce.cinco.product.ml.process;

import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook;
import graphmodel.Container;
import info.scce.cinco.product.ml.process.mlprocess.InferredPort;

public class IOPortPostCreate extends CincoPostCreateHook<InferredPort>{

	@Override
	public void postCreate(InferredPort object) {
		Container c = (Container) object.getContainer();
		int y = Definitions.Y_OFF + ((c.getNodes().size()) * Definitions.PORT_HEIGHT);
		object.move(Definitions.X_OFF, y);
		object.setName("data_" + c.getNodes().size());
		c.resize(c.getWidth(), y + 20);
	}
}