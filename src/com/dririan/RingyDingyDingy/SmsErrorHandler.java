package com.dririan.RingyDingyDingy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

public class SmsErrorHandler extends BroadcastReceiver {
    public static final String INTENT = "com.dririan.RingyDingyDingy.SMS_SENT";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().compareTo(INTENT) == 0) {
            int resultCode = getResultCode();
            String tag = "RingyDingyDingy";
            String prefix = "Error sending SMS: ";

            switch(resultCode) {
            case Activity.RESULT_OK:
                // Since the SMS was sent, no need to log anything
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                String msg = "ERROR_GENERIC_FAILURE";

                if(intent.hasExtra("errorCode"))
                    msg += " (" + Integer.toString(intent.getIntExtra("errorCode", 0)) + ")";

                Log.e(tag, prefix + msg);
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Log.e(tag, prefix + "ERROR_RADIO_OFF");
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                Log.e(tag, prefix + "ERROR_NULL_PDU");
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Log.e(tag, prefix + "ERROR_NO_SERVICE");
                break;
            default:
                Log.e(tag, prefix + "Unknown result code: " + Integer.toString(resultCode));
                break;
            }
        }
    }
}
