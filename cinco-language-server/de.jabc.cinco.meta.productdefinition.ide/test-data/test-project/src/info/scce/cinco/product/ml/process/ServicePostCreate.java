package info.scce.cinco.product.ml.process;

import de.jabc.cinco.meta.runtime.hook.CincoPostCreateHook;
import info.scce.cinco.product.ml.process.mlprocess.DataService;
import info.scce.cinco.product.ml.process.mlprocess.EndInputPort;
import info.scce.cinco.product.ml.process.mlprocess.ExternalService;
import info.scce.cinco.product.ml.process.mlprocess.InputPort;
import info.scce.cinco.product.ml.process.mlprocess.Inputs;
import info.scce.cinco.product.ml.process.mlprocess.InternalService;
import info.scce.cinco.product.ml.process.mlprocess.MLProcess;
import info.scce.cinco.product.ml.process.mlprocess.OutputPort;
import info.scce.cinco.product.ml.process.mlprocess.Outputs;
import info.scce.cinco.product.ml.process.mlprocess.StartOutputPort;
import jupyter.Function;
import jupyter.Parameter;

public class ServicePostCreate extends CincoPostCreateHook<DataService> {
	
	@Override
	public void postCreate(DataService object) {
		int y = 0;
		if(object instanceof ExternalService) {
			ExternalService es = (ExternalService)object;
			Function fun = (Function) es.getFun();
			if(fun != null) {
				es.setDocumentation(fun.getDocumentation());
				//add inputs
				for(Parameter i:fun.getInputs()) {
					es.newInputPort(i.getDelegateId(), Definitions.X_OFF, Definitions.Y_OFF+(y*Definitions.PORT_HEIGHT),Definitions.PORT_WIDTH, 18);
					y++;
				}
				//add outputs
				if(fun.getOutput() != null) {
					es.newOutputPort(fun.getOutput().getDelegateId(), Definitions.X_OFF, Definitions.Y_OFF+(y*Definitions.PORT_HEIGHT),Definitions.PORT_WIDTH, 18);
					y++;
				}
			}
		}
		if(object instanceof InternalService) {
			InternalService es = (InternalService)object;
			MLProcess p = es.getProMod();
			if(p != null) {
				es.setDocumentation(p.getDocumentation());
				//add inputs
				java.util.List<Inputs> inputs = p.getInputss();
				if(!inputs.isEmpty()) {
					for(StartOutputPort sop:inputs.get(0).getStartOutputPorts()) {
						//get type
						java.util.List<InputPort> inputPorts = sop.getInputPortSuccessors();
						if(!inputPorts.isEmpty()) {
							jupyter.Parameter type = (Parameter) inputPorts.get(0).getParameter();
							type.setName(sop.getName());
							es.newInputPort(
								type.getDelegateId(),
								Definitions.X_OFF,
								Definitions.Y_OFF+(y*Definitions.PORT_HEIGHT),
								Definitions.PORT_WIDTH, 18
							);
							y++;
						}
					}
				}
				//add outputs
				java.util.List<Outputs> outputs = p.getOutputss();
				if(!outputs.isEmpty()) {
					for(EndInputPort eip:outputs.get(0).getEndInputPorts()) {
						//get type
						java.util.List<OutputPort> outputPort = eip.getOutputPortPredecessors();
						if(!outputPort.isEmpty()) {
							jupyter.Parameter type = (Parameter) outputPort.get(0).getParameter();
							type.setName(eip.getName());
							es.newOutputPort(
								type.getDelegateId(),
								Definitions.X_OFF,
								Definitions.Y_OFF+(y*Definitions.PORT_HEIGHT),
								Definitions.PORT_WIDTH, 18
							);
							y++;
						}
					}
				}
			}
		}
		int height = Definitions.Y_OFF+(y * Definitions.PORT_HEIGHT);
		object.resize(object.getWidth(), height);
	}
}