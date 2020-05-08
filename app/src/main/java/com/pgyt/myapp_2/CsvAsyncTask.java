package com.pgyt.myapp_2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.pgyt.myapp_2.model.CategoryBean;
import com.pgyt.myapp_2.model.ContentsBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedHashSet;

import static com.pgyt.myapp_2.CommonConstants.BRACE_LEFT;
import static com.pgyt.myapp_2.CommonConstants.BRACE_RIGHT;
import static com.pgyt.myapp_2.CommonConstants.COLON;
import static com.pgyt.myapp_2.CommonConstants.COLUMN_CATEGORY_NAME;
import static com.pgyt.myapp_2.CommonConstants.COLUMN_CONTENTS;
import static com.pgyt.myapp_2.CommonConstants.COLUMN_CONTENTS_TITLE;
import static com.pgyt.myapp_2.CommonConstants.COLUMN_ID;
import static com.pgyt.myapp_2.CommonConstants.COMMA;
import static com.pgyt.myapp_2.CommonConstants.CSV_EXPORT;
import static com.pgyt.myapp_2.CommonConstants.CSV_IMPORT;
import static com.pgyt.myapp_2.CommonConstants.ESCAPE_DOUBLE_QUOTE;
import static com.pgyt.myapp_2.CommonConstants.EXPORT_FAILURE;
import static com.pgyt.myapp_2.CommonConstants.EXPORT_SUCCESS;
import static com.pgyt.myapp_2.CommonConstants.IMPORT_FAILURE;
import static com.pgyt.myapp_2.CommonConstants.IMPORT_SUCCESS;

/**
 * MyAsyncTask
 */

class CsvAsyncTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "CsvAsyncTask";
    private Listener listener;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private String importFilePath = null;
    private String exportFilePath = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // 前処理
        this.context = MyContext.getInstance().getMyContext();

    }

    /**
     * @param strings [import or export, importFilePath]
     * @return String result
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... strings) {

        Log.d(TAG, "doInBackground Start");

        this.importFilePath = strings[1];
        this.exportFilePath = strings[2];

        String impOrExp = strings[0];
        String result = null;

        // 本処理
        switch (impOrExp) {
            case CSV_EXPORT:
                Log.d(TAG, "doInBackground export");

                result = EXPORT_FAILURE;

                if (csvExport(exportFilePath)) {
                    result = EXPORT_SUCCESS;
                }

                break;

            case CSV_IMPORT:
                Log.d(TAG, "doInBackground import");

                result = IMPORT_FAILURE;

                if (csvImport(importFilePath)) {
                    result = IMPORT_SUCCESS;
                }

                break;

            default:

        }

        Log.d(TAG, "doInBackground end");

        return result;
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        // 後処理
        if (listener != null) {
            listener.onSuccess(result, this.importFilePath, this.exportFilePath);
        }
    }

    /**
     * CSVエクスポート処理
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private Boolean csvExport(String filepath) {
        Log.d(TAG, "csvExport Start");

        File outFile;
        PrintWriter printWriter = null;

        // 外部ストレージマウント確認
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return false;
        }

        // ディレクトリの存在確認・作成
        File outDir = new File(filepath);
//        if (!outDir.exists()) {
//            outDir.mkdir();
//        }

        try {
            // ファイル作成
            outFile = new File(outDir, "YourClipExport" + MainActivity.getNowDate() + ".json");
//            outFile.createNewFile();
            printWriter = new PrintWriter(new FileWriter(outFile));

            SQLiteDatabase sqLiteDatabase = new DBOpenHelper(context).getWritableDatabase();

            // コンテンツテーブル全件取得
            Cursor cursor = new DBHelper(sqLiteDatabase).selectAllContentsAsc();

            String id;
            String categoryName;
            String contentsTitle;
            String contents;
            String record;
            while (cursor.moveToNext()) {
                id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
                categoryName = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME));
                contentsTitle = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENTS_TITLE));
                contents = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENTS));
                record = BRACE_LEFT +
                        ESCAPE_DOUBLE_QUOTE + COLUMN_ID + ESCAPE_DOUBLE_QUOTE + COLON + ESCAPE_DOUBLE_QUOTE + id + ESCAPE_DOUBLE_QUOTE + COMMA +
                        ESCAPE_DOUBLE_QUOTE + COLUMN_CATEGORY_NAME + ESCAPE_DOUBLE_QUOTE + COLON + ESCAPE_DOUBLE_QUOTE + categoryName + ESCAPE_DOUBLE_QUOTE + COMMA +
                        ESCAPE_DOUBLE_QUOTE + COLUMN_CONTENTS_TITLE + ESCAPE_DOUBLE_QUOTE + COLON + ESCAPE_DOUBLE_QUOTE + contentsTitle + ESCAPE_DOUBLE_QUOTE + COMMA +
                        ESCAPE_DOUBLE_QUOTE + COLUMN_CONTENTS + ESCAPE_DOUBLE_QUOTE + COLON + ESCAPE_DOUBLE_QUOTE + contents + ESCAPE_DOUBLE_QUOTE +
                        BRACE_RIGHT;
                printWriter.println(record);
            }
            cursor.close();
            sqLiteDatabase.close();


        } catch (FileNotFoundException e) {
            // フォルダへのアクセス権限がない
            Log.d(TAG, "csvExport " + e.getCause());
            return false;

        } catch (Exception e) {
            Log.d(TAG, "csvExport " + e.getCause());
            return false;

        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
        Log.d(TAG, "csvExport End");
        return true;
    }

    /**
     * CSVインポート処理
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Boolean csvImport(String filePath) {
        Log.d(TAG, "csvImport Start");

        Uri uri = Uri.parse(filePath);
        if (uri == null) {
            return false;
        }

        SQLiteDatabase sqLiteDatabase = new DBOpenHelper(this.context).getWritableDatabase();
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream))) {

            // CSVファイル読み込み
            String line;
            LinkedHashSet<String> categorySet = new LinkedHashSet<>();
            while ((line = bufferedReader.readLine()) != null) {

                JSONObject json = new JSONObject(line);
                String category_name = json.getString(COLUMN_CATEGORY_NAME);
                String contents_title = json.getString(COLUMN_CONTENTS_TITLE);
                String contents = json.getString(COLUMN_CONTENTS);

                // 編集内容を登録
                ContentsBean param = new ContentsBean();
                param.setCategory_name(category_name);
                param.setContents_title(contents_title);
                param.setContents(contents);
                int id = (int) new DBHelper(sqLiteDatabase).insertContents(param);

                Log.d(TAG, "Insert Contents ID:" + id);

                // カテゴリーテーブルインサート用
                categorySet.add(category_name);
            }

            for (Object set : categorySet) {
                String category = (String) set;

                // カテゴリテーブルを検索
                Cursor cursor = new DBHelper(sqLiteDatabase).selectCategory(category);

                // 同一のカテゴリが存在する場合は後続処理を行わない。
                int cnt = cursor.getCount();
                cursor.close();
                if (cnt > 0) {
                    continue;
                }

                // カテゴリーテーブル登録
                CategoryBean param = new CategoryBean();
                param.setCategory_name(category);
                int id = (int) new DBHelper(sqLiteDatabase).insertCategory(param);

                Log.d(TAG, "Insert Category ID:" + id);

            }

        } catch (JSONException e) {
            e.printStackTrace();
            return false;

        } catch (IOException e) {
            e.printStackTrace();
            return false;

        } finally {
            sqLiteDatabase.close();
        }


        Log.d(TAG, "csvImport End");
        return true;
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onSuccess(String result, String importFilePath, String exportFilePath);
    }
}
