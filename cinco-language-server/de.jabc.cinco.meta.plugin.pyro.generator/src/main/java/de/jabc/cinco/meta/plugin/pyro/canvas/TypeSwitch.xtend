package de.jabc.cinco.meta.plugin.pyro.canvas

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class TypeSwitch extends Generatable{
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileName() '''pyro_typeswitch.js'''

	def content() {
	'''
		$current_model = null;
		
		/**
		 * Drag and Drop functionalities
		 */
		
		function confirm_drop(event) {
			«parsedEventData(
				currentModelTypeSwitchFoo(
					'''confirm_drop'''
				)
			)»
		}
		
		function drop_on_canvas(event) {
			«parsedEventData(
				currentModelTypeSwitchFoo(
					'''drop_on_canvas'''
				)
			)»
		}
		
		function getCurrentType() {
			return $current_model;
		}
	'''
	}
	
	def parsedEventData(CharSequence foo) {
		'''
			try {
			    const data = JSON.parse(event.dataTransfer.getData("text"));
			    if(!data) {
			        return;
			    }
			    «foo»
			} catch(e) {
			    return;
			}
		'''
	}
	
	/*
	 * fooName - name of the function accepting the an Event, having a preceding "_«g.jsCall»" in it's name (implemented for all concrete GraphModels)
	 */
	def currentModelTypeSwitchFoo(CharSequence fooName) {
		'''
			const modelType = getCurrentType();
			switch(modelType) {
				«FOR g: gc.concreteGraphModels»
				    case '«g.typeName»': {
				        «fooName»_«g.jsCall»(event);
				        return;
				    }
			    «ENDFOR»
			}
	    '''
	}
}
