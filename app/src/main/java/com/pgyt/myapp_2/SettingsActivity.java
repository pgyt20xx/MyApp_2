package com.pgyt.myapp_2;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.KeyEvent;

public class SettingsActivity extends PreferenceActivity
        implements LoaderManager.LoaderCallbacks<SharedPreferences> {
    private static String TAG = "SettingsActivity";

    private boolean settingChengedFlg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate Start");

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();

        SharedPreferencesLoader sharedPreferencesLoader = (SharedPreferencesLoader) getLoaderManager().initLoader(0, null, this);
        sharedPreferencesLoader.setPreferenceChengeListener(new SharedPreferencesLoader.PreferenceChengeListener() {
            @Override
            public boolean preferenceChenged(String key) {
                Log.d(TAG, key + " chenged");

                // 変更した設定によって処理を分ける場合
                switch (key) {
                    case "checkbox_status_bar_key":
                        settingChengedFlg = true;

                        break;

                    case "checkbox_maxrow_key":
                        settingChengedFlg = true;

                        break;
                }
                return false;
            }
        });

        Log.d(TAG, "onCreate End");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy Start");

        Log.d(TAG, "onDestroy End");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown Start");

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent data = new Intent();
            data.putExtra("settingChengedFlg", settingChengedFlg);
            setResult(RESULT_OK, data);
            finish();
            return true;
        }
        Log.d(TAG, "onKeyDown End");

        return false;
    }

    @Override
    public Loader<SharedPreferences> onCreateLoader(int id, Bundle bundle) {
        Log.d(TAG, "onCreateLoader Start");
        Log.d(TAG, "onCreateLoader End");

        return (new SharedPreferencesLoader(this));
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
