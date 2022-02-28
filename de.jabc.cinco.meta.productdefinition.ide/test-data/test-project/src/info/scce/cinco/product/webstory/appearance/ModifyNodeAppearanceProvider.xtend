package info.scce.cinco.product.webstory.appearance;

import info.scce.cinco.product.webstory.webstory.ModifyVariable
import style.Appearance
import style.Color
import style.StyleFactory
import de.jabc.cinco.meta.core.ge.style.generator.runtime.appearance.StyleAppearanceProvider

class ModifyNodeAppearanceProvider implements StyleAppearanceProvider<ModifyVariable> {
	
	val Color grey = StyleFactory.eINSTANCE.createColor
	val Color red = StyleFactory.eINSTANCE.createColor
	val Color green = StyleFactory.eINSTANCE.createColor
	
	new() {
		grey => [ r = 158 g = 158 b = 158 ]
		green => [ r = 62 g = 220 b = 62 ]
		red => [ r = 255 g = 32 b = 32 ]
	}

	override getAppearance(ModifyVariable node, String element) {
		val Appearance appearance = StyleFactory.eINSTANCE.createAppearance()
		appearance  => [
			if (element == "LED_green") {
				lineWidth = 1
				if (node.value) {
					foreground = green
					background = green
				}
				else {
					foreground = grey
					background = grey
				}
			}
			else if (element == "LED_red") {
				lineWidth = 1
				if (node.value) {
					foreground = grey
					background = grey
				}
				else {
					foreground = red
					background = red
				}
			}
		]
		appearance
	}
}
