package info.scce.rig.hooks.job.rule;

import de.jabc.cinco.meta.runtime.action.CincoCustomAction;
import info.scce.rig.pipeline.Rule;

public class AddChange extends CincoCustomAction<Rule> {

	@Override
	public boolean canExecute(Rule rule) {
		return rule.canNewChange();
	}
	
	@Override
	public String getName() {
		return "(+) Change";
	}
	
	@Override
	public void execute(Rule rule) {
		rule.newChange(0, 0);
	}
}
