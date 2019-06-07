package com.ujjwal.proxivity;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class PermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get permission to capture screen
        MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Log.d("Snap", "ask permission");
        startActivityForResult(projectionManager.createScreenCaptureIntent(), 55555);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 55555 && resultCode == RESULT_OK) {

            // get permission to capture screen
            ScreenshotService.data = (Intent) data.clone();
        }

        this.finish();
    }
}
