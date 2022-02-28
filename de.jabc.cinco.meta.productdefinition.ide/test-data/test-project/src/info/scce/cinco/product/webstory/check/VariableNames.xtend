package info.scce.cinco.product.webstory.check;

import info.scce.cinco.product.webstory.webstory.WebStory

import info.scce.cinco.product.webstory.mcam.modules.checks.WebStoryCheck

class VariableNames extends WebStoryCheck {

	override check(WebStory story) {
		
		// functional implementation working on Seqs from the jOOλ library
		story.variables.filter[name.nullOrEmpty].forEach[addError("Variable name is required.")]
		story.variables.filter[i|
			story.variables.filter[i.name.equals(it.name)].length>=2
		].forEach[addError('''Duplicate variable name '«name»'.''')]

		// alternative imperative implementation
		/*
		val seenNames = new HashSet<String>()
		for(variable: story.variables) {
			if(variable.name.nullOrEmpty) {
				variable.addError("Variable name is required.")
			}
			else {
				if (!seenNames.add(variable.name)) {
					variable.addError('''Duplicate variable name '«variable.name»'.''')
				}
			}
		}
		*/
	}
}
