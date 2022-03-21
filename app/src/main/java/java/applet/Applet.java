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

package java.applet;

import java.android.awt.AndroidAppletContext;
import java.android.awt.AndroidGraphics;
import java.awt.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;

public abstract class Applet extends Panel {
    AppletContext context = new AndroidAppletContext();
    AppletStub appletStub = null;
    HashMap<String, String> parameters = new HashMap<String, String>();
    URL codebase;

    public void init() {
    }

    public void start() {
    }

    public void setStub(AppletStub stub) {
        this.appletStub = stub;
    }

    public AppletContext getAppletContext() {
        return context;
    }

    public URL getDocumentBase() {
        if (appletStub != null)
            return appletStub.getDocumentBase();
        return codebase;
    }

    public URL getCodeBase() {
        return getDocumentBase();
    }

    public void setCodeBase(String url) {
        try {
            codebase = new URL(url);
        } catch (Exception e) {}
    }

    public void setParameters(HashMap<String, String> params) {
        parameters = params;
    }

    public String getParameter(String name) {
        if (appletStub != null) {
            String ret = appletStub.getParameter(name);
            if (ret != null)
                return ret;
        }

        return parameters.get(name);
    }
}
