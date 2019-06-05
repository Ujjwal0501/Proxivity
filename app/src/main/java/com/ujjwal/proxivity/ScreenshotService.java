package com.ujjwal.proxivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.ujjwal.proxivity.receivers.PermissionCheckReceiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ScreenshotService extends ScreenOnOffService {

    public static Display display;
    public static int mWidth, mHeight, mDensity, flags;
    public static DisplayMetrics metrics;
    public static final Handler handler = new Handler();
    public static MediaProjection mProjection = null;
    public static Intent data = null;
    public static String FILE_LOCATION;
    static MediaProjectionManager mProjectionManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        FILE_LOCATION = getExternalFilesDir(null).toString();
        super.onCreate();
//        Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show();
//        builder.setContentIntent(PendingIntent.getBroadcast(this, 0, new Intent(this, PermissionCheckReceiver.class), 0));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @TargetApi(21)
    public static void capture() {

        try {
            System.out.println("capturing");
            final ImageReader mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
            mProjection = mProjectionManager.getMediaProjection(Activity.RESULT_OK, data);

            mProjection.createVirtualDisplay("proxivity", mWidth, mHeight, mDensity, flags, mImageReader.getSurface(), null, handler);
            Log.d("Snap", "Virtual display created");

            mProjection.stop();
            Log.d("Snap", "projection stopped");

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

                    image.close();
                    reader.close();
                    Log.d("Snap", "Bitmap created");

                    Bitmap realSizeBitmap = Bitmap.createBitmap(bmp, 0, 0, metrics.widthPixels, bmp.getHeight());
                    bmp.recycle();

                    /* do something with [realSizeBitmap] */
                    Log.d("Snap", "Showing screenshot");

                    // save image to file
                    Log.d("Snap", "Saving image to file");
                    saveImage(realSizeBitmap);

                }
            }, handler);
            Log.d("Snap", "New image listener attached");

//        return mImageReader;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void saveImage(Bitmap bmp) {
        File file = new File(FILE_LOCATION, System.currentTimeMillis()+".png");
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
