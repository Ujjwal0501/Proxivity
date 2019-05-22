package com.ujjwal.proxivity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Testing";
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private Sensor accelerometerSensor;
    private SensorEventListener accelerometerSensorListener;
    private TextView textView1;
    private Button run;
    private Button stop;
    private Intent serviceIntent;
    private final int REQUEST_CODE = 1;
    Display display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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

        run.setEnabled(false);
        stop.setEnabled(true);

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
                    startService(serviceIntent);
                    run.setEnabled(false);
                    stop.setEnabled(true);
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(serviceIntent);
                run.setEnabled(true);
                stop.setEnabled(false);
            }
        });

        accelerometerSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                textView1.setText(event.sensor.getName());
                String string = "\n"+event.accuracy+"\n";
                for (int i = 0; i < event.values.length; i++)
                    string += event.values[i]+"\n";
                textView1.append(string);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sensorManager.registerListener(accelerometerSensorListener, accelerometerSensor,
                SensorManager.SENSOR_DELAY_UI);

        run.performClick();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(accelerometerSensorListener, accelerometerSensor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accelerometerSensorListener, accelerometerSensor,
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_CANCELED) Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        else if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            startService(serviceIntent);
            run.setEnabled(false);
            stop.setEnabled(true);
        }
    }
}
