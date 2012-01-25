package org.vorti.RingyDingyDingy;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.Random;

public class PreferencesManager {
    public static final String PREFERENCE_NAME = "RingyDingyDingy";
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

