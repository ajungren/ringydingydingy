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

import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class MessageHandler {
    public static int processMessage(Context context, String message, String source) {
        // If RingyDingyDingy is not enabled, don't do anything
        PreferencesManager preferencesManager = new PreferencesManager(context);
        if(!preferencesManager.getEnabled())
            return -1;

        // Get the activation code
        String code = preferencesManager.getCode();

        // Split the message into tokens
        String[] messageTokens = message.split("\\s+");

        String pagerCode = preferencesManager.getPagerCode();
        if(messageTokens[0].compareTo(pagerCode) == 0) {
            if(preferencesManager.pagerEnabled()) {
                Intent emergencyPageIntent = new Intent();
                emergencyPageIntent.setAction(RemoteRingActivity.PAGE_INTENT)
                                .putExtra("message", message.substring(pagerCode.length() + 1))
                                .putExtra("source", source)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(emergencyPageIntent);

                return R.string.sms_page_success;
            }
            else
                return R.string.sms_page_disabled;
        }

        if((messageTokens[0].compareToIgnoreCase("RingyDingyDingy") == 0 || messageTokens[0].compareToIgnoreCase("RDD") == 0) && messageTokens[1].compareTo(code) == 0 || messageTokens[0].compareTo(code) == 0) {
            int offset;
            if(messageTokens[0].compareTo(code) == 0)
                offset = 0;
            else
                offset = 1;

            if(messageTokens.length < offset+2 || messageTokens[offset+1].compareToIgnoreCase("ring") == 0) {
                // If a remote ring is already happening, don't start another
                if(RemoteRingActivity.ringtone != null && RemoteRingActivity.ringtone.isPlaying()) {
                    return R.string.sms_ring_was_ringing;
                }

                Intent remoteRingIntent = new Intent();
                remoteRingIntent.setAction(RemoteRingActivity.RING_INTENT)
                                .putExtra("source", source)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(remoteRingIntent);

                return R.string.sms_ring_success;
            }
            else if(messageTokens[offset+1].compareToIgnoreCase("help") == 0)
                return R.string.sms_help;
            else if(messageTokens[offset+1].compareToIgnoreCase("lock") == 0) {
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
            else if(messageTokens[offset+1].compareToIgnoreCase("stop") == 0) {
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
