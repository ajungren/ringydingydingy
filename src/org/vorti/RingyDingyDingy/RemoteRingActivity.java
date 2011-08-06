package org.vorti.RingyDingyDingy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.provider.Settings;

public class RemoteRingActivity extends Activity {
    public static final String INTENT = "org.vorti.RingyDingyDingy.REMOTE_RING";

    public AudioManager audioManager = null;
    public int oldMode = 0;
    public int oldVolume = 0;
    public Ringtone ringtone = null;
    public String source = "";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the source of the message
        Intent intent = this.getIntent();
        if(intent != null)
            source = intent.getData().getSchemeSpecificPart();
        else
            source = "unknown";

        // Prepare the AudioManager, set the ringer mode to normal, and set the volume to maximum
        audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
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
               .setMessage(Resources.getString(R.string.remote_ring_text, this) + " " + source)
               .setNeutralButton(R.string.remote_ring_stop_button, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       RemoteRingActivity.this.stopRinging();
                       RemoteRingActivity.this.finish();
                   }
               });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // If the activity gets closed for some random reason, stop ringing so we don't annoy the user
        stopRinging();
    }

    public void stopRinging() {
        // Stop the bloody ringer and reset the settings
        if(ringtone.isPlaying())
            ringtone.stop();
        audioManager.setRingerMode(oldMode);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, oldVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

}

