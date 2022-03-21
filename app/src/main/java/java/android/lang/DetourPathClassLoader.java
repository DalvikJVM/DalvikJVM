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

import dalvik.system.PathClassLoader;

public class DetourPathClassLoader extends PathClassLoader {
    DetourClassLoader classLoader;
    String name;

    public DetourPathClassLoader(String dexPath, ClassLoader parent, String name, DetourClassLoader classLoader) {
        super(dexPath, parent);
        this.classLoader = classLoader;
        this.name = name;
    }

    @Override
    public final Class<?> findClass(String name) {
        System.out.println("Find class " + name);
        try {
            if (classLoader.isCompiled(name))
                return super.findClass(name);
            System.out.println("Compiling class " + name);
            return classLoader.findClass(name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
