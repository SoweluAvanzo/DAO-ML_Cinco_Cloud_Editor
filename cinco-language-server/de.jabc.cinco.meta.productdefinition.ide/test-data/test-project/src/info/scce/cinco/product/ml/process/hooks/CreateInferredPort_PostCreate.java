package info.scce.cinco.product.ml.process.hooks;

import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook;
import info.scce.cinco.product.ml.process.mlprocess.Closure;
import info.scce.cinco.product.ml.process.mlprocess.Inputs;
import info.scce.cinco.product.ml.process.mlprocess.Outputs;

public class CreateInferredPort_PostCreate extends CincoPostCreateHook<Closure>{

	@Override
	public void postCreate(Closure cl) {
		if(cl instanceof Inputs){
			Inputs input = (Inputs) cl;
			input.newStartOutputPort(0,0);
		}else if(cl instanceof Outputs){
			Outputs output = (Outputs) cl;
			output.newEndInputPort(0,0);
		}
	}
}
