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

package javax.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Timer implements Runnable {
    private Thread thread;
    private long timer;
    private int delay;
    private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();
    private ActionListener listener;
    private boolean repeats;
    private boolean running;
    private int ticks;

    public Timer(int delay, ActionListener listener) {
        this.delay = delay;
        listeners.add(listener);
        System.out.println("Unimplemented method Timer.<init>(" + delay + ", " + listener + ") called");
    }

    public void addActionListener(ActionListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
        System.out.println("Unimplemented method Timer.addActionListener(" + listener + ") called");
    }

    public void setRepeats(boolean flag) {
        this.repeats = flag;
        System.out.println("Unimplemented method Timer.setRepeats(" + flag + ") called");
    }

    public void start() {
        if (thread == null) {
            running = true;
            thread = new Thread(this);
            thread.start();
        }
        this.timer = System.currentTimeMillis() + this.delay;
        System.out.println("Unimplemented method Timer.start() called");
    }

    public void stop() {
        if (thread != null) {
            running = false;
            // TODO: Check if it waits on thread to stop running
        }
    }

    private void doActionPerformed() {
        ActionEvent e = new ActionEvent(this, ticks++, "Timer");
        for (ActionListener listener : listeners)
            listener.actionPerformed(e);
    }

    public void run() {
        // TODO: This needs to run on swing thread

        while (running) {
            long time = System.currentTimeMillis();
            if (time >= timer) {
                doActionPerformed();
                timer = time + delay;
            }

            if (!repeats)
                break;

            // Sleep thread
            try { Thread.sleep(5); } catch (Exception e) {}
        }

        thread = null;
    }
}
