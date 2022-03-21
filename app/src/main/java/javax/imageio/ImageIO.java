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

package javax.imageio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageIO {
    public static void setUseCache(boolean enable) {
        System.out.println("Unimplemented method ImageIO.setUseCache(" + enable + ") called");
    }

    public static BufferedImage read(URL url) throws IOException {
        InputStream stream = url.openStream();
        BufferedImage image = read(stream);
        stream.close();
        return image;
    }

    public static BufferedImage read(InputStream input) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        input.close();
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        BufferedImage image = new BufferedImage(bitmap);
        return image;
    }

    public static BufferedImage read(File file) throws IOException {
        return read(new FileInputStream(file));
    }
}
