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

import javax.swing.border.Border;
import java.awt.*;

public abstract class JComponent extends Container {
    InputMap inputMap = new InputMap();
    ActionMap actionMap = new ActionMap();

    public InputMap getInputMap() {
        System.out.println("Unimplemented method JComponent.getInputMap() called");
        return inputMap;
    }

    public ActionMap getActionMap() {
        System.out.println("Unimplemented method JComponent.getActionMap() called");
        return actionMap;
    }

    public InputMap getInputMap(int condition) {
        System.out.println("Unimplemented method JComponent.getInputMap(" + condition + ") called");
        return inputMap;
    }

    public void setFont(Font font) {
        System.out.println("Unimplemented method JComponent.setFont(" + font + ") called");
    }

    public void setEnabled(boolean enabled) {
        System.out.println("Unimplemented method JComponent.setEnabled(" + enabled + ") called");
    }

    public void setBorder(Border border) {
        System.out.println("Unimplemented method JComponent.setBorder(" + border + ") called");
    }

    public void setAlignmentX(float alignmentX) {
        System.out.println("Unimplemented method JComponent.setAlignmentX(" + alignmentX + ") called");
    }

    public void setAlignmentY(float alignmentY) {
        System.out.println("Unimplemented method JComponent.setAlignmentY(" + alignmentY + ") called");
    }

    public void setToolTipText(String text) {
        System.out.println("Unimplemented method JComponent.setToolTipText(" + text + ") called");
    }

    public void revalidate() {
        System.out.println("Unimplemented method JComponent.revalidate() called");
    }

    public void setMinimumSize(Dimension minimumSize) {
        System.out.println("Unimplemented method JComponent.setMinimumSize(" + minimumSize.width + ", " + minimumSize.height + ") called");
    }

    public void setOpaque(boolean isOpaque) {
        System.out.println("Unimplemented method JComponent.setOpaque(" + isOpaque + ") called");
    }
}
