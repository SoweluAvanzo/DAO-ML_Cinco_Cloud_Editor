package info.scce.rig.hooks.job.rule;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Rule;

public class AddWhen extends CincoCustomAction<Rule> {

	@Override
	public boolean canExecute(Rule rule) {
		return rule.canNewWhen();
	}
	
	@Override
	public String getName() {
		return "(+) When";
	}
	
	@Override
	public void execute(Rule rule) {
		rule.newWhen(0, 0);
	}
}