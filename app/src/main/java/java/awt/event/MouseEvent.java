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

package java.awt.event;

import java.awt.Point;

public class MouseEvent extends InputEvent {
    private int x;
    private int y;
    private int clickCount;
    private boolean popupTrigger;
    private int button;

    public static final int BUTTON1 = 1;
    public static final int BUTTON2 = 2;
    public static final int BUTTON3 = 3;

    public static final int MOUSE_PRESSED = 501;
    public static final int MOUSE_RELEASED = 502;
    public static final int MOUSE_MOVED = 503;
    public static final int MOUSE_ENTERED = 504;
    public static final int MOUSE_EXITED = 505;
    public static final int MOUSE_DRAGGED = 506;
    public static final int MOUSE_WHEEL = 507;

    public MouseEvent(Object source, int id, long when, int modifiers, int x, int y, int clickCount, boolean popupTrigger, int button) {
        super(source, id);
        this.when = when;
        this.modifiers = modifiers;
        this.x = x;
        this.y = y;
        this.clickCount = clickCount;
        this.popupTrigger = popupTrigger;
        this.button = button;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getButton() {
        return button;
    }

    public long getWhen() {
        return when;
    }

    public void translatePoint(int x, int y) {
        this.x += x;
        this.y += y;
    }

    public Point getPoint() {
        return new Point(x, y);
    }

    private String _idString() {
        switch (id) {
            case MOUSE_PRESSED:
                return "MOUSE_PRESSED";
            case MOUSE_RELEASED:
                return "MOUSE_RELEASED";
            case MOUSE_MOVED:
                return "MOUSE_MOVED";
            case MOUSE_DRAGGED:
                return "MOUSE_DRAGGED";
            case MOUSE_ENTERED:
                return "MOUSE_ENTERED";
            case MOUSE_EXITED:
                return "MOUSE_EXITED";
            case MOUSE_WHEEL:
                return "MOUSE_WHEEL";
        }
        return Integer.toString(id);
    }

    @Override
    public String toString() {
        return "java.awt.event.MouseEvent[" + _idString() + ", (" + x + "," + y + "),button=" + button + ",modifiers=" + _modifierString();
    }
}
