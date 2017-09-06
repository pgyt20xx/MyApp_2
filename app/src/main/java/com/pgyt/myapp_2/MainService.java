package com.pgyt.myapp_2;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.pgyt.myapp_2.model.ContentsBean;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class MainService extends Service {

    private static final String TAG = "MainService";

    private static final String CLIP_BOARD_TITLE_NAME = "DummyContentsTitle";

    private static final String STATUS_BAR_TITLE = "Current ClipBoard";

    private static final int NOTIFICATION_ID = 10;

    private ClipboardManager mClipboardManager;

    private String mPreviousText;

    private String mClipBoard;


    NotificationCompat.Builder mBuilder;

    public MainService() {
        mPreviousText = "";
        mClipBoard = "";

    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate Start");

        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (mClipboardManager != null) {
            mClipboardManager.addPrimaryClipChangedListener(clipListener);
        } else {
            Log.e(TAG, "error get clipboard. service end.");
            this.stopSelf();
        }
        Log.d(TAG, "onCreate End");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand Start");

        // 通知を設定
        setNotification();

        Log.d(TAG, "onStartCommand End");
        // 強制終了時に再起動
        return START_STICKY;
    }


    /**
     * クリップボードの監視
     */
    private OnPrimaryClipChangedListener clipListener = new OnPrimaryClipChangedListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void onPrimaryClipChanged() {
            Log.d(TAG, "OnPrimaryClipChangedListener Start");

            if (mClipboardManager != null && mClipboardManager.hasPrimaryClip()) {
                ClipData data = mClipboardManager.getPrimaryClip();
                ClipData.Item item = data.getItemAt(0);

                // 2周目の呼び出し時は登録しない(ブラウザ内コピー等)
                if (item.getText().toString().equals(mPreviousText)) {
                    return;
                }

                mPreviousText = item.getText().toString();

                // 通知バーの更新
                setNotification();

                // コピーしたテキストの登録
                insertNewContents(item);

            }
            Log.d(TAG, "OnPrimaryClipChangedListener End");
        }
    };

    /**
     * テキストをDBに登録
     *
     * @param item ClipData.Item
     */
    private void insertNewContents(ClipData.Item item) {
        Log.d(TAG, "insertNewContents Start");

        // アプリ内のコンテンツは登録しない。
        SQLiteDatabase sqLiteDatabase = new DBOpenHelper(this.getApplicationContext()).getWritableDatabase();
        try {
            // 既にあるコンテンツは登録しない
            for (ContentsBean contents : MainActivity.mContentsList) {
                if (contents.getContents().equals(item.getText().toString())) {
                    return;
                }
            }

            // コンテンツの登録
            Toast.makeText(getApplicationContext(), "\"" + item.getText().toString() + "\"" + " copied", Toast.LENGTH_SHORT).show();
            ContentsBean contents = new ContentsBean();
            contents.setCategory_name(MainActivity.CLIPBOARD_TAB_NAME);
            contents.setContents_title(CLIP_BOARD_TITLE_NAME);
            contents.setContents(item.getText().toString());
            int id = (int) new DBHelper(sqLiteDatabase).insertContents(contents);

            // 1行目に追加
            contents.setId(id);
            MainActivity.mContentsList.add(0, contents);

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());

        } finally {
            sqLiteDatabase.close();
        }
        Log.d(TAG, "insertNewContents End");
    }

    /**
     * ステータスバーに常駐
     */
    void setNotification() {
        Log.d(TAG, "setNotification Start");

        if (mClipboardManager != null) {
            mClipBoard = mClipboardManager.getText().toString();
        }

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(STATUS_BAR_TITLE);
        mBuilder.setContentText(mClipBoard);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setOngoing(true);
        mBuilder.setAutoCancel(false);

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(NOTIFICATION_ID, notification);
        startForeground(NOTIFICATION_ID, notification);
        Log.d(TAG, "setNotification End");
    }

    /**
     * 通知バーの常駐解除
     */
    private void cancelNotification() {
        Log.d(TAG, "cancelNotification Start");

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(R.string.app_name);

        Log.d(TAG, "cancelNotification End");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy Start");
        super.onDestroy();
        cancelNotification();
        if (mClipboardManager != null) {
            mClipboardManager.removePrimaryClipChangedListener(clipListener);
        }

        Log.d(TAG, "onDestroy End");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind Start");

        Log.d(TAG, "onBind End");

        return null;
    }
}
