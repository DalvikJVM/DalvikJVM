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

public class JScrollPane extends JComponent {
    private JScrollBar verticalScrollBar;
    private JScrollBar horizontalScrollBar;

    public JScrollPane() {
        verticalScrollBar = new JScrollBar();
        horizontalScrollBar = new JScrollBar();
    }

    public JScrollPane(Component component) {
        this();
        System.out.println("Unimplemented method JScrollPane.<init>(" + component + ") called");
    }

    public void setViewportView(Component component) {
        System.out.println("Unimplemented method JScrollPane.setViewportView(" + component + ") called");
    }

    public JScrollBar getVerticalScrollBar() {
        return verticalScrollBar;
    }

    public JScrollBar getHorizontalScrollBar() {
        return horizontalScrollBar;
    }
}
