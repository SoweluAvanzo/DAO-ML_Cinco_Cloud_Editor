package entity.core;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity()
public class PyroEditorGridDB extends PanacheEntity {
    
    @javax.persistence.ManyToOne
    public entity.core.PyroUserDB user;
    
    @javax.persistence.ManyToOne
    public entity.core.PyroProjectDB project;
    
    @javax.persistence.OneToMany
    public java.util.Collection<entity.core.PyroEditorGridItemDB> items = new java.util.ArrayList<>();
    
    @javax.persistence.OneToMany
    public java.util.Collection<entity.core.PyroEditorWidgetDB> availableWidgets = new java.util.ArrayList<>();
    
    @Override
    public void delete() {
    	user = null;
    	project = null;
    	
    	java.util.Iterator<entity.core.PyroEditorGridItemDB> iter_items = items.iterator();
    	while(iter_items.hasNext()) {
    		entity.core.PyroEditorGridItemDB next = iter_items.next();
    		items.remove(next);
    		next.delete();
    		iter_items = items.iterator();
    	}
    	
    	java.util.Iterator<entity.core.PyroEditorWidgetDB> iter_widgets = availableWidgets.iterator();
    	while(iter_widgets.hasNext()) {
    		entity.core.PyroEditorWidgetDB next = iter_widgets.next();
    		availableWidgets.remove(next);
    		next.delete();
    		iter_widgets = availableWidgets.iterator();
    	}
    	
    	super.delete();
    }
}