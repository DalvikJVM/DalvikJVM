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

public abstract class FontMetrics {
    public int stringWidth(String str) {
        System.out.println("Unimplemented method FontMetrics.stringWidth(" + str + ") called");
        return str.length() * 4;
    }

    public int charWidth(char c) {
        System.out.println("Unimplemented method FontMetrics.charWidth(" + c + ") called");
        return 4;
    }

    public int getAscent() {
        System.out.println("Unimplemented method FontMetrics.getAscent() called");
        return 0;
    }

    public int getMaxAscent() {
        System.out.println("Unimplemented method FontMetrics.getMaxAscent() called");
        return 0;
    }

    public int getMaxDescent() {
        System.out.println("Unimplemented method FontMetrics.getMaxDescent() called");
        return 0;
    }

    public int getHeight() {
        System.out.println("Unimplemented method FontMetrics.getHeight() called");
        return 16;
    }
}
