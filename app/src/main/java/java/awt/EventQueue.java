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

import java.awt.event.ActionEvent;
import java.awt.event.InvocationEvent;
import java.util.ArrayList;
import java.util.List;

public class EventQueue {
    List<AWTEvent> events = new ArrayList<AWTEvent>();
    EventDispatchThread dispatchThread;

    public EventQueue() {
        dispatchThread = new EventDispatchThread(this);
    }

    protected void dispatchEvent(AWTEvent event) {
        Object source = event.getSource();
        if (source instanceof Component) {
            Component component = (Component)source;
            component.dispatchEvent(event);
        }

        if (event instanceof InvocationEvent) {
            InvocationEvent invocationEvent = (InvocationEvent)event;
            invocationEvent.dispatch();
        }
    }

    public AWTEvent getNextEvent() {
        AWTEvent ret;
        synchronized (this) {
            if (events.size() == 0)
                return null;

            ret = events.get(0);

            // TODO: Implement the "when" field

            events.remove(0);
        }
        return ret;
    }

    public static boolean isDispatchThread() {
        return (Thread.currentThread() == Toolkit.getDefaultToolkit().getSystemEventQueue().dispatchThread.thread);
    }

    public AWTEvent peekEvent() {
        return peekEvent(0);
    }

    public AWTEvent peekEvent(int id) {
        AWTEvent ret;
        synchronized (this) {
            if (events.size() == 0)
                return null;
            ret = events.get(id);
        }
        return ret;
    }

    public void postEvent(AWTEvent theEvent) {
        synchronized (this) {
            if (theEvent instanceof ActionEvent) {
                ActionEvent actionEvent = (ActionEvent)theEvent;
                if (actionEvent.getActionCommand().equals("dummy"))
                    return;
            }

            events.add(theEvent);
        }
    }

    public static void invokeLater(Runnable runnable) {
        EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        InvocationEvent event = new InvocationEvent(eventQueue, runnable, null, true);
        eventQueue.postEvent(event);
    }

    public static void invokeAndWait(Runnable runnable) throws InterruptedException {
        if (isDispatchThread())
            throw new Error("Can't call invokeAndWait from event dispatch thread");

        EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        Object notifyObject = new Object();
        InvocationEvent event = new InvocationEvent(eventQueue, runnable, notifyObject, true);

        synchronized (notifyObject) {
            eventQueue.postEvent(event);
            notifyObject.wait();
        }
    }
}
