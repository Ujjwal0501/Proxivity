package com.ujjwal.proxivity.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

public class OpenFile extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("id", 0);

        if (id != 0) {
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(id);

            Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/Pictures/Screenshots/");
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(i, "Open folder").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            //        intent.setDataAndType(uri, "resource/folder");
            //        context.startActivity(i);
        }
    }
}
