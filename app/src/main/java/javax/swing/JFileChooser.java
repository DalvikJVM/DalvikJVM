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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import com.dalvikjvm.DalvikJVM;

import java.awt.*;
import java.io.File;

public class JFileChooser extends JComponent {
    public static final int ERROR_OPTION = -1;
    public static final int APPROVE_OPTION = 0;
    public static final int CANCEL_OPTION = 1;

    public static final int FILES_ONLY = 0;
    public static final int DIRECTORIES_ONLY = 1;
    public static final int FILES_AND_DIRECTORIES = 2;

    public static JFileChooser instance;
    private boolean waiting;

    private int selectionMode;
    private File selectedFile;
    private int returnCode;

    public static String getPath(final Context context, final Uri uri) {
        String path = uri.toString();
        String filePath = "";

        if (!path.startsWith("content://"))
            return null;

        System.out.println("URI: " + path);

        if (path.contains("com.android.externalstorage.documents")) {
            int index = path.indexOf('%') + 3;
            filePath = path.substring(index);
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        // Sanitize
        filePath = filePath .replaceAll("%2F", "/")
                            .replaceAll("%20", " ");

        String fullPath = path + File.separator + filePath;

        System.out.println("getPath(): " + fullPath);

        return fullPath;
    }

    public void _setResult(Uri uri) {
        if (uri == null) {
            returnCode = CANCEL_OPTION;
            selectedFile = null;
        } else {
            returnCode = APPROVE_OPTION;
            String path = getPath(DalvikJVM.instance, uri);
            if (path != null)
                selectedFile = new File(path);
            else
                selectedFile = null;
        }
        waiting = false;
    }

    public JFileChooser(String currentDirectoryPath) {
        System.out.println("Unimplemented method JFileChooser.<init>(" + currentDirectoryPath + ") called");
    }

    public void setFileSelectionMode(int mode) {
        selectionMode = mode;
    }

    public int showDialog(Component parent, String approveButtonText) {
        String dialogAction = "";
        switch(selectionMode) {
            case FILES_ONLY:
                dialogAction = Intent.ACTION_GET_CONTENT;
                break;
            case DIRECTORIES_ONLY:
                dialogAction = Intent.ACTION_OPEN_DOCUMENT_TREE;
                break;
        }

        Intent intent = new Intent(dialogAction);

        switch(selectionMode) {
            case FILES_ONLY:
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                break;
            case DIRECTORIES_ONLY:
                break;
        }

        waiting = true;

        try {
            instance = this;
            DalvikJVM.instance.startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), DalvikJVM._FILE_SELECT_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR_OPTION;
        }

        // Wait on file selection
        while (waiting) {
            try { Thread.sleep(10); } catch (Exception e) {}
        }

        return returnCode;
    }

    public File getSelectedFile() {
        return selectedFile;
    }
}
