package com.pgyt.myapp_2;


import android.app.*;
import android.content.*;
import android.content.ClipboardManager.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v7.app.*;
import android.util.*;
import android.widget.*;
import com.pgyt.myapp_2.model.*;
import java.util.*;


public class MainService extends Service {

    private static final String TAG = "MainService";

    private static final int NOTIFICATION_ID = 10;

    private ClipboardManager mClipboardManager;

    private static final int CLIPBOARD_TAB_POSITON = 0;

    String mPreviousText = "";


    @Override
    public void onCreate() {
        super.onCreate();
        mClipboardManager = (ClipboardManager) getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
        if (mClipboardManager != null) {
            mClipboardManager.addPrimaryClipChangedListener(clipListener);
        } else {
            Log.e(TAG, "error get clipboard. service end.");
            this.stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "MyService#onStartCommand");

        // 通知を設定
        setNotification();

        // 強制終了時に再起動
        return START_STICKY;
    }


    /**
     * クリップボードの監視
     */
    private OnPrimaryClipChangedListener clipListener = new OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {

            if (mClipboardManager != null && mClipboardManager.hasPrimaryClip()) {
                ClipData data = mClipboardManager.getPrimaryClip();
                ClipData.Item item = data.getItemAt(0);

				// 2周目の呼び出し時は登録しない(ブラウザ内コピー等)
                if (mPreviousText.equals(item.getText().toString())) {
                    return;
                }
				
				mPreviousText = item.getText().toString();
                // コピーしたテキストの登録
                insertNewContents(item);

            }
        }
    };

    /**
     * テキストをDBに登録
     * @param item ClipData.Item
     */
    private void insertNewContents(ClipData.Item item){
        // 追加したコンテンツを格納
        LinkedHashMap<String, String> contentsMap = new LinkedHashMap<>();
		
        // 既存コンテンツ
        contentsMap = MainActivity.CONTENTS.get(MainActivity.TITLE_NAME.get(CLIPBOARD_TAB_POSITON));

        // 既に登録されているものは登録しない。
		List<String> tContents = new ArrayList<String>(contentsMap.values());
        if(tContents.lastIndexOf(item.getText().toString()) < 0){
            Toast.makeText(getApplicationContext(), "\"" + item.getText().toString() + "\"" + " copied", Toast.LENGTH_SHORT).show();
            try {
                DBHelper dBhelper = new DBHelper(getApplicationContext());
                ContentsBean param = new ContentsBean();
                param.setCategory_name(MainActivity.TITLE_NAME.get(CLIPBOARD_TAB_POSITON));
                param.setContents(item.getText().toString());
                Long id = dBhelper.insertContents(param);
				
				// 1行目に追加
				LinkedHashMap<String, String> tContentsMap = new LinkedHashMap<>();
				tContentsMap.put(id.toString(), item.getText().toString());
				if (contentsMap.size() != 0) {
					// 既存コンテンツ追加
					tContentsMap.putAll(contentsMap);
				}
				contentsMap = tContentsMap;
				MainActivity.CONTENTS.put(MainActivity.TITLE_NAME.get(CLIPBOARD_TAB_POSITON), contentsMap);

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }
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

    /**
     * 通知バーの常駐解除
     */
    private void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(R.string.app_name);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onReceive Application Start MyApp_2");
        super.onDestroy();
        cancelNotification();
        if (mClipboardManager != null) {
            mClipboardManager.removePrimaryClipChangedListener(clipListener);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
