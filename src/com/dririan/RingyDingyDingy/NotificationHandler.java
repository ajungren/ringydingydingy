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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationHandler extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;

    private static Notification notification = null;
    private static NotificationManager notificationManager = null;
    private static PreferencesManager preferencesManager = null;

    @SuppressWarnings("deprecation")
    public static void displayNotification(Context context, boolean force) {
        if(preferencesManager == null)
            preferencesManager = PreferencesManager.getInstance(context);

        if(notification == null && (force || preferencesManager.getShowNotification())) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notification = new Notification(R.drawable.icon, context.getString(R.string.notification_default_title), System.currentTimeMillis());

            notification.defaults = 0;      // This disables sound, vibration, lights, etc. for the notification
            notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;

            updateNotification(context);
        }
    }

    public static void displayNotification(Context context) {
        displayNotification(context, false);
    }

    public static void hideNotification() {
        if(notification != null) {
            notificationManager.cancel(NOTIFICATION_ID);
            notification = null;
        }
    }

    @SuppressWarnings("deprecation")
    public static void updateNotification(Context context) {
        CharSequence title, message;
        Intent intent = new Intent(ToggleHandler.INTENT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        if(preferencesManager == null)
            preferencesManager = PreferencesManager.getInstance(context);

        if(preferencesManager.getEnabled()) {
            title = context.getText(R.string.notification_enabled);
            message = context.getText(R.string.notification_enabled_message);
            notification.icon = R.drawable.icon;
        }
        else {
            title = context.getText(R.string.notification_disabled);
            message = context.getText(R.string.notification_disabled_message);
            notification.icon = R.drawable.icon_disabled;
        }

        notification.setLatestEventInfo(context, title, message, pendingIntent);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHandler.displayNotification(context);
    }
}
