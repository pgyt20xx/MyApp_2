package com.pgyt.myapp_2;

import android.app.LoaderManager;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity
        implements LoaderManager.LoaderCallbacks<SharedPreferences> {

    private static String TAG = "SettingsActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate Start");

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();

        getLoaderManager().initLoader(0, null, this);

        Log.d(TAG, "onCreate End");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy Start");


        Log.d(TAG, "onDestroy End");
    }

    @Override
    public Loader<SharedPreferences> onCreateLoader(int id, Bundle bundle) {
        Log.d(TAG, "onCreateLoader Start");
        Log.d(TAG, "onCreateLoader End");

        return(new SharedPreferencesLoader(this));
    }

    @Override
    public void onLoadFinished(Loader<SharedPreferences> loader, SharedPreferences sharedPreferences) {
        Log.d(TAG, "onLoadFinished Start");

        boolean statusBarValue = sharedPreferences.getBoolean("checkbox_status_bar_key", false);
        boolean maxRow = sharedPreferences.getBoolean("checkbox_maxrow_key", false);

        Log.d(TAG, "onLoadFinished statusBarValue: " + statusBarValue + " maxRow: " + maxRow);

        Log.d(TAG, "onLoadFinished End");

    }

    @Override
    public void onLoaderReset(Loader<SharedPreferences> loader) {
        Log.d(TAG, "onLoaderReset Start");

        Log.d(TAG, "onLoaderReset End");
    }
}
