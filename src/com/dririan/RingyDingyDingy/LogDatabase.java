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
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class LogDatabase {
    private static String[] allColumns = { LogOpenHelper.COLUMN_ID, LogOpenHelper.COLUMN_COMMAND,
                                           LogOpenHelper.COLUMN_ARGUMENT, LogOpenHelper.COLUMN_APP,
                                           LogOpenHelper.COLUMN_SOURCE, LogOpenHelper.COLUMN_TIMESTAMP };

    private SQLiteDatabase database;
    private LogOpenHelper openHelper;

    public LogDatabase(Context context) {
        openHelper = new LogOpenHelper(context);
    }

    public void addEntry(String command, String argument, String app, String source) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(LogOpenHelper.COLUMN_COMMAND, command);
        contentValues.put(LogOpenHelper.COLUMN_ARGUMENT, argument);
        contentValues.put(LogOpenHelper.COLUMN_APP, app);
        contentValues.put(LogOpenHelper.COLUMN_SOURCE, source);

        database.insert(LogOpenHelper.TABLE_NAME, null, contentValues);
    }

    public void close() {
        openHelper.close();
    }

    public List<LogEntry> getAllEntries() {
        Cursor cursor = database.query(LogOpenHelper.TABLE_NAME, allColumns, null, null, null, null, null);
        List<LogEntry> entries = new ArrayList<LogEntry>();
        LogEntry entry;

        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            entry = new LogEntry();

            entry.id = cursor.getLong(0);
            entry.command = cursor.getString(1);
            entry.argument = cursor.getString(2);
            entry.app = cursor.getString(3);
            entry.source = cursor.getString(4);
            entry.timestamp = cursor.getString(5);

            entries.add(entry);

            cursor.moveToNext();
        }

        return entries;
    }

    public void open() throws SQLiteException {
        database = openHelper.getWritableDatabase();
    }
}
