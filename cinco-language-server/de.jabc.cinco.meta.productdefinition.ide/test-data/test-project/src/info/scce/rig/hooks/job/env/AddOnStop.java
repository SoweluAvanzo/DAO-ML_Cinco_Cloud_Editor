//package info.scce.rig.hooks.job.env;
//
//import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
//import info.scce.rig.pipeline.Environment;
//
//public class AddOnStop extends CincoCustomAction<Environment> {
//	
//	@Override
//	public boolean canExecute(Environment env) {
//		return env.canNewOnStop();
//	}
//	
//	@Override
//	public String getName() {
//		return "(+)  On Stop";
//	}
//	@Override
//	public void execute (Environment env) {
//		env.newOnStop(0, 0);
//	}
//}
