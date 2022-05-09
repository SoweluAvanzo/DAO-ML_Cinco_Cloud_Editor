package entity.core;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity()
public class PyroEditorWidgetDB extends PanacheEntity {

    public String tab;
    public String key;
    public long position;
    
    @javax.persistence.ManyToOne
    public entity.core.PyroEditorGridDB grid;
    
    @javax.persistence.ManyToOne
    public entity.core.PyroEditorGridItemDB area;
    
    @Override
    public void delete() {
    	grid = null;
    	area = null;
    	super.delete();
    }
}