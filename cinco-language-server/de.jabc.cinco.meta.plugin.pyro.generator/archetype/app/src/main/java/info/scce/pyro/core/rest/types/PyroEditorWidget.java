package info.scce.pyro.core.rest.types;

public class PyroEditorWidget extends info.scce.pyro.rest.RESTBaseImpl {

	private PyroEditorGridItem area;

    @com.fasterxml.jackson.annotation.JsonProperty("area")
    public PyroEditorGridItem getarea() {
        return this.area;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("area")
    public void setarea(final PyroEditorGridItem area) {
        this.area = area;
    }
    
    private PyroEditorGrid grid;

    @com.fasterxml.jackson.annotation.JsonProperty("grid")
    public PyroEditorGrid getgrid() {
        return this.grid;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("grid")
    public void setgrid(final PyroEditorGrid grid) {
        this.grid = grid;
    }
	
    private String key;

    @com.fasterxml.jackson.annotation.JsonProperty("key")
    public String getkey() {
        return this.key;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("key")
    public void setkey(final String key) {
        this.key = key;
    }
    
    private String tab;

    @com.fasterxml.jackson.annotation.JsonProperty("tab")
    public String gettab() {
        return this.tab;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("tab")
    public void settab(final String tab) {
        this.tab = tab;
    }
    
    private Long position;

    @com.fasterxml.jackson.annotation.JsonProperty("position")
    public Long getposition() {
        return this.position;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("position")
    public void setposition(final Long position) {
        this.position = position;
    }
    
    public static PyroEditorWidget fromEntity(final entity.core.PyroEditorWidgetDB entity, info.scce.pyro.rest.ObjectCache objectCache) {

        if(objectCache.containsRestTo(entity)){
            return objectCache.getRestTo(entity);
        }
        
        final PyroEditorWidget result;
        result = new PyroEditorWidget();
        result.setId(entity.id);

        if (entity.area != null) {
        	result.setarea(PyroEditorGridItem.fromEntity(entity.area, objectCache));	
        }
        result.setgrid(PyroEditorGrid.fromEntity(entity.grid, objectCache));
        result.settab(entity.tab);
        result.setkey(entity.key);
        result.setposition(entity.position);
        
        objectCache.putRestTo(entity, result);

        return result;
    }
}