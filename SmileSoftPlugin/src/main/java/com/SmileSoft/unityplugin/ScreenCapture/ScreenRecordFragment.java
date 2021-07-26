package com.SmileSoft.unityplugin.ScreenCapture;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.SmileSoft.unityplugin.RuntimePermissionChecker;
import com.unity3d.player.UnityPlayer;

import java.io.File;

public class ScreenRecordFragment extends Fragment {

    private String TAG = "ScreenRecord_Fragment";
    private static final int REQUEST_SCREEN_RECORD_PERMISSION = 20;

    private static ScreenRecordFragment instance;
    public String[] screenRecordPermissions;
    private boolean canRecordAudio = false;

    Activity _activity;
    Context _context;


    //Called From Unity - Add thia fragment with Unity Activity and set callbacks
   /* public void SetUp() {
        UnityPlayer.currentActivity.getFragmentManager().beginTransaction().add(this, TAG).commit();
    }*/
    public void SetUp(Activity activity) {
       // Log.e("UNITY>>", "Inside SetUp Context is " + activity );
       // this._context = activity;
        activity.getFragmentManager().beginTransaction().add(this, TAG).commit();
       // ScreenRecordManager.instance().SetRecorder(_context);
    }


    public static ScreenRecordFragment instance() {
        if (instance == null) {
            instance = new ScreenRecordFragment();
        }
        return instance;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("UNITY>>", " Onactivity REs" + resultCode);
        if (requestCode == REQUEST_SCREEN_RECORD_PERMISSION) {
          /*  if (resultCode != -1) {
                // RecordManager.instance().ShowToast("Cannot record");
                //UnityPlayer.UnitySendMessage(this.mGameObject, this.mMethodName, "OnError");
                return;
            }*/
            RunRecordingBasedOnAndroidVersion(resultCode, data);
            //UnityPlayer.UnitySendMessage(this.mGameObject, this.mMethodName, "OnStartRecording");
        }
    }

    public void RunRecordingBasedOnAndroidVersion(int resultCode, Intent data) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent service = new Intent(_activity, ScreenRecorderForegroundService.class);
            service.putExtra("code", resultCode);
            service.putExtra("data", data);
            _activity.startForegroundService(service);
        } else {
            ScreenRecordManager.instance().SetMediaProjection(resultCode, data);
            ScreenRecordManager.instance().StartScreenRecord();
        }
    }

    //StartScreenRecord Call from Unity =--------------------------------------------

    public void StartRecording() {
        SetPermission();
        boolean hasPermission = RuntimePermissionChecker.instance().CheckPermissionFromFragment
                (screenRecordPermissions, REQUEST_SCREEN_RECORD_PERMISSION, _activity, this);
        if (hasPermission)
            ShowScreenRecordingPermissionPopUp();
    }

    public String StopRecording() {
        return ScreenRecordManager.instance().StopScreenRecord();
    }

    public void SetAudioCapabilities(boolean canRecordAudio) {
        this.canRecordAudio = canRecordAudio;
        ScreenRecordManager.instance().SetAudioCapabilities(canRecordAudio);
    }

    public void SetVideoName(String name) {
        ScreenRecordManager.instance().SetVideoFileName(name);
    }
    public void SetVideoStoredFolderName(String folderName) {
        ScreenRecordManager.instance().SetStoredFolderName(folderName);
    }
    public void SetVideoStoringDestination(String destination) {
        ScreenRecordManager.instance().SetVideoStoringDestination(destination);
    }

    public void SetGalleryAddingCapabilities(boolean canAddIntoGallery) {
        ScreenRecordManager.instance().SetGalleryAddingCapabilities(canAddIntoGallery);
    }
    public void SetBitrate(int bitRate) {
        ScreenRecordManager.instance().SetBitRate(bitRate);
    }

    public void SetVideoFps(int fps) {
        ScreenRecordManager.instance().SetFps(fps);
    }

    public void SetVideoSize(int width, int height) {
        ScreenRecordManager.instance().SetVideoSize(width, height);
    }

    public void SetVideoOutputFormat(int outputFormat) {
        ScreenRecordManager.instance().SetVideoOutputFormat(outputFormat);
    }

    public void SetVideoEncoder(int videoEncoder) {
        ScreenRecordManager.instance().SetVideoEncoder(videoEncoder);
    }

    public void PreviewVideo(String videoPath) {
        ScreenRecordManager.instance().PlayVideo(videoPath);
    }

    public void ShareVideo(String filePath, String message, String title, String fileProviderName) {

        final File photoFile = new File(filePath);
        Uri fileUri = null;
        fileUri = FileProvider.getUriForFile(UnityPlayer.currentActivity,
                fileProviderName,
                photoFile);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("video/*");
        UnityPlayer.currentActivity.startActivity(Intent.createChooser(shareIntent, title));
        Log.e("UNITY>>", "Share Single  file Called a " + getActivity());
    }


    //End Call from Unity =--------------------------------------------

    public void ShowScreenRecordingPermissionPopUp() {
        if (ScreenRecordManager.instance().GetMediaProjection() == null) {
            this.startActivityForResult(ScreenRecordManager.instance().GetProjectionManager().createScreenCaptureIntent(), REQUEST_SCREEN_RECORD_PERMISSION);
            return;
        }
    }

    private void SetPermission() {
        if (this.canRecordAudio)
            screenRecordPermissions = RuntimePermissionChecker.instance().
                    screenRecordPermissionWithAudio;
        else
            screenRecordPermissions = RuntimePermissionChecker.instance().
                    screenRecordPermissionWithoutAudio;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean hasPermission = RuntimePermissionChecker.instance().CheckResultedPermission(screenRecordPermissions, _activity);
        if (hasPermission)
            ShowScreenRecordingPermissionPopUp();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this._activity = getActivity();
        this._context = getContext();

        Log.e("UNITY>>", "Inside On Atach Context is " + _context );
        ScreenRecordManager.instance().SetRecorder(_context);
    }
}
