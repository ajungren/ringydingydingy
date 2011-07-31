package org.vorti.RingyDingyDingy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
    PreferenceManager preferencemanager = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        preferencemanager = new PreferenceManager(this.getSharedPreferences(PreferenceManager.PREFERENCE_NAME, Context.MODE_PRIVATE));
        updateCode();

        // Set up the button to reset the code
        Button generateButton = (Button)findViewById(R.id.generate_button);
        generateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                preferencemanager.resetCode();
                updateCode();
            }
        });

        Button setButton = (Button)findViewById(R.id.set_button);
        setButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.app_name)
                       .setMessage(R.string.preferences_warning)
                       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                        	   promptForCode(R.string.code_prompt_text);
                           }
                       })
                       .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                           }
                       });
                AlertDialog alertdialog = builder.create();
                alertdialog.show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        // We don't need to stick around after the user switches to another
        // app, so let's get out of the way
        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus)
            updateCode();
    }

    public void promptForCode(int promptText) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final EditText edittext = new EditText(MainActivity.this);
        edittext.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        edittext.setMaxLines(1);
        builder.setTitle(R.string.code_prompt_title)
               .setMessage(promptText)
               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       String string = edittext.getText().toString();
                       int length = string.length();
                       if(length >= 4 && length <= 8)
                           MainActivity.this.preferencemanager.setCode(string);
                       else
                           promptForCode(R.string.code_prompt_error_text);
                   }
               })
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                   }
               })
               .setView(edittext);
        AlertDialog alertdialog = builder.create();
        alertdialog.show();
    }

    public void updateCode() {
        // Get the activation code
        String code = preferencemanager.getCode();

        // Show the activation code on the TextView
        TextView textView = (TextView)findViewById(R.id.activation_code);
        textView.setText(code);
    }
}

