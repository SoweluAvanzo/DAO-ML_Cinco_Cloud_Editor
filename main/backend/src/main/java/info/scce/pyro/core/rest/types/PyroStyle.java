package info.scce.pyro.core.rest.types;

public class PyroStyle extends info.scce.pyro.rest.RESTBaseImpl {
   
	private java.lang.String navBgColor;

    @com.fasterxml.jackson.annotation.JsonProperty("navBgColor")
    public java.lang.String getnavBgColor() {
        return this.navBgColor;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("navBgColor")
    public void setnavBgColor(final java.lang.String navBgColor) {
        this.navBgColor = navBgColor;
    }
    
    private java.lang.String navTextColor;

    @com.fasterxml.jackson.annotation.JsonProperty("navTextColor")
    public java.lang.String getnavTextColor() {
        return this.navTextColor;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("navTextColor")
    public void setnavTextColor(final java.lang.String navTextColor) {
        this.navTextColor = navTextColor;
    }
    
    private java.lang.String bodyBgColor;

    @com.fasterxml.jackson.annotation.JsonProperty("bodyBgColor")
    public java.lang.String getbodyBgColor() {
        return this.bodyBgColor;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("bodyBgColor")
    public void setbodyBgColor(final java.lang.String bodyBgColor) {
        this.bodyBgColor = bodyBgColor;
    }
    
    private java.lang.String bodyTextColor;

    @com.fasterxml.jackson.annotation.JsonProperty("bodyTextColor")
    public java.lang.String getbodyTextColor() {
        return this.bodyTextColor;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("bodyTextColor")
    public void setbodyTextColor(final java.lang.String bodyTextColor) {
        this.bodyTextColor = bodyTextColor;
    }
    
    private java.lang.String primaryBgColor;

    @com.fasterxml.jackson.annotation.JsonProperty("primaryBgColor")
    public java.lang.String getprimaryBgColor() {
        return this.primaryBgColor;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("primaryBgColor")
    public void setprimaryBgColor(final java.lang.String primaryBgColor) {
        this.primaryBgColor = primaryBgColor;
    }    
    
    private java.lang.String primaryTextColor;

    @com.fasterxml.jackson.annotation.JsonProperty("primaryTextColor")
    public java.lang.String getprimaryTextColor() {
        return this.primaryTextColor;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("primaryTextColor")
    public void setprimaryTextColor(final java.lang.String primaryTextColor) {
        this.primaryTextColor = primaryTextColor;
    }
    
    private FileReference logo;

    @com.fasterxml.jackson.annotation.JsonProperty("logo")
    public FileReference getlogo() {
        return this.logo;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("logo")
    public void setlogo(final FileReference logo) {
        this.logo = logo;
    }
    
    public static PyroStyle fromEntity(
    		final entity.core.PyroStyleDB entity, 
    		final info.scce.pyro.rest.ObjectCache objectCache) {

        if (objectCache.containsRestTo(entity)) {
            return objectCache.getRestTo(entity);
        }
        
        final PyroStyle result;
        result = new PyroStyle();
        result.setId(entity.id);

        result.setnavBgColor(entity.navBgColor);
        result.setnavTextColor(entity.navTextColor);
        result.setbodyBgColor(entity.bodyBgColor);
        result.setbodyTextColor(entity.bodyTextColor);
        result.setprimaryBgColor(entity.primaryBgColor);
        result.setprimaryTextColor(entity.primaryTextColor);
        if (entity.logo != null) {
        	result.setlogo(new FileReference(entity.logo));
        }

        objectCache.putRestTo(entity, result);

        return result;
    }
}