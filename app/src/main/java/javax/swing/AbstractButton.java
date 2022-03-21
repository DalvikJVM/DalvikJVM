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

import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;

public class AbstractButton extends JComponent {
    public void setMargin(Insets insets) {
        System.out.println("Unimplemented method AbstractButton.setMargin(" + insets + ") called");
    }

    public void setFocusPainted(boolean b) {
        System.out.println("Unimplemented method AbstractButton.setFocusPainted(" + b + ") called");
    }

    public void addActionListener(ActionListener l) {
        System.out.println("Unimplemented method AbstractButton.addActionListener(" + l + ") called");
    }

    public void addChangeListener(ChangeListener l) {
        System.out.println("Unimplemented method AbstractButton.addChangeListener(" + l + ") called");
    }

    public void setEnabled(boolean enabled) {
        System.out.println("Unimplemented method AbstractButton.setEnabled(" + enabled + ") called");
    }

    public void setText(String text) {
        System.out.println("Unimplemented method AbstractButton.setText(" + text + ") called");
    }

    public void setActionCommand(String actionCommand) {
        System.out.println("Unimplemented method AbstractButton.setActionCommand(" + actionCommand + ") called");
    }

    public void setIcon(Icon defaultIcon) {
        System.out.println("Unimplemented method AbstractButton.setIcon(" + defaultIcon + ") called");
    }

    public void setSelectedIcon(Icon selectedIcon) {
        System.out.println("Unimplemented method AbstractButton.setIcon(" + selectedIcon + ") called");
    }

    public void setContentAreaFilled(boolean contentFilled) {
        System.out.println("Unimplemented method AbstractButton.setContentAreaFilled(" + contentFilled + ") called");
    }
}
