package entity.core;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;

@Entity()
public class PyroStyleDB extends PanacheEntity {
    
    public String navBgColor;
    public String navTextColor;
    public String bodyBgColor;
    public String bodyTextColor;
    public String primaryBgColor;
    public String primaryTextColor;
    @javax.persistence.OneToOne(cascade=javax.persistence.CascadeType.ALL)
    public entity.core.BaseFileDB profilePicture;
    @javax.persistence.OneToOne(cascade=javax.persistence.CascadeType.ALL)
    public entity.core.BaseFileDB logo;
}