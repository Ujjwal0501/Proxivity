package com.ujjwal.proxivity.receivers;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.ujjwal.proxivity.R;
import com.ujjwal.proxivity.ScreenOnOffService;
import com.ujjwal.proxivity.ScreenshotService;

public class CloseServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ProxivityNotificationChannel")
                .setAutoCancel(true)
                .setContentTitle("Proxivity")
                .setContentText("Tap here to start the Proxivity Background Service.")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentIntent(PendingIntent.getService(context, 0, new Intent(context, ScreenshotService.class), 0));

//        if (Build.VERSION.SDK_INT >= 21 ) builder.addInvisibleAction(R.drawable.ic_launcher_background, "Restart Service", PendingIntent.getService(context, 0, new Intent(context, ScreenshotService.class), 0));
//        else builder.addAction(R.drawable.ic_launcher_background, "Restart Service", PendingIntent.getService(context, 0, new Intent(context, ScreenshotService.class), 0));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        if (Build.VERSION.SDK_INT >= 17) builder.setShowWhen(false);
        notificationManagerCompat.notify(355555, builder.build());

        context.stopService(new Intent(context, ScreenOnOffService.class));
        context.stopService(new Intent(context, ScreenshotService.class));
    }
}
