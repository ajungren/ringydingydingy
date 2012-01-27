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

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity {
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;

    private CheckBoxPreference remoteLock;
    private EditTextPreference setCode;
    private Preference generateCode;

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(resultCode == 0)
            remoteLock.setChecked(false);
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setIcon(android.R.drawable.ic_dialog_alert)
                   .setTitle(R.string.app_name)
                   .setMessage(R.string.preferences_remote_lock_warning)
                   .setNeutralButton(R.string.ok, null)
                   .show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        generateCode = findPreference("generate_code");
        setCode = (EditTextPreference)findPreference("activation_code");
        remoteLock = (CheckBoxPreference)findPreference("remote_lock");

        generateCode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesActivity.this);
                PreferencesManager preferencesManager = new PreferencesManager(PreferencesActivity.this);
                String code = preferencesManager.resetCode();

                String message = Resources.getString(R.string.preferences_generate_code_dialog_text, PreferencesActivity.this).replace("<code>", code);
                builder.setTitle(R.string.app_name)
                       .setMessage(message)
                       .setNeutralButton(R.string.ok, null)
                       .show();

                return true;
            }
        });

        setCode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int length = ((String) newValue).length();
                if(length >= 4 && length <= 8 && !((String) newValue).contains(" "))
                    return true;
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesActivity.this);
                    builder.setTitle(R.string.app_name)
                           .setMessage(R.string.preferences_set_code_error)
                           .setNeutralButton(R.string.ok, null)
                           .show();

                    return false;
                }
            }
        });

        if(Integer.parseInt(Build.VERSION.SDK) < 8) {
            remoteLock.setEnabled(false);
            remoteLock.setSummary(R.string.preferences_remote_lock_needs_froyo);
        }
        else {
            LockingSupport lockingSupport = LockingSupport.getInstance(this);

            remoteLock.setChecked(lockingSupport.isActive());
            remoteLock.setSummaryOff(R.string.preferences_remote_lock_enable_summary);
            remoteLock.setSummaryOn(R.string.preferences_remote_lock_disable_summary);

            remoteLock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    LockingSupport lockingSupport = LockingSupport.getInstance(PreferencesActivity.this);

                    if(lockingSupport.isActive()) {
                        lockingSupport.removeAdmin();
                        remoteLock.setChecked(false);
                    }
                    else {
                        Intent lockingActivationIntent = lockingSupport.getActivationIntent();
                        PreferencesActivity.this.startActivityForResult(lockingActivationIntent, REQUEST_CODE_ENABLE_ADMIN);
                    }

                    return true;
                }
            });
        }
    }

}
