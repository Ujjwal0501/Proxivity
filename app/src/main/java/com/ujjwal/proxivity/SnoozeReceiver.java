package com.ujjwal.proxivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SnoozeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "You snoozed the service.", Toast.LENGTH_SHORT).show();
        ScreenOnOffService.sensorManager.unregisterListener(ScreenOnOffService.proximitySensorListener, ScreenOnOffService.proximitySensor);
        ScreenOnOffService.sensorManager.unregisterListener(ScreenOnOffService.accelerometerSensorListener, ScreenOnOffService.accelerometerSensor);
    }
}
