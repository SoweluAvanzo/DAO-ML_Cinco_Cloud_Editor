package info.scce.cinco.product.ml.process;

import de.jabc.cinco.meta.runtime.action.CincoDoubleClickAction;
import info.scce.cinco.product.ml.process.mlprocess.Closure;
import info.scce.cinco.product.ml.process.mlprocess.Inputs;
import info.scce.cinco.product.ml.process.mlprocess.Outputs;

public class CreateInferredPort extends CincoDoubleClickAction<Closure>{

	@Override
	public void execute(Closure element) {
		if(element instanceof Inputs) {
			((Inputs) element).newStartOutputPort(0, 0);
		}
		if(element instanceof Outputs) {
			((Outputs) element).newEndInputPort(0, 0);
		}
	}
}
