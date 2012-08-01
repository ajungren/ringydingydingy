/*
 * This file is part of RingyDingyDingy.
 * Copyright (C) 2011-2012 Ayron Jungren
 *
 * RingyDingyDingy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License only.
 *
 * RingyDingyDingy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RingyDingyDingy.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dririan.RingyDingyDingy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

public class RemoteRingActivity extends Activity {
    public static final String PAGE_INTENT = "com.dririan.RingyDingyDingy.COMMAND_PAGE";
    public static final String RING_INTENT = "com.dririan.RingyDingyDingy.COMMAND_RING";
    private static AlertDialog alertDialog = null;
    public static AudioManager audioManager = null;
    public static Ringtone ringtone = null;
    public static int oldMode = 0;
    public static int oldVolume = 0;
    public static RemoteRingActivity _instance = null;

    public String source = "";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _instance = this;

        // Get the source of the message
        Intent intent = this.getIntent();
        if(intent != null && intent.hasExtra("source"))
            source = intent.getStringExtra("source");
        else
            source = "unknown";

        // Get the contact name, if available
        if(Build.VERSION.SDK_INT >= 5) {
            String[] contact = ContactSupport.lookupByNumber(this, source);
            if(contact[0] != null)
                source = contact[0];
        }

        // Prepare the AudioManager, set the ringer mode to normal, and set the volume to maximum
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        oldMode = audioManager.getRingerMode();
        oldVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

        // Get the current ringtone
        ringtone = RingtoneManager.getRingtone(this, Settings.System.DEFAULT_RINGTONE_URI);
        if(ringtone == null)
            ringtone = RingtoneManager.getRingtone(this, Settings.System.DEFAULT_NOTIFICATION_URI);

        // Play the ringtone
        ringtone.play();

        // Show an AlertDialog and stop the ringtone when the user hits Stop
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
               .setNeutralButton(R.string.remote_ring_stop_button, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       RemoteRingActivity.stopRinging();
                   }
               });

        if(intent.getAction().compareTo(PAGE_INTENT) == 0)
            builder.setMessage(this.getString(R.string.page_from) + " " + source + ":\n" + intent.getStringExtra("message"));
        else if(intent.getAction().compareTo(RING_INTENT) == 0)
            builder.setMessage(this.getString(R.string.remote_ring_text) + " " + source);

        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // If the activity gets closed for some random reason, stop ringing so we don't annoy the user
        stopRinging();
    }

    public static boolean stopRinging() {
        // Stop the bloody ringer and reset the settings
        boolean wasStopped = false;

        if(RemoteRingActivity.ringtone != null && RemoteRingActivity.audioManager != null) {
            if(ringtone.isPlaying()) {
                ringtone.stop();
                wasStopped = true;
            }
            audioManager.setRingerMode(oldMode);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, oldVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }

        if(RemoteRingActivity.alertDialog != null)
            RemoteRingActivity.alertDialog.dismiss();
        if(RemoteRingActivity._instance != null)
            RemoteRingActivity._instance.finish();

        return wasStopped;
    }

}
