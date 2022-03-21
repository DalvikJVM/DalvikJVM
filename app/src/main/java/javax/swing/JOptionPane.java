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

import java.awt.*;

public class JOptionPane extends JComponent {
    public static final int OK_OPTION = 0;
    public static final int NO_OPTION = 1;

    public static int showConfirmDialog(Component parent, Object message, String title, int optionType, int messageType, Icon icon) {
        System.out.println("Unimplemented method JOptionPane.showConfirmDialog(" + parent + ", " + message + ", " + title + ", " + optionType + ", " + messageType + ", " + icon + ") called");
        return NO_OPTION;
    }

    public static void showMessageDialog(Component parent, Object message, String title, int messageType, Icon icon) {
        System.out.println("Unimplemented method JOptionPane.showMessageDialog(" + parent + ", " + message + ", " + title + ", " + messageType + ", " + icon + ") called");
    }
}
