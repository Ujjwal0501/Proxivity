package com.ujjwal.proxivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DecrementReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ScreenOnOffService.seconds > 30) ScreenOnOffService.seconds -= 30;
//        Toast.makeText(context, "The time is: "+ScreenOnOffService.seconds, Toast.LENGTH_SHORT).show();
        NotificationHelper.notificationLayout.setCharSequence(R.id.snooze, "setText", ScreenOnOffService.seconds+" seconds");
        ScreenOnOffService.addAction(context);
        ScreenOnOffService.notificationManagerCompat.notify(1155555, NotificationHelper.init(context).build());
    }
}
