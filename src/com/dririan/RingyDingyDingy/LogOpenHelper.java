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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LogOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "log.db";

    public static final String COLUMN_APP = "app";
    public static final String COLUMN_ARGUMENT = "argument";
    public static final String COLUMN_COMMAND = "command";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SOURCE = "source";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String TABLE_NAME = "log";

    public LogOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY ASC NOT NULL, " +
                   COLUMN_COMMAND + " TEXT NOT NULL, " + COLUMN_ARGUMENT + " TEXT, " + COLUMN_APP + " TEXT, " +
                   COLUMN_SOURCE + " TEXT, " + COLUMN_TIMESTAMP + " TEXT DEFAULT CURRENT_TIMESTAMP NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This needs to be implemented if DATABASE_VERSION is incremented to
        // update existing databases
    }
}
