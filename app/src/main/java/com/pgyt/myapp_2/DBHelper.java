package com.pgyt.myapp_2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.pgyt.myapp_2.model.CategoryBean;
import com.pgyt.myapp_2.model.ContentsBean;

import java.io.File;

import static com.pgyt.myapp_2.CommonConstants.BLANK_STRING;
import static com.pgyt.myapp_2.CommonConstants.TABLE_NAME_CATEGORY;
import static com.pgyt.myapp_2.CommonConstants.TABLE_NAME_CONTENTS;


/**
 * DBHelper
 */
class DBHelper {
    private SQLiteDatabase sqLiteDatabase;

    DBHelper(final SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    /**
     * カテゴリを登録する。
     *
     * @param params CategoryBean
     */
    long insertCategory(CategoryBean params) {
        long id = this.sqLiteDatabase.insert(TABLE_NAME_CATEGORY, BLANK_STRING, params.getParams());
        return id;
    }

    /**
     * コンテンツを登録する。
     *
     * @param params ContentsBean
     */
    long insertContents(ContentsBean params) {
        long id = this.sqLiteDatabase.insert(TABLE_NAME_CONTENTS, BLANK_STRING, params.getParams());
        return id;
    }

    /**
     * コンテンツを更新する。
     *
     * @param param ContentsBean
     */
    int updateContents(ContentsBean param) {
        ContentValues cv = new ContentValues();
        cv.put("contents", param.getContents());
        int cnt = this.sqLiteDatabase.update(TABLE_NAME_CONTENTS, cv, "id = ?", new String[]{String.valueOf(param.getId())});
        return cnt;
    }


    /**
     * カテゴリーを取得する。
     *
     * @param param String
     * @return Cursor
     */
    Cursor selectCategory(String param) {
        Cursor cursor;
        if (param.isEmpty()) {
            String sql = "SELECT id, category_name FROM CATEGORY ORDER BY id;";
            cursor = this.sqLiteDatabase.rawQuery(sql, null);
        } else {
            String sql = "SELECT id, category_name FROM CATEGORY WHERE category_name = ? ORDER BY id ASC;";
            cursor = this.sqLiteDatabase.rawQuery(sql, new String[]{param});
        }
        return cursor;
    }

    /**
     * 全テーブルのデータを削除する。
     */
    void deletetAll() {
        this.sqLiteDatabase.delete("CATEGORY", "category_name <> ?", new String[]{"CLIPBOARD"});
        this.sqLiteDatabase.delete("CONTENTS", null, null);
    }

    /**
     * カテゴリ名を指定してカテゴリを削除する。
     * 紐づくコンテンツテーブルの削除も行う。
     *
     * @param param String
     */
    void deletetCategory(String param) {
        this.sqLiteDatabase.delete("CATEGORY", "category_name = ?", new String[]{param});
        this.sqLiteDatabase.delete("CONTENTS", "category_name = ?", new String[]{param});
    }

    /**
     * IDを指定してコンテンツを削除する。
     *
     * @param param String
     */
    void deletetContents(String param) {
        this.sqLiteDatabase.delete("CONTENTS", "id = ?", new String[]{param});
    }

    /**
     * コンテンツを全件取得する。
     *
     * @return Cursor
     */
    Cursor selectAllContents() {
        String sql = "SELECT id, category_name, contents_title, contents FROM CONTENTS ORDER BY category_name ASC, id DESC;";
        return this.sqLiteDatabase.rawQuery(sql, null);
    }

    /**
     * カテゴリ名を指定してコンテンツを取得する。
     *
     * @return Cursor
     */
    Cursor selectContentsList(String[] param) {
        String sql = "SELECT id, category_name, contents_title, contents FROM CONTENTS WHERE category_name = ? ORDER BY id DESC LIMIT ?;";
        return this.sqLiteDatabase.rawQuery(sql, param);
    }

    /**
     * 指定したコンテンツを取得する。
     *
     * @return Cursor
     */
    Cursor selectContents(String param) {
        String sql = "SELECT id, category_name, contents_title, contents FROM CONTENTS WHERE contents = ? ORDER BY category_name ASC, id DESC;";
        return this.sqLiteDatabase.rawQuery(sql, new String[]{param});
    }

    /**
     * 指定したコンテンツを取得する。
     *
     * @return Cursor
     */
    Cursor selectContentsWhereCategoryNameId(String param[]) {
        String sql = "SELECT id, category_name, contents_title, contents FROM CONTENTS WHERE category_name = ? AND id < ? ORDER BY category_name ASC, id DESC LIMIT ?;";
        return this.sqLiteDatabase.rawQuery(sql, param);
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

    /**
     * データベース削除
     *
     * @param context Context
     * @return boolean
     * TODO; 不要であれば削除する
     */
    boolean isDatabaseDelete(final Context context) {
        boolean result = false;

        if (this.sqLiteDatabase != null) {
            File file = context.getDatabasePath(new DBOpenHelper(context).getDatabaseName());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                result = SQLiteDatabase.deleteDatabase(file);
            }
        }
        return result;
    }

}
