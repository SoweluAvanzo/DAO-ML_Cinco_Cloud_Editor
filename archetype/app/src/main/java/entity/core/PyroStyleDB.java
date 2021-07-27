package entity.core;


public class PyroStyleDB {
    public long id = 0;
    public String navBgColor;
    public String navTextColor;
    public String bodyBgColor;
    public String bodyTextColor;
    public String primaryBgColor;
    public String primaryTextColor;
    public String profilePicture;
    public String logo;
    
    public static PyroStyleDB fromPOJO(info.scce.pyro.style.StylePojo p) {
    	PyroStyleDB s = new PyroStyleDB();
    	s.navBgColor = p.navBgColor;
    	s.navTextColor = p.navTextColor;
    	s.bodyBgColor = p.bodyBgColor;
    	s.bodyTextColor = p.bodyTextColor;
    	s.primaryBgColor = p.primaryBgColor;
    	s.primaryTextColor = p.primaryTextColor;
    	s.logo = p.logo;
    	return s;
    }
}