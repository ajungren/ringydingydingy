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

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesManager {
    private static PreferencesManager _instance = null;

    private SharedPreferences sharedPreferences = null;

    public static PreferencesManager getInstance(Context context) {
        if(_instance == null) {
            _instance = new PreferencesManager();
            _instance.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }

        return _instance;
    }

    public String getCode() {
        String code = sharedPreferences.getString("activation_code", null);

        if(code == null)
            code = resetCode();

        return code;
    }

    public boolean getEnabled() {
        return sharedPreferences.getBoolean("enabled", true);
    }

    public int getLastSeenVersion() {
        return sharedPreferences.getInt("last_seen_version", 0);
    }

    public boolean getActivationLogEnabled() {
        return sharedPreferences.getBoolean("activation_log", true);
    }

    public String getPagerCode() {
        return sharedPreferences.getString("pager_code", "PageMe");
    }

    public String getRingtone() {
        return sharedPreferences.getString("ringtone", "content://settings/system/ringtone");
    }

    public boolean getShowNotification() {
        return sharedPreferences.getBoolean("show_notification", true);
    }

    public boolean googleVoiceTriggerEnabled() {
        return sharedPreferences.getBoolean("google_voice_trigger", true);
    }

    public boolean pagerEnabled() {
        return sharedPreferences.getBoolean("pager", false);
    }

    public String resetCode() {
        Random random = new Random();
        int codeInt = 0;
        while(codeInt < 100000) { // To make things less confusing, we make
                                  // sure the code will always be six digits,
                                  // not counting leading zeros.
            codeInt = random.nextInt(999999);
        }

        String code = Integer.toString(codeInt);
        setCode(code);
        return code;
    }

    public void setCode(String code) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("activation_code", code);
        editor.commit();
    }

    public void setEnabled(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("enabled", enabled);
        editor.commit();
    }

    public void setLastSeenVersion(int version) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("last_seen_version", version);
        editor.commit();
    }

    public void setRingtone(String ringtone) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ringtone", ringtone);
        editor.commit();
    }

    public boolean smsRepliesEnabled() {
        return sharedPreferences.getBoolean("send_sms_replies", true);
    }

    public boolean smsTriggerEnabled() {
        return sharedPreferences.getBoolean("sms_trigger", true);
    }

    public boolean toggleEnabled() {
        if(this.getEnabled()) {
            this.setEnabled(false);
            return false;
        }
        else {
            this.setEnabled(true);
            return true;
        }
    }
}
