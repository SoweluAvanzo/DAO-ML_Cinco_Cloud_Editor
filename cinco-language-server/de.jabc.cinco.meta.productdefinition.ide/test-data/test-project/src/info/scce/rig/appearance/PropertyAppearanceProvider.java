package info.scce.rig.appearance;

import de.jabc.cinco.meta.core.ge.style.generator.runtime.appearance.StyleAppearanceProvider;
import info.scce.rig.pipeline.Property;
import style.Appearance;
import style.StyleFactory;

public class PropertyAppearanceProvider implements StyleAppearanceProvider<Property> {

	@Override
	public Appearance getAppearance(Property property, String shape) {
		Appearance appearance = StyleFactory.eINSTANCE.createAppearance();
		switch (shape) {
			case "padlock":
				appearance.setTransparency(property.getIncoming().size() == 0 ? 1.0 : 0.0);
				break;
				
			case "propertyValue":
				appearance.setTransparency(property.getIncoming().size() == 0 ? 0.0 : 1.0);
				break;
		}
		return appearance;
	}
}
