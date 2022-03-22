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

import android.graphics.Typeface;

import java.android.awt.AndroidFontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.InputStream;

public class Font {
    AndroidFontMetrics fontMetrics;
    Typeface typeface;
    String name;
    int size;
    int style;

    public static final int PLAIN = 0;
    public static final int BOLD = 1;
    public static final int ITALIC = 2;

    public static Font defaultFont = new Font("Arial", Font.PLAIN, 12);

    public static Font createFont(int fontFormat, InputStream inputStream) {
        System.out.println("Unimplemented method Font.createFont(" + fontFormat + ", " + inputStream + ") called");
        return defaultFont;
    }

    public Font(String name, int style, int size) {
        boolean bold = (style & BOLD) == BOLD;
        boolean italic = (style & ITALIC) == ITALIC;

        int typefaceMask = Typeface.NORMAL;
        if (bold && italic)
            typefaceMask = Typeface.BOLD_ITALIC;
        else if (bold)
            typefaceMask = Typeface.BOLD;
        else if (italic)
            typefaceMask = Typeface.ITALIC;

        this.name = name;
        this.size = size;
        this.style = style;
        typeface = Typeface.create(name, typefaceMask);
        fontMetrics = new AndroidFontMetrics(this);
    }

    public int canDisplayUpTo(String text) {
        System.out.println("Unimplemented method Font.canDisplayUpTo(" + text + ") called");
        return text.length();
    }

    public FontMetrics getFontMetrics() {
        return fontMetrics;
    }

    public Typeface _getTypeface() {
        return typeface;
    }

    public int getSize() {
        return size;
    }

    public Font deriveFont(int style, float size) {
        return new Font(name, style, (int)size);
    }

    public Font deriveFont(float size) {
        return deriveFont(style, size);
    }

    public Rectangle2D getStringBounds(String text, FontRenderContext fontRenderContext) {
        Rectangle2D ret = new Rectangle2D(0, 0, fontMetrics.stringWidth(text), fontMetrics.getHeight());
        return ret;
    }
}
