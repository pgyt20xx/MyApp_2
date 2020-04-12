package com.pgyt.myapp_2;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.pgyt.myapp_2.model.ContentsBean;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.pgyt.myapp_2.CommonConstants.CLIPBOARD_TAB_NAME;
import static com.pgyt.myapp_2.CommonConstants.CLIP_BOARD_TITLE_NAME;

/**
 * MyAsyncTask
 */

public class MyAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "MyAsyncTask";
    private Listener listener;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // 前処理

    }

    @Override
    protected String doInBackground(Void... voids) {
        // 本処理

        Log.d(TAG, "doInBackground Start");

        Log.d(TAG, "doInBackground end");

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        // 後処理
        if (listener != null) {
            listener.onSuccess(result);
        }
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onSuccess(String result);
    }
}
