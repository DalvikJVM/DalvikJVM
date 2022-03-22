/**
 *	This file is part of DalvikJVM.
 *
 *	DalvikJVM is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	DalvikJVM is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with DalvikJVM.  If not, see <http://www.gnu.org/licenses/>.
 *
 *	Authors: see <https://github.com/DalvikJVM/DalvikJVM>
 */

package java.awt;

public class Color {
    public static Color black = new Color(0, 0, 0);
    public static Color gray = new Color(192, 192, 192);
    public static Color blue = new Color(0, 0, 255);
    public static Color white = new Color(255, 255, 255);
    public static Color lightGray = new Color(192, 192, 192);
    public static Color yellow = new Color(255, 255, 0);

    public static Color BLACK = black;
    public static Color WHITE = white;
    public static Color LIGHT_GRAY = lightGray;
    public static Color GRAY = gray;

    private int r;
    private int g;
    private int b;
    private int a;

    public Color(int rgb) {
        this.r = (rgb >> 16) & 0xFF;
        this.g = (rgb >> 8) & 0xFF;
        this.b = rgb & 0xFF;
        this.a = 0xFF;
    }

    public Color(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public Color(int rgba, boolean hasAlpha) {
        if (hasAlpha) {
            this.r = (rgba >> 24) & 0xFF;
            this.g = (rgba >> 16) & 0xFF;
            this.b = (rgba >> 8) & 0xFF;
            this.a = rgba & 0xFF;
        } else {
            this.r = (rgba >> 16) & 0xFF;
            this.g = (rgba >> 8) & 0xFF;
            this.b = rgba & 0xFF;
            this.a = 0xFF;
        }
    }

    public int getRGB() {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public Color darker() {
        return new Color((int)(r * 0.7f), (int)(g * 0.7f), (int)(b * 0.7f));
    }

    public Color brighter() {
        int hues[] = new int[3];
        hues[0] = r;
        hues[1] = g;
        hues[2] = b;

        if (r == 0 && g == 0 && b == 0) {
            hues[0] = 3;
            hues[1] = 3;
            hues[2] = 3;
        } else {
            for (int i = 0; i < 3; i++)
            {
                if (hues[i] > 2)
                    hues[i] = (int)Math.min(255.0f, hues[i] / 0.7f);
                if (hues[i] == 1 || hues[i] == 2)
                    hues[i] = 4;
            }
        }

        return new Color(hues[0], hues[1], hues[2]);
    }
}
