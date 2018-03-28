package com.pgyt.myapp_2;

import android.os.AsyncTask;

/**
 * Created by pgyt on 2018/03/24.
 */

public class MyAsyncTask extends AsyncTask<Void, Void, String> {

    private Listener listener;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // 前処理
    }

    @Override
    protected String doInBackground(Void... voids) {



        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        // 後処理
        listener.onSucces(result);
    }

    void setListener(Listener listener){
        this.listener = listener;
    }

    interface Listener{
        void onSucces(String result);
    }
}
