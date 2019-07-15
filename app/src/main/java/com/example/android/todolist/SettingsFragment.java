package com.example.android.todolist;

import android.os.Bundle;
import androidx.core.content.SharedPreferencesCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_main);
        //setContentView(R.layout.activity_main);
        //Preference myprep = findPreference("key_1");
    }
}
