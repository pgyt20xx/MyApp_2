package com.pgyt.myapp_2;

import android.content.*;
import android.database.*;
import android.net.*;
import android.os.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.pgyt.myapp_2.model.*;
import java.util.*;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        MainActivityFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String BLANK_STRING = "";

    private static final String TAG = "MainActivity";

    public static ArrayList<String> TITLE_NAME;

    public static HashMap<String, LinkedHashMap<String, String>> CONTENTS;

    private ArrayAdapter<String> adapter;

    private DBHelper dBhelper = null;

    private ViewPager mViewPager;


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

        // サービスを起動
        this.startService(new Intent(this, MainService.class));

        // フローティングアクションボタンを設定
        setFabEvent();

        // フラグメントの初期化
        initFragmentView();

        // ナビゲーションドロワーのリスト作成
        setNavigationDrawerListAdapter();

        // ナビゲーションドロワー設定
        setNavigationDrawer(toolbar);


    }

    /**
     * ナビゲーションドロワーリスト作成
     */
    private void setNavigationDrawerListAdapter() {

        // ナビゲーションドロワーに設定するリストを作成
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
        adapter = new ArrayAdapter<>(this, R.layout.drawer_list_item, TITLE_NAME);
        mDrawerList.setAdapter(adapter);

        //リスト項目が選択された時のイベントを追加
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewPager = (ViewPager) findViewById(R.id.pager);
                mViewPager.setCurrentItem(position);

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
                drawer.closeDrawers();
            }
        });

    }

    /**
     * ナビゲーションドロワー設定
     *
     * @param toolbar Toolbar
     */
    private void setNavigationDrawer(Toolbar toolbar) {

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
    }

    /**
     * フローティングアクションボタンのクリックイベントを定義
     */
    private void setFabEvent() {
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
     *
     * @return HashMap
     */
    HashMap<String, LinkedHashMap<String, String>> getAllContents() {
        // DBからカテゴリー名を取得する
        Cursor cursor;

        HashMap<String, LinkedHashMap<String, String>> result = new HashMap<>();

        try {
            dBhelper = new DBHelper(this.getApplicationContext());
            cursor = dBhelper.selectAllContents();

            boolean isEof = cursor.moveToFirst();

            String mapKey;
            while (isEof) {
                mapKey = cursor.getString(cursor.getColumnIndex("category_name"));
                LinkedHashMap<String, String> contentsMap = new LinkedHashMap<>();

                // 同一カテゴリーのリストを作成する。
                // カテゴリー名でソートされていることが前提
                while (isEof) {
                    if (!mapKey.equals(cursor.getString(cursor.getColumnIndex("category_name")))) {
                        break;
                    }
                    contentsMap.put(cursor.getString(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("contents")));
                    isEof = cursor.moveToNext();
                }
                result.put(mapKey, contentsMap);
            }
            cursor.close();

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        return result;
    }

    /**
     * カテゴリー名取得
     *
     * @return ArrayList
     */
    private ArrayList<String> getAllCategory() {
        // DBからカテゴリー名を取得する
        Cursor cursor;
        ArrayList<String> result = new ArrayList<>();

        try {
            dBhelper = new DBHelper(this.getApplicationContext());
            cursor = dBhelper.selectCategory(BLANK_STRING);

            boolean isEof = cursor.moveToFirst();
            while (isEof) {
                result.add(cursor.getString(cursor.getColumnIndex("category_name")));
                isEof = cursor.moveToNext();
            }
            cursor.close();

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        return result;
    }

    /**
     * フラグメントを初期化し画面を再描画する
     */
    private void initFragmentView() {
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
                if (position == 0) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });

        // ViewPagerをTabLayoutに設定
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * カテゴリ追加のダイアログイベント
     */
    private void categoryInsertEvent() {
        final EditText editView = new EditText(MainActivity.this);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
        dialog.setTitle(R.string.add_category);
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

                    // ナビゲーションドロワーの更新
                    adapter.notifyDataSetChanged();

                    // フラグメントの初期化
                    initFragmentView();

                    // 追加したページを開く
                    mViewPager = (ViewPager) findViewById(R.id.pager);
                    mViewPager.setCurrentItem(TITLE_NAME.size() - 1);

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
     * カテゴリ削除のダイアログイベント
     */
    private void categoryDeletetEvent() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
        dialog.setTitle(R.string.delete_category);

        // OKボタン押下時
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // 現在のフラグメントのpositionを取得
                mViewPager = (ViewPager) findViewById(R.id.pager);
                int position = mViewPager.getCurrentItem();

                // デフォルトタブでなければ削除
                if (position == 0) {
                    Snackbar.make(findViewById(R.id.activity_main), "CLIPBOARD cannot Delete", Snackbar.LENGTH_SHORT).show();

                } else {
                    String param = TITLE_NAME.get(position);
                    dBhelper.deletetCategory(param);

                    Snackbar.make(findViewById(R.id.activity_main), "Delete Success", Snackbar.LENGTH_SHORT).show();

                    // 変数からカテゴリーを削除
                    TITLE_NAME.remove(position);

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
     * コンテンツ追加のダイアログイベント
     */
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

					try {
						ContentsBean param = new ContentsBean();
						param.setCategory_name(TITLE_NAME.get(position));
						param.setContents(editView.getText().toString());
						Long id = dBhelper.insertContents(param);

						// 1行目に追加する
						LinkedHashMap<String, String> contentsMap;
						LinkedHashMap<String, String> tContentsMap = new LinkedHashMap<>();
						tContentsMap.put(id.toString(), editView.getText().toString());
						if (CONTENTS.containsKey(TITLE_NAME.get(position))) {
							// 既存コンテンツ追加
							tContentsMap.putAll(CONTENTS.get(TITLE_NAME.get(position)));

						}
						contentsMap = tContentsMap;
						CONTENTS.put(TITLE_NAME.get(position), contentsMap);
						
					} catch (Exception e) {
						Log.d(TAG, e.getMessage());
					}
					
					Snackbar.make(findViewById(R.id.activity_main), "Registration Success", Snackbar.LENGTH_SHORT).show();
                    
                    // フラグメントの初期化
                    initFragmentView();

                    // 元のページを開く
                    mViewPager.setCurrentItem(position);

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
            return MainActivityFragment.newInstance(position, TITLE_NAME.get(position), CONTENTS);
        }

        /**
         * タブにタイトルを設定
         *
         * @param position int
         * @return CharSequence
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return TITLE_NAME.get(position);
        }

        /**
         * 生成するページ数
         *
         * @return int
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

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item1:
                Log.d(TAG, "Item 1 Selected!");
                break;
            case R.id.menu_item2:
                Log.d(TAG, "Item 2 Selected!");
                break;
            case R.id.menu_item3:
                Log.d(TAG, "Item 3 Selected!");
                break;
            case R.id.menu_item4:
                Log.d(TAG, "Item 4 Selected!");
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            case R.id.add_category:
                categoryInsertEvent();
                return true;

            case R.id.delete_category:
                categoryDeletetEvent();
                return true;

            case R.id.action_settings:
                Intent intent = new android.content.Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRestart(){
        super.onRestart();

        // フラグメントの初期化
        initFragmentView();
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
