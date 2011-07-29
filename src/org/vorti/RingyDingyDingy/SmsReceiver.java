package org.vorti.RingyDingyDingy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String message = "";
        if(bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];

            // Get the activation code
            RingyDingyDingyPreferenceManager preferencemanager = new RingyDingyDingyPreferenceManager(context.getSharedPreferences(RingyDingyDingyPreferenceManager.PREFERENCE_NAME, Context.MODE_PRIVATE));
            String code = preferencemanager.getCode();

            for(int i=0; i< msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                message = msgs[i].getMessageBody().toString();

                if(("RingyDingyDingy " + code).compareToIgnoreCase(message) == 0) {
                    Intent remoteRingIntent = new Intent();
                    remoteRingIntent.setClass(context, RingyDingyDingyRemoteRing.class)
                                    .setData(Uri.fromParts("remotering", msgs[i].getOriginatingAddress(), null))
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(remoteRingIntent);
                }
            }
        }
    }

}

