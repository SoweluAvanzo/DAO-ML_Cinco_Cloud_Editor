package info.scce.rig.hooks.job.rule;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Rule;

public class AddAllowFailure extends CincoCustomAction<Rule> {

	@Override
	public boolean canExecute(Rule rule) {
		return rule.canNewAllowFailure();
	}
	
	@Override
	public String getName() {
		return "(+) Allow Failure";
	}
	
	@Override
	public void execute(Rule rule) {
		rule.newAllowFailure(0, 0);
	}
}
