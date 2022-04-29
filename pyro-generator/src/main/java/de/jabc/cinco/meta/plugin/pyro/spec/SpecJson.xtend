package de.jabc.cinco.meta.plugin.pyro.spec

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class SpecJson extends Generatable {

	new(GeneratorCompound gc) {
		super(gc)
	}

	def filename()'''spec.json'''

	def content()
	'''
	{
		"graphModelTypes": [
            «FOR g:gc.concreteGraphModels SEPARATOR ","»
                {
                    "typeName": "«g.typeName»",
                    "fileExtension": "«g.fileExtension»"
                }
            «ENDFOR»
        ]
    }
	'''
}
