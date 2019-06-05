package com.ujjwal.proxivity.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ujjwal.proxivity.PermissionActivity;
import com.ujjwal.proxivity.ScreenshotService;

public class PermissionCheckReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "hi", Toast.LENGTH_SHORT).show();
        if (ScreenshotService.data == null || ScreenshotService.mProjection == null) {
            System.out.println("Preparing to shoot");
            context.startActivity(new Intent(context, PermissionActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            System.out.println("Ready to shoot");
            ScreenshotService.capture();
        }
    }
}
