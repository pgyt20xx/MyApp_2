package com.pgyt.myapp_2;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.pgyt.myapp_2.CommonConstants.DB_NAME;
import static com.pgyt.myapp_2.CommonConstants.DB_VERSION;

/**
 * DBOpenHelper
 */
class DBOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBOpenHelper";

    private Context m_context;

    DBOpenHelper(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.m_context = context;
    }

    @Override
    public void onCreate(final SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate version : " + sqLiteDatabase.getVersion());
        this.execFileSQL(sqLiteDatabase, "create_table.sql");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, final int oldVersion, final int newVersion) {
        Log.d(TAG, "onUpgrade version : " + sqLiteDatabase.getVersion());
        Log.d(TAG, "onUpgrade oldVersion : " + oldVersion);
        Log.d(TAG, "onUpgrade newVersion : " + newVersion);
    }

    private void execFileSQL(SQLiteDatabase sqLiteDataBase, String fileName) {
        InputStream in = null;
        InputStreamReader inReader = null;
        BufferedReader reader = null;

        try {
            in = m_context.getAssets().open(fileName);
            inReader = new InputStreamReader(in, "UTF-8");
            reader = new BufferedReader(inReader);

            String s;
            while ((s = reader.readLine()) != null) {
                s = s.trim();

                if (0 < s.length()) {
                    sqLiteDataBase.execSQL(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inReader != null) {
                try {
                    inReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
