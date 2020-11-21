package com.SmileSoft.unityplugin;

import android.Manifest;
import android.app.Activity;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;


public class RuntimePermissionChecker {

    public String[] screenRecordPermissionWithoutAudio = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public String[] screenRecordPermissionWithAudio = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    public String[] storaegPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    // public String[] storaegPermissionList = new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION};
    public String[] cameraPluginPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private static RuntimePermissionChecker instance;


    public static RuntimePermissionChecker instance() {
        if (instance == null) {
            instance = new RuntimePermissionChecker();
        }
        return instance;
    }

    public boolean CheckPermissionFromActivity(final String[] permissionList, int requestCode, Activity context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        boolean result = CheckPermission(permissionList, context);
        if (result == false)
            context.requestPermissions(permissionList, requestCode);
        return result;

    }

    public boolean CheckPermissionFromFragment(final String[] permissionList, int requestCode, Activity context, Fragment fragment) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        boolean result = CheckPermission(permissionList, context);
        if (result == false)
            fragment.requestPermissions(permissionList, requestCode);
        return result;
    }

    private boolean CheckPermission(final String[] permissionList, Activity context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return IsAllPermissionGranted(permissionList, context);
    }

    public boolean CheckResultedPermission(String[] permissions, Activity context) {
        return IsAllPermissionGranted(permissions, context);
    }
    private boolean IsAllPermissionGranted(String[] permissions, Activity context) {

        Log.e("Unity>> p ", "context : " + context + " pList " + permissions);
        for (String permission : permissions) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }
}
