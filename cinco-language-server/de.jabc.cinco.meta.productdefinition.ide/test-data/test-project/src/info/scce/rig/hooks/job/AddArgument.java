package info.scce.rig.hooks.job;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Job;
import info.scce.rig.pipeline.ScriptArgument;

public class AddArgument extends CincoCustomAction<Job> {
	
	@Override
	public boolean canExecute(Job job) {
		return job.canNewScriptArgument();
	}
	
	@Override
	public String getName() {
		return "(+) Script Argument";
	}

	@Override
	public void execute(Job job) {
		ScriptArgument arg = job.newScriptArgument(0, 0);
		arg.setKey("arg" + (job.getNodes(ScriptArgument.class).size()-1));
	}
}