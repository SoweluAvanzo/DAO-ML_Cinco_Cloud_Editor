package info.scce.cinco.product.extendedflowgraph.action;

import info.scce.cinco.product.extendedflowgraph.extendedflowgraph.Start;
import de.jabc.cinco.meta.runtime.action.CincoCustomAction;

public class ShortestPathToEnd extends CincoCustomAction<Start> {
	static int index = 0;
	
	@Override
	public String getName() {
		return "Create External Library";
	}

	@Override
	/**
	 * @return always <code>true</code>, as the action can
	 * 		be executed for any Start node.
	 */
	public boolean canExecute(Start start) {
		return true;
	}

	@Override
	public void execute(Start start) {
		externallibrary.ExternalLibrary externalLibrary = new externallibrary.impl.ExternalLibraryImpl();
		externalLibrary.setFilename("ExternalLibrary_"+index);
		
		externallibrary.ExternalActivityLibrary eLibrary = new externallibrary.impl.ExternalActivityLibraryImpl();
		eLibrary.setName("eLibrary_"+index);
		eLibrary.setContainer(externalLibrary);
		externalLibrary.addExternalActivityLibrary(eLibrary);
		
		externallibrary.ExternalActivityA eActivityA = new externallibrary.impl.ExternalActivityAImpl();
		eActivityA.setName("eActivityA_"+index);
		eActivityA.setContainer(externalLibrary);
		externalLibrary.addExternalActivityA(eActivityA);
	}
}	
