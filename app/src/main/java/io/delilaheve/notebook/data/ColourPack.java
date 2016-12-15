package io.delilaheve.notebook.data;

import android.graphics.Color;

import io.delilaheve.notebook.common.Keys;

public class ColourPack {

    private int r;
    private int g;
    private int b;

    private String hex;

    private int colour;

    public ColourPack(int colour) {
        this.colour = colour;
        convert(Keys.CONVERT_COLOUR);
    }

    public int getColour() {
        return colour;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
        convert(Keys.CONVERT_HEX);
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
        convert(Keys.CONVERT_RGB);
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
        convert(Keys.CONVERT_RGB);
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
        convert(Keys.CONVERT_RGB);
    }

    private void convert(int source) {
        switch (source) {
            case Keys.CONVERT_COLOUR:
                break;
            case Keys.CONVERT_RGB:
                colour = Color.rgb(r, g, b);
                break;
            case Keys.CONVERT_HEX:
                colour = Color.parseColor(hex);
                break;
        }

        r = Color.red(colour);
        g = Color.green(colour);
        b = Color.blue(colour);

        hex = String.format("#%02X%02X%02X", r, g, b);
    }
}
