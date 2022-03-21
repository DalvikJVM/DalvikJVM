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
import java.awt.event.InvocationEvent;
import java.awt.event.MouseEvent;

public class SwingUtilities {
    public static boolean isLeftMouseButton(MouseEvent e) {
        return (e.getButton() == MouseEvent.BUTTON1);
    }

    public static boolean isRightMouseButton(MouseEvent e) {
        return e.isMetaDown();
    }

    public static void invokeLater(Runnable runnable) {
        EventQueue.invokeLater(runnable);
    }

    public static void invokeAndWait(Runnable runnable) {
        try {
            EventQueue.invokeAndWait(runnable);
        } catch (Exception e) {
            // TODO: Print stack trace???
            e.printStackTrace();
        }
    }
}
