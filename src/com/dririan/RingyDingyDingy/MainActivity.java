/*
 * This file is part of RingyDingyDingy.
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
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
    private PreferencesManager preferencesManager = null;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setRequestedOrientation(newConfig.orientation);
    }

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
        String remoteLockInformation = "";
        String remoteLockSettings = "";
        String settings = "";

        // Show the activation code on the TextView
        TextView textView = (TextView)findViewById(R.id.activation_code);
        textView.setText(code);

        // If we're on Froyo or newer, show information about remote locking
        if(Integer.parseInt(Build.VERSION.SDK) >= 8) {
            remoteLockSettings = " " + this.getString(R.string.remote_lock_settings);

            if(LockingSupport.getInstance(this).isActive()) {
                remoteLockInformation = this.getString(R.string.remote_lock_information);
                remoteLockSettings = remoteLockSettings.replace("<remote_lock_toggle>", "disable");
            }
            else {
                remoteLockInformation = this.getString(R.string.remote_lock_disabled);
                remoteLockSettings = remoteLockSettings.replace("<remote_lock_toggle>", "enable");
            }
        }

        // If we're on Honeycomb or newer, the Settings icon is on the action
        // bar. Otherwise, it's in the normal menu.
        if(Integer.parseInt(Build.VERSION.SDK) >= 11)
            settings = this.getString(R.string.preferences_header_settings_holo);
        else
            settings = this.getString(R.string.preferences_header_settings_default);

        // Update the header
        TextView header = (TextView)findViewById(R.id.header);
        String headerText = this.getString(R.string.preferences_header);
        header.setText(headerText.replace("<settings>", settings).replace("<remote_lock_information>", remoteLockInformation).replace("<remote_lock_settings>", remoteLockSettings).replace("<code>", code));
    }
}
