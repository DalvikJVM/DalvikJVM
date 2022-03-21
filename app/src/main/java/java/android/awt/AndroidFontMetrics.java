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

package java.android.awt;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.awt.*;

public class AndroidFontMetrics extends FontMetrics {
    Paint paint;
    Font font;
    int maxSize;

    public AndroidFontMetrics(Font font) {
        paint = new Paint();
        paint.setTypeface(font._getTypeface());
        paint.setTextSize(font.getSize());

        String testString = "zxcvbnm,./asdfghjkl;'\\qwertyuiop[]1234567890-=ZXCVBNM<>?ASDFGHJKL:\"|QWERTYUIOP{}!@#$%^&*()_+`~";
        Rect bounds = new Rect();
        paint.getTextBounds(testString, 0, testString.length(), bounds);
        maxSize = bounds.height();
    }

    @Override
    public int stringWidth(String str) {
        return (int)paint.measureText(str);
    }

    @Override
    public int charWidth(char c) {
        String str = Character.toString(c);
        return (int)paint.measureText(str);
    }

    @Override
    public int getHeight() {
        return maxSize;
    }
}
