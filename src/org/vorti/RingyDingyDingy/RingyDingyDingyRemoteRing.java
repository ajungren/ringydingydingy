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
import android.widget.TextView;

public class RingyDingyDingyRemoteRing extends Activity {
    public static final String INTENT = "org.vorti.RingyDingyDingy.REMOTE_RING";

    public AudioManager audiomanager = null;
    public int oldMode = 0;
    public int oldVolume = 0;
    public Ringtone ringtone = null;
    public String source = "";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.remote_ring);

        // Get the source of the message
        Intent intent = this.getIntent();
        if(intent != null)
            source = intent.getData().getSchemeSpecificPart();
        else
            source = "unknown";

        // Prepare the AudioManager, set the ringer mode to normal, and set the volume to maximum
        audiomanager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        oldMode = audiomanager.getRingerMode();
        oldVolume = audiomanager.getStreamVolume(AudioManager.STREAM_RING);
        audiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audiomanager.setStreamVolume(AudioManager.STREAM_RING, audiomanager.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

        // Get the current ringtone
        ringtone = RingtoneManager.getRingtone(this, Settings.System.DEFAULT_RINGTONE_URI);
        if(ringtone == null)
            ringtone = RingtoneManager.getRingtone(this, Settings.System.DEFAULT_NOTIFICATION_URI);

        // Play the ringtone
        ringtone.play();

        // An ugly kludge to append the source of the command to the resource string
        TextView textView = new TextView(this);
        textView.setText(R.string.remote_ring_text);
        textView.append(" " + source);

        // Show an AlertDialog and stop the ringtone when the user hits Stop
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
               .setMessage(textView.getText())
               .setNeutralButton(R.string.remote_ring_stop_button, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       RingyDingyDingyRemoteRing.this.stopRinging();
                       RingyDingyDingyRemoteRing.this.finish();
                   }
               });
        AlertDialog alertdialog = builder.create();
        alertdialog.show();
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
        audiomanager.setRingerMode(oldMode);
        audiomanager.setStreamVolume(AudioManager.STREAM_RING, oldVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

}

