package com.ujjwal.proxivity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class FloatingActions extends Service {

    boolean activity_background;
    private ImageView controller;
    private WindowManager mWindowManager;
    private View mOverlayView;
    private boolean CLOSE = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            activity_background = intent.getBooleanExtra("activity_background", false);

        }

        if (mOverlayView == null) {

            mOverlayView = LayoutInflater.from(this).inflate(R.layout.floating_actions, null);


            final WindowManager.LayoutParams params;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            else
                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);

            //Specify the view position
            params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
            params.x = 0;
            params.y = 100;


            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mOverlayView, params);

            Display display = mWindowManager.getDefaultDisplay();
            final Point size = new Point();
            display.getSize(size);


            controller = mOverlayView.findViewById(R.id.controller);

            controller.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (CLOSE) {
                        stopSelf();
                        return true;
                    }
                    return false;
                }
            });

            controller.setOnTouchListener(new View.OnTouchListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            CLOSE = true;

                            //remember the initial position.
                            initialX = params.x;
                            initialY = params.y;

                            //get the touch location
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();

                            return false;
                        case MotionEvent.ACTION_UP:
                            CLOSE = false;

                            if (Math.round(event.getRawX() - initialTouchX) < 5 || Math.round(event.getRawY() - initialTouchY) < 5)
                                adjustVolume();
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            CLOSE = false;

                            int xDiff = Math.round(event.getRawX() - initialTouchX);
                            int yDiff = Math.round(event.getRawY() - initialTouchY);


                            //Calculate the X and Y coordinates of the view.
                            params.x = initialX + xDiff;
                            params.y = initialY + yDiff;

                            //Update the layout with new X & Y coordinates
                            mWindowManager.updateViewLayout(mOverlayView, params);


                            return true;
                    }
                    return false;
                }
            });
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setTheme(R.style.AppTheme);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOverlayView != null)
            mWindowManager.removeView(mOverlayView);
    }

    private void adjustVolume() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (audioManager != null)
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
    }
}