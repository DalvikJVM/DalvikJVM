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

import javax.swing.table.*;

public class JTable extends JComponent {
    TableModel model;
    JTableHeader tableHeader = new JTableHeader();
    TableColumnModel columnModel = new DefaultTableColumnModel();
    ListSelectionModel listSelectionModel = new DefaultListSelectionModel();
    RowSorter sorter = new DefaultRowSorter();

    public JTable(TableModel model) {
        this.model = model;
        System.out.println("Unimplemented method JTable.<init>(" + model + ") called");
    }

    public void setRowSorter(RowSorter sorter) {
        this.sorter = sorter;
    }

    public ListSelectionModel getSelectionModel() {
        return listSelectionModel;
    }

    public void removeColumn(TableColumn column) {
        System.out.println("Unimplemented method JTable.removeColumn(" + column + ") called");
    }

    public void setFillsViewportHeight(boolean fills) {
        System.out.println("Unimplemented method JTable.setFillsViewportHeight(" + fills + ") called");
    }

    public void setAutoCreateRowSorter(boolean autoCreate) {
        System.out.println("Unimplemented method JTable.setAutoCreateRowSorter(" + autoCreate + ") called");
    }

    public void setDragEnabled(boolean drag) {
        System.out.println("Unimplemented method JTable.setDragEnabled(" + drag + ") called");
    }

    public TableModel getModel() {
        return model;
    }

    public JTableHeader getTableHeader() {
        return tableHeader;
    }

    public TableColumnModel getColumnModel() {
        return columnModel;
    }

    public void setDropMode(DropMode mode) {
        System.out.println("Unimplemented method JTable.setDropMode(" + mode + ") called");
    }

    public void setTransferHandler(TransferHandler handler) {
        System.out.println("Unimplemented method JTable.setTransferHandler(" + handler + ") called");
    }

    public int getSelectedRowCount() {
        System.out.println("Unimplemented method JTable.getSelectedRowCount() called");
        return 0;
    }
}
