package com.ujjwal.proxivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, MyService.class);
        context.startService(serviceIntent);
        Toast.makeText(context, context.getString(R.string.app_name)+" service started successfully!", Toast.LENGTH_SHORT).show();
    }
}
