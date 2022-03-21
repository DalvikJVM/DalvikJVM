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

package java.android.lang;

import com.dalvikjvm.MainActivity;

import java.util.Properties;

public class DetourSystem {
    public static void gc() {
        // TODO: Should we just not garbage collect at all?
        Runtime.getRuntime().gc();
    }

    public static void exit(int status) {
        // TODO: Implement System.exit to stop our JVM instead
        System.exit(status);
    }

    public static Properties getProperties() {
        System.out.println("Unimplemented method System.getProperties() called");
        return null;
    }

    public static String getProperty(String key) {
        String ret = System.getProperty(key);
        if (key.equals("user.home"))
            ret = MainActivity.cacheDir;
        if (key.equals("java.vendor"))
            ret = "Oracle Corporation";
        if (key.equals("java.version"))
            ret = "1.8.0_281";
        if (key.equals("java.vendor.url"))
            ret = "http://java.oracle.com/";
        System.out.println("Detouring " + key + " = " + ret);
        return ret;
    }

    public static String getProperty(String key, String def) {
        String ret = getProperty(key);
        if (ret == null)
            return def;
        return ret;
    }
}
