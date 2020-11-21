package com.SmileSoft.unityplugin.Gallery;


import android.app.Fragment;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;

import com.SmileSoft.unityplugin.GetActualPath;
import com.SmileSoft.unityplugin.RuntimePermissionChecker;
import com.unity3d.player.UnityPlayer;

import static android.app.Activity.RESULT_OK;

public class GalleryFragment extends Fragment {

    private String TAG = "Gallery_Fragment";
    private static final int REQUEST_FOR_SINGLE_ITEM_PICKING = 1;
    private static final int REQUEST_FOR_MULTIPLE_ITEM_PICKING = 2;
    private static final int REQUEST_GALLERY_OPEN = 3;

    String _gameObject;
    String _callback_gallery;

    //Called From Unity - Add thia fragment with Unity Activity and set callbacks
    public void SetUp(String gameObject, String callback) {
        UnityPlayer.currentActivity.getFragmentManager().beginTransaction().add(this, TAG).commit();
        this._gameObject = gameObject;
        this._callback_gallery = callback;
    }


    public void PickSingleItemFromGallery() {
        boolean hasPermission = RuntimePermissionChecker.instance().CheckPermissionFromFragment(RuntimePermissionChecker.instance().
                storaegPermissionList, REQUEST_FOR_SINGLE_ITEM_PICKING, getActivity(), this);

        if (hasPermission) {
            OpenGallery(false);
        }
    }

    public void PickMultipleItemFromGallery() {

        boolean hasPermission = RuntimePermissionChecker.instance().CheckPermissionFromFragment(RuntimePermissionChecker.instance().
                storaegPermissionList, REQUEST_FOR_MULTIPLE_ITEM_PICKING, getActivity(), this);

        if (hasPermission) {
            OpenGallery(true);
        }
    }

    private void OpenGallery(boolean IsMultipleItemPick) {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            intent.setType("image/* video/*");
        } else {
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        }

        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, IsMultipleItemPick);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        if (intent.resolveActivity(UnityPlayer.currentActivity.getPackageManager()) != null) {
            this.startActivityForResult(intent, REQUEST_GALLERY_OPEN);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermission = RuntimePermissionChecker.instance().CheckResultedPermission(RuntimePermissionChecker.instance().
                storaegPermissionList, getActivity());
        if (hasPermission) {
            switch (requestCode) {
                case REQUEST_FOR_SINGLE_ITEM_PICKING:
                    PickSingleItemFromGallery();
                    break;
                case REQUEST_FOR_MULTIPLE_ITEM_PICKING:
                    PickMultipleItemFromGallery();
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String pathFiles = "";
        String separator = "<Separate01234>";
        if (requestCode == REQUEST_GALLERY_OPEN && resultCode == RESULT_OK && data != null) {

            //Select Single Item
            if (data.getData() != null) {
                Uri fileUri = data.getData();
                //Log.e("UNITY>>", " uri : " + fileUri);
                // File file = new File(fileUri.getPath());//create path from uri
                // Log.e("UNITY>>", " file path : " + file.getAbsolutePath());
                String path_string = GetActualPath.getPath(getActivity(), fileUri);
                pathFiles = path_string + separator;
            }
            //For mUltiple Items
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();

                    String path_string = GetActualPath.getPath(getActivity(), uri) + separator;
                    pathFiles = pathFiles + path_string;
                }

            }
            //Log.e("UNITY>>", " Path is  : " + pathFiles);
            UnityPlayer.UnitySendMessage(this._gameObject, this._callback_gallery, pathFiles);
        }
    }
}
