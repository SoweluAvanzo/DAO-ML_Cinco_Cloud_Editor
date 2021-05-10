package info.scce.pyro.core.rest.types;

/**
 * Author zweihoff
 */

public class GraphModelShared extends info.scce.pyro.rest.RESTBaseImpl
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
    
    public static GraphModelShared fromEntity(
    		final long id,
    		final boolean isPublic,
    		final String runtimeType) {
    	final GraphModelShared result = new GraphModelShared();
        
        result.setId(id);
        result.setisPublic(isPublic);
        result.setRuntimeType(runtimeType);

        return result;
    }
}
