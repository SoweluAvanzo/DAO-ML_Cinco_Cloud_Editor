package style.impl;

import style.Color;

public class ColorImpl implements Color {


    private int r = 0;
    private int g = 0;
    private int b = 0;

    protected ColorImpl() {}

    @Override
    public int getR() {
        return r;
    }

    @Override
    public void setR(int value) {
        r = value;
    }

    @Override
    public int getG() {
        return g;
    }

    @Override
    public void setG(int value) {
        g = value;
    }

    @Override
    public int getB() {
        return b;
    }

    @Override
    public void setB(int value) {
        b = value;
    }
} // Color

