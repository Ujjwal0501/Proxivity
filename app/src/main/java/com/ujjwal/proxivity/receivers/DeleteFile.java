package com.ujjwal.proxivity.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.File;

public class DeleteFile extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("id", 0);

        if (id != 0) {
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(id);
            File file = new File(intent.getStringExtra("path"));
            if (file.exists()) {
                if (file.delete())
                    Toast.makeText(context, "File deleted.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "Cannot delete.", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(context, "File does not exist.", Toast.LENGTH_SHORT).show();
        }
    }
}
