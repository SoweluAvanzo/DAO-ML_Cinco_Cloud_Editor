package info.scce.pyro.core.highlight.impl;
import info.scce.pyro.core.command.CommandExecuter;
import info.scce.pyro.core.highlight.HighlightFactory;

/**
 * Author zweihoff
 */
@javax.enterprise.context.RequestScoped
public class HighlightFactoryImpl implements HighlightFactory {

	private CommandExecuter executer;

	public void warmup(CommandExecuter executer) {
		this.executer = executer;
	}

	public static HighlightFactory init() {
		return new HighlightFactoryImpl();
	}
	
	public CommandExecuter getCommandExecuter() {
		return executer;
	}
	
}
