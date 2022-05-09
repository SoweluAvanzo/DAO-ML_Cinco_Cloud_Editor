package info.scce.rig.hooks.job.rule;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Rule;

public class AddExists extends CincoCustomAction<Rule> {

	@Override
	public boolean canExecute(Rule rule) {
		return rule.canNewExists();
	}
	
	@Override
	public String getName() {
		return "(+) Exists";
	}
	
	@Override
	public void execute(Rule rule) {
		rule.newExists(0, 0);
	}
}
