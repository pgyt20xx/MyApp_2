package com.pgyt.myapp_2;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment {
    private static String TAG = "SettingsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate Start");

        addPreferencesFromResource(R.xml.preference);

        Log.d(TAG, "onCreate End");
    }

}
