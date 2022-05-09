package info.scce.cinco.product.ml.process.actions;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.cinco.product.ml.process.mlprocess.MLProcess;

public class CreateInputsOutputsContextMenu extends CincoCustomAction<MLProcess>{

	@Override
	public void execute(MLProcess mlprocess) {
		if (mlprocess.getInputss().isEmpty() && mlprocess.getOutputss().isEmpty()) {
			mlprocess.newInputs(0,0);
			mlprocess.newOutputs(500, 0);
		} else if (mlprocess.getInputss().isEmpty() && !mlprocess.getOutputss().isEmpty()) {
			mlprocess.newInputs(0, 0);
		} else if (!mlprocess.getInputss().isEmpty() && mlprocess.getOutputss().isEmpty()) {
			mlprocess.newOutputs(500, 0);
		}
	}
	
	public String getName() {
		return "Create Inputs/Outputs-node for InternalService";
	}
}
