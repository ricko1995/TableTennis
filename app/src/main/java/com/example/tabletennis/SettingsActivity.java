package com.example.tabletennis;

import android.net.sip.SipSession;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    static EditTextPreference minSpeedPref, maxSpeedPref, spinIntensityPref;
    boolean firstStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            firstStart = extras.getBoolean("firstStart");
        }
        if(firstStart) finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        importSettingsValue();
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        importSettingsValue();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            minSpeedPref = findPreference("minSpeed");
            maxSpeedPref = findPreference("maxSpeed");
            spinIntensityPref = findPreference("spinIntensity");

            if (minSpeedPref != null && maxSpeedPref != null && spinIntensityPref != null) {
                minSpeedPref.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
                maxSpeedPref.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
                spinIntensityPref.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
            }
        }
    }

    void importSettingsValue(){
        MainActivity.minV0 = Float.valueOf(minSpeedPref.getText());
        MainActivity.maxV0 = Float.valueOf(maxSpeedPref.getText());
        ShotParameters.spinIntensityCoefficient = Float.valueOf(spinIntensityPref.getText());
    }
}