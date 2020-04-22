package com.pgyt.myapp_2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.pgyt.myapp_2.CommonConstants.CSV_EXPORT;
import static com.pgyt.myapp_2.CommonConstants.CSV_IMPORT;

/**
 * MyAsyncTask
 */

class CsvAsyncTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "MyAsyncTask";
    private static final String EXPORT_SUCCESS = "export is succeed";
    private static final String IMPORT_SUCCESS = "import is succeed";
    private static final String EXPORT_FAILURE = "export is failed";
    private static final String IMPORT_FAILURE = "import is failed";
    private Listener listener;
    private Context context;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // 前処理
        this.context = MyContext.getInstance().getMyContext();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected String doInBackground(String... inOut) {

        Log.d(TAG, "doInBackground Start");

        String result = null;

        // 本処理
        switch (inOut[0]) {
            case CSV_EXPORT:
                Log.d(TAG, "doInBackground export");

                result = EXPORT_FAILURE;

                if (csvExport()) {

                    result = EXPORT_SUCCESS;

                }
                ;


                break;

            case CSV_IMPORT:
                Log.d(TAG, "doInBackground import");

                result = IMPORT_FAILURE;

                if (csvImport()) {

                    result = IMPORT_SUCCESS;
                }
                ;

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
            listener.onSuccess(result);
        }
    }

    /**
     * CSVエクスポート処理
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private Boolean csvExport() {
        Log.d(TAG, "csvExport Start");

        // TODO ディレクトリを指定できるようにする。
        // TODO ディレクトリ存在確認

        File outfile;
        PrintWriter printWriter = null;

        try {
            // ファイル作成
            outfile = new File(Environment.getDataDirectory().getPath(), "YourClipExport" + MainActivity.getNowDate() + ".csv");
            outfile.createNewFile();
            printWriter = new PrintWriter(new FileWriter(outfile));

            SQLiteDatabase sqLiteDatabase = new DBOpenHelper(context).getWritableDatabase();

            // コンテンツテーブル全件取得
            Cursor cursor = new DBHelper(sqLiteDatabase).selectAllContents();

            String categoryName;
            String contentsTitle;
            String contents;
            String record;
            while (cursor.moveToNext()){
                categoryName = cursor.getString(cursor.getColumnIndex("category_name"));
                contentsTitle = cursor.getString(cursor.getColumnIndex("contents_title"));
                contents = cursor.getString(cursor.getColumnIndex("contents"));
                record = "\"" + categoryName + "\",\"" + contentsTitle + "\",\"" + contents + "\"";
                printWriter.println(record);
            }
            cursor.close();
            sqLiteDatabase.close();


        } catch (FileNotFoundException e) {
            // フォルダへのアクセス権限がない
//            Toast.makeText(this.context, "You do not have access permission.", Toast.LENGTH_SHORT).show();
            return false;

        } catch (Exception e) {
//            Toast.makeText(this.context, "CSV output failed.", Toast.LENGTH_SHORT).show();
            return false;

        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
        Toast.makeText(this.context, "CSV output was successful.", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "csvExport End");
        return true;
    }

    /**
     * CSVインポート処理
     */
    private Boolean csvImport() {
        Log.d(TAG, "csvImport Start");
        Log.d(TAG, "csvImport End");
        return true;
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onSuccess(String result);
    }
}
