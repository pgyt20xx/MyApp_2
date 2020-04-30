package com.pgyt.myapp_2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.pgyt.myapp_2.model.CategoryBean;
import com.pgyt.myapp_2.model.ContentsBean;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.pgyt.myapp_2.CommonConstants.BLANK_STRING;
import static com.pgyt.myapp_2.CommonConstants.COLUMN_CATEGORY_NAME;
import static com.pgyt.myapp_2.CommonConstants.COLUMN_CONTENTS;
import static com.pgyt.myapp_2.CommonConstants.COLUMN_CONTENTS_TITLE;
import static com.pgyt.myapp_2.CommonConstants.COLUMN_ID;
import static com.pgyt.myapp_2.CommonConstants.MAX_ROWSIZE_DEFAULT;
import static com.pgyt.myapp_2.CommonConstants.MAX_ROWSIZE_MAXIMUM;
import static com.pgyt.myapp_2.MainActivity.*;

public class CommonMethod {
    private Context context;
    private static final String TAG = "CommonMethod";

    public CommonMethod(){
    }

    /**
     * カテゴリー名取得
     *
     * @return ArrayList
     */
    static ArrayList<CategoryBean> getAllCategory() {
        Log.d(TAG, "getAllCategory Start");

        Context context = MyContext.getInstance().getMyContext();
        // DBからカテゴリー名を取得する
        ArrayList<CategoryBean> result = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = new DBOpenHelper(context).getWritableDatabase();
        try {
            Cursor cursor = new DBHelper(sqLiteDatabase).selectCategory(BLANK_STRING);
            boolean isEof = cursor.moveToFirst();
            while (isEof) {
                CategoryBean category = new CategoryBean();
                category.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                category.setCategory_name(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
                result.add(category);
                isEof = cursor.moveToNext();
            }
            cursor.close();

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());

        } finally {
            sqLiteDatabase.close();
        }

        Log.d(TAG, "getAllCategory End");
        return result;
    }

    /**
     * 全コンテンツを取得
     *
     * @return HashMap
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    static LinkedHashMap<String, ArrayList<ContentsBean>> getAllContents(ArrayList<CategoryBean> categoryList) {
        Log.d(TAG, "getAllContents Start");

        Context context = MyContext.getInstance().getMyContext();

        // DBからカテゴリー名を取得する
        LinkedHashMap<String, ArrayList<ContentsBean>> result = new LinkedHashMap<>();
        SQLiteDatabase sqLiteDatabase = new DBOpenHelper(context).getWritableDatabase();

        // 設定から最大行数を取得
        int limit = MAX_ROWSIZE_DEFAULT;
        if (settingMaxRow) {
            limit = MAX_ROWSIZE_MAXIMUM;
        }
        try {
            for (CategoryBean category : categoryList) {
                Cursor cursor = new DBHelper(sqLiteDatabase).selectContentsList(new String[]{category.getCategory_name(), String.valueOf(limit)});
                boolean isEof = cursor.moveToFirst();
                ArrayList<ContentsBean> contentsList = new ArrayList<>();
                while (isEof) {
                    ContentsBean contents = new ContentsBean();
                    contents.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                    contents.setCategory_name(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
                    contents.setContents_title(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENTS_TITLE)));
                    contents.setContents(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENTS)));
                    contentsList.add(contents);
                    isEof = cursor.moveToNext();
                }
                result.put(category.getCategory_name(), contentsList);
                cursor.close();
            }

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());

        } finally {
            sqLiteDatabase.close();
        }

        Log.d(TAG, "getAllContents End");
        return result;
    }
}
