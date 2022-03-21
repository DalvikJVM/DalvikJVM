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

import java.awt.Point;

public abstract class Raster {
    protected int width;
    protected int height;
    protected DataBuffer dataBuffer;

    protected Raster(SampleModel sampleModel, DataBuffer dataBuffer, Point origin) {
        this.dataBuffer = dataBuffer;
        this.width = sampleModel.width;
        this.height = sampleModel.height;
    }

    public DataBuffer getDataBuffer() {
        return dataBuffer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public static WritableRaster createWritableRaster(SampleModel sm, DataBuffer db, Point location) {
        return new WritableRaster(sm, db, location);
    }
}
