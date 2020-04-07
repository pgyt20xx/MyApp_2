package com.pgyt.myapp_2;

import android.content.ClipboardManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by pgyt on 2018/03/24.
 */

public class MyAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "MyAsyncTask";
    private Listener listener;
    private ClipboardManager mClipboardManager;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // 前処理

    }

    @Override
    protected String doInBackground(Void... voids) {

        Log.d(TAG,"doInBackground Start");

        int i = 0;
        while (true) {
            try {
                Thread.sleep(1000);

                Log.d(TAG,"doInBackground" + String.valueOf(i));

            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }

            i++;
        }
//        TODO
//        return null;
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
