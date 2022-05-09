package info.scce.cinco.product.base.process.hook;

import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook;
import info.scce.cinco.product.base.process.baseprocess.Port;

public class PortPostCreate extends CincoPostCreateHook<Port>{
	
	public static final int Y_OFF = 25;
	public static final int PORT_HEIGHT = 20;

	@Override
	public void postCreate(Port object) {
		graphmodel.Container container = (graphmodel.Container) object.getContainer();
		int y = (container.getNodes().size() -1) * PORT_HEIGHT;
		object.move(5, Y_OFF + y);
		container.resize(container.getWidth(), Y_OFF + y + PORT_HEIGHT);
	}
}
