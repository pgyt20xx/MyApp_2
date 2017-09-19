package com.pgyt.myapp_2;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class SharedPreferencesLoader extends AsyncTaskLoader<SharedPreferences>
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static String TAG = "SharedPreferencesLoader";

    private PreferenceChengeListener preferenceChengeListener;
    private SharedPreferences prefs;

    public SharedPreferencesLoader(Context context) {
        super(context);
    }

    @Override
    public SharedPreferences loadInBackground() {
        Log.d(TAG, "loadInBackground Start");

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.registerOnSharedPreferenceChangeListener(this);

        Log.d(TAG, "loadInBackground End");

        return (prefs);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // notify loader that content has changed
        Log.d(TAG, "onSharedPreferenceChanged Start");

        onContentChanged();

        // 変更をアクティビティに通知
        preferenceChengeListener.preferenceChenged(key);

        Log.d(TAG, "onSharedPreferenceChanged End");
    }

    /**
     * starts the loading of the data
     * once result is ready the onLoadFinished method is called
     * in the main thread. It loader was started earlier the result
     * is return directly
     * method must be called from main thread.
     */
    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading Start");

        if (prefs != null) {
            deliverResult(prefs);
        }

        if (takeContentChanged() || prefs == null) {
            forceLoad();
        }

        Log.d(TAG, "onStartLoading End");

    }

    public static void persist(final SharedPreferences.Editor editor) {
        Log.d(TAG, "persist Start");

        editor.apply();

        Log.d(TAG, "persist End");

    }

    /**
     * チェックボックスチェンジのインターフェース
     */
    interface PreferenceChengeListener {
        boolean preferenceChenged(String key);
    }

    /**
     * ロングクリックイベントのリスナーセット
     *
     * @param listener OnItemLongClickListener
     */
    void setPreferenceChengeListener(PreferenceChengeListener listener) {
        this.preferenceChengeListener = listener;
    }
}
