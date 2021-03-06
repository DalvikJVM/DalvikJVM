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

import com.dalvikjvm.DalvikJVM;

import java.android.awt.AndroidToolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.AWTEventListener;

public abstract class Toolkit {
    private static Toolkit instance = new AndroidToolkit();
    private EventQueue eventQueue = new EventQueue();

    public void addAWTEventListener(AWTEventListener listener, long eventMask) {
        DalvikJVM.addAWTEventListener(listener);
    }

    public Clipboard getSystemClipboard() {
        System.out.println("Unimplemented method Toolkit.getSystemClipboard() called");
        return new Clipboard();
    }

    public static Toolkit getDefaultToolkit() {
        return instance;
    }

    public EventQueue getSystemEventQueue() {
        return eventQueue;
    }
}
