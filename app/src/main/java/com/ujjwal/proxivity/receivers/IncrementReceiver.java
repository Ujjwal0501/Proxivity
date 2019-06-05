package com.ujjwal.proxivity.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ujjwal.proxivity.NotificationHelper;
import com.ujjwal.proxivity.R;
import com.ujjwal.proxivity.ScreenOnOffService;

public class IncrementReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ScreenOnOffService.seconds += 30;
//        Toast.makeText(context, "The value is: "+ScreenOnOffService.seconds, Toast.LENGTH_SHORT).show();
        NotificationHelper.notificationLayout.setCharSequence(R.id.snooze, "setText", ScreenOnOffService.seconds+" seconds");
        NotificationHelper.addAction(context);
        ScreenOnOffService.notificationManagerCompat.notify(1155555, NotificationHelper.init(context).build());
    }
}
