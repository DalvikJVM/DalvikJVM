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

public abstract class Frame extends Window {
    public void setMaximizedBounds(Rectangle bounds) {
        System.out.println("Unimplemented method Frame.setMaximizedBounds(" + bounds + ") called");
    }

    public void setUndecorated(boolean undecorated) {
        System.out.println("Unimplemented method Frame.setUndecorated(" + undecorated + ") called");
    }

    public void setResizable(boolean resizable) {
        System.out.println("Unimplemented method Frame.setResizable(" + resizable + ") called");
    }

    public void setCursor(Cursor cursor) {
        System.out.println("Unimplemented method Frame.setCursor(" + cursor + ")");
    }
}
