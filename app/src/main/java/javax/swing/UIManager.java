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

public class UIManager {
    public class LookAndFeelInfo {
    }

    public static String getCrossPlatformLookAndFeelClassName() {
        System.out.println("Unimplemented method UIManager.getCrossPlatformLookAndFeelClassName() called");
        return "";
    }

    public static String getSystemLookAndFeelClassName() {
        System.out.println("Unimplemented method UIManager.getSystemLookAndFeelClassName() called");
        return "";
    }

    public static LookAndFeelInfo[] getInstalledLookAndFeels() {
        System.out.println("Unimplemented method UIManager.getInstalledLookAndFeels() called");
        return new LookAndFeelInfo[0];
    }

    public static void setLookAndFeel(String className) {
        System.out.println("Unimplemented method UIManager.setLookAndFeel(" + className + ") called");
    }

    public static Object put(Object key, Object value) {
        System.out.println("Unimplemented method UIManager.setLookAndFeel(" + key + ", " + value + ") called");
        return null;
    }
}
