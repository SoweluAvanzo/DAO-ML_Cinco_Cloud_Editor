package info.scce.pyro.core.rest.types;

/**
 * Author zweihoff
 */

public class PyroEditorGridItem extends info.scce.pyro.rest.RESTBaseImpl {

    private Long x;

    @com.fasterxml.jackson.annotation.JsonProperty("x")
    public Long getx() {
        return this.x;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("x")
    public void setx(final Long x) {
        this.x = x;
    }
    
    private Long y;

    @com.fasterxml.jackson.annotation.JsonProperty("y")
    public Long gety() {
        return this.y;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("y")
    public void sety(final Long y) {
        this.y = y;
    }
    
    private Long width;

    @com.fasterxml.jackson.annotation.JsonProperty("width")
    public Long getwidth() {
        return this.width;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("width")
    public void setwidth(final Long width) {
        this.width = width;
    }
    
    private Long height;

    @com.fasterxml.jackson.annotation.JsonProperty("height")
    public Long getheight() {
        return this.height;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("height")
    public void setheight(final Long height) {
        this.height = height;
    }
    
    private java.util.List<PyroEditorWidget> widgets = new java.util.LinkedList<>();

    @com.fasterxml.jackson.annotation.JsonProperty("widgets")
    public java.util.List<PyroEditorWidget> getwidgets() {
        return this.widgets;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("widgets")
    public void setwidgets(final java.util.List<PyroEditorWidget> widgets) {
        this.widgets = widgets;
    }
    
    public static PyroEditorGridItem fromEntity(final entity.core.PyroEditorGridItemDB entity, info.scce.pyro.rest.ObjectCache objectCache) {

        if(objectCache.containsRestTo(entity)){
            return objectCache.getRestTo(entity);
        }
        
        final PyroEditorGridItem result;
        result = new PyroEditorGridItem();
        result.setId(entity.id);

        result.setx(entity.x);
        result.sety(entity.y);
        result.setwidth(entity.width);
        result.setheight(entity.height);

        objectCache.putRestTo(entity, result);
        
        for(entity.core.PyroEditorWidgetDB o : entity.widgets){
            result.getwidgets().add(PyroEditorWidget.fromEntity(o, objectCache));
        }

        return result;
    }
}