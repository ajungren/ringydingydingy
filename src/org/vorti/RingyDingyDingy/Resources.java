package org.vorti.RingyDingyDingy;

import android.content.Context;
import android.widget.TextView;

public class Resources {

    public static String getString(int id, Context context) {
        TextView textView = new TextView(context);
        textView.setText(id);
        return (String)textView.getText();
    }

}

