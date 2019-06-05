package com.ujjwal.proxivity;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ujjwal.proxivity.receivers.ScreenOffAdminReceiver;
import com.ujjwal.proxivity.receivers.ScreenshotReceiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Testing";
    private TextView textView1;
    private Button run;
    private Button stop;
    private Intent serviceIntent;
    private final int REQUEST_CODE = 1;
    private Button take;
    MediaProjection mProjection;
    Display display;
    ImageView imageView;
    int mWidth, mHeight, mDensity, flags;
    final Handler handler = new Handler();
    ImageReader imageReader;

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
        serviceIntent = new Intent(getApplicationContext(), ScreenshotReceiver.class);
        display = getWindowManager().getDefaultDisplay();
        take = findViewById(R.id.take);
        imageView = findViewById(R.id.project);

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

                    // get permission to capture screen
//                    MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//                    Log.d("Snap", "ask permission");
//                    startActivityForResult(projectionManager.createScreenCaptureIntent(), 55555);
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

        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageBitmap(null);
//                Snackbar.make(v, "taking screenshot...", Snackbar.LENGTH_SHORT).show();
                Log.d("Snap", "Buttong clicked");

                MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                Log.d("Snap", "ask permission");
                startActivityForResult(projectionManager.createScreenCaptureIntent(), 5555);
//                mProjection = projectionManager.getMediaProjection(RESULT_OK, new Intent());
//                createImageReader();

//                new Runnable() {
//                    @Override@TargetApi(21)
//                    public void run() {
//                        MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//                        Log.d("Snap", "ask permission");
//                        startActivityForResult(projectionManager.createScreenCaptureIntent(), 55555);
//                    }
//                };
            }
        });


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
//                run();
//                run.setEnabled(false);
//                stop.setEnabled(true);

                // get permission to capture screen
                MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                Log.d("Snap", "ask permission");
                startActivityForResult(projectionManager.createScreenCaptureIntent(), 55555);
            } else if (requestCode == 55555 && resultCode == RESULT_OK && Build.VERSION.SDK_INT > 20) {
                // enable shoot button
                ((Button) ((LinearLayout) stop.getParent()).getChildAt(((LinearLayout) stop.getParent()).indexOfChild(stop)+1)).setEnabled(true);

//                Log.d("Snap", "permission granted");
//                MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//                Log.i("Snap", "action: "+data.getAction()+"-datastring"+data.getDataString()+"-package"+data.getPackage()+"-component"+data.getComponent()+"-class"+data.getClass());
//                mProjection = projectionManager.getMediaProjection(resultCode, data);
//                Log.d("Snap", "MediaProjection obtained");

                run.setEnabled(false);
                stop.setEnabled(true);

                if (!isMyServiceRunning(ScreenshotService.class)) {
                    new Thread() {
                        @Override
                        public void run() {
                            startService(new Intent(getApplicationContext(), ScreenshotService.class).putExtra("data", data));
                        }
                    }.start();
                    Snackbar.make(textView1, "Service started successfully.", Snackbar.LENGTH_SHORT).show();

                    // clone the data to use it in the service
//                    MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//                    ScreenshotService.mProjection = projectionManager.getMediaProjection(resultCode, data);
//                    ScreenshotService.data = (Intent) data.clone();

                    this.finish();
                } else {
                    Snackbar.make(textView1, "Service already running.", Snackbar.LENGTH_SHORT).show();
                }

            } else if (requestCode == 55555 && resultCode != RESULT_OK) {
                run();
                run.setEnabled(false);
                stop.setEnabled(true);
            } else if (requestCode == 5555 && resultCode == RESULT_OK && Build.VERSION.SDK_INT > 20) {
                Log.d("Snap", "permission granted");
                MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                Log.i("Snap", "action: "+data.getAction()+"-datastring"+data.getDataString()+"-package"+data.getPackage()+"-component"+data.getComponent()+"-class"+data.getClass());
                mProjection = projectionManager.getMediaProjection(resultCode, data);
                Log.d("Snap", "MediaProjection obtained");
                try {
                    Thread.sleep(90);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                createImageReader();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run() {
//        if (is)
        System.out.println(Thread.currentThread().getId());
        if (!isMyServiceRunning(ScreenOnOffService.class)) {
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

    @TargetApi(21)
    private void createImageReader() {

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        Point size = new Point();
        display.getRealSize(size);
        mWidth = size.x;
        mHeight = size.y;
        mDensity = metrics.densityDpi;

        final ImageReader mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);

        flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
        mProjection.createVirtualDisplay("screen-mirror", mWidth, mHeight, mDensity, flags, mImageReader.getSurface(), null, handler);
        Log.d("Snap", "Virtual display created");

        mProjection.stop();
        Log.d("Snap", "projection stopped");
//        if (Build.VERSION.SDK_INT < 19) return;
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Log.d("Snap", "Image available now");
                reader.setOnImageAvailableListener(null, handler);

                Image image = reader.acquireLatestImage();
                Log.d("Snap", "Got the image");

                final Image.Plane[] planes = image.getPlanes();
                final ByteBuffer buffer = planes[0].getBuffer();

                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * metrics.widthPixels;
                // create bitmap
                Bitmap bmp = Bitmap.createBitmap(metrics.widthPixels + (int) ((float) rowPadding / (float) pixelStride), metrics.heightPixels, Bitmap.Config.ARGB_8888);
                bmp.copyPixelsFromBuffer(buffer);

                // save image to file
                saveImage(bmp);

                image.close();
                reader.close();
                Log.d("Snap", "Bitmap created");

                Bitmap realSizeBitmap = Bitmap.createBitmap(bmp, 0, 0, metrics.widthPixels, bmp.getHeight());
                bmp.recycle();

                /* do something with [realSizeBitmap] */
                Log.d("Snap", "Showing screenshot");
                imageView.setImageBitmap(realSizeBitmap);
                saveImage(realSizeBitmap);


            }
        }, handler);
        Log.d("Snap", "New image listener attached");

//        return mImageReader;
    }

    @TargetApi(21)
    public void takeNow(View v){
        mProjection.createVirtualDisplay("screen-mirror", mWidth, mHeight, mDensity, flags, imageReader.getSurface(), null, handler);
        Log.d("Snap", "Virtual display created");

        mProjection.stop();
        Log.d("Snap", "projection stopped");
        v.setEnabled(false);
    }

    private void saveImage(Bitmap bmp) {
        File file = new File(getExternalFilesDir(null), System.currentTimeMillis()+".png");
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
