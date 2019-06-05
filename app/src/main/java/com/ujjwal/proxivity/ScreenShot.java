package com.ujjwal.proxivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.widget.Toast;

public class ScreenShot {
    static MediaProjection mediaProjection;
    static MediaProjectionManager mediaProjectionManager;
    static Intent captureIntent;
    static int REQUEST_CODE = 55555;

    @TargetApi(21)
    public void init(Context context) {
        mediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        captureIntent = mediaProjectionManager.createScreenCaptureIntent();
        if (captureIntent != null) {
//            context.startActivityForResult(intent, 55555);
//            mediaProjection = mediaProjectionManager.getMediaProjection(55555, captureIntent);
        } else {
            Toast.makeText(context, "Could not create screen capture Intent.", Toast.LENGTH_SHORT).show();
        }
    }
}
