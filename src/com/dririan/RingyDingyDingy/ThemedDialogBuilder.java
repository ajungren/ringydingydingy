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

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;

public class ThemedDialogBuilder {
    public static AlertDialog.Builder getBuilder(Context context) {
        if(Build.VERSION.SDK_INT >= 14)
            return DeviceDefaultDialogBuilder.getBuilder(context);
        else if(Build.VERSION.SDK_INT >= 11)
            return HoloDialogBuilder.getBuilder(context);
        else
            return new AlertDialog.Builder(context);
    }
}
