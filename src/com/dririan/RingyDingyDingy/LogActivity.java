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

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

public class LogActivity extends ListActivity {
    private ListView listView;
    private LogDatabase database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);

        database = new LogDatabase(this);
        database.open();

        listView = (ListView) findViewById(android.R.id.list);

        // Display a progress bar while entries are loaded
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        progressBar.setIndeterminate(true);
        listView.setEmptyView(progressBar);
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        List<LogEntry> entries = database.getAllEntries();
        ArrayAdapter<LogEntry> arrayAdapter = new ArrayAdapter<LogEntry>(this, android.R.layout.simple_list_item_1, entries);

        setListAdapter(arrayAdapter);
    }

    @Override
    protected void onPause() {
        database.close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        database.open();
        super.onResume();
    }
}
