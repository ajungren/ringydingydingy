package org.vorti.RingyDingyDingy;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
    PreferencesManager preferencesManager = null;

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
        updateCode();
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
            updateCode();
    }

    public void updateCode() {
        // Get the activation code
        String code = preferencesManager.getCode();

        // Show the activation code on the TextView
        TextView textView = (TextView)findViewById(R.id.activation_code);
        textView.setText(code);

        // Update the header
        TextView header = (TextView)findViewById(R.id.header);
        String headerText = Resources.getString(R.string.preferences_header, this);
        header.setText(headerText.replace("<code>", code));
    }
}

