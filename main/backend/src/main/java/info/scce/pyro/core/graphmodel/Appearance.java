package info.scce.pyro.core.graphmodel;

public class Appearance {
	
    private String name;
    private String id;
    private int backgroundR;
    private int backgroundG;
    private int backgroundB;
    private int foregroundR;
    private int foregroundG;
    private int foregroundB;
    private String lineStyle;
    private int lineWidth = -1;
    private boolean lineInVisible;
    private double transparency = -1.0;
    private float angle = -1.0F;
    private String parent;
    private String fontName;
    private int fontSize;
    private boolean fontIsBold;
    private boolean fontIsItalic;
    private String filled = "UNDEF";
    private String imagePath;

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public String getName() {
        return name;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public String getId() {
        return id;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("background_r")
    public int getBackgroundR() {
        return backgroundR;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("background_r")
    public void setBackgroundR(int backgroundR) {
        this.backgroundR = backgroundR;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("background_g")
    public int getBackgroundG() {
        return backgroundG;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("background_g")
    public void setBackgroundG(int backgroundG) {
        this.backgroundG = backgroundG;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("background_b")
    public int getBackgroundB() {
        return backgroundB;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("background_b")
    public void setBackgroundB(int backgroundB) {
        this.backgroundB = backgroundB;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("foreground_r")
    public int getForegroundR() {
        return foregroundR;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("foreground_r")
    public void setForegroundR(int foregroundR) {
        this.foregroundR = foregroundR;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("foreground_g")
    public int getForegroundG() {
        return foregroundG;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("foreground_g")
    public void setForegroundG(int foregroundG) {
        this.foregroundG = foregroundG;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("foreground_b")
    public int getForegroundB() {
        return foregroundB;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("foreground_b")
    public void setForegroundB(int foregroundB) {
        this.foregroundB = foregroundB;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("lineStyle")
    public String getLineStyle() {
        return lineStyle;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("lineStyle")
    public void setLineStyle(String lineStyle) {
        this.lineStyle = lineStyle;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("lineWidth")
    public int getLineWidth() {
        return lineWidth;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("lineWidth")
    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("lineInVisible")
    public boolean isLineInVisible() {
        return lineInVisible;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("lineInVisible")
    public void setLineInVisible(boolean lineInVisible) {
        this.lineInVisible = lineInVisible;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("transparency")
    public double getTransparency() {
        return transparency;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("transparency")
    public void setTransparency(double transparency) {
        this.transparency = transparency;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("angle")
    public float getAngle() {
        return angle;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("angle")
    public void setAngle(float angle) {
        this.angle = angle;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("parent")
    public String getParent() {
        return parent;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("parent")
    public void setParent(String parent) {
        this.parent = parent;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("fontName")
    public String getFontName() {
        return fontName;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("fontName")
    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("fontSize")
    public int getFontSize() {
        return fontSize;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("fontSize")
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("fontBold")
    public boolean isFontIsBold() {
        return fontIsBold;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("fontBold")
    public void setFontIsBold(boolean fontIsBold) {
        this.fontIsBold = fontIsBold;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("fontItalic")
    public boolean isFontIsItalic() {
        return fontIsItalic;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("fontItalic")
    public void setFontIsItalic(boolean fontIsItalic) {
        this.fontIsItalic = fontIsItalic;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("filled")
    public String getFilled() {
        return filled;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("filled")
    public void setFilled(String filled) {
        this.filled = filled;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("imagePath")
    public String getImagePath() {
        return imagePath;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("imagePath")
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}