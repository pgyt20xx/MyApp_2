package com.pgyt.myapp_2;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by yuich on 2017/08/22.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }
}
