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
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
    private PreferencesManager preferencesManager = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        preferencesManager = new PreferencesManager(this);
        updateHeader();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
        case R.id.settings:
            Intent preferencesActivity = new Intent(this, PreferencesActivity.class);
            startActivity(preferencesActivity);

            return true;
        default:
            return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus)
            updateHeader();
    }

    public void updateHeader() {
        // Get the activation code
        String code = preferencesManager.getCode();
        String remoteLock = "";
        String settings = "";

        // Show the activation code on the TextView
        TextView textView = (TextView)findViewById(R.id.activation_code);
        textView.setText(code);

        // If we're on Froyo or newer, show information about remote locking
        if(Build.VERSION.SDK_INT >= 8) {
            if(LockingSupport.getInstance(this).isActive())
                remoteLock = this.getString(R.string.main_header_remote_lock_enabled);
            else
                remoteLock = this.getString(R.string.main_header_remote_lock_disabled);
        }

        // If we're on Honeycomb or newer, the Settings icon is on the action
        // bar. Otherwise, it's in the normal menu.
        if(Build.VERSION.SDK_INT >= 11)
            settings = this.getString(R.string.main_header_settings_holo);
        else
            settings = this.getString(R.string.main_header_settings_default);

        // Update the header
        TextView header = (TextView)findViewById(R.id.header);
        String headerText = this.getString(R.string.main_header);
        header.setText(headerText.replace("<settings>", settings).replace("<remote_lock>", remoteLock).replace("<code>", code));
    }
}
