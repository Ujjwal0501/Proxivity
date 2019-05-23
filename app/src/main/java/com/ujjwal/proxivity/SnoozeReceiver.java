package com.ujjwal.proxivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class SnoozeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Toast.makeText(context, "Service snoozed for "+ ScreenOnOffService.seconds + " seconds.", Toast.LENGTH_SHORT).show();
        ScreenOnOffService.sensorManager.unregisterListener(ScreenOnOffService.proximitySensorListener, ScreenOnOffService.proximitySensor);
        ScreenOnOffService.sensorManager.unregisterListener(ScreenOnOffService.accelerometerSensorListener, ScreenOnOffService.accelerometerSensor);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d("Proxivity", "Back on track.");
                ScreenOnOffService.sensorManager.registerListener(ScreenOnOffService.proximitySensorListener, ScreenOnOffService.proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
                ScreenOnOffService.sensorManager.registerListener(ScreenOnOffService.accelerometerSensorListener, ScreenOnOffService.accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        };

        Timer timer = new Timer("ProxivityTimer");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, ScreenOnOffService.seconds);
        timer.schedule(timerTask, cal.getTime());
    }
}
