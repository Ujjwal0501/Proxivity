package com.ujjwal.proxivity.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ujjwal.proxivity.PermissionActivity;
import com.ujjwal.proxivity.ScreenshotService;

public class PermissionCheckReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ScreenshotService.data == null || ScreenshotService.mProjection == null) {
            context.startActivity(new Intent(context, PermissionActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            ScreenshotService.capture();
        }
    }
}
