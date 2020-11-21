package com.SmileSoft.PluginTest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.SmileSoft.unityplugin.RuntimePermissionChecker;

public class OpenGalleryActivity extends AppCompatActivity {

    private static final int REQUEST_FOR_SINGLE_ITEM_PICKING = 1;
    private static final int REQUEST_FOR_MULTIPLE_ITEM_PICKING = 2;
    private static final int REQUEST_GALLERY_OPEN = 3;

    //GalleryFrag gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_gallery);

        //gallery = new GalleryFrag();
       // gallery.SetUp(this,"Gobj","callback");
    }


   /* public void PickSingleItem(View view) {
        gallery.PickSingleItemFromGallery();
    }

    public void PickMultipleItem(View view) {
        gallery.PickMultipleItemFromGallery();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean hasPermission = RuntimePermissionChecker.instance().CheckResultedPermission(RuntimePermissionChecker.instance().
                storaegPermissionList, this);
        if (hasPermission) {
            switch (requestCode) {
                case REQUEST_FOR_SINGLE_ITEM_PICKING:
                    gallery.PickSingleItemFromGallery();
                    break;
                case REQUEST_FOR_MULTIPLE_ITEM_PICKING:
                    gallery.PickMultipleItemFromGallery();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("UNITY>>" , "APP On Activity res : " + requestCode);
        String pathFiles = "";
        String separator = "<Separate01234>";
        if (requestCode == REQUEST_GALLERY_OPEN && resultCode == RESULT_OK && data != null) {

            //Select Single Item
            if (data.getData() != null) {

                Uri fileUri = data.getData();
                String path_string = GetActualPath.getPath(this, fileUri);
                pathFiles =   path_string + separator;
                //  UnityPlayer.UnitySendMessage(_gameObject, _callback, resultCode == RESULT_OK ? path_string : "");
            }
            //For mUltiple Items
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    String path_string = GetActualPath.getPath(this, uri)+ separator;
                    pathFiles = pathFiles+ path_string;
                }
            }

            Log.e("UNITY>>" , "APP Activity res : " + pathFiles);
           // UnityPlayer.UnitySendMessage(_gameObject, _callback_gallery,pathFiles);
        }
    }
*/
}