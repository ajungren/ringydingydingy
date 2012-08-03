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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MessageHandler {
    public static boolean processMessage(Context context, BroadcastReceiver resultReceiver, String message, String source) {
        PreferencesManager preferencesManager = PreferencesManager.getInstance(context);

        // If RingyDingyDingy is not enabled, don't do anything
        if(!preferencesManager.getEnabled())
            return false;

        // Get the activation code
        String code = preferencesManager.getCode();

        // Split the message into tokens
        String[] messageTokens = message.split("\\s+");

        String pagerCode = preferencesManager.getPagerCode();
        if(messageTokens[0].compareToIgnoreCase(pagerCode) == 0) {
            Intent intent = new Intent(ApiHandler.INTENT_RING);
            Intent logIntent = new Intent(LogHandler.INTENT_LOG);
            String pagerMessage = message.substring(pagerCode.length() + 1);

            intent.putExtra("message", pagerMessage)
                  .putExtra("source", source);

            logIntent.putExtra("command", "ring")
                     .putExtra("argument", message.substring(pagerCode.length() + 1))
                     .putExtra("app", "RingyDingyDingy")
                     .putExtra("source", source);
            context.sendBroadcast(logIntent);

            context.sendOrderedBroadcast(intent, ApiHandler.PERMISSION_HANDLE_INTERNAL, resultReceiver, null, ApiHandler.RESULT_UNKNOWN_COMMAND, null, null);
            return true;
        }

        if((messageTokens[0].compareToIgnoreCase("RingyDingyDingy") == 0 || messageTokens[0].compareToIgnoreCase("RDD") == 0) &&
            messageTokens[1].compareToIgnoreCase(code) == 0 || messageTokens[0].compareToIgnoreCase(code) == 0) {
            Intent intent = new Intent().putExtra("source", source);
            String permission = ApiHandler.PERMISSION_HANDLE_INTERNAL;

            int offset;
            if(messageTokens[0].compareTo(code) == 0)
                offset = 0;
            else
                offset = 1;

            String command = "";
            if(messageTokens.length > offset+1)
                command = messageTokens[offset+1];

            if(messageTokens.length < offset+2 || command.compareToIgnoreCase("ring") == 0) {
                command = "ring";
                intent.setAction(ApiHandler.INTENT_RING);
            }
            else if(command.compareToIgnoreCase("help") == 0)
                intent.setAction("com.dririan.RingyDingyDingy.COMMAND_HELP");
            else if(command.compareToIgnoreCase("lock") == 0)
                intent.setAction(ApiHandler.INTENT_LOCK);
            else if(command.compareToIgnoreCase("stop") == 0)
                intent.setAction(ApiHandler.INTENT_STOP);
            else {
                // The command is unknown, so let external apps handle it
                intent.setAction("com.dririan.RingyDingyDingy.COMMAND_" + command.toUpperCase());
                permission = ApiHandler.PERMISSION_HANDLE;
            }

            Intent logIntent = new Intent(LogHandler.INTENT_LOG);
            logIntent.putExtra("command", command)
                     .putExtra("app", "RingyDingyDingy")
                     .putExtra("source", source);
            context.sendBroadcast(logIntent);

            context.sendOrderedBroadcast(intent, permission, resultReceiver, null, ApiHandler.RESULT_UNKNOWN_COMMAND, null, null);
            return true;
        }

        return false;
    }
}
