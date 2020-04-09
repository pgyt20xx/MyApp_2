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
 * Created by pgyt on 2018/03/24.
 */

public class MyAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "MyAsyncTask";
    private Listener listener;
    private ClipboardManager mClipboardManager;
    private Context context;
    private String mPreviousText;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // 前処理

    }

    @Override
    protected String doInBackground(Void... voids) {

        Log.d(TAG,"doInBackground Start");

        this.context = MyContext.getInstance().getMyContext();
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

                        // 通知バーを更新する為にサービスにコールバックする
                        listener.onSuccess(item.getText().toString());
                        Log.d(TAG,"doInBackground " + item.getText().toString());
                    }

                    // コピーしたテキストの登録
                    insertNewContents(item);
                }




            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
//        TODO
//        return null;
    }

    /**
     * テキストをDBに登録
     *
     * @param item ClipData.Item
     */
    private void insertNewContents(ClipData.Item item) {
        Log.d(TAG, "insertNewContents Start");

        // アプリ内のコンテンツは登録しない。
        SQLiteDatabase sqLiteDatabase = new DBOpenHelper(this.context).getWritableDatabase();
        try {
            // 既にあるコンテンツは登録しない
            Cursor cursor = new DBHelper(sqLiteDatabase).selectContents(item.getText().toString());
            int cnt = cursor.getCount();
            cursor.close();
            if (cnt > 0) {
                return;
            }

            // コンテンツの登録
            Toast.makeText(context, "\"" + item.getText().toString() + "\"" + " copied", Toast.LENGTH_SHORT).show();
            ContentsBean contents = new ContentsBean();
            contents.setCategory_name(CLIPBOARD_TAB_NAME);
            contents.setContents_title(CLIP_BOARD_TITLE_NAME);
            contents.setContents(item.getText().toString());
            new DBHelper(sqLiteDatabase).insertContents(contents);

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());

        } finally {
            sqLiteDatabase.close();
        }
        Log.d(TAG, "insertNewContents End");
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
        void onClipChanged(String clipItem);

    }
}
