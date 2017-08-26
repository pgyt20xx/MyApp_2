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
		
		Log.d(TAG, "onCreate Start");
		
		// データ取得
		initAllData();

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

		Log.d(TAG, "onCreate End");

    }
	
	private void initAllData() {
		Log.d(TAG, "initData Start");
		
		// 登録されているカテゴリー名を保持する
        TITLE_NAME = getAllCategory();

        // 登録されているコンテンツを取得
        CONTENTS = getAllContents();
		
		Log.d(TAG, "initData End");
		
	}

    /**
     * ナビゲーションドロワーリスト作成
     */
    private void setNavigationDrawerListAdapter() {
		
		Log.d(TAG, "setNavigationDrawerList Start");

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
		
		Log.d(TAG, "setNavigationDrawerList End");

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
		
		Log.d(TAG, "getAllContents Start");
		
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

		Log.d(TAG, "getAllContents End");
		
        return result;
    }

    /**
     * カテゴリー名取得
     *
     * @return ArrayList
     */
    private ArrayList<String> getAllCategory() {
		
		Log.d(TAG, "getAllCategory Start");
		
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
		
		Log.d(TAG, "getAllCategory End");

        return result;
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
                if (position == 0) {
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
     * カテゴリ追加のダイアログイベント
     */
    private void categoryInsertEvent() {
		
		Log.d(TAG, "categoryInsertEvent Start");
		
        final EditText editView = new EditText(MainActivity.this);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
        dialog.setTitle(R.string.add_category);
        dialog.setView(editView);

        // OKボタン押下時
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
				
				Log.d(TAG, "categoryInsertEvent Click OK");
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

				Log.d(TAG, "categoryInsertEvent Click cancel");
				
            }
        });

        dialog.show();
		
		Log.d(TAG, "categoryInsertEvent End");
    }

    /**
     * カテゴリ削除のダイアログイベント
     */
    private void categoryDeletetEvent() {
		
		Log.d(TAG, "categoryDeleteEvent Start");

        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
        dialog.setTitle(R.string.delete_category);

        // OKボタン押下時
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
				
				Log.d(TAG, "categoryDeleteEvent Click OK");

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
				
				Log.d(TAG, "categoryDeleteEvent Click cancel");

            }
        });

        dialog.show();
		
		Log.d(TAG, "categoryDeleteEvent End");
    }
	
	private void deletetAllEvent() {
		
		Log.d(TAG, "deleteAllEvent Start");

        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
        dialog.setTitle(R.string.menu_delete_all);

        // OKボタン押下時
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					
					Log.d(TAG, "deleteAllEvent Click OK");
					
					try {
						dBhelper.deletetAll();
						initAllData();
					} catch (Exception e) {
						Log.d(TAG, e.getMessage());
					}

					Snackbar.make(findViewById(R.id.activity_main), "Delete All data Success", Snackbar.LENGTH_SHORT).show();

					// フラグメントの初期化
					initFragmentView();

                    // 追加したページを開く
                    mViewPager = (ViewPager) findViewById(R.id.pager);
                    mViewPager.setCurrentItem(0);
					
				}
			});
        // Cancelボタン押下時
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {

					Log.d(TAG, "deleteAllEvent Click cancel");
					
				}
			});

        dialog.show();
		
		Log.d(TAG, "deleteAllEvent Click End");
		
    }

    /**
     * コンテンツ追加のダイアログイベント
     */
    private void contentsInsertEvent() {
		
		Log.d(TAG, "contentsInsertEvent Start");
		
        final EditText editView = new EditText(MainActivity.this);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
        dialog.setTitle(R.string.fab_title);
        dialog.setView(editView);

        // OKボタン押下時
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
				Log.d(TAG, "contentsInsertEvent Click OK");
				
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
				Log.d(TAG, "contentsInsertEvent Click Cancel");
				
            }
        });

        dialog.show();
		Log.d(TAG, "contentsInsertEvent End");
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
			Log.d(TAG, "SectionsPagerAdapter getItem Start");
			
			Log.d(TAG, "SectionsPagerAdapter getItem End");
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
			Log.d(TAG, "SectionsPagerAdapter getTitle Start");

			Log.d(TAG, "SectionsPagerAdapter getTitle End");
            return TITLE_NAME.get(position);
        }

        /**
         * 生成するページ数
         *
         * @return int
         */
        @Override
        public int getCount() {
			Log.d(TAG, "SectionsPagerAdapter getCount Start");

			Log.d(TAG, "SectionsPagerAdapter getCount End");
            return TITLE_NAME.size();
        }

        @Override
        public int getItemPosition(Object object) {
			Log.d(TAG, "SectionsPagerAdapter getItemPosition Start");

			Log.d(TAG, "SectionsPagerAdapter getItemPosition End");
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
		Log.d(TAG, "SectionsPagerAdapter onNavigationItemSelected Start");

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
		
		Log.d(TAG, "SectionsPagerAdapter onNavigationItemSelected End");
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
		Log.d(TAG, "onCreareOptionMenu Start");
		
        getMenuInflater().inflate(R.menu.menu_main, menu);
		
		Log.d(TAG, "onCreareOptionMenu getItemPosition End");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected getItemPosition Start");
		
        // 押下されたメニューで分岐
        switch (item.getItemId()) {
            case R.id.add_category:
                categoryInsertEvent();
                return true;

            case R.id.delete_category:
                categoryDeletetEvent();
                return true;
				
			case R.id.all_delete:
                deletetAllEvent();
                return true;

            case R.id.action_settings:
                Intent intent = new android.content.Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

        }
		Log.d(TAG, "onOptionsItemSelected getItemPosition End");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRestart(){
        super.onRestart();
		Log.d(TAG, "onRestsrt Start");
		
		Toast.makeText(getApplicationContext(), "restart", Toast.LENGTH_SHORT).show();

        // フラグメントの初期化
        initFragmentView();
		
		Log.d(TAG, "onRestsrt End");
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		Log.d(TAG, "onPageScrolled Start");

		Log.d(TAG, "onPageScrolled End");
    }

    @Override
    public void onPageSelected(int position) {
		Log.d(TAG, "onPageSelected Start");

		Log.d(TAG, "onPageSelected End");
    }

    @Override
    public void onPageScrollStateChanged(int state) {
		Log.d(TAG, "onPageScrollStateChanged Start");

		Log.d(TAG, "onPageScrollStateChanged End");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
		Log.d(TAG, "onFragmentInteraction Start");

		Log.d(TAG, "onFragmentInteraction End");

    }
}
