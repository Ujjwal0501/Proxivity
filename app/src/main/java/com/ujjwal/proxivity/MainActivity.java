package com.ujjwal.proxivity;

import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Testing";
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private Sensor accelerometerSensor;
    private SensorEventListener accelerometerSensorListener;
    private SensorEventListener proximitySensorListener;
    private TextView textView;
    private TextView textView1;
    private Button run;
    private Button stop;
    private Intent serviceIntent;
    Display display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        textView = (TextView) findViewById(R.id.result);
        textView1 = (TextView) findViewById(R.id.result1);
        run = (Button) findViewById(R.id.run);
        stop = (Button) findViewById(R.id.stop);
        serviceIntent = new Intent(getApplicationContext(), MyService.class);
        display = getWindowManager().getDefaultDisplay();

        if (proximitySensor == null) {
            Toast.makeText(this, "Proximity sensor unavailable.", Toast.LENGTH_SHORT).show();
        }
        if (accelerometerSensor == null) {
            Toast.makeText(this, "Accelerometer sensor unavailable.", Toast.LENGTH_SHORT).show();
        }

        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(serviceIntent);
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(serviceIntent);
            }
        });
        proximitySensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                textView.setText(event.sensor.getName());
                if (event.values[0] < proximitySensor.getMaximumRange()) {
                    String string = "\n"+event.accuracy+"\n";
                    for (int i = 0; i < event.values.length; i++)
                        string += event.values[i];
                    textView.append(string);
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                } else {
                    String string = "\n"+event.accuracy+"\n";
                    for (int i = 0; i < event.values.length; i++)
                        string += event.values[i];
                    textView.append(string);
                    getWindow().getDecorView().setBackgroundColor(Color.RED);
                }

//                sensorManager.unregisterListener(proximitySensorListener, proximitySensor);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        accelerometerSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                textView1.setText(event.sensor.getName());
                String string = "\n"+event.accuracy+"\n";
                for (int i = 0; i < event.values.length; i++)
                    string += event.values[i]+"\n";
                textView1.append(string);
                if (event.values[1] < 0) {
//                    DevicePolicyManager policyManager = (DevicePolicyManager) getApplicationContext()
//                            .getSystemService(Context.DEVICE_POLICY_SERVICE);
//                    ComponentName adminReceiver = new ComponentName(getApplicationContext(),
//                            ScreenOffAdminReceiver.class);
//                    boolean admin = policyManager.isAdminActive(adminReceiver);
//                    if (admin) {
//                        Log.i(TAG, "Going to sleep now.");
//                        policyManager.lockNow();
//                    } else {
//                        Log.i(TAG, "Not an admin");
//                        Toast.makeText(getApplicationContext(), "Not an admin",
//                                Toast.LENGTH_SHORT).show();
//                    }
                    //Get the window from the context
//                    WindowManager wm = Context.getSystemService(Context.WINDOW_SERVICE);

                    //Lock device
//                    DevicePolicyManager mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
                }

//                sensorManager.unregisterListener(accelerometerSensorListener, accelerometerSensor);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(proximitySensorListener, proximitySensor,
                SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(accelerometerSensorListener, accelerometerSensor,
                SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(proximitySensorListener, proximitySensor);
        sensorManager.unregisterListener(accelerometerSensorListener, accelerometerSensor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(proximitySensorListener, proximitySensor,
                SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(accelerometerSensorListener, accelerometerSensor,
                SensorManager.SENSOR_DELAY_UI);
    }
}
