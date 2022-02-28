package info.scce.cinco.product.webstory.generator

import info.scce.cinco.product.webstory.webstory.WebStory
import info.scce.cinco.product.webstory.webstory.util.WebStorySwitch

class VariableDeclarationsGenerator extends WebStorySwitch<CharSequence> {
	
	def generate(WebStory story) '''
		«FOR name: story.variables.map[n|n.name]»
			var «name» = false;
		«ENDFOR»
	'''
	
}