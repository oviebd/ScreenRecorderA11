package com.SmileSoft.unityplugin;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileUtils {

    public String CreateFolder(String folderName) {

        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/" + folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String folderPath = folder.toString();
        Log.e("UNITY>>" , "Android : folder path : " + folderPath);
        return folderPath;
    }

  /*  public String GetFoldetPath(String folderName) {
        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/" + folderName);
        if (!folder.exists()) {
            //  folder.mkdirs();
        }
        String folderPath = folder.toString();
        return folderPath;
    }*/
}
