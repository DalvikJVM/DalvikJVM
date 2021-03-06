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

public class Event {
    public long when;
    public int x;
    public int y;
    public Object target;
    public int id;
    public int key;
    public Object arg;
    public char _keyChar;
    public int _awtKey;
    public int modifiers;
    public int _awtAmount;

    public static final int SHIFT_MASK = 1;
    public static final int CTRL_MASK = 2;
    public static final int META_MASK = 4;
    public static final int ALT_MASK = 8;

    public static final int GOT_FOCUS = 1004;
    public static final int LOST_FOCUS = 1005;

    public static final int MOUSE_DOWN = 501;
    public static final int MOUSE_UP = 502;
    public static final int MOUSE_MOVE = 503;
    public static final int MOUSE_ENTER = 504;
    public static final int MOUSE_EXIT = 505;
    public static final int MOUSE_DRAG = 506;

    public static final int _MOUSE_SCROLL_UP = 520;
    public static final int _MOUSE_SCROLL_DOWN = 521;

    public static final int KEY_PRESS = 401;
    public static final int KEY_RELEASE = 402;

    public Event(Object target, long when, int id, int x, int y, int key, int modifiers, Object arg)
    {
        this.target = target;
        this.id = id;
        this.arg = arg;
        this.when = when;
        this.modifiers = modifiers;
        this.key = key;
        this.x = x;
        this.y = y;

        _keyChar = '\0';
        _awtKey = -1;
    }

    public Event(Object target, int id, Object arg) {
        this(target, System.currentTimeMillis(), id, -1, -1, -1, 0, arg);
    }

    public int _getKey() {
        if (_awtKey != -1)
            return _awtKey;
        return key;
    }

    public boolean shiftDown() {
        return ((modifiers & SHIFT_MASK) == SHIFT_MASK);
    }

    public boolean metaDown() {
        return ((modifiers & META_MASK) == META_MASK);
    }

    public boolean controlDown() {
        return ((modifiers & CTRL_MASK) == CTRL_MASK);
    }

    // "Returns a string representing the state of this Event. This method is intended to be used only for debugging purposes,
    // and the content and format of the returned string may vary between implementations. The returned string may be empty
    // but may not be null."
    // https://docs.oracle.com/javase/7/docs/api/java/awt/Event.html#paramString()
    protected String paramString() {
        return "";
    }
}
