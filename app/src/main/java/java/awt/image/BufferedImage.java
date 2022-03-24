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

package java.awt.image;

import android.graphics.Bitmap;

import java.android.awt.AndroidGraphics;
import java.awt.*;
import java.awt.image.*;
import java.util.Hashtable;

public class BufferedImage extends Image implements ImageConsumer, RenderedImage {
    AndroidGraphics graphics;
    WritableRaster raster;

    public BufferedImage(ColorModel cm, WritableRaster raster, boolean isRasterPremultiplied, Hashtable<?,?> properties) {
        this(raster.getWidth(), raster.getHeight());

        this.raster = raster;
    }

    public BufferedImage(int w, int h) {
        if (w == 0 || h == 0)
            return;
        graphics = new AndroidGraphics(w, h);
    }

    public BufferedImage(int width, int height, int imageType) {
        this(width, height);
    }

    public Image getScaledInstance(int width, int height, int hints) {
        System.out.println("Unimplemented method BufferedImage.getScaledInstance(" + width + ", " + height + ", " + hints + ") called");
        return this;
    }

    public BufferedImage(Bitmap bitmap) {
        graphics = new AndroidGraphics(bitmap);
    }

    public void _updateBitmap() {
        if (raster != null) {
            DataBufferInt buffer = (DataBufferInt)raster.getDataBuffer();
            int[] pixels = buffer.getData();

            setPixels(0, 0, getWidth(), getHeight(), null, pixels, 0, getWidth());
        }
    }

    public Bitmap _getBitmap() {
        return graphics._getBitmap();
    }

    @Override
    public void flush() {
        System.out.println("Unimplemented method BufferedImage.flush() called");
    }

    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    @Override
    public int getWidth() {
        return _getBitmap().getWidth();
    }

    @Override
    public int getHeight() {
        return _getBitmap().getHeight();
    }

    @Override
    public int getWidth(ImageObserver observer) {
        return _getBitmap().getWidth();
    }

    @Override
    public int getHeight(ImageObserver observer) {
        return _getBitmap().getHeight();
    }

    @Override
    public void setDimensions(int width, int height) {
        graphics.setSize(width, height);
    }

    @Override
    public void setProperties(Hashtable<?, ?> props) {
    }

    @Override
    public void setColorModel(ColorModel model) {
    }

    @Override
    public void setHints(int hintflags) {
    }

    public void getPixels(int x, int y, int w, int h, int[] pix, int off, int scansize) {
        graphics._getPixels(pix, off, scansize, x, y, w, h);
    }

    @Override
    public void setPixels(int x, int y, int w, int h, ColorModel model, byte[] bytePixels, int off, int scansize) {
        int bpp = model.pixel_bits / 8;

        Bitmap bitmap = _getBitmap();

        int[] pixels = new int[(h * scansize) + scansize];

        for (int x2 = 0; x2 < scansize; x2++) {
            for (int y2 = 0; y2 < h; y2++) {
                int index = ((y + y2) * scansize) + (x + x2);
                int byteIndex = ((y + y2) * scansize * bpp) + ((x + x2) * bpp);

                int argb = 0xFF000000;

                if (model instanceof IndexColorModel) {
                    IndexColorModel colorModel = (IndexColorModel)model;
                    int modelIndex = bytePixels[byteIndex];
                    int r = colorModel.r[modelIndex] & 0xFF;
                    int g = colorModel.g[modelIndex] & 0xFF;
                    int b = colorModel.b[modelIndex] & 0xFF;
                    int a = 0xFF;
                    if (colorModel.a != null)
                        a = colorModel.a[modelIndex] & 0xFF;
                    argb = (a << 24) | (r << 16) | (g << 8) | b;
                } else {
                    System.out.println("[FIXME] Unimplemented BufferedImage.setPixels color model!");
                }

                pixels[index] = argb;
            }
        }

        graphics._setPixels(pixels, off, scansize, 0, 0, w, h);
    }

    @Override
    public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize) {
        Bitmap bitmap = _getBitmap();
        for (int i = 0; i < scansize; i++) {
            for (int j = 0; j < h; j++) {
                if (i >= w)
                    continue;

                int offset = off + (j * w) + i;
                pixels[offset] = 0xFF000000 | (pixels[offset] & 0xFFFFFF);
            }
        }

        if (x + w > bitmap.getWidth())
            w = bitmap.getWidth() - x;
        if (y + h > bitmap.getHeight())
            h = bitmap.getHeight() - y;

        graphics._setPixels(pixels, off, scansize, x, y, w, h);
        System.out.println("Unimplemented method BufferedImage.setPixels(" + x + ", " + y + ", " + w + ", " + h + ")");
    }

    @Override
    public void imageComplete(int status) {
    }
}
