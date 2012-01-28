/*
 * This file is part of RingyDingyDingy.
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

import java.util.ArrayList;
import java.util.Arrays;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
    private PreferencesManager preferencesManager = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = null;
        String message = "";

        if(bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            messages = new SmsMessage[pdus.length];

            // Get the activation code
            preferencesManager = new PreferencesManager(context);
            String code = preferencesManager.getCode();

            for(int i=0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                message = messages[i].getMessageBody().toString();
                ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(message.trim().split("\\s+")));
                String source = messages[i].getOriginatingAddress();

                if(tokens.get(0).compareToIgnoreCase("RingyDingyDingy") == 0 && tokens.get(1).compareTo(code) == 0) {
                    // Drop the SMS message so it doesn't go to the user's inbox
                    this.abortBroadcast();

                    if(tokens.size() < 3)
                        tokens.add("ring");

                    if(tokens.get(2).compareToIgnoreCase("help") == 0)
                        sendSMS(source, Resources.getString(R.string.sms_help, context));
                    else if(tokens.get(2).compareToIgnoreCase("lock") == 0) {
                        if(Integer.parseInt(Build.VERSION.SDK) >= 8) {
                            LockingSupport lockingSupport = LockingSupport.getInstance(context);
                            if(lockingSupport.isActive()) {
                                lockingSupport.lock();
                                sendSMS(source, Resources.getString(R.string.sms_lock_success, context));
                            }
                            else
                                sendSMS(source, Resources.getString(R.string.sms_lock_needs_permission, context));
                        }
                        else
                            sendSMS(source, Resources.getString(R.string.sms_lock_needs_froyo, context));
                    }
                    else if(tokens.get(2).compareToIgnoreCase("ring") == 0) {
                        // If a remote ring is already happening, don't start another
                        if(RemoteRingActivity.ringtone != null && RemoteRingActivity.ringtone.isPlaying())
                            return;

                        Intent remoteRingIntent = new Intent();
                        remoteRingIntent.setClass(context, RemoteRingActivity.class)
                                        .setData(Uri.fromParts("remotering", source, null))
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(remoteRingIntent);
                        sendSMS(source, Resources.getString(R.string.sms_ring, context));
                    }
                    else if(tokens.get(2).compareToIgnoreCase("stop") == 0) {
                        if(RemoteRingActivity.stopRinging())
                            sendSMS(source, Resources.getString(R.string.sms_stop_success, context));
                        else
                            sendSMS(source, Resources.getString(R.string.sms_stop_was_not_ringing, context));
                    }
                    else
                        sendSMS(source, Resources.getString(R.string.sms_unknown_command, context));
                }
            }
        }
    }

    private void sendSMS(String destination, String message) {
        if(preferencesManager.smsRepliesEnabled()) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(destination, null, message, null, null);
        }
    }

}
