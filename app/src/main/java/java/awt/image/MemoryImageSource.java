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

import java.awt.*;
import java.util.Hashtable;

public class MemoryImageSource implements ImageProducer {
    int[] pix;
    byte[] pixByte;
    int w;
    int h;
    int off;
    int scan;
    ColorModel cm;

    public MemoryImageSource(int w, int h, ColorModel cm, byte[] pix, int off, int scan) {
        this.cm = cm;
        this.w = w;
        this.h = h;
        this.pixByte = pix;
        this.off = off;
        this.scan = scan;
    }

    public MemoryImageSource(int w, int h, int[] pix, int off, int scan) {
        this.cm = new DirectColorModel(32, 0xFF0000, 0xFF00, 0xFF, 0xFF000000);
        this.w = w;
        this.h = h;
        this.pix = pix;
        this.off = off;
        this.scan = scan;
    }

    @Override
    public void addConsumer(ImageConsumer ic) {
    }

    public Image createImage() {
        BufferedImage image = new BufferedImage(w, h);

        if (cm instanceof IndexColorModel) {
            IndexColorModel indexCM = (IndexColorModel)cm;
            Bitmap bitmap = image._getBitmap();
            for (int x = 0; x < scan; x++) {
                for (int y = 0; y < h; y++) {
                    if (x >= w)
                        continue;

                    int offset = off + (y * scan) + x;
                    int index = pixByte[offset] % indexCM.size;
                    int r = indexCM.r[index] & 0xFF;
                    int g = indexCM.g[index] & 0xFF;
                    int b = indexCM.b[index] & 0xFF;
                    int a = 0xFF;
                    if (indexCM.a != null)
                        a = indexCM.a[index] & 0xFF;
                    int pixel = (a << 24) | (r << 16) | (g << 8) | b;
                    bitmap.setPixel(x, y, pixel);
                }
            }
        } else if (cm instanceof DirectColorModel) {
            DirectColorModel directCM = (DirectColorModel)cm;
            Bitmap bitmap = image._getBitmap();
            for (int x = 0; x < scan; x++) {
                for (int y = 0; y < h; y++) {
                    if (x >= w)
                        continue;

                    if (pix != null) {
                        int offset = off + (y * scan) + x;
                        int r = pix[offset] & directCM.getRedMask();
                        int g = pix[offset] & directCM.getGreenMask();
                        int b = pix[offset] & directCM.getBlueMask();
                        int a = pix[offset] & directCM.getAlphaMask();
                        switch (directCM.getPixelSize()) {
                            case 24:
                                a = 0xFF000000;
                                break;
                        }
                        int pixel = r | g | b | a;
                        bitmap.setPixel(x, y, pixel);
                    }
                }
            }
        } else {
            System.out.println("MemoryImageSource.createImage(): Unsupported Color Model '" + cm + "'");
        }

        return image;
    }
}
