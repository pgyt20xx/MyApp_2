package com.pgyt.myapp_2;

import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.pgyt.myapp_2.model.CategoryBean;
import com.pgyt.myapp_2.model.ContentsBean;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        MainActivityFragment.OnFragmentInteractionListener {

    DBHelper dBhelper = null;

    private ViewPager mViewPager;

    SectionsPagerAdapter mSectionsPagerAdapter;

    private static final String BLANK_STRING = "";

    private static ArrayList<String> TITLE_NAME;

    private static HashMap<String, ArrayList<String>> CONTENTS;

    /**
     * タグ:MainActivity
     */
    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 登録されているカテゴリー名を保持する
        TITLE_NAME = getAllCategory();

        // 登録されているコンテンツを取得
        CONTENTS = getAllContents();

        // アクティビティを設定
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // フローティングアクションボタンを設定
        setFabEvent();

        // フラグメントの初期化
        initFragmentView();

    }

    /**
     * フローティングアクションボタンのクリックイベントを定義
     */
    private void setFabEvent(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentsInsertEvent();
            }
        });
    }

    /**
     * 全コンテンツを取得
     * @return
     */
    private HashMap<String, ArrayList<String>> getAllContents(){
        // DBからカテゴリー名を取得する
        Cursor cursor = null;
        try {
            dBhelper = new DBHelper(this.getApplicationContext());
            cursor = dBhelper.selectAllContents();

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        HashMap<String, ArrayList<String>> result = new HashMap<>();

        boolean isEof = cursor.moveToFirst();

        String mapKey;
        while (isEof) {
            mapKey = cursor.getString(cursor.getColumnIndex("category_name"));
            ArrayList<String> contentsList = new ArrayList<>();
            boolean isTmpEof = cursor.moveToFirst();

            // 同一カテゴリーのリストを作成する。
            while (isTmpEof) {
                contentsList.add(cursor.getString(cursor.getColumnIndex("contents")));
                isTmpEof = cursor.moveToNext();
            }
            contentsList.add("SAMPLE");
            result.put(mapKey, contentsList);
            isEof = cursor.moveToNext();
        }
        cursor.close();

        return result;
    }

    /**
     * カテゴリー名取得
     * @return
     */
    private ArrayList<String> getAllCategory(){
        // DBからカテゴリー名を取得する
        Cursor cursor = null;
        try {
            dBhelper = new DBHelper(this.getApplicationContext());
            cursor = dBhelper.selectCategory(BLANK_STRING);

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        ArrayList<String> result = new ArrayList<>();

        boolean isEof = cursor.moveToFirst();
        while (isEof) {
            result.add(cursor.getString(cursor.getColumnIndex("category_name")));
            isEof = cursor.moveToNext();
        }
        cursor.close();

        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 押下されたメニューで分岐
        switch (item.getItemId()) {
            case R.id.item1:
                categoryInsertEvent();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * フラグメントを初期化し画面を再描画する
     */
    private void initFragmentView(){
        // フラグメントを取得する
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // xmlからTabLayoutの取得
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        // xmlからViewPagerを取得
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        // ViewPagerにページを設定
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.addOnPageChangeListener(this);


        // ViewPagerをTabLayoutに設定
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * カテゴリ追加のダイアログイベント
     */
    private void categoryInsertEvent() {
        final EditText editView = new EditText(MainActivity.this);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
        dialog.setTitle(R.string.menu_item1);
        dialog.setView(editView);

        // OKボタン押下時
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 値が入力された場合はDBに登録
                if (!TextUtils.isEmpty(editView.getText())) {
                    CategoryBean param = new CategoryBean();
                    param.setCategory_name(editView.getText().toString());
                    dBhelper.insertCategory(param);

                    // TODO: Exception
                    Snackbar.make(findViewById(R.id.activity_main), "Registration Success", Snackbar.LENGTH_SHORT).show();

                    // 新規タブ追加
                    TITLE_NAME.add((editView.getText()).toString());

                    // フラグメントの初期化
                    initFragmentView();

                }
            }
        });
        // Cancelボタン押下時
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        dialog.show();
    }

    private void contentsInsertEvent() {
        final EditText editView = new EditText(MainActivity.this);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
        dialog.setTitle(R.string.fab_title);
        dialog.setView(editView);

        // OKボタン押下時
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 値が入力された場合はDBに登録
                if (!TextUtils.isEmpty(editView.getText())) {

                    // 現在のフラグメントのpositionを取得
                    mViewPager = (ViewPager) findViewById(R.id.pager);
                    int position = mViewPager.getCurrentItem();

                    ContentsBean param = new ContentsBean();
                    param.setCategory_name(TITLE_NAME.get(position));
                    param.setContents(editView.getText().toString());
                    dBhelper.insertContents(param);

                    // TODO: Exception
                    Snackbar.make(findViewById(R.id.activity_main), "Registration Success", Snackbar.LENGTH_SHORT).show();

                    ArrayList<String> contentsList;
                    if(!CONTENTS.containsKey(TITLE_NAME.get(position))){
                        // 新規コンテンツ追加
                        contentsList = new ArrayList();
                    } else {
                        // 既存コンテンツに追加
                        contentsList = CONTENTS.get(TITLE_NAME.get(position));
                    }
                    contentsList.add(editView.getText().toString());
                    CONTENTS.put(TITLE_NAME.get(position), contentsList);

                    // フラグメントの初期化
                    initFragmentView();

                }
            }
        });
        // Cancelボタン押下時
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        dialog.show();
    }

    /**
     * FragmentPagerAdapter呼び出し
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * フラグメントを取得
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {
            return MainActivityFragment.newInstance(position + 1, TITLE_NAME.get(position), CONTENTS.get(TITLE_NAME.get(position)));
        }

        /**
         * タブにタイトルを設定
         * @param position
         * @return
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return TITLE_NAME.get(position);
        }

        /**
         * 生成するページ数
         *
         * @return
         */
        @Override
        public int getCount() {
            return TITLE_NAME.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
