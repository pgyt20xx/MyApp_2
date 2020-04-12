package com.pgyt.myapp_2;

import android.app.Application;
import android.content.Context;

/**
 * MyContext
 */
public class MyContext {
    private static MyContext instance = null;
    private Context applicationContext;

    static void onCreateApplication(Context applicationContext) {
        instance = new MyContext(applicationContext);
    }

    private MyContext(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static MyContext getInstance() {
        if (instance == null) {
            throw new RuntimeException("MyContext should be initialized");
        }
        return instance;
    }

    public Context getMyContext() {
        return this.applicationContext;
    }

    public static class MyApplication extends Application {
        @Override
        public void onCreate() {
            super.onCreate();
            MyContext.onCreateApplication(getApplicationContext());
        }
    }
}