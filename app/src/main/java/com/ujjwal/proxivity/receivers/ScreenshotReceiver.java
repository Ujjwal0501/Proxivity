package com.ujjwal.proxivity.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ujjwal.proxivity.ScreenshotService;

public class ScreenshotReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Snap", "taking screenshot....");
        ScreenshotService.capture();
    }
}
