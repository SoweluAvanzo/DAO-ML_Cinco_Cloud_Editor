package style.impl;


import style.Font;

public class FontImpl implements Font {

    private String fontName = null;
    private int size = -1;
    private boolean isBold = false;
    private boolean isItalic = false;

    protected FontImpl() {}


    @Override
    public String getFontName() {
        return fontName;
    }

    @Override
    public void setFontName(String value) {
        fontName = value;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void setSize(int value) {
        size = value;
    }

    @Override
    public boolean isIsBold() {
        return isBold;
    }

    @Override
    public void setIsBold(boolean value) {
        isBold = value;
    }

    @Override
    public boolean isIsItalic() {
        return isItalic;
    }

    @Override
    public void setIsItalic(boolean value) {
        isItalic = value;
    }
}
