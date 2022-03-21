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

import javax.swing.plaf.ProgressBarUI;
import java.awt.*;

public class JProgressBar extends JComponent {
    int max = 100;
    int value = 0;
    String text = "";

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Color prevColor = g.getColor();

        float ratio = 1.0f;
        if (max != 0)
            ratio = (float)value / max;

        System.out.println("RATIO: " + ratio + " (" + value + ":" + max + ")");
        System.out.println("SIZE: "  + getWidth() + "x" + getHeight());

        g.setColor(foregroundColor);
        g.fillRect(0, 0, (int)(getWidth() * ratio), getHeight());
        g.setColor(Color.white);
        int strWidth = g.getFont().getFontMetrics().stringWidth(text);
        int strHeight = g.getFont().getFontMetrics().stringWidth(text);
        g.drawString(text, (getWidth() / 2) - (strWidth / 2), (getHeight() / 2));

        g.setColor(prevColor);
    }

    public void setUI(ProgressBarUI ui) {
        System.out.println("Unimplemented method JProgressBar.setUI(" + ui + ") called");
    }

    public void setMaximum(int value) {
        this.max = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setBorderPainted(boolean paint) {
        System.out.println("Unimplemented method JProgressBar.setBorderPainted(" + paint + ") called");
    }

    public void setStringPainted(boolean paint) {
        System.out.println("Unimplemented method JProgressBar.setStringPainted(" + paint + ") called");
    }

    public void setString(String text) {
        this.text = text;
    }
}
