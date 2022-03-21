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

package javax.swing.table;

import javax.swing.*;

public class JTableHeader extends JComponent {
    TableCellRenderer renderer = new DefaultTableCellRenderer();

    public void setReorderingAllowed(boolean allowReordering) {
        System.out.println("Unimplemented method JTableHeader.setReorderingAllowed(" + allowReordering + ") called");
    }

    public TableCellRenderer getDefaultRenderer() {
        return renderer;
    }

    public void setDefaultRenderer(TableCellRenderer renderer) {
        this.renderer = renderer;
    }
}
