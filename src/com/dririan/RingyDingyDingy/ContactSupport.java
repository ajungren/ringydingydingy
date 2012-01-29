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

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;

public class ContactSupport {

    public static String[] lookupByNumber(Context context, String phoneNumber) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        String[] contact = new String[1];

        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        cursor = contentResolver.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);

        int index = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
        if(cursor != null) {
            if(cursor.getCount() > 0) {
                cursor.moveToNext();
                contact[0] = cursor.getString(index);
            }
            else
                contact[0] = null;
        }
        cursor.close();

        return contact;
    }

}
