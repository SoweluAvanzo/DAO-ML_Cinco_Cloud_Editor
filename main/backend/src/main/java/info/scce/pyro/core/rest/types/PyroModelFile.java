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
	
	public static PyroModelFile fromEntity(final entity.empty.EmptyDB entity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(objectCache.containsRestTo(entity)){
			return objectCache.getRestTo(entity);
		}
		final PyroModelFile result;
		result = new PyroModelFile();
		result.setId(entity.id);
		result.set__type("empty.Empty");
		result.setisPublic(entity.isPublic);
		
		result.setfilename(entity.filename);
		result.setextension(entity.extension);
		
		objectCache.putRestTo(entity, result);
		
		return result;
	}
	
	public static PyroModelFile fromEntity(final entity.primerefs.PrimeRefsDB entity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(objectCache.containsRestTo(entity)){
			return objectCache.getRestTo(entity);
		}
		final PyroModelFile result;
		result = new PyroModelFile();
		result.setId(entity.id);
		result.set__type("primerefs.PrimeRefs");
		result.setisPublic(entity.isPublic);
		
		result.setfilename(entity.filename);
		result.setextension(entity.extension);
		
		objectCache.putRestTo(entity, result);
		
		return result;
	}
	
	public static PyroModelFile fromEntity(final entity.hierarchy.HierarchyDB entity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(objectCache.containsRestTo(entity)){
			return objectCache.getRestTo(entity);
		}
		final PyroModelFile result;
		result = new PyroModelFile();
		result.setId(entity.id);
		result.set__type("hierarchy.Hierarchy");
		result.setisPublic(entity.isPublic);
		
		result.setfilename(entity.filename);
		result.setextension(entity.extension);
		
		objectCache.putRestTo(entity, result);
		
		return result;
	}
	
	public static PyroModelFile fromEntity(final entity.hooksandactions.HooksAndActionsDB entity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(objectCache.containsRestTo(entity)){
			return objectCache.getRestTo(entity);
		}
		final PyroModelFile result;
		result = new PyroModelFile();
		result.setId(entity.id);
		result.set__type("hooksandactions.HooksAndActions");
		result.setisPublic(entity.isPublic);
		
		result.setfilename(entity.filename);
		result.setextension(entity.extension);
		
		objectCache.putRestTo(entity, result);
		
		return result;
	}
	
	public static PyroModelFile fromEntity(final entity.flowgraph.FlowGraphDB entity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(objectCache.containsRestTo(entity)){
			return objectCache.getRestTo(entity);
		}
		final PyroModelFile result;
		result = new PyroModelFile();
		result.setId(entity.id);
		result.set__type("flowgraph.FlowGraph");
		result.setisPublic(entity.isPublic);
		
		result.setfilename(entity.filename);
		result.setextension(entity.extension);
		
		objectCache.putRestTo(entity, result);
		
		return result;
	}
	
	public static PyroModelFile fromEntity(final entity.externallibrary.ExternalLibraryDB entity, info.scce.pyro.rest.ObjectCache objectCache) {
		if(objectCache.containsRestTo(entity)){
			return objectCache.getRestTo(entity);
		}
		final PyroModelFile result;
		result = new PyroModelFile();
		result.setId(entity.id);
		result.set__type("externallibrary.ExternalLibrary");
		result.setisPublic(false); // ecores do not have this property
		
		result.setfilename(entity.filename);
		result.setextension(entity.extension);
		
		objectCache.putRestTo(entity, result);
		
		return result;
	}
}


