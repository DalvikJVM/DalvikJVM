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

import java.util.Vector;

public class DefaultTableModel extends AbstractTableModel {
    Vector data = new Vector();

    public void addRow(Vector rowData) {
        System.out.println("Unimplemented method DefaultTableModel.addRow(" + rowData + ") called");
    }

    public Vector getDataVector() {
        System.out.println("Unimplemented method DefaultTableModel.getDataVector() called");
        return data;
    }

    public int getRowCount() {
        System.out.println("Unimplemented method DefaultTableModel.getRowCount() called");
        return 0;
    }
}
