package com.pgyt.myapp_2;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.pgyt.myapp_2.model.ContentsBean;

import static com.pgyt.myapp_2.CommonConstants.CLIPBOARD_TAB_NAME;
import static com.pgyt.myapp_2.CommonConstants.CLIP_BOARD_TITLE_NAME;
import static com.pgyt.myapp_2.CommonConstants.NOTIFICATION_ID;
import static com.pgyt.myapp_2.CommonConstants.STATUS_BAR_TITLE;


public class MainService extends Service {
    private static final String TAG = "MainService";

    private ClipboardManager mClipboardManager;
    private String mPreviousText;
    private String mClipBoard;
    private boolean settingDisplayStatusBar;
    private NotificationCompat.Builder mBuilder;
    private MyAsyncTask task;

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
            task = new MyAsyncTask();
            task.setListener(createAsyncTaskListener());
            task.execute();
        } else {
            Log.e(TAG, "error get clipboard. service end.");
            this.stopSelf();
        }
        Log.d(TAG, "onCreate End");
    }

    private MyAsyncTask.Listener createAsyncTaskListener() {
        return new MyAsyncTask.Listener() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, result);
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClipChanged(String clipItem) {
                setNotification();
            }

        };
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand Start");

        // アプリの設定を取得
        getPreference();

        // 通知を設定
        if (this.settingDisplayStatusBar) {
            setNotification();
        } else {
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.cancel(NOTIFICATION_ID);
        }

        Log.d(TAG, "onStartCommand End");
        // 強制終了時に再起動
        return START_STICKY;
    }


    /**
     * 設定を取得する。
     */
    private void getPreference() {

        // 設定値を取得
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // ステータスバーに表示するか
        this.settingDisplayStatusBar = preferences.getBoolean("checkbox_status_bar_key", false);
    }


    /**
     * クリップボードの監視
     * TODO バックグラウンドで動作しない。→Android10
     */
    private OnPrimaryClipChangedListener clipListener = new OnPrimaryClipChangedListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void onPrimaryClipChanged() {
            Log.d(TAG, "OnPrimaryClipChangedListener Start");

            if (mClipboardManager != null && mClipboardManager.hasPrimaryClip()) {
                ClipData data = mClipboardManager.getPrimaryClip();

                // データチェック
                if (data == null) {
                    return;
                }

                ClipData.Item item = data.getItemAt(0);
                if (item == null || item.getText() == null) {
                    return;
                }

                // 2周目の呼び出し時は登録しない(ブラウザ内コピー等)
                if (item.getText().toString().equals(mPreviousText)) {
                    return;
                }

                // 2週目チェック用の変数
                mPreviousText = item.getText().toString();

                // 通知バーの更新
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setNotification();
                }

                // コピーしたテキストの登録
//                insertNewContents(item);

            }
            Log.d(TAG, "OnPrimaryClipChangedListener End");
        }
    };



    /**
     * ステータスバーに常駐
     * TODO
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    void setNotification() {
        Log.d(TAG, "setNotification Start");

        // チャンネル登録
        String channelId = "clip"; // 通知チャンネルのIDにする任意の文字列
        String name = "更新情報"; // 通知チャンネル名
        int importance = NotificationManager.IMPORTANCE_MIN; // デフォルトの重要度
        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription("通知チャンネルの説明"); // 必須ではない

        // 通知チャンネルの設定のデフォルト値。設定必須ではなく、ユーザーが変更可能。
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel.enableVibration(false);
        channel.enableLights(true);

        // ランチャー上でアイコンバッジを表示するかどうか
        channel.setShowBadge(false);
        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.createNotificationChannel(channel);

        mClipBoard = "";
        // TODO :getPrimaryClip instead
        if (mClipboardManager != null && mClipboardManager.getText() != null) {
            mClipBoard = mClipboardManager.getText().toString();
        }

        mBuilder = new NotificationCompat.Builder(this, channelId);
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

//        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        mNotifyMgr.notify(NOTIFICATION_ID, notification);
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, mBuilder.build());
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
        task.setListener(null);
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
