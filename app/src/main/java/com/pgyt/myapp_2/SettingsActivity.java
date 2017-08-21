package com.pgyt.myapp_2;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by yuich on 2017/08/22.
 */

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();

    }
}
