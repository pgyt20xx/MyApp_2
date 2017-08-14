package com.pgyt.myapp_2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.pgyt.myapp_2.model.CategoryBean;
import com.pgyt.myapp_2.model.ContentsBean;

import java.io.File;


public class DBHelper
{
	public static final String TAG = "DBHelper";
    public SQLiteDatabase sqLiteDatabase;
    private final DBOpenHelper dbOpenHelper;

    private static final String BLANK_STRING = "";
    private static final String TABLE_NAME_CATEGORY = "CATEGORY";
    private static final String TABLE_NAME_CONTENTS = "CONTENTS";


    public DBHelper(final Context context) {
        this.dbOpenHelper = new DBOpenHelper(context);
        establishDb();
    }

    private void establishDb(){
        if(this.sqLiteDatabase == null){
            this.sqLiteDatabase = this.dbOpenHelper.getWritableDatabase();
        }

        // 完成したら削除する
        isDatabaseDelete(dbOpenHelper.m_context);
    }

    /**
     * カテゴリーテーブルのインサート文
     * @param params
     */
    public void insertCategory(CategoryBean params){
        this.sqLiteDatabase.insert(TABLE_NAME_CATEGORY, BLANK_STRING, params.getParams());
    }

    /**
     * コンテンツテーブルのインサート文
     * @param params
     */
    public void insertContents(ContentsBean params){
        this.sqLiteDatabase.insert(TABLE_NAME_CONTENTS, BLANK_STRING, params.getParams());
    }

    public void cleanup(){
        if (this.sqLiteDatabase != null){
            this.sqLiteDatabase.close();
            this.sqLiteDatabase = null;
        }
    }

    /**
     * カテゴリーテーブルのセレクト文
     * @param param
     * @return
     */
    public Cursor selectCategory(String param){
        SQLiteDatabase readDb = dbOpenHelper.getReadableDatabase();
        Cursor cursor = null;
        if(param.isEmpty()){
            String sql = "SELECT id, category_name FROM CATEGORY ORDER BY id;";
            cursor = readDb.rawQuery(sql, null);
        }else{
            String sql = "SELECT id, category_name FROM CATEGORY WHERE category_name = '" + param + "' ORDER BY id;";
            cursor = readDb.rawQuery(sql, new String[]{param});
        }
        return cursor;
    }

    /**
     * カテゴリーテーブルの削除
     * 紐づくコンテンツテーブルの削除も行う。
     * @param param
     */
    public void deletetCategory(String param){
        SQLiteDatabase readDb = dbOpenHelper.getReadableDatabase();
        readDb.delete("CATEGORY", "category_name = ?", new String[]{param});
        readDb.delete("CONTENTS", "category_name = ?", new String[]{param});
    }

    /**
     * コンテンツテーブルの内容をすべて取得するのセレクト文
     * @return
     */
    public Cursor selectAllContents(){
        SQLiteDatabase readDb = dbOpenHelper.getReadableDatabase();
        Cursor cursor = null;

        String sql = "SELECT id, category_name, contents FROM CONTENTS ORDER BY category_name, id;";
        cursor = readDb.rawQuery(sql, null);

        return cursor;
    }

    public boolean isDatabaseDelete (final Context context){
        boolean result = false;

        if(this.sqLiteDatabase != null){
            File file = context.getDatabasePath(dbOpenHelper.getDatabaseName());

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                result = this.sqLiteDatabase.deleteDatabase(file);
            }
        }
        return result;
    }

}
