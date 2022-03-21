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

import com.dalvikjvm.DalvikJVM;
import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexPathList;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public abstract class DetourClassLoader extends ClassLoader {
    public ArrayList<String> compiledClasses = null;

    public Class<?> findClass(String name) {
        return null;
    }

    public static void addPath(String s) throws Exception {
        System.out.println("Adding path: " + s);

        Field field = BaseDexClassLoader.class.getDeclaredField("pathList");
        field.setAccessible(true);
        DexPathList list = (DexPathList)field.get(DalvikJVM.instance.dexLoader);
        field.setAccessible(false);

        Method method = DexPathList.class.getDeclaredMethod("addDexPath", new Class[]{String.class, File.class});
        method.setAccessible(true);
        method.invoke(list, s, null);
        method.setAccessible(false);
    }

    public boolean isCompiled(String name) {
        return compiledClasses.contains(name);
    }

    public static void addDependency(HashSet<String> hashSet, String name) {
        while (name.startsWith("["))
            name = name.substring(1);

        name = name.trim();

        if (name.equals("I") || name.equals("Z") || name.equals("C") || name.equals("J") || name.equals("B"))
            return;

        if (name.startsWith("L") && name.endsWith(";"))
            name = name.substring(1, name.length() - 1);
        while (name.startsWith("["))
            name = name.substring(1);
        name = name.replaceAll("/", ".");

        hashSet.add(name);
    }

    public static HashSet<String> getDependencies(byte[] data) {
        HashSet<String> classDependencies = new HashSet<String>();

        ClassReader reader;
        try {
            reader = new ClassReader(data);
        } catch (Exception e) {
            return null;
        }
        ClassNode node = new ClassNode();
        reader.accept(node, ClassReader.SKIP_DEBUG);

        classDependencies.add(node.superName);

        Iterator<FieldNode> fieldList = node.fields.iterator();
        while (fieldList.hasNext()) {
            FieldNode fieldNode = fieldList.next();
            addDependency(classDependencies, fieldNode.desc);
        }

        Iterator<String> interfaceList = node.interfaces.iterator();
        while (interfaceList.hasNext()) {
            String c = interfaceList.next();
            addDependency(classDependencies, c);
        }

        Iterator<MethodNode> methodNodeList = node.methods.iterator();
        while (methodNodeList.hasNext()) {
            MethodNode methodNode = methodNodeList.next();

            Iterator<AbstractInsnNode> insnNodeList = methodNode.instructions.iterator();
            while (insnNodeList.hasNext()) {
                AbstractInsnNode insnNode = insnNodeList.next();

                if (insnNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                    MethodInsnNode call = (MethodInsnNode) insnNode;
                    addDependency(classDependencies, call.owner);
                }

                if (insnNode.getOpcode() == Opcodes.INVOKESTATIC) {
                    MethodInsnNode call = (MethodInsnNode) insnNode;
                    addDependency(classDependencies, call.owner);
                }

                if (insnNode.getOpcode() == Opcodes.INVOKESPECIAL) {
                    MethodInsnNode call = (MethodInsnNode) insnNode;
                    addDependency(classDependencies, call.owner);
                }

                if (insnNode.getOpcode() == Opcodes.INVOKEINTERFACE) {
                    MethodInsnNode call = (MethodInsnNode) insnNode;
                    addDependency(classDependencies, call.owner);
                }

                if (insnNode.getOpcode() == Opcodes.GETFIELD) {
                    FieldInsnNode field = (FieldInsnNode) insnNode;
                    addDependency(classDependencies, field.owner);
                }

                if (insnNode.getOpcode() == Opcodes.PUTFIELD) {
                    FieldInsnNode field = (FieldInsnNode) insnNode;
                    addDependency(classDependencies, field.owner);
                }

                if (insnNode.getOpcode() == Opcodes.PUTSTATIC) {
                    FieldInsnNode field = (FieldInsnNode) insnNode;
                    addDependency(classDependencies, field.owner);
                }

                if (insnNode.getOpcode() == Opcodes.GETSTATIC) {
                    FieldInsnNode field = (FieldInsnNode) insnNode;
                    addDependency(classDependencies, field.owner);
                }

                if (insnNode.getOpcode() == Opcodes.NEW) {
                    TypeInsnNode type = (TypeInsnNode) insnNode;
                    addDependency(classDependencies, type.desc);
                }
            }
        }

        return classDependencies;
    }

    public Class<?> defineClassJava(String name, byte[] b, int off, int len) {
        System.out.println("Define: " + name);

        if (compiledClasses == null)
            compiledClasses = new ArrayList<String>();

        try {
            if (isCompiled(name))
                return DalvikJVM.instance.dexLoader.loadClass(name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String dexPath = DalvikJVM.instance.compileClass(name, b, off, len);
        try { DetourClassLoader.addPath(dexPath); } catch (Exception e) {}
        compiledClasses.add(name);

        HashSet<String> dependencies = getDependencies(b);

        for (String dependency : dependencies) {
            if (!isCompiled(dependency)) {
                // Compile missing class
                System.out.println("Dependency: " + dependency);
                Class<?> c = findClass(dependency);
            }
        }

        try {
            return DalvikJVM.instance.dexLoader.loadClass(name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
