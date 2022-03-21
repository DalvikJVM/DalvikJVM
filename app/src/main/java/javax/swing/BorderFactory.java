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

package javax.swing;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class BorderFactory {
    public static Border createEmptyBorder() {
        return createEmptyBorder(0, 0, 0, 0);
    }

    public static Border createEmptyBorder(int top, int left, int bottom, int right) {
        System.out.println("Unimplemented method BorderFactory.createEmptyBorder(" + top + ", " + left + ", " + bottom + ", " + right + ") called");
        return new EmptyBorder(top, left, bottom, right);
    }

    public static MatteBorder createMatteBorder(int top, int left, int bottom, int right, Color color) {
        System.out.println("Unimplemented method BorderFactory.createMatteBorder(" + top + ", " + left + ", " + bottom + ", " + right + ", " + color + ") called");
        return new MatteBorder(top, left, bottom, right);
    }

    public static CompoundBorder createCompoundBorder(Border outsideBorder, Border insideBorder) {
        System.out.println("Unimplemented method BorderFactory.createCompoundBorder(" + outsideBorder + ", " + insideBorder + ") called");
        return new CompoundBorder(outsideBorder, insideBorder);
    }
}
