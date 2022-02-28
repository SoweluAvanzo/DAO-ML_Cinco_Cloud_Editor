package info.scce.cinco.product.webstory.check;

import info.scce.cinco.product.webstory.webstory.WebStory;
import info.scce.cinco.product.webstory.mcam.modules.checks.WebStoryCheck

class BackgroundImagePresent extends WebStoryCheck {
	
	override check(WebStory story) {
		story.screens.filter[backgroundImage.nullOrEmpty].forEach [
				addError("Screen's background image must be set")
		]
	}
}
