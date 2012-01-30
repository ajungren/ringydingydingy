package com.dririan.RingyDingyDingy;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

public class MessageHandler {

    public static int processMessage(Context context, ArrayList<String> message, String source) {
        // Get the activation code
        PreferencesManager preferencesManager = new PreferencesManager(context);
        String code = preferencesManager.getCode();

        if(message.get(0).compareToIgnoreCase("RingyDingyDingy") == 0 && message.get(1).compareTo(code) == 0) {
            if(message.size() < 3)
                message.add("ring");

            if(message.get(2).compareToIgnoreCase("help") == 0)
                return R.string.sms_help;
            else if(message.get(2).compareToIgnoreCase("lock") == 0) {
                if(Build.VERSION.SDK_INT >= 8) {
                    LockingSupport lockingSupport = LockingSupport.getInstance(context);
                    if(lockingSupport.isActive()) {
                        lockingSupport.lock();
                        return R.string.sms_lock_success;
                    }
                    else
                        return R.string.sms_lock_needs_permission;
                }
                else
                    return R.string.sms_lock_needs_froyo;
            }
            else if(message.get(2).compareToIgnoreCase("ring") == 0) {
                // If a remote ring is already happening, don't start another
                if(RemoteRingActivity.ringtone != null && RemoteRingActivity.ringtone.isPlaying()) {
                    return R.string.sms_ring_was_ringing;
                }

                Intent remoteRingIntent = new Intent();
                remoteRingIntent.setClass(context, RemoteRingActivity.class)
                                .setData(Uri.fromParts("remotering", source, null))
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(remoteRingIntent);
                return R.string.sms_ring_success;
            }
            else if(message.get(2).compareToIgnoreCase("stop") == 0) {
                if(RemoteRingActivity.stopRinging())
                    return R.string.sms_stop_success;
                else
                    return R.string.sms_stop_was_not_ringing;
            }
            else
                return R.string.sms_unknown_command;
        }

        return -1;
    }

}
