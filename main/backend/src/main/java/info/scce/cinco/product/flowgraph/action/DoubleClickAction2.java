package info.scce.cinco.product.flowgraph.action;

import de.jabc.cinco.meta.runtime.action.CincoDoubleClickAction;
import graphmodel.IdentifiableElement;

public class DoubleClickAction2 extends CincoDoubleClickAction<IdentifiableElement>{

	@Override
	public void execute(IdentifiableElement element) {
		// TODO Auto-generated method stub
		System.out.println("I am a doubleClickAction!");
	}

}
