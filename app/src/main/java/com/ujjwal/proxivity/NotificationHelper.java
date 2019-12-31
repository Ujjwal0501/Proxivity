package com.ujjwal.proxivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.ujjwal.proxivity.receivers.CloseServiceReceiver;
import com.ujjwal.proxivity.receivers.DecrementReceiver;
import com.ujjwal.proxivity.receivers.IncrementReceiver;
import com.ujjwal.proxivity.receivers.PermissionCheckReceiver;
import com.ujjwal.proxivity.receivers.SnoozeReceiver;
import com.ujjwal.proxivity.receivers.StartNowReceiver;

public class NotificationHelper {
    public static RemoteViews notificationLayout;

    public static NotificationCompat.Builder build(Context context) {
        notificationLayout = new RemoteViews(context.getPackageName(), R.layout.notification_layout);

        return init(context);
    }

    public static void createFeatureChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.main_channel);
            String description = context.getString(R.string.main_channel_description);
            String channel_id = context.getString(R.string.main_channel_id);
            int importance = NotificationManager.IMPORTANCE_NONE;
            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void createScreenshotChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.screenshot_channel);
            String description = context.getString(R.string.screenshot_channel_description);
            String channel_id = context.getResources().getString(R.string.screenshot_channel_id);
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static NotificationCompat.Builder init(Context context) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getResources().getString(R.string.main_channel_id))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Proxivity")
                .setContentText("Control the background service.")
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setCustomContentView(notificationLayout)
                .setSound(null)
                .setOngoing(true);
        return builder;
    }

    public static void addAction(Context context) {

        // add functionality to the notification buttons
        notificationLayout.setOnClickPendingIntent(R.id.start,
                PendingIntent.getBroadcast(context, 0, new Intent(context, StartNowReceiver.class), 0));
        notificationLayout.setOnClickPendingIntent(R.id.snooze,
                PendingIntent.getBroadcast(context, 0, new Intent(context, SnoozeReceiver.class), 0));
        notificationLayout.setOnClickPendingIntent(R.id.inc,
                PendingIntent.getBroadcast(context, 0, new Intent(context, IncrementReceiver.class), 0));
        notificationLayout.setOnClickPendingIntent(R.id.dec,
                PendingIntent.getBroadcast(context, 0, new Intent(context, DecrementReceiver.class), 0));
        notificationLayout.setOnClickPendingIntent(R.id.exit,
                PendingIntent.getBroadcast(context, 0, new Intent(context, CloseServiceReceiver.class), 0));
        notificationLayout.setOnClickPendingIntent(R.id.shoot,
                PendingIntent.getBroadcast(context, 0, new Intent(context, PermissionCheckReceiver.class), 0));
    }
}
