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
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;

public class MainService extends Service{

    private static final String TAG = "MainService";

    private static final int NOTIFICATION_ID = 10;

	private ClipboardManager mClipboardManager;
	
	String mPreviousText = "";
	

    @Override
    public void onCreate() {
        super.onCreate();
		mClipboardManager = (ClipboardManager) getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
        if (mClipboardManager != null) {
            mClipboardManager.addPrimaryClipChangedListener(clipListener);
        } else {
            Log.e(TAG, "ClipboardServiceの取得に失敗しました。サービスを終了します。");
            this.stopSelf();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onReceive Application Start MyApp_2");

        // TODO: 完成後削除
        Toast.makeText(this, "MyService#onStartCommand", Toast.LENGTH_SHORT).show();

        // 通知を設定
        setNotification();
//		
//		cm = (ClipboardManager) getSystemService(this.CLIPBOARD_SERVICE);
//
//		cm.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
//				@Override
//				public void onPrimaryClipChanged() {
//					if (cm != null && cm.hasPrimaryClip()) {
//						ClipData data = cm.getPrimaryClip();
//						ClipData.Item item = data.getItemAt(0);
//						Toast
//							.makeText(
//							getApplicationContext(),
//							"コピーあるいはカットされた文字列:\n"
//                            + item.coerceToText(getApplicationContext()),
//							Toast.LENGTH_SHORT)
//							.show();
//					}				
//				}
//			}
//		);
		

        // 強制終了時に再起動
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
		super.onDestroy();
        if (mClipboardManager != null) {
            mClipboardManager.removePrimaryClipChangedListener(clipListener);
        }
		Log.d(TAG, "onReceive Application Start MyApp_2");
        Toast.makeText(this, "MyService#onDestroy", Toast.LENGTH_SHORT).show();
    }
	
	private OnPrimaryClipChangedListener clipListener = new OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
			
            if (mClipboardManager != null && mClipboardManager.hasPrimaryClip()) {
                ClipData data = mClipboardManager.getPrimaryClip();
                ClipData.Item item = data.getItemAt(0);
				
				if(mPreviousText.equals(item.getText().toString())) {
					return;
				} else{
					/// do something
					mPreviousText = item.getText().toString();
				}
                Toast
                    .makeText(
					getApplicationContext(),
					"コピーあるいはカットされた文字列:\n"
					+ mPreviousText,
					Toast.LENGTH_SHORT)
                    .show();
            }
        }
    };

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
