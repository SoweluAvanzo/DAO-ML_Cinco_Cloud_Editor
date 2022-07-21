package info.scce.cinco.product.webstory.appearance

import info.scce.cinco.product.webstory.webstory.Screen
import style.Appearance
import style.StyleFactory
import de.jabc.cinco.meta.core.ge.style.generator.runtime.appearance.StyleAppearanceProvider

class ScreenAppearanceProvider implements StyleAppearanceProvider<Screen> {

	override Appearance getAppearance(Screen screen, String element) {
		val Appearance appearance = StyleFactory.eINSTANCE.createAppearance()
		if ("bgimg".equals(element) && !screen.backgroundImage.nullOrEmpty) {
			appearance.imagePath = screen.backgroundImage
		} else {
			appearance.imagePath = null
		}
		appearance
	}
}
