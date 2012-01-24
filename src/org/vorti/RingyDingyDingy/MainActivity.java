package org.vorti.RingyDingyDingy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
    PreferenceManager preferenceManager = null;

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

        preferenceManager = new PreferenceManager(this.getSharedPreferences(PreferenceManager.PREFERENCE_NAME, Context.MODE_PRIVATE));
        updateCode();

        // Set up the button to reset the code
        Button generateButton = (Button)findViewById(R.id.generate_button);
        generateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                preferenceManager.resetCode();
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
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
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
    public void onStop() {
        super.onStop();

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
        final EditText editText = new EditText(MainActivity.this);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        editText.setMaxLines(1);
        builder.setTitle(R.string.code_prompt_title)
               .setMessage(promptText)
               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       String string = editText.getText().toString();
                       int length = string.length();
                       if(length >= 4 && length <= 8)
                           MainActivity.this.preferenceManager.setCode(string);
                       else
                           promptForCode(R.string.code_prompt_error_text);
                   }
               })
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                   }
               })
               .setView(editText);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void updateCode() {
        // Get the activation code
        String code = preferenceManager.getCode();

        // Show the activation code on the TextView
        TextView textView = (TextView)findViewById(R.id.activation_code);
        textView.setText(code);

        // Update the header
        TextView header = (TextView)findViewById(R.id.header);
        String headerText = Resources.getString(R.string.preferences_header, this);
        header.setText(headerText.replace("<code>", code));
    }
}

