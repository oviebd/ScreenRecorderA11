package com.SmileSoft.PluginTest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.SmileSoft.unityplugin.ScreenCapture.ScreenRecordFragment;

public class MainActivity extends AppCompatActivity {

    ScreenRecordFragment recorderFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recorderFrag = new ScreenRecordFragment();
        recorderFrag.SetUp(this);
        recorderFrag.SetGalleryAddingCapabilities(true);


        recorderFrag.SetVideoStoredFolderName("TestFolder");
        recorderFrag.SetBitrate(4000000);
        recorderFrag.SetVideoFps(30);
        recorderFrag.SetAudioCapabilities(true);

        recorderFrag.SetVideoEncoder(2);//H264

        //recorderFrag.SetUp();
        // UnityRecorder.instance().SetContext(this);
    }

    public void Start(View view) {
        recorderFrag.StartRecording();
        //UnityRecorder.instance().StartRecording();
    }

    public void End(View view) {

        //UnityRecorder.instance().StopRecording();
        String path = recorderFrag.StopRecording();
        Toast.makeText(this, "Video Stored at : " + path, Toast.LENGTH_SHORT).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Log.e("UNITY>>", "Activity Result in Main " + resultCode);
       /* if (resultCode == -1) {
            return;
        }*/
        if (requestCode == 20) {

            recorderFrag.RunRecordingBasedOnAndroidVersion(resultCode, data);
        }
    }

   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean hasPermission = RuntimePermissionChecker.instance().CheckResultedPermission(RuntimePermissionChecker.instance().
                screenRecordPermissionWithAudio, this);
        if (hasPermission)
            UnityRecorder.instance().ShowPopUp();

        Log.e("UNITY>>", "Activity APp Permission Result axxess " + hasPermission);
    }*/
}
