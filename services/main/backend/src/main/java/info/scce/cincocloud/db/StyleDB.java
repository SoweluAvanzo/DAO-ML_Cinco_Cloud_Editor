package info.scce.cincocloud.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class StyleDB extends PanacheEntity {

  public String navBgColor;
  public String navTextColor;
  public String bodyBgColor;
  public String bodyTextColor;
  public String primaryBgColor;
  public String primaryTextColor;

  @OneToOne(cascade = CascadeType.ALL)
  public BaseFileDB profilePicture;

  @OneToOne(cascade = CascadeType.ALL)
  public BaseFileDB logo;
}
