package info.scce.pyro.style;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Author zweihoff
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StylePojo {
    public String navBgColor;
    public String navTextColor;
    public String bodyBgColor;
    public String bodyTextColor;
    
    public String primaryBgColor;
    public String primaryTextColor;
    
    public String logo;
}