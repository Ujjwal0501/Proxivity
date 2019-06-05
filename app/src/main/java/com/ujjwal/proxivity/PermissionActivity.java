package com.ujjwal.proxivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.media.projection.MediaProjectionManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.ujjwal.proxivity.ScreenshotService;

import java.io.IOException;
import java.security.Key;

import static com.ujjwal.proxivity.ScreenshotService.metrics;

public class PermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get permission to capture screen
        MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Log.d("Snap", "ask permission");
        startActivityForResult(projectionManager.createScreenCaptureIntent(), 55555);

        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("input keyevent 3");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 55555 && resultCode == RESULT_OK) {

            // get permission to capture screen
            MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            ScreenshotService.data = (Intent) data.clone();
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            ScreenshotService.display = wm.getDefaultDisplay();
            metrics = new DisplayMetrics();
            ScreenshotService.display.getMetrics(metrics);
            Point size = new Point();
            ScreenshotService.display.getRealSize(size);
            ScreenshotService.mWidth = size.x;
            ScreenshotService.mHeight = size.y;
            ScreenshotService.mDensity = metrics.densityDpi;
            ScreenshotService.flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
            ScreenshotService.capture();
            this.moveTaskToBack(true);
        }
    }
}
