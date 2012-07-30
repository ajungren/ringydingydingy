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

public class ToggleHandler extends BroadcastReceiver {
    public static final String INTENT = "com.dririan.RingyDingyDingy.TOGGLE";

    @Override
    public void onReceive(Context context, Intent intent) {
        PreferencesManager preferencesManager = new PreferencesManager(context);

        preferencesManager.toggleEnabled();

        PreferencesActivity.updateEnabled();
        NotificationHandler.updateNotification(context);
    }
}
