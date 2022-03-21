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

public class KeyEvent extends InputEvent {
    private int keyCode;
    private char keyChar;

    public static final int KEY_PRESSED = 401;
    public static final int KEY_RELEASED = 402;
    public static final int KEY_TYPED = 400;

    public KeyEvent(Object source, int id, long when, int modifiers, int keyCode, char keyChar) {
        super(source, id);
        this.when = when;
        this.modifiers = modifiers;
        this.keyCode = keyCode;
        this.keyChar = keyChar;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public char getKeyChar() {
        return keyChar;
    }

    public static String getKeyText(int keyCode) {
        System.out.println("Unimplemented method KeyEvent.getKeyText(" + keyCode + ") called");
        return "";
    }

    private String _idString() {
        switch (id) {
            case KEY_PRESSED:
                return "KEY_PRESSED";
            case KEY_TYPED:
                return "KEY_TYPED";
            case KEY_RELEASED:
                return "KEY_RELEASED";
        }
        return Integer.toString(id);
    }

    @Override
    public String toString() {
        return "java.awt.event.KeyEvent[" + _idString() + ",keyCode=" + keyCode + ",keyChar='" + keyChar + "',modifiers=" + _modifierString();
    }
}
