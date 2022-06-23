package de.jabc.cinco.meta.plugin.pyro.canvas

import de.jabc.cinco.meta.core.utils.CincoUtil
import de.jabc.cinco.meta.plugin.pyro.util.FileGenerator
import de.jabc.cinco.meta.plugin.pyro.util.FileHandler
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import de.jabc.cinco.meta.plugin.pyro.util.MGLExtension
import de.jabc.cinco.meta.plugin.pyro.util.Escaper
import mgl.GraphicalModelElement

class Generator extends FileGenerator {
	
	extension MGLExtension mglExtension
	extension Escaper = new Escaper
	
	new(String base) {
		super(base)
		
	}
	
	def generator(GeneratorCompound gc, String projectLocation) {
		
		mglExtension = gc.mglExtension
		
		// create typeSwitch
		{
			val path = "web/js/"
			val gen = new TypeSwitch(gc)
			generateFile(path,
				gen.fileName(),
				gen.content()
			)
		}
		
		//create shapes
		{
			gc.mglModels.forEach[g|{
				//web.asset.js.graphmodel
				val path = "web/js/"+g.name.lowEscapeDart
				val gen = new Shapes(gc)
				val styles = CincoUtil.getStyles(g)
				generateFile(path,
					gen.fileNameShapes(g),
					gen.contentShapes(styles, g)
				)
			}]
		}
		
		//copy images
		gc.mglModels.forEach[g|{
				val styles = CincoUtil.getStyles(g)
				g.elements
				.filter(GraphicalModelElement)
				.filter[!isIsAbstract]
				.map[styling(styles)]
				.map[getImages]
				.flatten
				.forEach[e|FileHandler.copyFile(e,e.path,basePath+"/web/"+e.iconPath(false).toString.toLowerCase,true, projectLocation)]			
			}
		]
		
		//create controller
		{
			gc.mglModels.forEach[m|{
				//web.asset.js.graphmodel.controller
				val path = "web/js/"+m.name.lowEscapeDart+"/"
				m.concreteGraphModels.forEach[g|
					val gen = new Controller(gc)
					val styles = CincoUtil.getStyles(g)
					generateFile(path + '''«g.name.lowEscapeDart»''',
						gen.fileNameController,
						gen.contentController(g,styles)
					)
				]
			}]
		}
	}
}
