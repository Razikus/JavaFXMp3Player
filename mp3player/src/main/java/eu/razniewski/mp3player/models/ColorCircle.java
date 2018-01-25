package eu.razniewski.mp3player.models;

import javafx.scene.paint.Color;

public class ColorCircle {

    private Color color;
    private double hue = 0;
    private boolean colorSwitcher = false;

    private double getNextHue(double from) {
        if(from == 360 || from == 0) {
            colorSwitcher = !colorSwitcher;
        }

        if(colorSwitcher) {
            return from + 0.4;
        } else{
            return from - 0.4;
        }
    }

    public ColorCircle() {
        color = Color.hsb(hue, 0.68, 0.77);
    }

    public Color getColor() {
        hue = getNextHue(hue);
        color = Color.hsb(hue, 0.68, 0.77);
        return color;
    }
}
