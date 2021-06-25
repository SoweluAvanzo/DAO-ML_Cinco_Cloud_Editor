package de.jabc.cinco.meta.plugin.pyro.frontend.pages.editor.palette.graphs.graphmodel

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.Node
import mgl.GraphModel

class PaletteBuilder extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileNamePaletteBuilder() '''«paletteBuilderFile»'''
	
	def contentPaletteBuilder(GraphModel g) '''
	
	import 'package:«gc.projectName.escapeDart»/src/pages/editor/palette/list/list_view.dart';
	import 'package:«gc.projectName.escapeDart»/«g.modelFilePath»' as «g.modelPackage.name.lowEscapeJava»;
	
	
	class «g.name.fuEscapeDart»PaletteBuilder {
	
	  static List<MapList> build(«g.dartFQN» graph)
	  {
	    List<MapList> paletteMap = new List();
		«FOR group:g.elements.filter(Node).filter[creatabel].groupBy[paletteGroup].entrySet»
			paletteMap.add(new MapList('«group.key»',values: [
				«FOR entry:group.value SEPARATOR ","»
					new MapListValue('«entry.displayName»',instance: new «entry.dartFQN»(), identifier: "«entry.typeName»",«IF entry.hasIcon»imgPath:'«entry.iconPath»'«ENDIF»)
			  	«ENDFOR»
			]));
		«ENDFOR»
	    return paletteMap;
	  }
	}
	
	'''
	
}
