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

package com.dalvikjvm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Dexecrator {
    public static String jarPath;
    public static String outputPath;

    private static Printer printer = new Textifier();
    private static TraceMethodVisitor mp = new TraceMethodVisitor(printer);

    private static final int OPCODE_INT2BYTE = 145;
    private static final int OPCODE_INT2CHAR = 146;
    private static final int OPCODE_INT2SHORT = 147;

    public static void dumpClass(ClassNode node) {
        BufferedWriter writer = null;

        try {
            new File("dump").mkdirs();

            File file = new File("dump/" + node.name + ".dump");
            file.mkdirs();
            file.delete();
            writer = new BufferedWriter(new FileWriter(file));

            writer.write(decodeAccess(node.access) + node.name + " extends " + node.superName + ";\n");
            writer.write("\n");

            Iterator<FieldNode> fieldNodeList = node.fields.iterator();
            while (fieldNodeList.hasNext()) {
                FieldNode fieldNode = fieldNodeList.next();
                writer.write(
                        decodeAccess(fieldNode.access) + fieldNode.desc + " " + fieldNode.name + ";\n");
            }

            writer.write("\n");

            Iterator<MethodNode> methodNodeList = node.methods.iterator();
            while (methodNodeList.hasNext()) {
                MethodNode methodNode = methodNodeList.next();
                writer.write(
                        decodeAccess(methodNode.access) + methodNode.name + " " + methodNode.desc + ":\n");

                Iterator<AbstractInsnNode> insnNodeList = methodNode.instructions.iterator();
                while (insnNodeList.hasNext()) {
                    AbstractInsnNode insnNode = insnNodeList.next();
                    String instruction = decodeInstruction(insnNode);
                    writer.write(instruction);
                }
                writer.write("\n");
            }

            writer.close();
        } catch (Exception e) {
            try {
                writer.close();
            } catch (Exception e2) {
            }
        }
    }

    private static String decodeAccess(int access) {
        String res = "";

        if ((access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC) res += "public ";
        if ((access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE) res += "private ";
        if ((access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED) res += "protected ";

        if ((access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) res += "static ";
        if ((access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL) res += "final ";
        if ((access & Opcodes.ACC_VOLATILE) == Opcodes.ACC_VOLATILE) res += "protected ";
        if ((access & Opcodes.ACC_SYNCHRONIZED) == Opcodes.ACC_SYNCHRONIZED) res += "synchronized ";
        if ((access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT) res += "abstract ";
        if ((access & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE) res += "interface ";

        return res;
    }

    private static String decodeInstruction(AbstractInsnNode insnNode) {
        insnNode.accept(mp);
        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();
        return sw.toString();
    }

    public static void printPatch(String name, ClassNode node, MethodNode methodNode) {
        System.out.print(name + " detoured in " + node.name);
        if (methodNode != null)
            System.out.print("." + methodNode.name + methodNode.desc);
        System.out.println();
    }

    public static byte[] patch(byte[] data) {
        ClassReader reader;
        try {
            reader = new ClassReader(data);
        } catch (Exception e) {
            return data;
        }
        ClassNode node = new ClassNode();
        reader.accept(node, ClassReader.SKIP_DEBUG);

        Iterator<MethodNode> methodNodeList = node.methods.iterator();

        if (node.superName.equals("java/lang/ClassLoader")) {
            node.superName = "java/android/lang/DetourClassLoader";
            printPatch("extends ClassLoader", node, null);
        }

        while (methodNodeList.hasNext()) {
            MethodNode methodNode = methodNodeList.next();

            boolean charReturnType = methodNode.desc.endsWith("C");
            boolean booleanReturnType = methodNode.desc.endsWith("Z");

            Iterator<AbstractInsnNode> insnNodeList = methodNode.instructions.iterator();
            while (insnNodeList.hasNext()) {
                AbstractInsnNode insnNode = insnNodeList.next();

                if (insnNode.getOpcode() == Opcodes.IRETURN) {
                    // Dalvik VM verifier patch, Java allows this but dalvik doesn't.
                    // Documented here: https://android.googlesource.com/platform/art/+/master/runtime/verifier/method_verifier.cc#2165
                    if (charReturnType) {
                        methodNode.instructions.insertBefore(insnNode, new InsnNode(OPCODE_INT2CHAR));
                        printPatch("Java Class Verifier Return (char)", node, methodNode);
                    }

                    if (booleanReturnType) {
                        LabelNode jmpNode = new LabelNode();
                        methodNode.instructions.insertBefore(insnNode, new MethodInsnNode( Opcodes.INVOKESTATIC,
                                "java/android/lang/DetourVerifier", "fixCastInt", "(I)I"));
                        printPatch("Java Class Verifier Return (Boolean)", node, methodNode);
                    }
                }

                if (insnNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                    MethodInsnNode call = (MethodInsnNode) insnNode;

                    if (node.superName.equals("java/android/lang/DetourClassLoader") && call.name.equals("defineClass") && call.desc.equals("(Ljava/lang/String;[BII)Ljava/lang/Class;")) {
                        call.name = "defineClassJava";
                        printPatch("ClassLoader.defineClass(String, byte[], int, int)", node, methodNode);
                    }

                    if (node.superName.equals("java/lang/Thread") && call.name.equals("getContextClassLoader") && call.desc.equals("()Ljava/lang/ClassLoader;")) {
                        methodNode.instructions.insert(call, new MethodInsnNode( Opcodes.INVOKESTATIC,
                                "java/android/lang/DetourThread", "getClassLoader", "(Ljava/lang/ClassLoader;)Ljava/lang/ClassLoader;"));
                        printPatch("Thread.getContextClassLoader()", node, methodNode);
                    }
                }

                if (insnNode.getOpcode() == Opcodes.INVOKESTATIC) {
                    MethodInsnNode call = (MethodInsnNode) insnNode;

                    // Patch InetAddress.getByName(String)
                    // Android prefers ipv6, desktop is preffering ipv4 and causing crashes
                    if (call.owner.equals("java/net/InetAddress") && call.name.equals("getByName")) {
                        call.owner = "java/android/net/DetourInetAddress";
                        printPatch("InetAddress.getByName(String)", node, methodNode);
                    }

                    // Patch System.getProperty(String)
                    if (call.owner.equals("java/lang/System") && call.name.equals("getProperty")) {
                        call.owner = "java/android/lang/DetourSystem";
                        printPatch("System.getProperty(String)", node, methodNode);
                    }

                    // Patch System.getProperties()
                    if (call.owner.equals("java/lang/System") && call.name.equals("getProperties")) {
                        call.owner = "java/android/lang/DetourSystem";
                        printPatch("System.getProperties()", node, methodNode);
                    }

                    // Patch System.gc()
                    if (call.owner.equals("java/lang/System") && call.name.equals("gc")) {
                        call.owner = "java/android/lang/DetourSystem";
                        printPatch("System.gc()", node, methodNode);
                    }

                    // Patch System.exit()
                    if (call.owner.equals("java/lang/System") && call.name.equals("exit")) {
                        call.owner = "java/android/lang/DetourSystem";
                        printPatch("System.exit(int)", node, methodNode);
                    }
                }

                if (insnNode.getOpcode() == Opcodes.INVOKESPECIAL) {
                    MethodInsnNode call = (MethodInsnNode) insnNode;

                    // FileInputStream(String)
                    if (call.owner.equals("java/io/FileInputStream") && call.name.equals("<init>") && call.desc.equals("(Ljava/lang/String;)V")) {
                        methodNode.instructions.insertBefore(call, new MethodInsnNode( Opcodes.INVOKESTATIC,
                                "java/android/io/DetourFilePath", "convert", "(Ljava/lang/String;)Ljava/lang/String;"));
                        printPatch("FileInputStream(String)", node, methodNode);
                    }

                    // ZipFile(String)
                    if (call.owner.equals("java/util/zip/ZipFile") && call.name.equals("<init>") && call.desc.equals("(Ljava/lang/String;)V")) {
                        methodNode.instructions.insertBefore(call, new MethodInsnNode( Opcodes.INVOKESTATIC,
                                "java/android/io/DetourFilePath", "convert", "(Ljava/lang/String;)Ljava/lang/String;"));
                        printPatch("ZipFile(String)", node, methodNode);
                    }

                    // FileOutputStream(String)
                    if (call.owner.equals("java/io/FileOutputStream") && call.name.equals("<init>") && call.desc.equals("(Ljava/lang/String;)V")) {
                        methodNode.instructions.insertBefore(call, new MethodInsnNode( Opcodes.INVOKESTATIC,
                                "java/android/io/DetourFilePath", "convert", "(Ljava/lang/String;)Ljava/lang/String;"));
                        printPatch("FileOutputStream(String)", node, methodNode);
                    }

                    // FileOutputStream(String, Boolean)
                    if (call.owner.equals("java/io/FileOutputStream") && call.name.equals("<init>") && call.desc.equals("(Ljava/lang/String;Z)V")) {
                        methodNode.instructions.insertBefore(call, new InsnNode(Opcodes.DUP_X1));
                        methodNode.instructions.insertBefore(call, new InsnNode(Opcodes.POP));
                        methodNode.instructions.insertBefore(call, new MethodInsnNode( Opcodes.INVOKESTATIC,
                                "java/android/io/DetourFilePath", "convert", "(Ljava/lang/String;)Ljava/lang/String;"));
                        methodNode.instructions.insertBefore(call, new InsnNode(Opcodes.DUP_X1));
                        methodNode.instructions.insertBefore(call, new InsnNode(Opcodes.POP));
                        printPatch("FileOutputStream(String, Boolean)", node, methodNode);
                    }

                    // File(String)
                    if (call.owner.equals("java/io/File") && call.name.equals("<init>") && call.desc.equals("(Ljava/lang/String;)V")) {
                        methodNode.instructions.insertBefore(call, new MethodInsnNode( Opcodes.INVOKESTATIC,
                                "java/android/io/DetourFilePath", "convert", "(Ljava/lang/String;)Ljava/lang/String;"));
                        printPatch("File(String)", node, methodNode);
                    }

                    // File(String, String)
                    if (call.owner.equals("java/io/File") && call.name.equals("<init>") && call.desc.equals("(Ljava/lang/String;Ljava/lang/String;)V")) {
                        methodNode.instructions.insertBefore(call, new InsnNode(Opcodes.DUP_X1));
                        methodNode.instructions.insertBefore(call, new InsnNode(Opcodes.POP));
                        methodNode.instructions.insertBefore(call, new MethodInsnNode( Opcodes.INVOKESTATIC,
                                "java/android/io/DetourFilePath", "convert", "(Ljava/lang/String;)Ljava/lang/String;"));
                        methodNode.instructions.insertBefore(call, new InsnNode(Opcodes.DUP_X1));
                        methodNode.instructions.insertBefore(call, new InsnNode(Opcodes.POP));
                        printPatch("File(String, String)", node, methodNode);
                    }
                }
            }
        }

        // Dump assembly
        dumpClass(node);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }

    public static boolean fetch(String jarURL, String outputURL) {
        if (jarURL.endsWith(".class")) {
            File classFile = new File(jarURL);
            byte[] data = new byte[(int)classFile.length()];

            try {
                FileInputStream stream = new FileInputStream(classFile);
                stream.read(data);
                stream.close();
            } catch (Exception e) {
                return false;
            }

            data = patch(data);

            try {
                FileOutputStream stream = new FileOutputStream(outputURL);
                stream.write(data);
                stream.close();
            } catch (Exception e) {
                return false;
            }

            return true;
        }

        try {
            JarInputStream in = new JarInputStream(new FileInputStream(jarURL));
            ZipOutputStream zout = new ZipOutputStream(new CheckedOutputStream(new FileOutputStream(outputURL), new Adler32()));

            JarEntry entry;
            while ((entry = in.getNextJarEntry()) != null) {
                // Check if file is needed
                String name = entry.getName();

                // Read class to byte array
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int readSize;
                while ((readSize = in.read(data, 0, data.length)) != -1) bOut.write(data, 0, readSize);
                byte[] classData = bOut.toByteArray();
                bOut.close();

                System.out.println(name);

                if (name.endsWith(".class"))
                    classData = patch(classData);

                ZipEntry zipEntry = new ZipEntry(name);
                zout.putNextEntry(zipEntry);
                zout.write(classData, 0, classData.length);
                zout.closeEntry();
            }
            in.close();
            zout.finish();
            zout.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void main(String args[]) {
        jarPath = args[0];
        outputPath = args[1];

        System.out.println("Loading Java file at '" + jarPath + "'");

        fetch(jarPath, outputPath);

        System.out.println("Finished, exported to '" + outputPath + "'");

        return;
    }
}
