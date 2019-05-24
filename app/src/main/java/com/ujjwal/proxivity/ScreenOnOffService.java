package com.ujjwal.proxivity;

import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class ScreenOnOffService extends Service {

    private final String TAG = "Testing";
    public static SensorManager sensorManager;
    public static Sensor proximitySensor;
    public static Sensor accelerometerSensor;
    public static SensorEventListener accelerometerSensorListener;
    public static SensorEventListener proximitySensorListener;
    public static boolean state = false;
    public static int seconds = 30;
    static NotificationManagerCompat notificationManagerCompat;
    NotificationCompat.Builder builder;
    Display display;
    PowerManager pm;
    PowerManager.WakeLock wl;
    int pflag = 0;

    class MyServiceBinder extends Binder {
        public ScreenOnOffService getService() {
            return ScreenOnOffService.this;
        }
    }

    private IBinder binder = new MyServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();

//        new Thread() { }.start();
        appendLog("\nonCreate is on ThreadID: " + Thread.currentThread().getId()+"");

        Log.d(TAG, "In-onCreate on threadID "+Thread.currentThread().getId());
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        NotificationHelper.createNotificationChannel(this);
        builder = NotificationHelper.build(this);
        notificationManagerCompat = NotificationManagerCompat.from(this);
//        notificationManagerCompat.notify(155555, builder.build());

        if (proximitySensor == null) {
            Toast.makeText(this, "Proximity sensor unavailable.", Toast.LENGTH_SHORT).show();
            stopSelf();
        }
        if (accelerometerSensor == null) {
            Toast.makeText(this, "Accelerometer unavailable.", Toast.LENGTH_SHORT).show();
            stopSelf();
        }

        proximitySensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.values[0] < proximitySensor.getMaximumRange())
                    pflag = 1;
                else pflag = 0;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        accelerometerSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values[1] < -0.8 && pflag == 1 && Build.VERSION.SDK_INT > 19 && display.getState() == Display.STATE_ON) {
                    DevicePolicyManager policyManager = (DevicePolicyManager) getApplicationContext()
                            .getSystemService(Context.DEVICE_POLICY_SERVICE);
                    ComponentName adminReceiver = new ComponentName(getApplicationContext(),
                            ScreenOffAdminReceiver.class);
                    boolean admin = policyManager.isAdminActive(adminReceiver);
                    if (admin) {
                        policyManager.lockNow();

                    } else {
                        Log.i(TAG, "Not an admin");
                    }
                }
                else if (event.values[1] < -2.8 && pflag == 0 && Build.VERSION.SDK_INT > 19 && display.getState() != Display.STATE_ON) {
                    wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "DEBUG: My Tag");
                    wl.acquire(1);
                    if (wl.isHeld()) wl.release();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sensorManager.registerListener(proximitySensorListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(accelerometerSensorListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "In onBind");
        // TODO: Return the communication channel to the service.
        return binder;
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(proximitySensorListener, proximitySensor);
        sensorManager.unregisterListener(accelerometerSensorListener, accelerometerSensor);
        Log.d(TAG, " In onDestroy");
//        notificationManagerCompat.cancel(155555);
        state = false;
        this.appendLog("Service Stopped:\n");
        super.onDestroy();
//        Toast.makeText(this, "Service stopped.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, " In onStartCommand");
        state = true;
        this.appendLog("onStart is on ThreadID: " + Thread.currentThread().getId() +"\nService Started:");

        addAction(this);
        startForeground(1155555, builder.build());
        return START_STICKY;
//        return S
    }

    public void appendLog(String... text) {
        File logFile = new File(getExternalFilesDir(null),"proxivity_log.txt");
//        System.out.println(getFilesDir());                      // /data/data/com.ujjwal.proxivity/files
//        System.out.println(getExternalFilesDir(null));     // /storage/emulated/0/Android/data/com.ujjwal.proxivity/files
//        System.out.println(getCacheDir());                      // /data/data/com.ujjwal.proxivity/cache
//        System.out.println(getExternalCacheDir());              // /storage/emulated/0/Android/data/com.ujjwal.proxivity/cache
//        System.out.println(Environment.getDataDirectory());         // /data
//        System.out.println(Environment.getDownloadCacheDirectory());    // /cache
//        System.out.println(Environment.getExternalStorageDirectory());  // /storage/emulated/0
//        System.out.println(Environment.getRootDirectory());             // /system
//        System.out.println(Environment.getExternalStoragePublicDirectory(null));

        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text[0] + " " + new Timestamp(System.currentTimeMillis()));
//            buf.append("\n" + text[1] + " ");
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void addAction(Context context) {

        // add functionality to the notification buttons
        NotificationHelper.notificationLayout.setOnClickPendingIntent(R.id.start,
                PendingIntent.getBroadcast(context, 0, new Intent(context, StartNowReceiver.class), 0));
        NotificationHelper.notificationLayout.setOnClickPendingIntent(R.id.snooze,
                PendingIntent.getBroadcast(context, 0, new Intent(context, SnoozeReceiver.class), 0));
        NotificationHelper.notificationLayout.setOnClickPendingIntent(R.id.inc,
                PendingIntent.getBroadcast(context, 0, new Intent(context, IncrementReceiver.class), 0));
        NotificationHelper.notificationLayout.setOnClickPendingIntent(R.id.dec,
                PendingIntent.getBroadcast(context, 0, new Intent(context, DecrementReceiver.class), 0));
        NotificationHelper.notificationLayout.setOnClickPendingIntent(R.id.exit,
                PendingIntent.getBroadcast(context, 0, new Intent(context, CloseServiceReceiver.class), 0));
    }
}
