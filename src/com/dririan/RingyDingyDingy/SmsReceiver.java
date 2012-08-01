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
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {
    private PreferencesManager preferencesManager = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        preferencesManager = PreferencesManager.getInstance(context);

        if(intent.getAction().compareTo("android.provider.Telephony.SMS_RECEIVED") == 0) {
            // Check if the SMS trigger is enabled
            if(!preferencesManager.smsTriggerEnabled())
                return;

            Bundle bundle = intent.getExtras();
            SmsMessage[] messages = null;
            String message = "";

            if(bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                messages = new SmsMessage[pdus.length];

                for(int i=0; i < messages.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    message = messages[i].getMessageBody().toString();
                    String source = messages[i].getOriginatingAddress();
                    int returnMessageId = -1;

                    returnMessageId = MessageHandler.processMessage(context, new SmsReceiver(), message, source);
                    if(returnMessageId != -1) {
                        // Drop the SMS message so it doesn't go to the user's inbox
                        this.abortBroadcast();
                    }
                }
            }
        }
        else {
            int messageId = R.string.sms_unknown_command;
            int resultCode = getResultCode();

            if(intent.getAction().compareTo(ApiHandler.LOCK_INTENT) == 0) {
                switch(resultCode) {
                case Activity.RESULT_OK:
                    messageId = R.string.sms_lock_success;
                    break;
                case ApiHandler.RESULT_NEEDS_FROYO:
                    messageId = R.string.sms_lock_needs_froyo;
                    break;
                case ApiHandler.RESULT_NOT_ACTIVE:
                    messageId = R.string.sms_lock_needs_permission;
                    break;
                default:
                    messageId = R.string.sms_unknown_error;
                    break;
                }
            }
            else if(intent.getAction().compareTo(ApiHandler.STOP_INTENT) == 0) {
                switch(resultCode) {
                case Activity.RESULT_OK:
                    messageId = R.string.sms_stop_success;
                    break;
                case ApiHandler.RESULT_NOT_RINGING:
                    messageId = R.string.sms_stop_was_not_ringing;
                    break;
                default:
                    messageId = R.string.sms_unknown_error;
                    break;
                }
            }
            else if(resultCode == Activity.RESULT_OK)
                messageId = R.string.sms_success;

            sendSms(context, intent.getStringExtra("source"), context.getString(messageId).replace("<code>", preferencesManager.getCode()));
        }
    }

    private void sendSms(Context context, String destination, String message) {
        if(preferencesManager.smsRepliesEnabled()) {
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent("com.dririan.RingyDingyDingy.SMS_SENT"), 0);

            Log.d("RingyDingyDingy", "Sending SMS to " + destination + ": " + message);
            SmsManager.getDefault().sendTextMessage(destination, null, message, sentIntent, null);
        }
    }

}
