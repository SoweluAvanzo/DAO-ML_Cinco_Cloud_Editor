package info.scce.rig.hooks.job.rule;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Rule;

public class AddIf extends CincoCustomAction<Rule> {

	@Override
	public boolean canExecute(Rule rule) {
		return rule.canNewVarExp();
	}
	
	@Override
	public String getName() {
		return "(+) If";
	}
	
	@Override
	public void execute(Rule rule) {
		rule.newVarExp(0, 0);
	}
}
