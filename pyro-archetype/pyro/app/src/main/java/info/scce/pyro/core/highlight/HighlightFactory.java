package info.scce.pyro.core.highlight;
import info.scce.pyro.core.command.CommandExecuter;

/**
 * Author zweihoff
 */
public interface HighlightFactory {

	public HighlightFactory eINSTANCE = info.scce.pyro.core.highlight.impl.HighlightFactoryImpl
			.init();
	public void warmup(CommandExecuter executer);
	
	public CommandExecuter getCommandExecuter();
}
