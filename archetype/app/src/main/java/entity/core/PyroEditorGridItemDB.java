package entity.core;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity()
public class PyroEditorGridItemDB extends PanacheEntity {
    
    public long x;
    public long y;
    public long width;
    public long height;
    
    @javax.persistence.OneToMany
    public java.util.Collection<entity.core.PyroEditorWidgetDB> widgets = new java.util.ArrayList<>();
    
    @Override
    public void delete() {
    	java.util.Iterator<entity.core.PyroEditorWidgetDB> iter_widgets = widgets.iterator();
    	while(iter_widgets.hasNext()) {
    		entity.core.PyroEditorWidgetDB next = iter_widgets.next();
    		widgets.remove(next);
    		next.delete();
    		iter_widgets = widgets.iterator();
    	}
    	
    	super.delete();
    }
}

