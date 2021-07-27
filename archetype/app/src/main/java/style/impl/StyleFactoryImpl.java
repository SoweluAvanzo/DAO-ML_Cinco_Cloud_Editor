package style.impl;

import style.Appearance;
import style.Color;
import style.Font;
import style.StyleFactory;

public class StyleFactoryImpl implements StyleFactory {

    public static StyleFactory init() {
        return new StyleFactoryImpl();
    }

    private StyleFactoryImpl() {
    }


    public Color createColor() {
        ColorImpl color = new ColorImpl();
        return color;
    }


    public Appearance createAppearance() {
        AppearanceImpl appearance = new AppearanceImpl();
        return appearance;
    }

    public Font createFont() {
        FontImpl font = new FontImpl();
        return font;
    }



} //StyleFactoryImpl
