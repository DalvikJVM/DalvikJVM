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

public class DirectColorModel extends PackedColorModel {
    private int bits;
    private int rmask;
    private int gmask;
    private int bmask;
    private int amask;

    public DirectColorModel(int bits, int rmask, int gmask, int bmask, int amask) {
        this.bits = bits;
        this.rmask = rmask;
        this.gmask = gmask;
        this.bmask = bmask;
        this.amask = amask;
        System.out.println("Unimplemented method DirectColorModel.<init>(" + bits + ", " + rmask + ", " + gmask + ", " + bmask + ", " + amask + ") called");
    }

    public DirectColorModel(int bits, int rmask, int gmask, int bmask) {
        this(bits, rmask, gmask, bmask, 0xFF000000);
    }

    public int getRedMask() {
        return rmask;
    }

    public int getGreenMask() {
        return gmask;
    }

    public int getBlueMask() {
        return bmask;
    }

    public int getAlphaMask() {
        return amask;
    }
}
