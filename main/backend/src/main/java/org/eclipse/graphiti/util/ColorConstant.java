package org.eclipse.graphiti.util;

public class ColorConstant implements IColorConstant {

    private int red;

    private int green;

    private int blue;

    /**
     * Creates a new {@link ColorConstant} given the desired red, green and blue
     * values expressed as ints in the range 0 to 255 (where 0 is black and 255
     * is full brightness).
     *
     * @param red
     *            the amount of red in the color
     * @param green
     *            the amount of green in the color
     * @param blue
     *            the amount of blue in the color
     */
    public ColorConstant(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * Creates a new {@link ColorConstant} for for a given String, which defines
     * the RGB values in hexadecimal format. This means, that the String must
     * have a length of 6 characters. Example: <code>getColor("FF0000")</code>
     * returns a red color.
     *
     * @param hexRGBString
     *            The RGB values in hexadecimal format.
     * @since 0.8
     */
    public ColorConstant(String hexRGBString) {
        this(Integer.valueOf( hexRGBString.substring( 0, 2 ), 16 ), Integer.valueOf( hexRGBString.substring( 2, 4 ), 16 ), Integer.valueOf( hexRGBString.substring( 4, 6 ), 16 ));
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

}
