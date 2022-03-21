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

package java.android.io;

import android.os.Environment;
import com.dalvikjvm.DalvikJVM;

import java.io.File;

public class DetourFilePath {
    public static String convert(String str) {
        System.out.println("Path: " + str);

        // Check if string is already converted
        if (str.startsWith(DalvikJVM.cacheDir) || str.startsWith(DalvikJVM.config.workingDirectory)) {
            System.out.println("Accessing path '" + str + "'");
            return str;
        }

        String ret = str;

        // Standardized file seperator
        ret = str.replaceAll("/", File.separator);
        ret = str.replaceAll("\\\\", File.separator);

        // Remove starting seperator
        if (ret.startsWith(File.separator))
            ret = ret.substring(1);
        if (ret.startsWith("." + File.separator))
            ret = ret.substring(2);

        // Remove invalid characters
        ret = ret.replaceAll(":", "");

        // Make sure it points to somewhere we can make files
        ret = DalvikJVM.config.workingDirectory + File.separator + ret;

        File f = new File(ret);
        ret = f.getAbsolutePath();
        if (str.endsWith(File.separator))
            ret += File.separator;
        System.out.println("Detouring path '" + str + " to '" + ret + "'");
        return ret;
    }
}
