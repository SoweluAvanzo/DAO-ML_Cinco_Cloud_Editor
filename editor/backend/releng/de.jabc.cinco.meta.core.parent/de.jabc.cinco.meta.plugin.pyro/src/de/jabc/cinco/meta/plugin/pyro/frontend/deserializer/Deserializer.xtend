package de.jabc.cinco.meta.plugin.pyro.frontend.deserializer

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.MGLModel

class Deserializer extends Generatable {
	
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filenameCommandPropertyDeserializer()
	'''command_property_deserializer.dart'''
	
	def contentCommandPropertyDeserializer()
	'''
	import 'package:«gc.projectName.escapeDart»/src/model/core.dart' as core;
	import 'package:«gc.projectName.escapeDart»/src/model/command.dart';
	class CommandPropertyDeserializer
	{
	  static Command deserialize(dynamic jsog)
	  {
	    if(jsog['commandType'] == 'CreateNode'){
	      return CreateNodeCommand.fromJSOG(jsog);
	    }
	    return null;
	  }
	}
	'''
	
	def fileNameGraphmodelPropertyDeserializer(String graphModelName)
	'''«graphModelName.lowEscapeDart»_property_deserializer.dart'''
	
	def contentGraphmodelPropertyDeserializer(MGLModel g)
	'''
		import 'package:«gc.projectName.escapeDart»/src/model/core.dart' as core;
		import 'package:«gc.projectName.escapeDart»/«g.modelFilePath»';
		
		class «g.propertyDeserializer»
		{
		  static core.IdentifiableElement deserialize(dynamic jsog,Map cache)
		  {
		  	if(jsog.containsKey('@ref')){
		  		return cache[jsog['@ref']];
		  	}
		    //for each graphmodel element, no types
		    «FOR elem:g.elements.filter[!isIsAbstract] SEPARATOR " else "
		    »if(jsog['runtimeType'] == '«elem.restFQN»'){
		      return «elem.name.fuEscapeDart».fromJSOG(jsog,cache);
		    }«
		    ENDFOR»
		    throw new Exception("Unknown element type: ${jsog['runtimeType']}");
		  }
		}
	'''
	
	def fileNamePropertyDeserializer()
	'''property_deserializer.dart'''
	
	def contentPropertyDeserializer()
	'''
		import 'package:«gc.projectName.escapeDart»/src/model/core.dart' as core;
		«FOR g:gc.mglModels»
			import '«g.propertyDeserializerFile»';
		«ENDFOR»
		
		class PropertyDeserializer
		{
		  static core.IdentifiableElement deserialize(dynamic jsog, String graphModelType, Map cache)
		  {
		    //for each graphmodel
		    «FOR g:gc.graphMopdels SEPARATOR " else "
		    »if(graphModelType == '«g.name.fuEscapeDart»' || graphModelType == '«g.name.lowEscapeDart»'){
		    	return «g.propertyDeserializer».deserialize(jsog,cache);
		    }«ENDFOR»
		    return null;
		  }
		}
	'''
}
