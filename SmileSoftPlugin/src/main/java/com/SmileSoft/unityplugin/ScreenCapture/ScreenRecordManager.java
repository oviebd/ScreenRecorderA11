package com.SmileSoft.unityplugin.ScreenCapture;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class ScreenRecordManager {

    static ScreenRecordManager instance;
    private Context context;
    private MediaRecorder recorder;
    private MediaProjection mediaProjection;
    private MediaProjectionManager projectionManager;
    private VirtualDisplay virtualDisplay;
    private DisplayMetrics displayMetrics;
    private String appDir, gameObject, methodName;
    private String filePath;

    private int bitRate = 0, fps = 0;
    private boolean isVideoRecording = false;
    private int resolution_width = 0, resolution_height = 0;
    private int videoEncoder = -2;
    private int videoFormat = -2;
    private boolean canRecordAudio = false;
    private boolean canAddIntoGallery = true;
    private boolean isVideoStoredInPrivateLocation = false;
    private String folderName, fileName;

    public static ScreenRecordManager instance() {
        if (instance == null) {
            instance = new ScreenRecordManager();
        }
        return instance;
    }

    public void SetRecorder(Context context) {
        this.context = context;

        displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(this.displayMetrics);
    }

    public MediaProjectionManager GetProjectionManager() {
        if (projectionManager == null)
            projectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        return projectionManager;
    }


    public MediaProjection GetMediaProjection() {
        return mediaProjection;
    }

    public void SetMediaProjection(int resultCode, Intent data) {
        mediaProjection = GetProjectionManager().getMediaProjection(resultCode, data);
    }

    public void StartScreenRecord() {

        if (isVideoRecording == true)
            return;

        try {
            InitializeRecorder();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (GetProjectionManager() != null) {
            this.virtualDisplay = createVirtualDisplay();

            if(this.virtualDisplay != null){
                recorder.start();
                isVideoRecording = true;
            }else {
                Log.e("UNITY>>", "Virtal display Null");
               // OnErrorRecordVideo();
            }

        }
    }

    private VirtualDisplay createVirtualDisplay() {

        if(mediaProjection == null)
            Log.e("UNITY>>", "Media projection Null");
        else
            Log.e("UNITY>>", "Media projection Not Null Null");

        if(displayMetrics == null)
            Log.e("UNITY>>", "Display Matrix Null");
        else
            Log.e("UNITY>>", "Display Matrix Not Null");
        try {
            return mediaProjection.createVirtualDisplay("record", displayMetrics.widthPixels, displayMetrics.heightPixels, displayMetrics.densityDpi, 16, recorder
                    .getSurface(), null, null);
        } catch (Exception e) {
             Log.e("UNITY>>", "Catch in Display");
            //OnErrorRecordVideo();
            return null;
        }
    }

    public String StopScreenRecord() {

        if (isVideoRecording == false)
            return null;

        if (this.virtualDisplay != null) {
            this.virtualDisplay.release();
        }

        if (this.mediaProjection != null) {
            this.mediaProjection.stop();
            this.mediaProjection = null;
        }

        try {
            this.recorder.stop();
        } catch (RuntimeException stopException) {
            //ShowToast("Can not record in stop record");
        }
        this.recorder.release();
        isVideoRecording = false;
        this.recorder = null;
        if(canAddIntoGallery == true){
            addRecordingToMediaLibrary();
        }

        //UnityPlayer.UnitySendMessage("Screen Recorder", "OnVideoRecordCompleted", this.filePath);

        stopService();
        return this.filePath;
    }

    private String SetFilePath() {

        if (this.folderName == null || this.folderName == "")
            folderName = context.getPackageName();

        if(this.appDir == null || this.appDir == ""){
            File appDir = new File(Environment.getExternalStorageDirectory() + "/" + folderName);
            if (!appDir.exists())
                appDir.mkdir();

            this.appDir = appDir.getAbsolutePath();
        }

        if (this.fileName == null || this.fileName == "")
            this.fileName = "_Screen_Record";

        return SetVideoFileName(this.fileName);
    }
   /* private String SetFilePath() {

        if (this.folderName == null || this.folderName == "")
            folderName = context.getPackageName();

       *//* if(this.appDir == null || this.appDir == ""){
            File appDir = new File(Environment.getExternalStorageDirectory() + "/" + folderName);
            if (!appDir.exists())
                appDir.mkdir();

            this.appDir = appDir.getAbsolutePath();
        }*//*

        if (this.fileName == null || this.fileName == "")
            this.fileName = "_Screen_Record";

        return SetVideoFileName(this.fileName);
    }*/

    public void InitializeRecorder() throws IOException {

        recorder = new MediaRecorder();

        if (this.canRecordAudio)
            this.recorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        this.recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE); //2

        if (this.videoFormat < 0)
            this.recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); //2
        else
            this.recorder.setOutputFormat(this.videoFormat);

        if (this.videoEncoder < 0)
            this.recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264); //2
        else
            this.recorder.setVideoEncoder(this.videoEncoder);

        if (this.canRecordAudio)
            this.recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


        if (bitRate == 0)
            this.recorder.setVideoEncodingBitRate(4000000);
        else
            this.recorder.setVideoEncodingBitRate(this.bitRate);

        if (this.fps == 0)
            this.recorder.setVideoFrameRate(30);
        else
            this.recorder.setVideoFrameRate(this.fps);

        if (this.resolution_height == 0 && this.resolution_width == 0)
            this.recorder.setVideoSize(this.displayMetrics.widthPixels, this.displayMetrics.heightPixels);
        else
            this.recorder.setVideoSize(this.resolution_width, this.resolution_height);

        recorder.setOutputFile(SetFilePath());

        this.recorder.prepare();
    }


    public void SetAudioCapabilities(boolean canRecordAudio) {
        this.canRecordAudio = canRecordAudio;
    }

    public void SetVideoOutputFormat(int format) {
        this.videoFormat = format;
    }

    public void SetVideoEncoder(int encoder) {
        this.videoEncoder = encoder;
    }

    public void SetBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public void SetFps(int fps) {
        this.fps = fps;
    }

    public void SetVideoSize(int width, int height) {
        this.resolution_width = width;
        this.resolution_height = height;
    }

    public void SetGalleryAddingCapabilities(boolean canAddIntoGallery) {
        this.canAddIntoGallery = canAddIntoGallery;
    }

    public void SetVideoStoringDestination(String destination) {
        this.appDir = destination;
    }
    public void SetStoredFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String SetVideoFileName(String fileName) {
        this.fileName = fileName;
        //this.filePath = (this.appDir + "/" + fileName + ".mp4");
        this.filePath =  fileName + ".mp4";
        return this.filePath;
    }

    //this func is used by Unity side to set callback when record status changed
    public void setCallback(String gameObject, String methodName) {
        this.gameObject = gameObject;
        this.methodName = methodName;
    }

    //this func move the recorded video to gallery
    private void addRecordingToMediaLibrary() {
        ContentValues values = new ContentValues(3);
        values.put("title", this.fileName);
        values.put("mime_type", "video/mp4");
        values.put("_data", this.filePath);
        context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }

    public void PlayVideo(String videoPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(videoPath), "video/*");
        context.startActivity(intent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(context, ScreenRecorderForegroundService.class);
        context.stopService(serviceIntent);
    }

    public void ShowToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
