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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity {
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    public static PreferencesActivity _instance = null;

    private CheckBoxPreference enabled;
    private CheckBoxPreference googleVoiceTrigger;
    private CheckBoxPreference remoteLock;
    private EditTextPreference setCode;
    private EditTextPreference setPagerCode;
    private CheckBoxPreference showNotification;
    private Preference generateCode;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _instance = this;

        addPreferencesFromResource(R.xml.preferences);

        enabled = (CheckBoxPreference) findPreference("enabled");
        generateCode = findPreference("generate_code");
        googleVoiceTrigger = (CheckBoxPreference) findPreference("google_voice_trigger");
        setCode = (EditTextPreference) findPreference("activation_code");
        setPagerCode = (EditTextPreference) findPreference("pager_code");
        showNotification = (CheckBoxPreference) findPreference("show_notification");
        remoteLock = (CheckBoxPreference) findPreference("remote_lock");

        enabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                PreferencesManager preferencesManager = PreferencesManager.getInstance(PreferencesActivity.this);
                boolean nowEnabled = preferencesManager.toggleEnabled();

                NotificationHandler.updateNotification(PreferencesActivity.this);
                enabled.setChecked(nowEnabled);

                return false;
            }
        });

        generateCode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesActivity.this);
                PreferencesManager preferencesManager = PreferencesManager.getInstance(PreferencesActivity.this);
                String code = preferencesManager.resetCode();

                // Notify the EditTextPreference setCode that the code has changed
                setCode.setText(code);

                String message = PreferencesActivity.this.getString(R.string.preferences_generate_code_dialog_text).replace("<code>", code);
                builder.setTitle(R.string.app_name)
                       .setMessage(message)
                       .setNeutralButton(R.string.ok, null)
                       .show();

                return true;
            }
        });

        try {
            PackageManager packageManager = this.getPackageManager();
            packageManager.getPackageInfo("com.google.android.apps.googlevoice", 0);

            googleVoiceTrigger.setSummaryOff(R.string.preferences_google_voice_trigger_enable_summary);
            googleVoiceTrigger.setSummaryOn(R.string.preferences_google_voice_trigger_disable_summary);
        }
        catch(PackageManager.NameNotFoundException e) {
            googleVoiceTrigger.setPersistent(false);
            googleVoiceTrigger.setChecked(false);
            googleVoiceTrigger.setEnabled(false);
            googleVoiceTrigger.setSummary(R.string.preferences_google_voice_trigger_needs_app);
        }

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

        setPagerCode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(!((String) newValue).contains(" "))
                    return true;

                AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesActivity.this);
                builder.setTitle(R.string.app_name)
                       .setMessage(R.string.preferences_pager_error)
                       .setNeutralButton(R.string.ok, null)
                       .show();

                return false;
            }
        });

        showNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // IMPORTANT: Because this fires BEFORE the preference is
                // changed, if it is checked, it is about to be unchecked, etc.
                if(showNotification.isChecked())
                    NotificationHandler.hideNotification();
                else
                    // The true in this call forces displayNotification to
                    // ignore the value of the show_notification preference
                    NotificationHandler.displayNotification(PreferencesActivity.this, true);

                return true;
            }
        });

        if(Build.VERSION.SDK_INT >= 8) {
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
        else {
            remoteLock.setEnabled(false);
            remoteLock.setSummary(R.string.preferences_remote_lock_needs_froyo);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _instance = null;
    }

    public static void updateEnabled() {
        if(_instance != null) {
            PreferencesManager preferencesManager = PreferencesManager.getInstance(_instance);
            _instance.enabled.setChecked(preferencesManager.getEnabled());
        }
    }
}
