package info.scce.cinco.product.flowgraph.appearance;

import style.Appearance;
import style.LineStyle;
import style.StyleFactory;
import de.jabc.cinco.meta.core.ge.style.generator.runtime.appearance.StyleAppearanceProvider;
import info.scce.cinco.product.flowgraph.flowgraph.End;
import info.scce.cinco.product.flowgraph.flowgraph.Transition;

/**
 * This class implements a dynamic appearance for the simpleArrow style. 
 * It simply sets the lineStyle to DASH in case the target node is of 
 * the type End.
 *
 */
public class SimpleArrowAppearance implements StyleAppearanceProvider<Transition> {

	@Override
	public Appearance getAppearance(Transition transition, String element) {
		// element can be ignored here, as there are no named inner elements in the simpleArrow style
		Appearance appearance = StyleFactory.eINSTANCE.createAppearance();
		appearance.setLineWidth(2);
		if (transition.getTargetElement() instanceof End)
			appearance.setLineStyle(LineStyle.DASH);
		else
			appearance.setLineStyle(LineStyle.SOLID);
		return appearance;
	}

}
