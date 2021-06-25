package de.jabc.cinco.meta.plugin.pyro.preview

import de.jabc.cinco.meta.core.utils.CincoUtil
import de.jabc.cinco.meta.plugin.pyro.canvas.Shapes
import de.jabc.cinco.meta.plugin.pyro.util.FileGenerator
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class Generator extends FileGenerator {
	
	new(String base) {
		super(base)
	}
	
	def generate(GeneratorCompound gc) {
		//generate html file
		{
			//preview
			val path = "preview"
			val gen = new IndexHTML(gc)
			generateFile(path,
				gen.filename,
				gen.content
			)
		}
		//generate canvas model
		{
			//preview
			gc.mglModels.forEach[g|{
				val styles = CincoUtil.getStyles(g)
				val path = "preview/js"
				val gen = new Shapes(gc)
				generateFile(path,
					gen.fileNameShapes(g),
					gen.contentShapes(styles, g)
				)
			}]
		}
		
		//copy static resources
		de.jabc.cinco.meta.plugin.pyro.Generator.copyResources("frontend/app/web",basePath + "/preview/vendor")
	}
	
	
}
