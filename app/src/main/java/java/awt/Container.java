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

package java.awt;

import java.beans.PropertyChangeListener;

public abstract class Container extends Component {
    protected Dimension minimumSize = new Dimension(1, 1);

    public void add(Component comp, Object constraints) {
        add(comp);
        System.out.println("Unimplemented method Container.add(" + comp + ", " + constraints + ") called");
    }

    public void setFocusCycleRoot(boolean focusCycleRoot) {
        System.out.println("Unimplemented method Container.setFocusCycleRoot(" + focusCycleRoot + ") called");
    }

    public void setLayout(LayoutManager layout) {
        System.out.println("Unimplemented method Container.setLayout(" + layout + ") called");
    }

    public Insets getInsets() {
        System.out.println("Unimplemented method Container.getInsets() called");
        return new Insets(getX(), getY(), getWidth(), getHeight());
    }

    public Dimension getMinimumSize() {
        return minimumSize;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        System.out.println("Unimplemented method Container.addPropertyChangeListener(" + listener + ") called");
    }
}
