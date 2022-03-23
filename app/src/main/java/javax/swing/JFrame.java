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

public class JFrame extends Frame {
    Container contentPane = null;

    public JFrame() {
    }

    public JFrame(String title) {
        setTitle(title);
    }

    public Container getContentPane() {
        if (contentPane == null) {
            contentPane = new JPanel();
            contentPane.setBackground(Color.WHITE);
            setContentPane(contentPane);
        }
        System.out.println("Unimplemented method JFrame.getContentPane(" + contentPane + ") called");
        return contentPane;
    }

    public void setContentPane(Container contentPane) {
        if (this.contentPane != contentPane)
            remove(this.contentPane);
        add(contentPane);
        this.contentPane = contentPane;
    }

    public void setDefaultCloseOperation(int operation) {
        System.out.println("Unimplemented method JFrame.setDefaultCloseOperation(" + operation + ") called");
    }
}
