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

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LogActivity extends ListActivity {
    private ListView listView;
    private LogDatabase database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);

        database = new LogDatabase(this);
        listView = (ListView) findViewById(android.R.id.list);

        updateEntries();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.log, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
        case R.id.clear:
            AlertDialog.Builder builder = ThemedDialogBuilder.getBuilder(this);

            database.open();
            if(database.isEmpty()) {
                builder.setTitle(R.string.app_name)
                       .setMessage(this.getString(R.string.log_already_empty))
                       .setNeutralButton(R.string.ok, null)
                       .show();
            }
            else {
                builder.setTitle(R.string.app_name)
                       .setMessage(this.getString(R.string.log_clear_prompt))
                       .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) {
                               database.open();
                               database.clear();
                               database.close();
                               LogActivity.this.finish();
                           }
                       })
                       .setNegativeButton(android.R.string.no, null)
                       .show();

                return true;
            }
            database.close();
        default:
            return super.onOptionsItemSelected(menuItem);
        }
    }

    private void updateEntries() {
        database.open();
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);

        if(database.isEmpty()) {
            // Display a notice that the log is empty
            TextView textView = new TextView(this);
            textView.setText(R.string.log_empty);
            textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            textView.setPadding(10, 10, 10, 10);
            listView.setEmptyView(textView);
            root.addView(textView);
        }
        else {
            // Display a progress bar while entries are loaded
            ProgressBar progressBar = new ProgressBar(this);
            progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            progressBar.setIndeterminate(true);
            listView.setEmptyView(progressBar);;
            root.addView(progressBar);

            List<LogEntry> entries = database.getAllEntries();
            ArrayAdapter<LogEntry> arrayAdapter = new ArrayAdapter<LogEntry>(this, android.R.layout.simple_list_item_1, entries);

            setListAdapter(arrayAdapter);
        }
        database.close();
    }
}
