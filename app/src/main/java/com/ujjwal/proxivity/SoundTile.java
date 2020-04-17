package com.ujjwal.proxivity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

@TargetApi(24)
public class SoundTile extends TileService {
    @Override
    public void onClick() {
        super.onClick();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (audioManager != null)
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
    }
}
