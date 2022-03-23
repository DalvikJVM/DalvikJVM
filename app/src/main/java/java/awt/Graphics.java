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

import java.awt.image.ImageObserver;

public abstract class Graphics {
    protected Color currentColor;
    protected Font currentFont;
    protected Rectangle clipBounds;
    protected Shape clipShape;
    protected Point translatePoint = new Point(0, 0);

    public Graphics create() {
        System.out.println("Unimplemented method Graphics.create() called");
        return this;
    }

    public Point _getTranslate() {
        return translatePoint;
    }

    public void translate(int x, int y) {
        translatePoint.x = x;
        translatePoint.y = y;
    }

    public FontMetrics getFontMetrics() {
        if (currentFont == null)
            return null;
        return currentFont.getFontMetrics();
    }

    public FontMetrics getFontMetrics(Font f) {
        return f.getFontMetrics();
    }

    public Rectangle getClipBounds() {
        return new Rectangle(0, 0, 0, 0);
    }

    public void setColor(Color color) {
        currentColor = color;
    }

    public Color getColor() {
        return currentColor;
    }

    public Font getFont() {
        return currentFont;
    }

    public void setFont(Font font) {
        currentFont = font;
    }

    public Shape getClip() {
        return clipShape;
    }

    public void clipRect(int x, int y, int width, int height) {
        clipBounds = new Rectangle(x, y, width, height);
    }

    public void setClip(Shape clip) {
        clipShape = clip;
    }

    public void fillOval(int x, int y, int width, int height) {
        System.out.println("Unimplemented method Graphics.fillOval(" + x + ", " + y + ", " + width + ", " + height + ") called");
    }

    public void drawOval(int x, int y, int width, int height) {
        System.out.println("Unimplemented method Graphics.drawOval(" + x + ", " + y + ", " + width + ", " + height + ") called");
    }

    public void fillRect(int x, int y, int width, int height) {
        System.out.println("Unimplemented method Graphics.fillRect(" + x + ", " + y + ", " + width + ", " + height + ") called");
    }

    public void drawRect(int x, int y, int width, int height) {
        System.out.println("Unimplemented method Graphics.drawRect(" + x + ", " + y + ", " + width + ", " + height + ") called");
    }

    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        System.out.println("Unimplemented method Graphics.drawImage(" + img + ", " + x + ", " + y + ", " + observer + ") called");
        return false;
    }

    public boolean drawImage(Image img, int x, int y, int w, int h, ImageObserver observer) {
        System.out.println("Unimplemented method Graphics.drawImage(" + img + ", " + x + ", " + y + ", " + w + ", " + h + ", " + observer + ") called");
        return false;
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        System.out.println("Unimplemented method Graphics.fillPolygon(" + xPoints + ", " + yPoints + ", " + nPoints + ") called");
    }

    public void drawLine(int x, int y, int x2, int y2) {
        System.out.println("Unimplemented method Graphics.drawLine(" + x + ", " + y + ", " + x2 + ", " + y2 + ") called");
    }

    public void drawString(String str, int x, int y) {
        System.out.println("Unimplemented method Graphics.drawString(" + str + ", " + x + ", " + y + ") called");
    }

    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        System.out.println("Unimplemented method Graphics.copyArea(" + x + ", " + y + ", " + width + ", " + height + ", " + dx + ", " + dy + ") called");
    }

    public void dispose() {
        System.out.println("Unimplemented method Graphics.dispose() called");
    }
}
