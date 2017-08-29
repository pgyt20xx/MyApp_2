package com.pgyt.myapp_2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pgyt.myapp_2.model.CategoryBean;
import com.pgyt.myapp_2.model.ContentsBean;


class DBHelper {
    private SQLiteDatabase sqLiteDatabase;
    private static final String BLANK_STRING = "";
    private static final String TABLE_NAME_CATEGORY = "CATEGORY";
    private static final String TABLE_NAME_CONTENTS = "CONTENTS";


    DBHelper(final SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    /**
     * カテゴリーテーブルのインサート文
     *
     * @param params CategoryBean
     */
    long insertCategory(CategoryBean params) {
        long id = this.sqLiteDatabase.insert(TABLE_NAME_CATEGORY, BLANK_STRING, params.getParams());
        return id;
    }

    /**
     * コンテンツテーブルのインサート文
     *
     * @param params ContentsBean
     */
    long insertContents(ContentsBean params) {
        long id = this.sqLiteDatabase.insert(TABLE_NAME_CONTENTS, BLANK_STRING, params.getParams());
        return id;
    }


    /**
     * カテゴリーテーブルのセレクト文
     *
     * @param param String
     * @return Cursor
     */
    Cursor selectCategory(String param) {
        Cursor cursor;
        if (param.isEmpty()) {
            String sql = "SELECT id, category_name FROM CATEGORY ORDER BY id;";
            cursor = sqLiteDatabase.rawQuery(sql, null);
        } else {
            String sql = "SELECT id, category_name FROM CATEGORY WHERE category_name = ? ORDER BY id;";
            cursor = sqLiteDatabase.rawQuery(sql, new String[]{param});
        }
        return cursor;
    }

    /**
     * 全テーブルの削除
     */
    void deletetAll() {
        sqLiteDatabase.delete("CATEGORY", "category_name <> ?", new String[]{"CLIPBOARD"});
        sqLiteDatabase.delete("CONTENTS", null, null);
    }

	/**
     * カテゴリーテーブルの削除
     * 紐づくコンテンツテーブルの削除も行う。
     *
     * @param param String
     */
    void deletetCategory(String param) {
        sqLiteDatabase.delete("CATEGORY", "category_name = ?", new String[]{param});
        sqLiteDatabase.delete("CONTENTS", "category_name = ?", new String[]{param});
    }
	
    /**
     * コンテンツテーブルの削除
     *
     * @param param String
     */
    void deletetContents(String param) {
        sqLiteDatabase.delete("CONTENTS", "id = ?", new String[]{param});
    }

    /**
     * コンテンツテーブルの内容をすべて取得するのセレクト文
     *
     * @return Cursor
     */
    Cursor selectAllContents() {
        String sql = "SELECT id, category_name, contents_title, contents FROM CONTENTS ORDER BY category_name, id DESC;";
        return sqLiteDatabase.rawQuery(sql, null);
    }

//    /**
//     * クリーンアップ
//     * TODO; 不要であれば削除する
//     */
//    void cleanup(){
//        if (this.sqLiteDatabase != null){
//            this.sqLiteDatabase.close();
//            this.sqLiteDatabase = null;
//        }
//    }

//    /**
//     * データベース削除
//     * @param context Context
//     * @return boolean
//     * TODO; 不要であれば削除する
//     */
//    boolean isDatabaseDelete(final Context context){
//        boolean result = false;
//
//        if(this.sqLiteDatabase != null){
//            File file = context.getDatabasePath(dbOpenHelper.getDatabaseName());
//
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
//                result = SQLiteDatabase.deleteDatabase(file);
//            }
//        }
//        return result;
//    }

}
