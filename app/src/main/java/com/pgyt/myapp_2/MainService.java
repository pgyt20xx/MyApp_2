package com.pgyt.myapp_2;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class MainService extends Service{

    private static final String TAG = "MainService";

    private static final int NOTIFICATION_ID = 10;


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onReceive Application Start MyApp_2");

        // TODO: 完成後削除
        Toast.makeText(this, "MyService#onStartCommand", Toast.LENGTH_SHORT).show();

        // 通知を設定
        setNotification();

        // 強制終了時に再起動
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onReceive Application Start MyApp_2");
        Toast.makeText(this, "MyService#onDestroy", Toast.LENGTH_SHORT).show();
    }

    /**
     * ステータスバーに常駐
     */
    void setNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(getString(R.string.app_name));
        mBuilder.setContentText("CLIP_BOARD");
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

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
