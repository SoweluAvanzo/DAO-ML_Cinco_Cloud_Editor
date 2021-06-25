package info.scce.pyro.core.rest.types;

/**
 * Author zweihoff
 */

public class PyroEditorGrid extends info.scce.pyro.rest.RESTBaseImpl {

    private PyroUser user = new PyroUser();

    @com.fasterxml.jackson.annotation.JsonProperty("user")
    public PyroUser geuser() {
        return this.user;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("user")
    public void setuser(final PyroUser user) {
        this.user = user;
    }
    
    private PyroProject project = new PyroProject();

    @com.fasterxml.jackson.annotation.JsonProperty("project")
    public PyroProject getproject() {
        return this.project;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("project")
    public void setproject(final PyroProject project) {
        this.project = project;
    }
    
    
    
    private java.util.List<PyroEditorGridItem> items = new java.util.LinkedList<>();

    @com.fasterxml.jackson.annotation.JsonProperty("items")
    public java.util.List<PyroEditorGridItem> getitems() {
        return this.items;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("items")
    public void setitems(final java.util.List<PyroEditorGridItem> items) {
        this.items = items;
    }
    
    
    
    private java.util.List<PyroEditorWidget> availableWidgets = new java.util.LinkedList<>();

    @com.fasterxml.jackson.annotation.JsonProperty("availableWidgets")
    public java.util.List<PyroEditorWidget> getavailableWidgets() {
        return this.availableWidgets;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("availableWidgets")
    public void setavailableWidgets(final java.util.List<PyroEditorWidget> availableWidgets) {
        this.availableWidgets = availableWidgets;
    }
    
    

    public static PyroEditorGrid fromEntity(final entity.core.PyroEditorGridDB entity, info.scce.pyro.rest.ObjectCache objectCache) {

        if(objectCache.containsRestTo(entity)){
            return objectCache.getRestTo(entity);
        }
        final PyroEditorGrid result;
        result = new PyroEditorGrid();
        result.setId(entity.id);

        result.setuser(PyroUser.fromEntity(entity.user,objectCache));
        result.setproject(PyroProject.fromEntity(entity.project,objectCache));
        
        objectCache.putRestTo(entity, result);
        
        for(entity.core.PyroEditorGridItemDB o : entity.items){
            result.getitems().add(PyroEditorGridItem.fromEntity(o, objectCache));
        }

        for(entity.core.PyroEditorWidgetDB o : entity.availableWidgets){
            result.getavailableWidgets().add(PyroEditorWidget.fromEntity(o, objectCache));
        }

        return result;
    }
}