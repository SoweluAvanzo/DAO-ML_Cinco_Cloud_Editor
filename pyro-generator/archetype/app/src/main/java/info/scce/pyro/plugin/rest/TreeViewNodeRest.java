package info.scce.pyro.plugin.rest;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.util.List;

/**
 * Author zweihoff
 */

public class TreeViewNodeRest extends info.scce.pyro.rest.RESTBaseImpl
{

    private String __type;

    @com.fasterxml.jackson.annotation.JsonProperty("__type")
    public String get__type() {
        return this.__type;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("__type")
    public void set__type(final String __type) {
        this.__type = __type;
    }

    private String label;

    @com.fasterxml.jackson.annotation.JsonProperty("label")
    public String getlabel() {
        return this.label;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("label")
    public void setlabel(final String label) {
        this.label = label;
    }

    private String iconpath;

    @com.fasterxml.jackson.annotation.JsonProperty("iconpath")
    public String geticonpath() {
        return this.iconpath;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("iconpath")
    public void seticonpath(final String iconpath) {
        this.iconpath = iconpath;
    }

    private boolean isClickable;

    @com.fasterxml.jackson.annotation.JsonProperty("isClickable")
    public boolean getisClickable() {
        return this.isClickable;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("isClickable")
    public void setisClickable(final boolean isClickable) {
        this.isClickable = isClickable;
    }

    private boolean isDoubleClickable;

    @com.fasterxml.jackson.annotation.JsonProperty("isDoubleClickable")
    public boolean getisDoubleClickable() {
        return this.isDoubleClickable;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("isDoubleClickable")
    public void setisDoubleClickable(final boolean isDoubleClickable) {
        this.isDoubleClickable = isDoubleClickable;
    }

    private boolean isDragable;

    @com.fasterxml.jackson.annotation.JsonProperty("isDragable")
    public boolean getisDragable() {
        return this.isDragable;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("isDragable")
    public void setisDragable(final boolean isDragable) {
        this.isDragable = isDragable;
    }

    private List<TreeViewNodeRest> children;

    @com.fasterxml.jackson.annotation.JsonProperty("children")
    public List<TreeViewNodeRest> getchildren() {
        return this.children;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("children")
    public void setchildren(final List<TreeViewNodeRest> children) {
        this.children = children;
    }

    public static TreeViewNodeRest fromEntity(
            final PanacheEntity entity,
            info.scce.pyro.rest.ObjectCache objectCache,
            String label,
            String iconpath,
            String type,
            boolean isClickable,
            boolean isDoubleClickable,
            boolean isDraggable,
            List<TreeViewNodeRest> children
    ) {
        if (objectCache.containsRestTo(entity)) {
            return objectCache.getRestTo(entity);
        }
        final TreeViewNodeRest result = new TreeViewNodeRest();
        if(entity != null) {
        	// container node like a list should not be cached and has an entity that is null
            objectCache.putRestTo(entity, result);
            result.setId(entity.id);
        } else {
        	// node is just a container, no entity
        	result.setId(-1);
        }        
        
        result.set__type(type);
        result.seticonpath(iconpath);
        result.setlabel(label);
        result.setisClickable(isClickable);
        result.setisDoubleClickable(isDoubleClickable);
        result.setisDragable(isDraggable);
        result.setchildren(children);

        return result;
    }
}
