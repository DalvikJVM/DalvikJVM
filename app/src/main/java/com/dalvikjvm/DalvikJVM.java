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

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import dalvik.system.DexClassLoader;

import javax.swing.*;
import java.android.awt.AndroidGraphicsDevice;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.jar.Attributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class DalvikJVM extends AppCompatActivity {
    public static Applet applet;
    public static InputMethodManager inputMethodManager;
    public static DalvikJVM instance;
    public static String cacheDir;
    public static String homeDir;
    public static JVMConfig config;
    private boolean shiftPressed;
    private boolean altShiftPressed;
    private boolean ctrlPressed;
    private boolean altCtrlPressed;
    private boolean altPressed;
    private boolean altAltPressed;
    public boolean virtualShift;
    private boolean[] keyPressed = new boolean[256];

    private List<Event> eventQueue = new ArrayList<Event>();

    public static final int KEYBOARD_DELAY = 50;

    private Object eventQueueLock = new Object();

    public boolean hasFocus;

    public DexClassLoader dexLoader;

    public static Component renderTarget = null;
    private static List<AWTEventListener> eventQueueListener = new ArrayList<AWTEventListener>();

    // Intent codes
    public static final int _FILE_SELECT_CODE = 0;
    public static final int _JAR_SELECT_CODE = 1;
    public static final int _PERMISSION_SELECT_CODE = 2;

    public DalvikJVM() {
        super();
        instance = this;
        virtualShift = false;
    }

    public static void addAWTEventListener(AWTEventListener listener) {
        eventQueueListener.add(listener);
    }

    private static void zipEntry(String parentDir, File inputFolderPath, ZipOutputStream zos) throws IOException {
        File[] files = inputFolderPath.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().startsWith("__staging")) {
                continue;
            }

            if (files[i].isDirectory()) {
                zipEntry(parentDir, files[i], zos);
                continue;
            }

            MainCanvas.setStatusText("Zipping " + files[i].getName());

            String zipName = files[i].getAbsolutePath().substring(parentDir.length() + 1);
            System.out.println("Adding file: " + zipName);
            byte[] buffer = new byte[1024];
            FileInputStream fis = new FileInputStream(files[i]);
            zos.putNextEntry(new ZipEntry(zipName));
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            fis.close();
        }
    }

    private static void zip(File inputFolderPath, File outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);

            zipEntry(inputFolderPath.getAbsolutePath(), inputFolderPath, zos);

            zos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                MainCanvas.setStatusText("Unzipping " + file.getName());
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            }
        } finally {
            zis.close();
        }

        MainCanvas.setStatusText(null);
    }

    public void compileDirectory(File targetDirectory, File classesDex) {
        File[] directoryListing = targetDirectory.listFiles();
        for (File child : directoryListing) {
            // Directory, recurse into it
            if (child.isDirectory()) {
                compileDirectory(child, classesDex);
                continue;
            }

            // Resource file, handle it differently
            if (!child.getName().endsWith(".class")) {
                System.out.println("Non-class file: " + child.getAbsolutePath());
                continue;
            }

            // Generate output path
            String output = child.getAbsolutePath().substring(0, child.getAbsolutePath().lastIndexOf(".")) + ".dex";
            if (classesDex != null)
                output = classesDex.getAbsolutePath();

            // Compile .class to .dex
            try {
                MainCanvas.setStatusText("Compiling " + child.getName());
                String[] args = {"--verbose", "--dex", "--min-sdk-version", "26", "--no-strict", "--output=" + output, child.getAbsolutePath()};
                com.android.dx.command.Main.main(args);
                Runtime.getRuntime().gc();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Remove .class after .dex has been made
            child.delete();
        }
    }

    public void mergeDirectory(File targetDirectory, File classesDex, List<String> args) {
        boolean isRoot = false;

        if (args == null) {
            args = new ArrayList<String>();
            args.add(classesDex.getAbsolutePath());
            isRoot = true;
        }

        File[] directoryListing = targetDirectory.listFiles();
        for (File child : directoryListing) {
            // Directory, recurse into it
            if (child.isDirectory()) {
                mergeDirectory(child, classesDex, args);
                continue;
            }

            // Resource file, handle it differently
            if (!child.getName().endsWith(".dex")) {
                continue;
            }

            // Add .dex file
            args.add(child.getAbsolutePath());
        }

        if (isRoot) {
            try {
                String[] argArray = new String[args.size()];
                args.toArray(argArray);
                com.android.dx.merge.DexMerger.main(argArray);
                for (int i = 1; i < argArray.length; i++)
                    new File(argArray[i]).delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case _FILE_SELECT_CODE: {
                Uri uri = null;
                if (resultCode == Activity.RESULT_OK) {
                    uri = data.getData();
                }
                JFileChooser.instance._setResult(uri);
                break;
            }
            case _JAR_SELECT_CODE: {
                if (resultCode != Activity.RESULT_OK)
                    break;

                Uri uri = data.getData();

                // TODO: Allow user to define this config
                config = new JVMConfig();
                config.emulatedJREVersion = JVMConfig.EmulatedJREVersion.ORACLE_8;
                config.classPath = JFileChooser.getPath(this, uri);
                config.applet = false;
                /*config.classMain = "fleas";
                config.applet = true;
                config.appletCodeBase = "https://logg.biz/runescape/2005-08/jagex.com/fleacircus/";
                config.appletSize = new Dimension(644, 390);*/

                // Define rest of config automatically
                config.workingDirectory = config.classPath.substring(0, config.classPath.lastIndexOf("/"));

                // TODO: Hide UI, this should be handled way better if we had a ui, lol
                final Button button = findViewById(R.id.button_launch);
                button.setEnabled(false);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Execute in DalvikJVM environment
                        runDalvikJVM();
                    }
                }).start();
                break;
            }
        }
    }

    public String compileClass(String name, byte[] data, int off, int len) {
        // Append .class to name
        name = name + ".class";

        File targetDirectory = new File(cacheDir + "/staging");
        deleteRecursive(targetDirectory);

        try {
            targetDirectory.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File inputFile = new File(targetDirectory.getAbsolutePath() + "/__" + name);
        try {
            FileOutputStream outputStream = new FileOutputStream(inputFile);
            outputStream.write(data, off, len);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File outputClassStaging = new File(targetDirectory.getAbsolutePath() + "/" + name);
        Dexecrator.patch(inputFile.getAbsolutePath(), outputClassStaging.getAbsolutePath());

        // Clean up unpatched class
        String inputHash = Dexecrator.getLastPatchHash();
        inputFile.delete();

        File outputFile = new File(getDir("dex", Context.MODE_PRIVATE), inputHash + ".dex");

        if (!outputFile.exists()) {
            File dexFile = new File(targetDirectory.getAbsolutePath() + "/__tmp.dex");

            compileDirectory(targetDirectory, dexFile);
            dexFile.renameTo(outputFile);
        }

        MainCanvas.setStatusText("Cleaning up");
        deleteRecursive(targetDirectory);

        MainCanvas.setStatusText(null);

        return outputFile.getAbsolutePath();
    }

    public String compileJAR(String input) {
        File inputFile = new File(input);

        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                if (ze.getName().equals("classes.dex")) {
                    zis.close();
                    return input;
                }
            }
            zis.close();
        } catch (Exception e) {
            try { zis.close(); } catch (Exception e2) {}
        }

        File targetDirectory = new File(cacheDir + "/staging");
        deleteRecursive(targetDirectory);

        try {
            targetDirectory.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        MainCanvas.setStatusText("Launching " + input);
        File outputJARStaging = new File(targetDirectory.getAbsolutePath() + "/__staging.jar");
        Dexecrator.patch(inputFile.getAbsolutePath(), outputJARStaging.getAbsolutePath());

        String inputHash = Dexecrator.getLastPatchHash();
        File outputFile = new File(getDir("dex", Context.MODE_PRIVATE), inputHash + ".zip");

        if (!outputFile.exists()) {
            try {
                unzip(outputJARStaging, targetDirectory);
            } catch (Exception e) {
                e.printStackTrace();
            }

            File dexFile = new File(targetDirectory.getAbsolutePath() + "/classes.dex");
            File outputFileStaging = new File(targetDirectory.getAbsolutePath() + "/__staging.zip");

            compileDirectory(targetDirectory, null);
            MainCanvas.setStatusText("Building classes.dex");
            mergeDirectory(targetDirectory, dexFile, null);
            zip(targetDirectory, outputFileStaging);
            outputFileStaging.renameTo(outputFile);
        }

        MainCanvas.setStatusText("Cleaning up");
        deleteRecursive(targetDirectory);

        MainCanvas.setStatusText(null);

        return outputFile.getAbsolutePath();
    }

    public void enterPipMode(View view) {
        enterPictureInPictureMode();
    }

    public void copyFile(int resource, File dexInternalStoragePath) {
        if (dexInternalStoragePath.exists()) {
            dexInternalStoragePath.delete();
        } else {
            dexInternalStoragePath.mkdirs();
            dexInternalStoragePath.delete();
        }

        System.out.println(dexInternalStoragePath.getAbsolutePath());

        try {
            BufferedInputStream bis = new BufferedInputStream(getResources().openRawResource(resource));
            OutputStream dexWriter = new BufferedOutputStream(new FileOutputStream(dexInternalStoragePath));
            byte[] buf = new byte[1024];
            int len;
            while ((len = bis.read(buf, 0, 1024)) > 0)
                dexWriter.write(buf, 0, len);
            dexWriter.close();
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addEventQueue(Event e) {
        synchronized (eventQueueLock) {
            eventQueue.add(e);
        }
    }

    public static void processAWTEvent(AWTEvent evt) {
        for (AWTEventListener listener : eventQueueListener)
            listener.eventDispatched(evt);
    }

    public void processEventQueue() {
        synchronized (eventQueueLock) {
            ListIterator<Event> it = eventQueue.listIterator();
            while (it.hasNext()) {
                Event event = it.next();
                Component component = (Component) event.target;
                if (event.when <= System.currentTimeMillis()) {
                    System.out.println("ID: " + event.id + ", " + event._getKey());
                    component.dispatchComponentEvent(event);
                    it.remove();
                }
            }
        }
    }

    public static void setTarget(Component component) {
        renderTarget = component;
        System.out.println("RENDER TARGET: " + component);
    }

    public static Component getTarget() {
        return renderTarget;
    }

    public void runDalvikJVM() {
        // Load and compile jar
        File dexInternalStoragePath = new File(config.classPath);
        String dexPath = compileJAR(dexInternalStoragePath.getAbsolutePath());

        try {
            // Load our compiled dexes
            dexLoader = new DexClassLoader(dexPath, "", null, getClassLoader());

            // Load manifest information
            Enumeration<URL> resources = dexLoader.getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                try {
                    java.util.jar.Manifest manifest = new java.util.jar.Manifest(resources.nextElement().openStream());
                    Attributes attributes = manifest.getMainAttributes();

                    // Load attributes
                    String mainClass = attributes.getValue(Attributes.Name.MAIN_CLASS);

                    // Set main class
                    if (config.classMain == null && mainClass != null)
                        config.classMain = mainClass;
                } catch (IOException e) {}
            }

            Class<?> client = dexLoader.loadClass(config.classMain);

            if (config.applet) {
                // Running applet configuration
                applet = (Applet) client.newInstance();
                applet.setParameters(config.appletParameters);
                applet.setCodeBase(config.appletCodeBase);
                applet.setSize(config.appletSize.width, config.appletSize.height);
                applet.init();
                applet.start();
                setTarget(applet);
            } else {
                // Running desktop configuration
                String[] args = config.desktopParameters.values().toArray(new String[0]);
                Method meth = client.getMethod("main", String[].class);
                meth.invoke(null, (Object)args);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initDalvikJVM() {
        cacheDir = new File(getFilesDir().getAbsolutePath() + File.separator).getAbsolutePath() + "/";
        homeDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DalvikJVM";

        File fileHomeDir = new File(homeDir);
        if (!fileHomeDir.exists())
            fileHomeDir.mkdirs();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try { this.getSupportActionBar().hide(); } catch (NullPointerException e) {}
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        AndroidGraphicsDevice.setupDisplayMode(MainCanvas.instance.getWidth(), MainCanvas.instance.getHeight());

        // Check if we need to request permissions
        List<String> permissionRequest = new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
            permissionRequest.add(Manifest.permission.INTERNET);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // Request needed permissions
        if (permissionRequest.size() > 0) {
            String[] permissions = permissionRequest.toArray(new String[0]);
            ActivityCompat.requestPermissions(DalvikJVM.this, permissions, _PERMISSION_SELECT_CODE);
        } else {
            initDalvikJVM();
        }

        final Button button = findViewById(R.id.button_launch);
        button.setEnabled(getTarget() == null);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, _JAR_SELECT_CODE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        hasFocus = true;

        if (getTarget() != null) {
            Event focusEvent = new Event(getTarget(), Event.GOT_FOCUS, getTarget());
            addEventQueue(focusEvent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        hasFocus = false;

        if (getTarget() != null) {
            Event focusEvent = new Event(getTarget(), Event.LOST_FOCUS, getTarget());
            addEventQueue(focusEvent);

            enterPipMode(findViewById(R.id.game_canvas));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case _PERMISSION_SELECT_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    initDalvikJVM();
                break;
        }
    }

    public static float convertDpToPixel(float dp) {
        return dp * ((float)instance.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_MEDIUM);
    }

    public boolean isShiftPressed() {
        return (shiftPressed || altShiftPressed || virtualShift);
    }

    public boolean isCtrlPressed() {
        return (ctrlPressed || altCtrlPressed);
    }

    public boolean isAltPressed() {
        return (altPressed || altAltPressed);
    }

    public Event convertAndroidKeycode(int keycode, int id, boolean forceShift) {
        boolean shift = forceShift | isShiftPressed();
        boolean ctrl = isCtrlPressed();
        boolean alt = isAltPressed();
        Event evt = new Event(getTarget(), id, null);

        if (shift)
            evt.modifiers |= java.awt.event.InputEvent.SHIFT_MASK;
        if (ctrl)
            evt.modifiers |= java.awt.event.InputEvent.CTRL_MASK;
        if (alt)
            evt.modifiers |= java.awt.event.InputEvent.ALT_MASK;

        switch(keycode) {
            case KeyEvent.KEYCODE_DEL:
                evt.key = 8;
                evt._keyChar = '\b';
                break;
            case KeyEvent.KEYCODE_ENTER:
                evt.key = 10;
                evt._keyChar = '\n';
                break;
            case KeyEvent.KEYCODE_ESCAPE:
                evt.key = 27;
                break;
            case KeyEvent.KEYCODE_SPACE:
                evt.key = 32;
                evt._keyChar = ' ';
                break;
            case KeyEvent.KEYCODE_POUND:
                evt.key = 35;
                evt._keyChar = '#';
                evt._awtKey = 51;
                break;
            case KeyEvent.KEYCODE_APOSTROPHE:
                evt._awtKey = 222;
                if (shift) {
                    evt.key = 34;
                    evt._keyChar = '\"';
                } else {
                    evt.key = 39;
                    evt._keyChar = '\'';
                }
                break;
            case KeyEvent.KEYCODE_STAR:
                evt._awtKey = 56;
                evt.key = 42;
                evt._keyChar = '*';
                break;
            case KeyEvent.KEYCODE_COMMA:
                evt._awtKey = 44;
                if (shift) {
                    evt.key = 60;
                    evt._keyChar = '<';
                } else {
                    evt.key = 44;
                    evt._keyChar = ',';
                }
                break;
            case KeyEvent.KEYCODE_MINUS:
                evt._awtKey = 45;
                if (shift) {
                    evt.key = 95;
                    evt._keyChar = '_';
                } else {
                    evt.key = 45;
                    evt._keyChar = '-';
                }
                break;
            case KeyEvent.KEYCODE_PERIOD:
                evt._awtKey = 46;
                if (shift) {
                    evt.key = 62;
                    evt._keyChar = '>';
                } else {
                    evt.key = 46;
                    evt._keyChar = '.';
                }
                break;
            case KeyEvent.KEYCODE_SLASH:
                evt._awtKey = 47;
                if (shift) {
                    evt.key = 63;
                    evt._keyChar = '?';
                } else {
                    evt.key = 47;
                    evt._keyChar = '/';
                }
                break;
            case KeyEvent.KEYCODE_0:
                evt._awtKey = 48;
                if (shift) {
                    evt.key = 41;
                    evt._keyChar = ')';
                } else {
                    evt.key = 48;
                    evt._keyChar = '0';
                }
                break;
            case KeyEvent.KEYCODE_1:
                evt._awtKey = 49;
                if (shift) {
                    evt.key = 33;
                    evt._keyChar = '!';
                } else {
                    evt.key = 49;
                    evt._keyChar = '1';
                }
                break;
            case KeyEvent.KEYCODE_2:
                evt._awtKey = 50;
                if (shift) {
                    evt.key = 64;
                    evt._keyChar = '@';
                } else {
                    evt.key = 50;
                    evt._keyChar = '2';
                }
                break;
            case KeyEvent.KEYCODE_3:
                evt._awtKey = 51;
                if (shift) {
                    evt.key = 35;
                    evt._keyChar = '#';
                } else {
                    evt.key = 51;
                    evt._keyChar = '3';
                }
                break;
            case KeyEvent.KEYCODE_4:
                evt._awtKey = 52;
                if (shift) {
                    evt.key = 36;
                    evt._keyChar = '$';
                } else {
                    evt.key = 52;
                    evt._keyChar = '4';
                }
                break;
            case KeyEvent.KEYCODE_5:
                evt._awtKey = 53;
                if (shift) {
                    evt.key = 37;
                    evt._keyChar = '%';
                } else {
                    evt.key = 53;
                    evt._keyChar = '5';
                }
                break;
            case KeyEvent.KEYCODE_6:
                evt._awtKey = 54;
                if (shift) {
                    evt.key = 94;
                    evt._keyChar = '^';
                } else {
                    evt.key = 54;
                    evt._keyChar = '6';
                }
                break;
            case KeyEvent.KEYCODE_7:
                evt._awtKey = 55;
                if (shift) {
                    evt.key = 38;
                    evt._keyChar = '&';
                } else {
                    evt.key = 55;
                    evt._keyChar = '7';
                }
                break;
            case KeyEvent.KEYCODE_8:
                evt._awtKey = 56;
                if (shift) {
                    evt.key = 42;
                    evt._keyChar = '*';
                } else {
                    evt.key = 56;
                    evt._keyChar = '8';
                }
                break;
            case KeyEvent.KEYCODE_9:
                evt._awtKey = 57;
                if (shift) {
                    evt.key = 40;
                    evt._keyChar = '(';
                } else {
                    evt.key = 57;
                    evt._keyChar = '9';
                }
                break;
            case KeyEvent.KEYCODE_SEMICOLON:
                evt._awtKey = 59;
                if (shift) {
                    evt.key = 58;
                    evt._keyChar = ':';
                } else {
                    evt.key = 59;
                    evt._keyChar = ';';
                }
                break;
            case KeyEvent.KEYCODE_EQUALS:
                evt._awtKey = 61;
                if (shift) {
                    evt.key = 43;
                    evt._keyChar = '+';
                } else {
                    evt.key = 61;
                    evt._keyChar = '=';
                }
                break;
            case KeyEvent.KEYCODE_AT:
                evt._awtKey = 50;
                evt.key = 64;
                evt._keyChar = '@';
                break;
            case KeyEvent.KEYCODE_PLUS:
                evt._awtKey = 61;
                evt.key = 43;
                evt._keyChar = '+';
                break;
            case KeyEvent.KEYCODE_LEFT_BRACKET:
                evt._awtKey = 91;
                if (shift) {
                    evt.key = 123;
                    evt._keyChar = '{';
                } else {
                    evt.key = 91;
                    evt._keyChar = '[';
                }
                break;
            case KeyEvent.KEYCODE_BACKSLASH:
                evt._awtKey = 92;
                if (shift) {
                    evt.key = 124;
                    evt._keyChar = '|';
                } else {
                    evt.key = 92;
                    evt._keyChar = '\\';
                }
                break;
            case KeyEvent.KEYCODE_RIGHT_BRACKET:
                evt._awtKey = 93;
                if (shift) {
                    evt.key = 125;
                    evt._keyChar = '}';
                } else {
                    evt.key = 93;
                    evt._keyChar = ']';
                }
                break;
            case KeyEvent.KEYCODE_GRAVE:
                evt._awtKey = 192;
                if (shift) {
                    evt.key = 126;
                    evt._keyChar = '~';
                } else {
                    evt.key = 96;
                    evt._keyChar = '`';
                }
                break;
            case KeyEvent.KEYCODE_A:
                evt._awtKey = 65;
                if (shift) {
                    evt.key = 65;
                    evt._keyChar = 'A';
                } else {
                    evt.key = 97;
                    evt._keyChar = 'a';
                }
                break;
            case KeyEvent.KEYCODE_B:
                evt._awtKey = 66;
                if (shift) {
                    evt.key = 66;
                    evt._keyChar = 'B';
                } else {
                    evt.key = 98;
                    evt._keyChar = 'b';
                }
                break;
            case KeyEvent.KEYCODE_C:
                evt._awtKey = 67;
                if (shift) {
                    evt.key = 67;
                    evt._keyChar = 'C';
                } else {
                    evt.key = 99;
                    evt._keyChar = 'c';
                }
                break;
            case KeyEvent.KEYCODE_D:
                evt._awtKey = 68;
                if (shift) {
                    evt.key = 68;
                    evt._keyChar = 'D';
                } else {
                    evt.key = 100;
                    evt._keyChar = 'd';
                }
                break;
            case KeyEvent.KEYCODE_E:
                evt._awtKey = 69;
                if (shift) {
                    evt.key = 69;
                    evt._keyChar = 'E';
                } else {
                    evt.key = 101;
                    evt._keyChar = 'e';
                }
                break;
            case KeyEvent.KEYCODE_F:
                evt._awtKey = 70;
                if (shift) {
                    evt.key = 70;
                    evt._keyChar = 'F';
                } else {
                    evt.key = 102;
                    evt._keyChar = 'f';
                }
                break;
            case KeyEvent.KEYCODE_G:
                evt._awtKey = 71;
                if (shift) {
                    evt.key = 71;
                    evt._keyChar = 'G';
                } else {
                    evt.key = 103;
                    evt._keyChar = 'g';
                }
                break;
            case KeyEvent.KEYCODE_H:
                evt._awtKey = 72;
                if (shift) {
                    evt.key = 72;
                    evt._keyChar = 'H';
                } else {
                    evt.key = 104;
                    evt._keyChar = 'h';
                }
                break;
            case KeyEvent.KEYCODE_I:
                evt._awtKey = 73;
                if (shift) {
                    evt.key = 73;
                    evt._keyChar = 'I';
                } else {
                    evt.key = 105;
                    evt._keyChar = 'i';
                }
                break;
            case KeyEvent.KEYCODE_J:
                evt._awtKey = 74;
                if (shift) {
                    evt.key = 74;
                    evt._keyChar = 'J';
                } else {
                    evt.key = 106;
                    evt._keyChar = 'j';
                }
                break;
            case KeyEvent.KEYCODE_K:
                evt._awtKey = 75;
                if (shift) {
                    evt.key = 75;
                    evt._keyChar = 'K';
                } else {
                    evt.key = 107;
                    evt._keyChar = 'k';
                }
                break;
            case KeyEvent.KEYCODE_L:
                evt._awtKey = 76;
                if (shift) {
                    evt.key = 76;
                    evt._keyChar = 'L';
                } else {
                    evt.key = 108;
                    evt._keyChar = 'l';
                }
                break;
            case KeyEvent.KEYCODE_M:
                evt._awtKey = 77;
                if (shift) {
                    evt.key = 77;
                    evt._keyChar = 'M';
                } else {
                    evt.key = 109;
                    evt._keyChar = 'm';
                }
                break;
            case KeyEvent.KEYCODE_N:
                evt._awtKey = 78;
                if (shift) {
                    evt.key = 78;
                    evt._keyChar = 'N';
                } else {
                    evt.key = 110;
                    evt._keyChar = 'n';
                }
                break;
            case KeyEvent.KEYCODE_O:
                evt._awtKey = 79;
                if (shift) {
                    evt.key = 79;
                    evt._keyChar = 'O';
                } else {
                    evt.key = 111;
                    evt._keyChar = 'o';
                }
                break;
            case KeyEvent.KEYCODE_P:
                evt._awtKey = 80;
                if (shift) {
                    evt.key = 80;
                    evt._keyChar = 'P';
                } else {
                    evt.key = 112;
                    evt._keyChar = 'p';
                }
                break;
            case KeyEvent.KEYCODE_Q:
                evt._awtKey = 81;
                if (shift) {
                    evt.key = 81;
                    evt._keyChar = 'Q';
                } else {
                    evt.key = 113;
                    evt._keyChar = 'q';
                }
                break;
            case KeyEvent.KEYCODE_R:
                evt._awtKey = 82;
                if (shift) {
                    evt.key = 82;
                    evt._keyChar = 'R';
                } else {
                    evt.key = 114;
                    evt._keyChar = 'r';
                }
                break;
            case KeyEvent.KEYCODE_S:
                evt._awtKey = 83;
                if (shift) {
                    evt.key = 83;
                    evt._keyChar = 'S';
                } else {
                    evt.key = 115;
                    evt._keyChar = 's';
                }
                break;
            case KeyEvent.KEYCODE_T:
                evt._awtKey = 84;
                if (shift) {
                    evt.key = 84;
                    evt._keyChar = 'T';
                } else {
                    evt.key = 116;
                    evt._keyChar = 't';
                }
                break;
            case KeyEvent.KEYCODE_U:
                evt._awtKey = 85;
                if (shift) {
                    evt.key = 85;
                    evt._keyChar = 'U';
                } else {
                    evt.key = 117;
                    evt._keyChar = 'u';
                }
                break;
            case KeyEvent.KEYCODE_V:
                evt._awtKey = 86;
                if (shift) {
                    evt.key = 86;
                    evt._keyChar = 'V';
                } else {
                    evt.key = 118;
                    evt._keyChar = 'v';
                }
                break;
            case KeyEvent.KEYCODE_W:
                evt._awtKey = 87;
                if (shift) {
                    evt.key = 87;
                    evt._keyChar = 'W';
                } else {
                    evt.key = 119;
                    evt._keyChar = 'w';
                }
                break;
            case KeyEvent.KEYCODE_X:
                evt._awtKey = 88;
                if (shift) {
                    evt.key = 88;
                    evt._keyChar = 'X';
                } else {
                    evt.key = 120;
                    evt._keyChar = 'x';
                }
                break;
            case KeyEvent.KEYCODE_Y:
                evt._awtKey = 89;
                if (shift) {
                    evt.key = 89;
                    evt._keyChar = 'Y';
                } else {
                    evt.key = 121;
                    evt._keyChar = 'y';
                }
                break;
            case KeyEvent.KEYCODE_Z:
                evt._awtKey = 90;
                if (shift) {
                    evt.key = 90;
                    evt._keyChar = 'Z';
                } else {
                    evt.key = 122;
                    evt._keyChar = 'z';
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                evt.key = 1004; // up
                evt._awtKey = 38;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                evt.key = 1005; // down
                evt._awtKey = 40;
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                evt.key = 1006; // left
                evt._awtKey = 37;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                evt.key = 1007; // right
                evt._awtKey = 39;
                break;
            case KeyEvent.KEYCODE_F1:
                evt.key = 1008;
                evt._awtKey = 112;
                break;
            case KeyEvent.KEYCODE_F2:
                evt.key = 1009;
                evt._awtKey = 113;
                break;
            case KeyEvent.KEYCODE_F3:
                evt.key = 1010;
                evt._awtKey = 114;
                break;
            case KeyEvent.KEYCODE_F4:
                evt.key = 1011;
                evt._awtKey = 115;
                break;
            case KeyEvent.KEYCODE_F5:
                evt.key = 1012;
                evt._awtKey = 116;
                break;
            case KeyEvent.KEYCODE_F6:
                evt.key = 1013;
                evt._awtKey = 117;
                break;
            case KeyEvent.KEYCODE_F7:
                evt.key = 1014;
                evt._awtKey = 118;
                break;
            case KeyEvent.KEYCODE_F8:
                evt.key = 1015;
                evt._awtKey = 119;
                break;
            case KeyEvent.KEYCODE_F9:
                evt.key = 1016;
                evt._awtKey = 120;
                break;
            case KeyEvent.KEYCODE_F10:
                evt.key = 1017;
                evt._awtKey = 121;
                break;
            case KeyEvent.KEYCODE_F11:
                evt.key = 1018;
                evt._awtKey = 122;
                break;
            case KeyEvent.KEYCODE_F12:
                evt.key = 1019;
                evt._awtKey = 123;
                break;
            case KeyEvent.KEYCODE_SHIFT_LEFT:
            case KeyEvent.KEYCODE_SHIFT_RIGHT:
                evt._awtKey = 16;
                break;
            case KeyEvent.KEYCODE_CTRL_LEFT:
            case KeyEvent.KEYCODE_CTRL_RIGHT:
                evt._awtKey = 17;
                break;
            case KeyEvent.KEYCODE_ALT_LEFT:
            case KeyEvent.KEYCODE_ALT_RIGHT:
                evt._awtKey = 18;
                break;
        }
        return evt;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT)
            shiftPressed = true;
        if (keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT)
            altShiftPressed = true;
        if (keyCode == KeyEvent.KEYCODE_CTRL_LEFT)
            ctrlPressed = true;
        if (keyCode == KeyEvent.KEYCODE_CTRL_RIGHT)
            altCtrlPressed = true;
        if (keyCode == KeyEvent.KEYCODE_ALT_LEFT)
            altPressed = true;
        if (keyCode == KeyEvent.KEYCODE_ALT_RIGHT)
            altAltPressed = true;

        if (DalvikJVM.getTarget() == null)
            return super.onKeyDown(keyCode, event);

        keyPressed[keyCode] = true;

        Event evt = convertAndroidKeycode(keyCode, Event.KEY_PRESS, event.isShiftPressed());
        addEventQueue(evt);

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT)
            shiftPressed = false;
        if (keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT)
            altShiftPressed = false;
        if (keyCode == KeyEvent.KEYCODE_CTRL_LEFT)
            ctrlPressed = false;
        if (keyCode == KeyEvent.KEYCODE_CTRL_RIGHT)
            altCtrlPressed = false;
        if (keyCode == KeyEvent.KEYCODE_ALT_LEFT)
            altPressed = false;
        if (keyCode == KeyEvent.KEYCODE_ALT_RIGHT)
            altAltPressed = false;

        if (DalvikJVM.getTarget() == null)
            return super.onKeyUp(keyCode, event);

        int releaseOffset = 0;
        if (!keyPressed[keyCode]) {
            Event evt = convertAndroidKeycode(keyCode, Event.KEY_PRESS, event.isShiftPressed());
            addEventQueue(evt);
            releaseOffset += KEYBOARD_DELAY;
        }
        Event evt = convertAndroidKeycode(keyCode, Event.KEY_RELEASE, event.isShiftPressed());
        evt.when += releaseOffset;
        addEventQueue(evt);

        return super.onKeyUp(keyCode, event);
    }
}