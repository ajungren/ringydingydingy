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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ApiHandler extends BroadcastReceiver {
    // RingyDingyDingy API level
    public static final int API_LEVEL = 0;

    // Permissions
    public static final String PERMISSION_HANDLE = "com.dririan.RingyDingyDingy.HANDLE_COMMAND";
    public static final String PERMISSION_HANDLE_INTERNAL = "com.dririan.RingyDingyDingy.HANDLE_INTERNAL_COMMAND";
    public static final String PERMISSION_EXECUTE = "com.dririan.RingyDingyDingy.EXECUTE_COMMAND";

    // Generic result codes
    public static final int RESULT_UNKNOWN_COMMAND = -42;

    // COMMAND_LOCK result codes
    public static final int RESULT_NEEDS_FROYO = 1;
    public static final int RESULT_NOT_ACTIVE = 2;

    // COMMAND_RING result codes
    public static final int RESULT_ALREADY_RINGING = 1;
    public static final int RESULT_PAGER_DISABLED = 2;

    // COMMAND_STOP result codes
    public static final int RESULT_NOT_RINGING = 1;

    // Pre-defined command intents
    public static final String LOCK_INTENT = "com.dririan.RingyDingyDingy.COMMAND_LOCK";
    public static final String RING_INTENT = "com.dririan.RingyDingyDingy.COMMAND_RING";
    public static final String STOP_INTENT = "com.dririan.RingyDingyDingy.COMMAND_STOP";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action.compareTo(LOCK_INTENT) == 0) {
            if(Build.VERSION.SDK_INT >= 8) {
                LockingSupport lockingSupport = LockingSupport.getInstance(context);
                if(lockingSupport.isActive()) {
                    lockingSupport.lock();
                    setResultCode(Activity.RESULT_OK);
                }
                else
                    setResultCode(RESULT_NOT_ACTIVE);
            }
            else
                setResultCode(RESULT_NEEDS_FROYO);
        }
        else if(action.compareTo(RING_INTENT) == 0) {
            // If a remote ring is already happening, don't start another
            if(RemoteRingActivity.ringtone != null && RemoteRingActivity.ringtone.isPlaying())
                setResultCode(RESULT_ALREADY_RINGING);
            else {
                Intent newIntent = new Intent(context, RemoteRingActivity.class);

                if(intent.hasExtra("message")) {
                    if(!PreferencesManager.getInstance(context).pagerEnabled()) {
                        setResultCode(ApiHandler.RESULT_PAGER_DISABLED);
                        return;
                    }
                    newIntent.putExtra("message", intent.getStringExtra("message"));
                }
                if(intent.hasExtra("source"))
                    newIntent.putExtra("source", intent.getStringExtra("source"));

                newIntent.setAction(action)
                         .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(newIntent);
                setResultCode(Activity.RESULT_OK);
            }
        }
        else if(action.compareTo(STOP_INTENT) == 0) {
            if(RemoteRingActivity.stopRinging())
                setResultCode(Activity.RESULT_OK);
            else
                setResultCode(RESULT_NOT_RINGING);
        }
    }
}
