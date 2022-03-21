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

import com.dalvikjvm.MainActivity;

import java.awt.event.WindowListener;

public abstract class Window extends Container {
    public enum Type {
        NORMAL,
        POPUP,
        UTILITY,
    }

    @Override
    public void setVisible(boolean show) {
        super.setVisible(show);
        if (show)
            MainActivity.setTarget(this);
    }

    public void toFront() {
        System.out.println("Unimplemented method Window.toFront() called");
    }

    public void setLocationRelativeTo(Component c) {
        System.out.println("Unimplemented method Window.setLocationRelativeTo(" + c + ") called");
    }

    public void setIconImage(Image image) {
        System.out.println("Unimplemented method Window.setIconImage(" + image + ") called");
    }

    public void addWindowListener(WindowListener listener) {
        System.out.println("Unimplemented method Window.addWindowListener(" + listener + ")");
    }

    public void setTitle(String title) {
        System.out.println("Unimplemented method Window.setTitle(" + title + ")");
    }

    public void dispose() {
        System.out.println("Unimplemented method Window.dispose()");
    }

    public void pack() {
        Dimension newSize = new Dimension(preferredSize.width, preferredSize.height);
        for (Component child : children) {
            if (child.getPreferredSize().width > newSize.width)
                newSize.width = child.getPreferredSize().width;
            if (child.getPreferredSize().height > newSize.height)
                newSize.height = child.getPreferredSize().height;
        }

        if (newSize.width < minimumSize.width)
            newSize.width = minimumSize.width;
        if (newSize.height < minimumSize.height)
            newSize.height = minimumSize.height;

        setSize(newSize.width, newSize.height);
        System.out.println("Unimplemented method Window.pack(" + newSize.width + ", " + newSize.height + ")");
    }

    public void setAutoRequestFocus(boolean autoRequestFocus) {
        System.out.println("Unimplemented method Window.setAutoRequestFocus(" + autoRequestFocus + ")");
    }

    public void setType(Type type) {
        System.out.println("Unimplemented method Window.setType(" + type + ")");
    }

    public void setAlwaysOnTop(boolean onTop) {
        System.out.println("Unimplemented method Window.setAlwaysOnTop(" + onTop + ")");
    }

    public void setMinimumSize(Dimension minimumSize) {
        this.minimumSize = minimumSize;
    }
}
