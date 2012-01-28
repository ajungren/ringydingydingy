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

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesManager {
    private SharedPreferences sharedPreferences = null;

    public PreferencesManager(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getCode() {
        String code = sharedPreferences.getString("activation_code", null);

        if(code == null)
            code = resetCode();

        return code;
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
}
