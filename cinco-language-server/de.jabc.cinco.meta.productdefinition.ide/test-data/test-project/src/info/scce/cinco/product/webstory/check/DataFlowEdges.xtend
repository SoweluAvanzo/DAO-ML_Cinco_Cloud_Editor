package info.scce.cinco.product.webstory.check

import info.scce.cinco.product.webstory.mcam.modules.checks.WebStoryCheck
import info.scce.cinco.product.webstory.webstory.WebStory

class DataFlowEdges extends WebStoryCheck {
	
	override check(WebStory story) {
		story.variables.filter[outgoing.empty].forEach[
			addWarning("Variable is never read")
		]
		story.variables.filter[incoming.empty].forEach[
			addWarning("Variable is never written")
		]
	}
}