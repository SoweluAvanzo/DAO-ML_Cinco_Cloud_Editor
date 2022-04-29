package info.scce.pyro.core.rest.types;

public class PyroSettings extends info.scce.pyro.rest.RESTBaseImpl {
   
    private PyroStyle style;

    @com.fasterxml.jackson.annotation.JsonProperty("style")
    public PyroStyle getstyle() {
        return this.style;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("style")
    public void setstyle(final PyroStyle style) {
        this.style = style;
    }
    
    

    public static PyroSettings fromEntity(
    		final entity.core.PyroSettingsDB entity, 
    		final info.scce.pyro.rest.ObjectCache objectCache) {
        
        final PyroSettings result;
        result = new PyroSettings();
        result.setId(entity.id);
        result.setstyle(PyroStyle.fromEntity(entity.style, objectCache));
        
                
        return result;
    }
}