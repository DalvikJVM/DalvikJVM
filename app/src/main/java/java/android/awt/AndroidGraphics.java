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

import android.graphics.*;
import android.graphics.Canvas;

import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class AndroidGraphics extends Graphics2D {
    protected Bitmap backbuffer;
    protected Canvas canvas;
    protected Paint paint;
    protected AlphaComposite alphaComposite = AlphaComposite.SrcOver;
    protected Object renderLock = new Object();
    protected Component parent;

    public AndroidGraphics(Component parent) {
        this(parent.getWidth(), parent.getHeight());
        this.parent = parent;
    }

    @Override
    public Graphics create() {
        return new AndroidGraphics(this);
    }

    public void _present(Canvas canvas, Paint paint, Rect srcRect, Rect drawDstRect)
    {
        synchronized (renderLock) {
            canvas.drawBitmap(_getBitmap(), srcRect, drawDstRect, paint);
        }
    }

    public void _setPixels(int[] pixels, int offset, int stride, int x, int y, int w, int h) {
        synchronized (renderLock) {
            backbuffer.setPixels(pixels, offset, stride, x, y, w, h);
        }
    }

    public void _getPixels(int[] pixels, int offset, int stride, int x, int y, int w, int h) {
        synchronized (renderLock) {
            backbuffer.getPixels(pixels, offset, stride, x, y, w, h);
        }
    }

    public AndroidGraphics(AndroidGraphics graphics) {
        backbuffer = graphics._getBitmap();
        canvas = graphics._getCanvas();
        paint = graphics._getPaint();

        // TODO: Does it copy translation?
    }

    public AndroidGraphics(Bitmap bitmap) {
        backbuffer = bitmap;
        canvas = new Canvas(backbuffer);
        paint = new Paint(Paint.HINTING_ON);
        paint.setLinearText(true);

        __init();
    }

    public AndroidGraphics(int w, int h) {
        setSize(w, h);

        __init();
    }

    private void __init() {
        setColor(Color.WHITE);
        setFont(Font.defaultFont);
    }

    public Paint _getPaint() {
        return paint;
    }

    public Canvas _getCanvas() {
        return canvas;
    }

    public Bitmap _getBitmap() {
        return backbuffer;
    }

    public void setSize(int width, int height) {
        synchronized (renderLock) {
            backbuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(backbuffer);
            paint = new Paint(Paint.HINTING_ON);
            paint.setLinearText(true);
        }

        setColor(currentColor);
        setFont(currentFont);
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);

        if (color == null)
            return;

        synchronized (renderLock) {
            paint.setColor(currentColor.getRGB());
        }
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        synchronized (renderLock) {
            x += translatePoint.x;
            y += translatePoint.y;

            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha((int) (alphaComposite.getAlpha() * 255.0f));
            canvas.drawRect(new Rect(x, y, x + width, y + height), paint);
        }
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        synchronized (renderLock) {
            x += translatePoint.x;
            y += translatePoint.y;

            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha((int) (alphaComposite.getAlpha() * 255.0f));
            canvas.drawOval(new RectF(x, y, x + width, y + height), paint);
        }
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        synchronized (renderLock) {
            x += translatePoint.x;
            y += translatePoint.y;

            paint.setStyle(Paint.Style.STROKE);
            paint.setAlpha((int) (alphaComposite.getAlpha() * 255.0f));
            canvas.drawOval(new RectF(x, y, x + width, y + height), paint);
        }
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        synchronized (renderLock) {
            x += translatePoint.x;
            y += translatePoint.y;

            paint.setStyle(Paint.Style.STROKE);
            paint.setAlpha((int) (alphaComposite.getAlpha() * 255.0f));
            canvas.drawRect(new Rect(x, y, x + width, y + height), paint);
        }
    }

    @Override
    public void drawLine(int x, int y, int x2, int y2) {
        synchronized (renderLock) {
            x += translatePoint.x;
            y += translatePoint.y;

            paint.setStyle(Paint.Style.STROKE);
            paint.setAlpha((int) (alphaComposite.getAlpha() * 255.0f));
            canvas.drawLine(x, y, x2, y2, paint);
        }
    }

    @Override
    public void drawString(String str, int x, int y) {
        synchronized (renderLock) {
            x += translatePoint.x;
            y += translatePoint.y;
            System.out.println("DRAW_STRING: " + str + ": " + x + ", " + y + ", " + currentFont._getTypeface() + ", " + currentFont.getSize() + ", " + currentColor.getRGB());
            paint.setTypeface(currentFont._getTypeface());
            paint.setTextSize(currentFont.getSize());
            paint.setAlpha((int) (alphaComposite.getAlpha() * 255.0f));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(str, x, y, paint);
        }
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        if (img == null)
            return false;
        return drawImage(img, x, y, img.getWidth(), img.getHeight(), observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int w, int h, ImageObserver observer) {
        if (img == null)
            return false;

        Rect destRect;

        synchronized (renderLock) {
            x += translatePoint.x;
            y += translatePoint.y;

            BufferedImage image = (BufferedImage) img;
            Rect imageRect = new Rect(0, 0, img.getWidth(), img.getHeight());
            destRect = new Rect(x, y, x + w, y + h);
            image._updateBitmap();
            paint.setAlpha((int) (alphaComposite.getAlpha() * 255.0f));
            canvas.drawBitmap(image._getBitmap(), imageRect,
                    destRect, paint);
        }

        if (observer != null) {
            observer.imageUpdate(img, ImageObserver.ALLBITS, x, y, destRect.width(), destRect.height());
        }

        return true;
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        int destX = x + dx;
        int destY = y + dy;

        synchronized (renderLock) {
            x += translatePoint.x;
            y += translatePoint.y;

            System.out.println("Unimplemented method Graphics.copyArea(" + x + ", " + y + ", " + width + ", " + height + ", " + dx + ", " + dy + ") called");
            System.out.println("x" + destX + ",y" + destY);

            if (y + height > backbuffer.getHeight() || x + width > backbuffer.getWidth())
                return;

            Bitmap croppedBitmap = Bitmap.createBitmap(backbuffer, x, y, width, height);
            Rect src = new Rect(0, 0, croppedBitmap.getWidth(), croppedBitmap.getHeight());
            Rect dst = new Rect(destX, destY, destX + width, destY + height);

            canvas.drawBitmap(croppedBitmap, src, dst, paint);
        }
    }

    @Override
    public void setComposite(Composite composite) {
        synchronized (renderLock) {
            if (composite instanceof AlphaComposite) {
                alphaComposite = (AlphaComposite) composite;
            } else {
                alphaComposite = AlphaComposite.SrcOver;
            }
        }
    }
}
