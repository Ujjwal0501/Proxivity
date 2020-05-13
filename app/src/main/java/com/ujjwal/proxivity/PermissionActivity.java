package com.ujjwal.proxivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class PermissionActivity extends AppCompatActivity {
    private final int STORAGE_PERMISSION = 112, SCREENCAPTURE_PERMISSION = 113;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!ScreenshotService.FILE_LOCATION.exists()) startActivityForResult(new Intent(this, PermissionStore.class), STORAGE_PERMISSION);
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(new Intent(this, PermissionStore.class), STORAGE_PERMISSION);
        } else {

            // get permission to capture screen
            MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            Log.d("Snap", "ask permission");
            startActivityForResult(projectionManager.createScreenCaptureIntent(), SCREENCAPTURE_PERMISSION);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCREENCAPTURE_PERMISSION) {

            if (resultCode == RESULT_OK) {
                // get permission to capture screen
                ScreenshotService.data = (Intent) data.clone();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ScreenshotService.capture();
                    }
                }, 900);
            } else {
                Toast.makeText(this, "Capture permission denied.", Toast.LENGTH_LONG).show();
            }
            finish();

        } else if (requestCode == STORAGE_PERMISSION) {

            ScreenshotService.FILE_LOCATION.mkdirs();
            if (resultCode == RESULT_OK) {
                // get permission to capture screen
                MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                Log.d("Snap", "ask permission");
                startActivityForResult(projectionManager.createScreenCaptureIntent(), SCREENCAPTURE_PERMISSION);
            } else {
                Toast.makeText(this, "Storage permission required for capturing.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
