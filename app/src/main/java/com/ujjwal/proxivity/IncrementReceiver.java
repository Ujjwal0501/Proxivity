package com.ujjwal.proxivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class IncrementReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ScreenOnOffService.seconds += 30;
//        Toast.makeText(context, "The value is: "+ScreenOnOffService.seconds, Toast.LENGTH_SHORT).show();
        NotificationHelper.notificationLayout.setCharSequence(R.id.snooze, "setText", ScreenOnOffService.seconds+" seconds");
        ScreenOnOffService.addAction(context);
        ScreenOnOffService.notificationManagerCompat.notify(1155555, NotificationHelper.init(context).build());
    }
}
