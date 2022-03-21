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

import java.awt.font.FontRenderContext;

public abstract class Graphics2D extends Graphics {
    protected FontRenderContext fontRenderContext = new FontRenderContext();

    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        System.out.println("Unimplemented method Graphics2D.setRenderingHint(" + hintKey + ", " + hintValue + ")");
    }

    public FontRenderContext getFontRenderContext() {
        System.out.println("Unimplemented method Graphics2D.getFontRenderContext()");
        return fontRenderContext;
    }

    public void setComposite(Composite composite) {
        System.out.println("Unimplemented method Graphics2D.setComposite(" + composite + ")");
    }
}
