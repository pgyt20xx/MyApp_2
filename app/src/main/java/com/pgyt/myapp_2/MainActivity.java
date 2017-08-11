package com.pgyt.myapp_2;

import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.EditText;

import com.pgyt.myapp_2.model.CategoryBean;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        MainActivityFragment.OnFragmentInteractionListener {

    ViewPager mViewPager;

    DBHelper dBhelper = null;

    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * デフォルトタブ
     */
    private static int PAGE_COUNT = 0;

    private static final String BLANK_STRING = "";

    private static ArrayList<String> TITLE_NAME;

    /**
     * タグ:MainActivity
     */
    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // DBからカテゴリー名を取得する
        Cursor cursor = null;
        try {
            dBhelper = new DBHelper(this.getApplicationContext());
            cursor = dBhelper.selectCategory(BLANK_STRING);

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        // 取得したレコードの件数がページ数
        PAGE_COUNT = cursor.getCount();

        // 登録されているカテゴリー名を保持する
        TITLE_NAME = new ArrayList<>();
        boolean isEof = cursor.moveToFirst();
        while (isEof) {
            TITLE_NAME.add(cursor.getString(cursor.getColumnIndex("category_name")));
            isEof = cursor.moveToNext();
        }
        cursor.close();

        // フラグメントを取得する
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // 描画処理
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                createDialogEvent();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    /**
     * ダイアログイベント
     */
    private void createDialogEvent() {
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

                    // TODO:タブ追加方法を考える。
                    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                    tabLayout.addTab(tabLayout.newTab().setText(editView.getText()));

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
            return MainActivityFragment.newInstance(position + 1);
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
}
