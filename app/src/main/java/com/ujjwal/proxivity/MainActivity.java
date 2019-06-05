package com.ujjwal.proxivity;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ujjwal.proxivity.receivers.ScreenOffAdminReceiver;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Testing";
    private TextView textView1;
    private Button run;
    private Button stop;
    private final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        textView1 = (TextView) findViewById(R.id.result1);
        run = (Button) findViewById(R.id.run);
        stop = (Button) findViewById(R.id.stop);

        if (proximitySensor == null) {
            Toast.makeText(this, "Proximity sensor unavailable.", Toast.LENGTH_SHORT).show();
        }
        if (accelerometerSensor == null) {
            Toast.makeText(this, "Accelerometer sensor unavailable.", Toast.LENGTH_SHORT).show();
        }

        if (isMyServiceRunning(ScreenOnOffService.class)) {
            run.setEnabled(false);
            stop.setEnabled(true);
        } else {
            stop.setEnabled(false);
            run.setEnabled(true);
        }

        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevicePolicyManager policyManager = (DevicePolicyManager) getApplicationContext()
                        .getSystemService(Context.DEVICE_POLICY_SERVICE);
                ComponentName adminReceiver = new ComponentName(getApplicationContext(),
                        ScreenOffAdminReceiver.class);
                boolean admin = policyManager.isAdminActive(adminReceiver);

                if (!admin) {
                    Log.i(TAG, "Not an admin");
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                            adminReceiver);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "ANY EXTRA DESCRIPTION");
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    run();

                    run.setEnabled(false);
                    stop.setEnabled(true);
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(), ScreenOnOffService.class));
                stopService(new Intent(getApplicationContext(), ScreenshotService.class));
                run.setEnabled(true);
                stop.setEnabled(false);
                Snackbar.make(textView1, "Service is stopped.", Snackbar.LENGTH_SHORT).show();
            }
        });

        run.performClick();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CODE && resultCode == RESULT_CANCELED)
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            else if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
                run();
                run.setEnabled(false);
                stop.setEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run() {
//        if (is)
        System.out.println(Thread.currentThread().getId());
        if (!isMyServiceRunning(ScreenshotService.class)) {
            new Thread() {
                @Override
                public void run() {
                    startService(new Intent(getApplicationContext(), ScreenshotService.class));
                }
            }.start();
            Snackbar.make(textView1, "Service started successfully.", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(textView1, "Service already running.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

/**
 *
 05-25 16:59:12.076 30611-30611/com.ujjwal.proxivity D/Snap: permission granted
 05-25 16:59:12.082 30611-30611/com.ujjwal.proxivity D/Snap: MediaProjection obtained
 05-25 16:59:12.174 30611-30611/com.ujjwal.proxivity I/BufferQueue: [unnamed-30611-0](this:0xb445a400,id:0,api:0,p:-1,c:-1) BufferQueue core=(30611:com.ujjwal.proxivity)
 05-25 16:59:12.175 30611-30611/com.ujjwal.proxivity I/BufferQueueConsumer: [unnamed-30611-0](this:0xb445a400,id:0,api:0,p:-1,c:30611) connect(C): consumer=(30611:com.ujjwal.proxivity) controlledByApp=true
 05-25 16:59:12.176 30611-30611/com.ujjwal.proxivity I/BufferQueueConsumer: [unnamed-30611-0](this:0xb445a400,id:0,api:0,p:-1,c:30611) setConsumerName: unnamed-30611-0
 05-25 16:59:12.176 30611-30611/com.ujjwal.proxivity I/BufferQueueConsumer: [unnamed-30611-0](this:0xb445a400,id:0,api:0,p:-1,c:30611) setDefaultBufferSize: width=720 height=1280
 05-25 16:59:12.183 30611-30611/com.ujjwal.proxivity D/Snap: Virtual display created
 05-25 16:59:12.185 30611-30611/com.ujjwal.proxivity D/Snap: New image listener attached
 05-25 16:59:12.192 30611-30611/com.ujjwal.proxivity D/Snap: projection stopped
 05-25 16:59:12.192 30611-30611/com.ujjwal.proxivity D/ActivityThread: SEND_RESULT handled : 0 / ResultData{token=android.os.BinderProxy@2e0bf5f6 results[ResultInfo{who=null, request=55555, result=-1, data=Intent { (has extras) }}]}
 05-25 16:59:12.193 30611-30611/com.ujjwal.proxivity D/FeatureProxyBase: FeatureProxyBase class constructor
 05-25 16:59:12.194 30611-30611/com.ujjwal.proxivity D/MultiWindow: MultiWindowProxy constructor.
 05-25 16:59:12.193 30611-30611/com.ujjwal.proxivity D/ActivityThread: ACT-AM_ON_RESUME_CALLED ActivityRecord{14be8291 token=android.os.BinderProxy@2e0bf5f6 {com.ujjwal.proxivity/com.ujjwal.proxivity.MainActivity}}
 05-25 16:59:12.194 30611-30611/com.ujjwal.proxivity D/FeatureProxyBase: getService(), serviceName = multiwindow_service_v1
 05-25 16:59:12.195 30611-30611/com.ujjwal.proxivity D/ActivityThread: ACT-RESUME_ACTIVITY handled : 0 / android.os.BinderProxy@2e0bf5f6
 05-25 16:59:12.203 30611-30629/com.ujjwal.proxivity I/BufferQueueProducer: [unnamed-30611-0](this:0xb445a400,id:0,api:1,p:200,c:30611) connect(P): api=1 producer=(200:/system/bin/surfaceflinger) producerControlledByApp=false
 05-25 16:59:12.204 30611-30628/com.ujjwal.proxivity I/BufferQueueProducer: [unnamed-30611-0](this:0xb445a400,id:0,api:1,p:200,c:30611) setBufferCount: count = 5
 05-25 16:59:12.204 30611-30628/com.ujjwal.proxivity I/BufferQueueConsumer: [unnamed-30611-0](this:0xb445a400,id:0,api:1,p:200,c:30611) getReleasedBuffers: returning mask 0xffffffffffffffff
 05-25 16:59:12.207 30611-30629/com.ujjwal.proxivity I/BufferQueueProducer: [unnamed-30611-0](this:0xb445a400,id:0,api:1,p:200,c:30611) new GraphicBuffer needed
 05-25 16:59:12.209 30611-30629/com.ujjwal.proxivity D/GraphicBuffer: register, handle(0xb46fb0a0) (w:720 h:1280 s:720 f:0x1 u:0x080803)
 05-25 16:59:12.228 30611-30611/com.ujjwal.proxivity D/Snap: Image available now
 05-25 16:59:12.231 30611-30629/com.ujjwal.proxivity I/BufferQueueProducer: [unnamed-30611-0](this:0xb445a400,id:0,api:1,p:-1,c:30611) disconnect(P): api 1
 05-25 16:59:12.239 30611-30629/com.ujjwal.proxivity I/BufferQueueConsumer: [unnamed-30611-0](this:0xb445a400,id:0,api:1,p:-1,c:30611) getReleasedBuffers: returning mask 0xffffffffffffffff
 05-25 16:59:12.239 30611-30611/com.ujjwal.proxivity D/Snap: Got the image
 05-25 16:59:12.345 30611-30611/com.ujjwal.proxivity I/BufferQueueConsumer: [unnamed-30611-0](this:0xb445a400,id:0,api:1,p:-1,c:-1) disconnect(C)
 05-25 16:59:12.345 30611-30611/com.ujjwal.proxivity D/GraphicBuffer: unregister, handle(0xb46fb0a0) (w:720 h:1280 s:720 f:0x1 u:0x080803)
 05-25 16:59:12.346 30611-30611/com.ujjwal.proxivity D/Snap: Bitmap created
 05-25 16:59:12.357 30611-30611/com.ujjwal.proxivity D/Snap: Showing screenshot
 *
 */
