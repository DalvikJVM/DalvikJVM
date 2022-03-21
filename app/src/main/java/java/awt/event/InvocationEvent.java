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

import java.awt.*;

public class InvocationEvent extends AWTEvent {
    Runnable runnable;
    boolean dispatched;
    Object notifier;
    boolean catchThrowables;
    Exception exception;

    public InvocationEvent(Object source, Runnable runnable, Object notifier, boolean catchThrowables) {
        super(source);
        this.runnable = runnable;
        this.notifier = notifier;
        this.catchThrowables = catchThrowables;
    }

    public void dispatch() {
        if (dispatched)
            return;

        dispatched = true;
        if (catchThrowables) {
            try {
                runnable.run();
            } catch (Exception e) {
                exception = e;

                // TODO: Do we actually stacktrace?
                e.printStackTrace();
            }
        } else {
            runnable.run();
        }

        if (notifier != null) {
            synchronized (notifier) {
                notifier.notify();
            }
        }
    }
}
