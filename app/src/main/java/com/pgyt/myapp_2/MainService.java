package com.pgyt.myapp_2;


import android.app.*;
import android.content.*;
import android.content.ClipboardManager.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    private String mPreviousText;
	
	private String mClipBoard;

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
                // コピーしたテキストの登録
				mClipBoard = item.getText().toString();
                insertNewContents(item);

            }
            Log.d(TAG, "OnPrimaryClipChangedListener End");
        }
    };

    /**
     * テキストをDBに登録
     * @param item ClipData.Item
     */
    private void insertNewContents(ClipData.Item item){
        Log.d(TAG, "insertNewContents Start");

        // アプリ内のコンテンツは登録しない。
        SQLiteDatabase sqLiteDatabase = new DBOpenHelper(this.getApplicationContext()).getWritableDatabase();
        try {
            Cursor cursor = new DBHelper(sqLiteDatabase).selectAllContents();
            boolean isEof = cursor.moveToFirst();
            ArrayList<String> allContents = new ArrayList<>();
            while (isEof) {
                allContents.add(cursor.getString(cursor.getColumnIndex("contents")));
                isEof = cursor.moveToNext();
            }
            cursor.close();

            // アプリ内登録コンテンツは登録しない。
            if(allContents.lastIndexOf(item.getText().toString()) >= 0){
                return;
            }

            // 既存コンテンツ
            LinkedHashMap<String, String[]> contentsMap = new LinkedHashMap<>();
            if (MainActivity.CONTENTS.containsKey(MainActivity.CLIPBOARD_TAB_NAME)) {
                contentsMap = MainActivity.CONTENTS.get(MainActivity.CLIPBOARD_TAB_NAME);
            }

            // コンテンツの登録
            Toast.makeText(getApplicationContext(), "\"" + item.getText().toString() + "\"" + " copied", Toast.LENGTH_SHORT).show();
            ContentsBean param = new ContentsBean();
            param.setCategory_name(MainActivity.CLIPBOARD_TAB_NAME);
            param.setContents_title("DummyContentsTitle");
            param.setContents(item.getText().toString());
            Long id = new DBHelper(sqLiteDatabase).insertContents(param);

            // 1行目に追加
            LinkedHashMap<String, String[]> tContentsMap = new LinkedHashMap<>();
            tContentsMap.put(id.toString(), new String[]{"DummyContentsTitle", item.getText().toString()});
            if (contentsMap.size() != 0) {
                // 既存コンテンツ追加
                tContentsMap.putAll(contentsMap);
            }
            contentsMap = tContentsMap;
            MainActivity.CONTENTS.put(MainActivity.CLIPBOARD_TAB_NAME, contentsMap);
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
		
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(mClipBoard);
        mBuilder.setContentText(mPreviousText);
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
