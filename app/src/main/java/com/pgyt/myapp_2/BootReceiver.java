package com.pgyt.myapp_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(TAG, "onReceive Application Start MyApp_2");
//            Intent intentActivity = new Intent(context, MainActivity.class);
//            intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intentActivity);

            Toast.makeText(context, "Application Start", Toast.LENGTH_LONG).show();

            // ステータスバーに表示
            new MainActivity().setNotification();
        }
    }
}
