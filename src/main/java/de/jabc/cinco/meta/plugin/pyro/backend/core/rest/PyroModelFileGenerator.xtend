package de.jabc.cinco.meta.plugin.pyro.backend.core.rest

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class PyroModelFileGenerator extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileName()
	'''PyroModelFile.java'''
	
	def content()
	'''
	package info.scce.pyro.core.rest.types;
	
	/**
	 * Author zweihoff
	 */
	
	public class PyroModelFile extends PyroFile
	{
		private boolean isPublic;
	
	    @com.fasterxml.jackson.annotation.JsonProperty("isPublic")
	    public boolean getisPublic() {
	        return this.isPublic;
	    }
	
	    @com.fasterxml.jackson.annotation.JsonProperty("isPublic")
	    public void setisPublic(final boolean isPublic) {
	        this.isPublic = isPublic;
	    }
		«FOR g:gc.graphMopdels»
			
			public static PyroModelFile fromEntity(final «g.entityFQN» entity, info.scce.pyro.rest.ObjectCache objectCache) {
				if(objectCache.containsRestTo(entity)){
					return objectCache.getRestTo(entity);
				}
				final PyroModelFile result;
				result = new PyroModelFile();
				result.setId(entity.id);
				result.set__type("«g.typeName»");
				result.setisPublic(entity.isPublic);
				
				result.setfilename(entity.filename);
				result.setextension(entity.extension);
				
				objectCache.putRestTo(entity, result);
				
				return result;
			}
		«ENDFOR»
		«FOR g:gc.ecores»
			
			public static PyroModelFile fromEntity(final «g.entityFQN» entity, info.scce.pyro.rest.ObjectCache objectCache) {
				if(objectCache.containsRestTo(entity)){
					return objectCache.getRestTo(entity);
				}
				final PyroModelFile result;
				result = new PyroModelFile();
				result.setId(entity.id);
				result.set__type("«g.typeName»");
				result.setisPublic(false); // ecores do not have this property
				
				result.setfilename(entity.filename);
				result.setextension(entity.extension);
				
				objectCache.putRestTo(entity, result);
				
				return result;
			}
		«ENDFOR»
	}

	
	'''
	
}
