/*
 * Copyright (C) 2015 Jan "KekS" M.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package de.hsmainz.gi.indoornavcl.util;

import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * @author  justin o'dwyer
 * @see     <a href="https://github.com/justinodwyer/Beacon-Scanner-and-Logger/blob/master/src/net/jmodwyer/beacon/beaconPoC/FileHelper.java">github</a>
 * @version 03.02.2015
 *
 * @author  KekS (mailto:keks@keksfabrik.eu), 03.02.2015
 */
public class FileHelper {

    private static final String TAG = "FileHelper";
    private OutputStreamWriter  osw;
    private File                extStorage;
    private final String        FILENAME_PREFIX = "pos";

    /**
     * Initialise extStorage using the reference passed in.
     *
     * @param   file        external {@link java.io.File}
     */
    public FileHelper(File file) {
        extStorage = file;
    }

    /**
     * Create a file using the String passed in as content.
     *
     * @param   fileData    String representing the data we want to write to a file.
     */
    public void createFile(String fileData) {
        try {
            if (isExternalStorageAvailableAndWritable()) {
                String now = (DateFormat.format("dd-MM-yyyy_HH-mm-ss", new java.util.Date()).toString());
                File file = new File(extStorage, FILENAME_PREFIX + "_" + now + ".json");
                FileOutputStream fos = new FileOutputStream(file);
                osw = new OutputStreamWriter(fos);
                osw.write(fileData);
                osw.flush();
            }
        } catch (IOException e) {
            Log.w(TAG, "Problem writing to file", e);
            e.printStackTrace();
        } finally {
            try {
                osw.close();
            } catch (IOException e) {
                Log.w(TAG, "Problem closing file", e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Delete the file referenced by the path passed in.
     *
     * @param   path    String representing the path to the file.
     * @return  {@link java.lang.Boolean#TRUE} if deletion was successful, {@link java.lang.Boolean#FALSE} otherwise.
     */
    public boolean deleteFile(String path) {
        File file = new File(path);
        return file.delete();
    }

    /**
     * Return the file referenced by the path passed in.
     *
     * @param   path    String representing the path to the file.
     * @return  the {@link java.io.File} at the location designated by the path String passed in.
     */
    public File getFile(String path) {
        File file = new File(path);
        return file;
    }


    /**
     * List the paths of all files in the app's external storage directory.
     *
     * @return  {@link java.util.ArrayList} containing all discovered file paths.
     */
    public ArrayList<String> listFiles() {
        ArrayList<String> paths = new ArrayList<>();
        File[] fileList = extStorage.listFiles();
        for (File file : fileList) {
            if (file.isFile()) {
                paths.add(file.getPath());
            }
        }
        return paths;
    }

    /**
     * Is external storage available and can we write to it?
     *
     * @return  {@link java.lang.Boolean#TRUE} if storage is available and writeable, {@link java.lang.Boolean#FALSE} otherwise.
     */
    public static boolean isExternalStorageAvailableAndWritable() {
        boolean externalStorageAvailable = false;
        boolean externalStorageWritable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            //---you can read and write the media---
            externalStorageAvailable = externalStorageWritable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            //---you can only read the media---
            externalStorageAvailable = true;
            externalStorageWritable = false;
        } else {
            //---you cannot read nor write the media---
            externalStorageAvailable = externalStorageWritable = false;
        }
        return externalStorageAvailable && externalStorageWritable;
    }

}
