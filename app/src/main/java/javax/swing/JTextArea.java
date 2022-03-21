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

import javax.swing.text.JTextComponent;
import java.awt.*;

public class JTextArea extends JTextComponent {
    public JTextArea(String text) {
        System.out.println("Unimplemented method JTextArea.<init>(" + text + ") called");
    }

    public void setLineWrap(boolean wrap) {
        System.out.println("Unimplemented method JTextArea.setLineWrap(" + wrap + ") called");
    }

    public void setWrapStyleWord(boolean word) {
        System.out.println("Unimplemented method JTextArea.setWrapStyleWord(" + word + ") called");
    }

    public void setDisabledTextColor(Color color) {
        System.out.println("Unimplemented method JTextArea.setDisabledTextColor(" + color + ") called");
    }
}
