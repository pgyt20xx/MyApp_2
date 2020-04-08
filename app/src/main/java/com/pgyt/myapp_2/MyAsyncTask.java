package com.pgyt.myapp_2;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
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
    private String mPreviousText;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // 前処理

    }

    @Override
    protected String doInBackground(Void... voids) {

        Log.d(TAG,"doInBackground Start");

        Context context = MyContext.getInstance().getMyContext();
        if (context == null) {
            return null;
        }

        mClipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        if (mClipboardManager == null) {
            return null;
        }

        while (true) {
            try {
                Thread.sleep(2000);

                if (mClipboardManager != null && mClipboardManager.hasPrimaryClip()) {
                    ClipData data = mClipboardManager.getPrimaryClip();

                    ClipData.Item item = data.getItemAt(0);
                    if (item == null || item.getText() == null) {
                        continue;
                    }

                    // 2周目の呼び出し時は登録しない(ブラウザ内コピー等)
                    if (item.getText().toString().equals(mPreviousText)) {
                        continue;
                    }

                    // チェック用の変数
                    mPreviousText = item.getText().toString();

                    // 通知バーの更新
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // TODO
                        //setNotification();
                        Log.d(TAG,"doInBackground " + item.getText().toString());

                    }
                }




            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
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
