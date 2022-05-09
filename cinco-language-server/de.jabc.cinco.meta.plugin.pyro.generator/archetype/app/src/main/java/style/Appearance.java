package style;


public interface Appearance {
    
    String getId();
    void setId(String id);

    Color getBackground();


    void setBackground(Color value);


    Color getForeground();


    void setForeground(Color value);


    LineStyle getLineStyle();


    void setLineStyle(LineStyle value);


    int getLineWidth();


    void setLineWidth(int value);


    Boolean getLineInVisible();


    void setLineInVisible(Boolean value);


    double getTransparency();


    void setTransparency(double value);

    float getAngle();


    void setAngle(float value);


    String getName();


    void setName(String value);


    Appearance getParent();


    void setParent(Appearance value);


    Font getFont();

    void setFont(Font value);


    BooleanEnum getFilled();


    void setFilled(BooleanEnum value);


    String getImagePath();

    void setImagePath(String value);

} // Appearance
