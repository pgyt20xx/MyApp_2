package com.pgyt.myapp_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pgyt.myapp_2.model.CategoryBean;
import com.pgyt.myapp_2.model.ContentsBean;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        MainActivityFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {

    public static final String CLIPBOARD_TAB_NAME = "CLIPBOARD";
    private static final String BLANK_STRING = "";
    private static final String TAG = "MainActivity";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CATEGORY_NAME = "category_name";
    private static final String COLUMN_CONTENTS_TITLE = "contents_title";
    private static final String COLUMN_CONTENTS = "contents";
    public static final int CLIPBOARD_TAB_POSITON = 0;
    public static final int REQUEST_CODE_EDIT_CONTENTS = 1001;
    public static ArrayList<CategoryBean> mCategoryList;
    public static LinkedHashMap<String, ArrayList<ContentsBean>> mContentsListMap;
    private boolean settingMaxRow;
    private int fragmentPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate Start");

        // アクティビティを設定
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SQLiteDatabase sqLiteDatabase = new DBOpenHelper(this.getApplicationContext()).getWritableDatabase();
        new DBHelper(sqLiteDatabase).isDatabaseDelete(this);

        // 設定を取得
        getPreference();

        // サービスを起動
        this.startService(new Intent(this, MainService.class));

        // データ取得
        initAllData();

        // フラグメントの初期化
        initFragmentView();

        // ナビゲーションドロワー設定
        setNavigationDrawer(toolbar);

        Log.d(TAG, "onCreate End");
    }

    /**
     * 設定を取得する。
     */
    private void getPreference() {

        // 設定値を取得
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // ステータスバーに表示するか
        this.settingMaxRow = preferences.getBoolean("checkbox_maxrow_key", false);
    }

    private void initAllData() {
        Log.d(TAG, "initData Start");

        // 登録されているカテゴリー名を保持する
        mCategoryList = getAllCategory();

        // 登録されているコンテンツを取得
        mContentsListMap = getAllContents(mCategoryList);

        Log.d(TAG, "initData End");
    }


    /**
     * フラグメントを初期化し画面を再描画する
     */
    private void initFragmentView() {
        Log.d(TAG, "initFragment Start");

        // フラグメントを取得する
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // xmlからTabLayoutの取得
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        // xmlからViewPagerを取得
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        // ViewPagerにページを設定
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.addOnPageChangeListener(this);

        // ViewPagerのページ遷移イベント
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == CLIPBOARD_TAB_POSITON) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });

        // ViewPagerをTabLayoutに設定
        tabLayout.setupWithViewPager(viewPager);
        Log.d(TAG, "initFragment End");
    }

    /**
     * カテゴリー名取得
     *
     * @return ArrayList
     */
    private ArrayList<CategoryBean> getAllCategory() {
        Log.d(TAG, "getAllCategory Start");

        // DBからカテゴリー名を取得する
        ArrayList<CategoryBean> result = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = new DBOpenHelper(this.getApplicationContext()).getWritableDatabase();
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
    LinkedHashMap<String, ArrayList<ContentsBean>> getAllContents(ArrayList<CategoryBean> categoryList) {
        Log.d(TAG, "getAllContents Start");

        // DBからカテゴリー名を取得する
        LinkedHashMap<String, ArrayList<ContentsBean>> result = new LinkedHashMap<>();
        SQLiteDatabase sqLiteDatabase = new DBOpenHelper(this.getApplicationContext()).getWritableDatabase();

        // 設定から最大行数を取得
        String limit = "50";
        if(this.settingMaxRow) {
           limit = "100";
        }
        try {
            for (CategoryBean category : categoryList) {
                Cursor cursor = new DBHelper(sqLiteDatabase).selectContentsList(new String[] {category.getCategory_name(), limit});
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

    /**
     * ナビゲーションドロワー設定
     *
     * @param toolbar Toolbar
     */
    private void setNavigationDrawer(Toolbar toolbar) {
        Log.d(TAG, "setNavigationDrawer Start");

        // ナビゲーションドロワーの設定
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // リスナー設定
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        Log.d(TAG, "setNavigationDrawerList End");
    }

    /**
     * フラグメントがコンテンツを削除したら呼び出される。
     *
     * @param v View
     */
    public void onContentsChanged(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreareOptionMenu Start");
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);

        Log.d(TAG, "onCreareOptionMenu getItemPosition End");
        return true;
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestsrt Start");

        // 変数内のコンテンツ数を取得
        int contentsSize = 0;
        for (Map.Entry<String, ArrayList<ContentsBean>> entry : mContentsListMap.entrySet()) {
            contentsSize += entry.getValue().size();
        }

        // DBに登録されているコンテンツ数を取得
        int contentsSizeDb = 0;
        SQLiteDatabase sqLiteDatabase = new DBOpenHelper(this.getApplicationContext()).getWritableDatabase();
        try {
            Cursor cursor = new DBHelper(sqLiteDatabase).selectAllContents();
            contentsSizeDb = cursor.getCount();
            cursor.close();

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());

        } finally {
            sqLiteDatabase.close();
        }

        // 変数内とDB内のコンテンツ数を比較
        // サービスからのインサートに対応する。
        if (contentsSize != contentsSizeDb) {

            // データ取得
            initAllData();

            // フラグメントを初期化する;
            initFragmentView();
        }

        Log.d(TAG, "onRestsrt End");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume Start");


        Log.d(TAG, "onResume End");
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        Log.d(TAG, "onPageScrolled position: " + position + "  positionOffset: " + positionOffset + "positionOffsetPixels: " + positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        Log.d(TAG, "onPageSelected Start");

        this.fragmentPosition = position;
        Log.d(TAG, "onPageSelected " + position);

        Log.d(TAG, "onPageSelected End");
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.d(TAG, "onPageScrollStateChanged Start");

        Log.d(TAG, "onPageScrollStateChanged End");
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "SectionsPagerAdapter onNavigationItemSelected Start");

        Log.d(TAG, "SectionsPagerAdapter onNavigationItemSelected End");
        return true;
    }

    @Override
    public void onFragmentInteractionListener(View v) {

    }

    /**
     * FragmentPagerAdapter呼び出し
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * フラグメントを取得
         *
         * @param position int
         * @return MainActivityFragment
         */
        @Override
        public Fragment getItem(int position) {

            Log.d(TAG, "SectionsPagerAdapter getItem " + position);
            return MainActivityFragment.newInstance(position, mCategoryList.get(position).getCategory_name());
        }

        /**
         * タブにタイトルを設定
         *
         * @param position int
         * @return CharSequence
         */
        @Override
        public CharSequence getPageTitle(int position) {

            Log.d(TAG, "SectionsPagerAdapter getTitle " + position);
            return mCategoryList.get(position).getCategory_name();
        }

        /**
         * 生成するページ数
         *
         * @return int
         */
        @Override
        public int getCount() {

            Log.d(TAG, "SectionsPagerAdapter getCount " + mCategoryList.size());
            return mCategoryList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            Log.d(TAG, "SectionsPagerAdapter getItemPosition Start");

            Log.d(TAG, "SectionsPagerAdapter getItemPosition End");
            return POSITION_NONE;
        }

    }
}
