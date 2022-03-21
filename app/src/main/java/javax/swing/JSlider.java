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

import javax.swing.event.ChangeListener;
import java.util.Dictionary;

public class JSlider extends JComponent {
    public void addChangeListener(ChangeListener l) {
        System.out.println("Unimplemented method JSlider.addChangeListener(" + l + ") called");
    }

    public void setOrientation(int orientation) {
        System.out.println("Unimplemented method JSlider.setOrientation(" + orientation + ") called");
    }

    public void setMinimum(int minimum) {
        System.out.println("Unimplemented method JSlider.setMinimum(" + minimum + ") called");
    }

    public void setMaximum(int maximum) {
        System.out.println("Unimplemented method JSlider.setMaximum(" + maximum + ") called");
    }

    public void setMinorTickSpacing(int n) {
        System.out.println("Unimplemented method JSlider.setMinorTickSpacing(" + n + ") called");
    }

    public void setMajorTickSpacing(int n) {
        System.out.println("Unimplemented method JSlider.setMajorTickSpacing(" + n + ") called");
    }

    public void setPaintTicks(boolean paintTicks) {
        System.out.println("Unimplemented method JSlider.setPaintTicks(" + paintTicks + ") called");
    }

    public void setPaintLabels(boolean paintLabels) {
        System.out.println("Unimplemented method JSlider.setPaintLabels(" + paintLabels + ") called");
    }

    public void setSnapToTicks(boolean snapTicks) {
        System.out.println("Unimplemented method JSlider.setSnapToTicks(" + snapTicks + ") called");
    }

    public void setLabelTable(Dictionary dictionary) {
        System.out.println("Unimplemented method JSlider.setLabelTable(" + dictionary + ") called");
    }
}
