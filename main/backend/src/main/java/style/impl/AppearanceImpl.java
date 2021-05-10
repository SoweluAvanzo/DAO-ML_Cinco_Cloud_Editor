package style.impl;


import style.*;

public class AppearanceImpl implements Appearance {

	private String id;
    private Color background;
    private Color foreground;
    private LineStyle lineStyle = LineStyle.UNSPECIFIED;
    private int lineWidth = -1;
    private Boolean lineInVisible = Boolean.FALSE;
    private double transparancy = -1.0;
    private float angle = -1.0F;
    private String name = null;
    private Appearance parent;
    private Font font;
    private BooleanEnum filled = BooleanEnum.UNDEF;
    private String imagePath;

    protected AppearanceImpl() {}
    
    @Override
    public String getId() {
    	return id;
    }
    
    @Override
    public void setId(String id) {
    	this.id = id;
    }

    @Override
    public Color getBackground() {
        return background;
    }

    @Override
    public void setBackground(Color value) {
        background = value;
    }

    @Override
    public Color getForeground() {
        return foreground;
    }

    @Override
    public void setForeground(Color value) {
        foreground = value;
    }

    @Override
    public LineStyle getLineStyle() {
        return lineStyle;
    }

    @Override
    public void setLineStyle(LineStyle value) {
        lineStyle = value;
    }

    @Override
    public int getLineWidth() {
        return lineWidth;
    }

    @Override
    public void setLineWidth(int value) {
        lineWidth = value;
    }

    @Override
    public Boolean getLineInVisible() {
        return lineInVisible;
    }

    @Override
    public void setLineInVisible(Boolean value) {
        lineInVisible = value;
    }

    @Override
    public double getTransparency() {
        return transparancy;
    }

    @Override
    public void setTransparency(double value) {
        transparancy = value;
    }

    @Override
    public float getAngle() {
        return angle;
    }

    @Override
    public void setAngle(float value) {
        angle = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String value) {
        name = value;
    }

    @Override
    public Appearance getParent() {
        return parent;
    }

    @Override
    public void setParent(Appearance value) {
        parent = value;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public void setFont(Font value) {
        font = value;
    }

    @Override
    public BooleanEnum getFilled() {
        return filled;
    }

    @Override
    public void setFilled(BooleanEnum value) {
        filled = value;
    }

    @Override
    public String getImagePath() {
        return imagePath;
    }

    @Override
    public void setImagePath(String value) {
        imagePath = value;
    }
} // Appearance
