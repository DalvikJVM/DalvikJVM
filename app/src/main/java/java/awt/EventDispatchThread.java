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

public class EventDispatchThread implements Runnable {
    public static EventDispatchThread instance;
    private EventQueue queue;
    public Thread thread;

    public EventDispatchThread(EventQueue queue) {
        this.queue = queue;
        this.thread = new Thread(this);
        this.thread.start();

        instance = this;
    }

    @Override
    public void run() {
        while (true) {
            AWTEvent event;
            while ((event = queue.getNextEvent()) != null) {
                long now = System.currentTimeMillis();
                queue.dispatchEvent(event);
            }

            try { Thread.sleep(10); } catch (Exception e) {}
        }
    }
}
