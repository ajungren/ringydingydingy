package org.vorti.RingyDingyDingy;

import android.content.SharedPreferences;
import java.util.Random;

public class PreferenceManager {
    public static final String PREFERENCE_NAME = "RingyDingyDingy";
    private SharedPreferences sharedPreferences = null;

    public PreferenceManager(SharedPreferences preferences) {
        sharedPreferences = preferences;
    }

    public String getCode() {
        String code = sharedPreferences.getString("activationCode", null);

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
        editor.putString("activationCode", code);
        editor.commit();
    }
}

