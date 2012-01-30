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

import java.util.ArrayList;
import java.util.Arrays;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

            for(int i=0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                message = messages[i].getMessageBody().toString();
                ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(message.trim().split("\\s+")));
                String source = messages[i].getOriginatingAddress();
                int returnMessageId = -1;

                returnMessageId = MessageHandler.processMessage(context, tokens, source);
                if(returnMessageId != -1) {
                    // Drop the SMS message so it doesn't go to the user's inbox
                    this.abortBroadcast();

                    // sendSMS(String, String) needs preferencesManager to
                    // determine if SMS replies are enabled or not
                    preferencesManager = new PreferencesManager(context);
                    sendSMS(source, context.getString(returnMessageId));
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
