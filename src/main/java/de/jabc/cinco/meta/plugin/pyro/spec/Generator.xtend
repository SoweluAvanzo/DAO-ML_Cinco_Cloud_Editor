package de.jabc.cinco.meta.plugin.pyro.spec

import de.jabc.cinco.meta.core.utils.CincoUtil
import de.jabc.cinco.meta.plugin.pyro.util.FileGenerator
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class Generator extends FileGenerator {

	new(String base) {
		super(base)
	}

	def generate(GeneratorCompound gc) {
		//generate spec.json
		{
			val gen = new SpecJson(gc)
			generateFile(".",
				gen.filename,
				gen.content
			)
		}
	}
}
