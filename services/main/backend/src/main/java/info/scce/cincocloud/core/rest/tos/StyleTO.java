package info.scce.cincocloud.core.rest.tos;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.db.StyleDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.rest.RESTBaseImpl;

public class StyleTO extends RESTBaseImpl {

  private String navBgColor;
  private String navTextColor;
  private String bodyBgColor;
  private String bodyTextColor;
  private String primaryBgColor;
  private String primaryTextColor;
  private FileReferenceTO logo;

  public static StyleTO fromEntity(
      final StyleDB entity,
      final ObjectCache objectCache
  ) {
    if (objectCache.containsRestTo(entity)) {
      return objectCache.getRestTo(entity);
    }

    final var result = new StyleTO();
    result.setId(entity.id);
    result.setnavBgColor(entity.navBgColor);
    result.setnavTextColor(entity.navTextColor);
    result.setbodyBgColor(entity.bodyBgColor);
    result.setbodyTextColor(entity.bodyTextColor);
    result.setprimaryBgColor(entity.primaryBgColor);
    result.setprimaryTextColor(entity.primaryTextColor);

    if (entity.logo != null) {
      result.setlogo(new FileReferenceTO(entity.logo));
    }

    objectCache.putRestTo(entity, result);

    return result;
  }

  @JsonProperty("navBgColor")
  public String getnavBgColor() {
    return this.navBgColor;
  }

  @JsonProperty("navBgColor")
  public void setnavBgColor(final String navBgColor) {
    this.navBgColor = navBgColor;
  }

  @JsonProperty("navTextColor")
  public String getnavTextColor() {
    return this.navTextColor;
  }

  @JsonProperty("navTextColor")
  public void setnavTextColor(final String navTextColor) {
    this.navTextColor = navTextColor;
  }

  @JsonProperty("bodyBgColor")
  public String getbodyBgColor() {
    return this.bodyBgColor;
  }

  @JsonProperty("bodyBgColor")
  public void setbodyBgColor(final String bodyBgColor) {
    this.bodyBgColor = bodyBgColor;
  }

  @JsonProperty("bodyTextColor")
  public String getbodyTextColor() {
    return this.bodyTextColor;
  }

  @JsonProperty("bodyTextColor")
  public void setbodyTextColor(final String bodyTextColor) {
    this.bodyTextColor = bodyTextColor;
  }

  @JsonProperty("primaryBgColor")
  public String getprimaryBgColor() {
    return this.primaryBgColor;
  }

  @JsonProperty("primaryBgColor")
  public void setprimaryBgColor(final String primaryBgColor) {
    this.primaryBgColor = primaryBgColor;
  }

  @JsonProperty("primaryTextColor")
  public String getprimaryTextColor() {
    return this.primaryTextColor;
  }

  @JsonProperty("primaryTextColor")
  public void setprimaryTextColor(final String primaryTextColor) {
    this.primaryTextColor = primaryTextColor;
  }

  @JsonProperty("logo")
  public FileReferenceTO getlogo() {
    return this.logo;
  }

  @JsonProperty("logo")
  public void setlogo(final FileReferenceTO logo) {
    this.logo = logo;
  }
}
