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

public abstract class InputEvent extends ComponentEvent {
    protected int modifiers;
    protected long when;
    private boolean consumed;

    public static final int ALT_GRAPH_MASK = 32;
    public static final int ALT_MASK = 8;
    public static final int SHIFT_MASK = 1;
    public static final int CTRL_MASK = 2;
    public static final int META_MASK = 3;
    public static final int BUTTON1_DOWN_MASK = 1024;
    public static final int BUTTON2_DOWN_MASK = 2048;
    public static final int BUTTON3_DOWN_MASK = 4096;
    public static final int ALT_GRAPH_DOWN_MASK = 8192;
    public static final int ALT_DOWN_MASK = 512;
    public static final int SHIFT_DOWN_MASK = 64;
    public static final int CTRL_DOWN_MASK = 128;
    public static final int META_DOWN_MASK = 256;

    public static final int BUTTON1_MASK = 16;
    public static final int BUTTON2_MASK = 8;
    public static final int BUTTON3_MASK = 4;

    public InputEvent(Object source, int id) {
        super(source, id);
    }

    public void consume() {
        consumed = true;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public long getWhen() {
        return when;
    }

    public int getModifiers() {
        return modifiers;
    }

    public boolean isAltDown() {
        return ((modifiers & ALT_DOWN_MASK) == ALT_DOWN_MASK);
    }

    public boolean isAltGraphDown() {
        return ((modifiers & ALT_GRAPH_DOWN_MASK) == ALT_GRAPH_DOWN_MASK);
    }

    public boolean isControlDown() {
        return ((modifiers & CTRL_DOWN_MASK) == CTRL_DOWN_MASK);
    }

    public boolean isShiftDown() {
        return ((modifiers & SHIFT_DOWN_MASK) == SHIFT_DOWN_MASK);
    }

    public boolean isMetaDown() {
        return ((modifiers & META_DOWN_MASK) == META_DOWN_MASK);
    }

    public boolean isPopupTrigger() {
        return false;
    }

    protected String _modifierString() {
        String ret = "";
        if (isShiftDown())
            ret += "Shift+";
        if (isAltDown())
            ret += "Alt+";
        if (isAltGraphDown())
            ret += "AltGr+";
        if (isControlDown())
            ret += "Ctrl+";
        if (isMetaDown())
            ret += "Meta+";

        if (ret.length() == 0)
            return "";
        else
            return ret.substring(0, ret.length() - 1);
    }
}
