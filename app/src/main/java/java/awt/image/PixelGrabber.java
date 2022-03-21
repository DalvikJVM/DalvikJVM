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

import java.awt.*;
import java.util.Hashtable;

public class PixelGrabber implements ImageConsumer {
    Image img;
    int x;
    int y;
    int w;
    int h;
    int[] pix;
    int off;
    int scansize;

    public PixelGrabber(Image img, int x, int y, int w, int h, int[] pix, int off, int scansize) {
        this.img = img;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.pix = pix;
        this.off = off;
        this.scansize = scansize;
    }

    public boolean grabPixels() {
        if (pix == null)
            return false;

        BufferedImage bufferedImage = (BufferedImage)img;
        bufferedImage.getPixels(x, y, w, h, pix, off, scansize);
        return true;
    }

    @Override
    public void setDimensions(int width, int height) {
        System.out.println("Unimplemented method PixelGrabber.setDimensions() called");
    }

    @Override
    public void setProperties(Hashtable<?, ?> props) {
        System.out.println("Unimplemented method PixelGrabber.setProperties() called");
    }

    @Override
    public void setColorModel(ColorModel model) {
        System.out.println("Unimplemented method PixelGrabber.setColorModel() called");
    }

    @Override
    public void setHints(int hintflags) {
        System.out.println("Unimplemented method PixelGrabber.setHints() called");
    }

    @Override
    public void setPixels(int x, int y, int w, int h, ColorModel model, byte[] pixels, int off, int scansize) {
        System.out.println("Unimplemented method PixelGrabber.setPixels(byte) called");
    }

    @Override
    public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize) {
        System.out.println("Unimplemented method PixelGrabber.setPixels(int) called");
    }

    @Override
    public void imageComplete(int status) {
        System.out.println("Unimplemented method PixelGrabber.imageComplete() called");
    }
}
