package com.ujjwal.proxivity.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ujjwal.proxivity.PermissionActivity;
import com.ujjwal.proxivity.ScreenshotService;

public class PermissionCheckReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        if (ScreenshotService.data == null) {
            System.out.println("Preparing to shoot");
            context.startActivity(new Intent(context, PermissionActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            try {
                Thread.sleep(900);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Ready to shoot");
            ScreenshotService.capture();
        }
    }
}
