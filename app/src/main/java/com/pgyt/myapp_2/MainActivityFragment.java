package com.pgyt.myapp_2;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.os.*;
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

import android.content.ClipboardManager;
import android.support.v7.widget.PopupMenu;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.CLIPBOARD_SERVICE;
import static com.pgyt.myapp_2.MainActivity.CLIPBOARD_TAB_NAME;
import static com.pgyt.myapp_2.MainActivity.CLIPBOARD_TAB_POSITON;
import static com.pgyt.myapp_2.MainActivity.REQUEST_CODE_EDIT_CONTENTS;
import static com.pgyt.myapp_2.MainActivity.REQUEST_CODE_SETTING;
import static com.pgyt.myapp_2.MainActivity.mMaxRowSize;
import static com.pgyt.myapp_2.MainActivity.mCategoryList;
import static com.pgyt.myapp_2.MainActivity.mContentsListMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CATEGORY_NAME = "category_name";
    private static final String COLUMN_CONTENTS_TITLE = "contents_title";
    private static final String COLUMN_CONTENTS = "contents";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_TITLE_NAME = "title_name";
    private static final String TAG = "MainActivityFragment";
    private static final boolean CHECK_VISIBLE_FLG_ON = true;
    private static final boolean CHECK_VISIBLE_FLG_OFF = false;
    private ViewPager mViewPager;
    private CustomAdapter mRecyclerAdapter;
    private CustomActionModeCallback mActionModeCallback;
    private ArrayAdapter<String> mDrawerAdapter;
    private OnSettingChengedListener settingChengedListener;
    public PopupMenu popupMenu = null;


    // コンストラクタ
    public MainActivityFragment() {
    }

    static MainActivityFragment newInstance(int page, String title) {
        Log.d(TAG, "newInstance Start");

        MainActivityFragment fragment = new MainActivityFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, page);
        args.putString(ARG_TITLE_NAME, title);
        fragment.setArguments(args);

        Log.d(TAG, "newInstance End");


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate Start");

        if (getArguments() != null) {

            Log.d(TAG, "OnCreate");

        }
        Log.d(TAG, "onCreate End");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView Start");

        // パラメータ取得
        final String mCategoryName = getArguments().getString(ARG_TITLE_NAME);

        View view = inflater.inflate(R.layout.content_main, container, false);

        // ページャー取得
        mViewPager = (ViewPager) getActivity().findViewById(R.id.pager);

        // ナビゲーションドロワーリスト設定
        setDrawerList();

        // リサイクルビューの設定
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new RecilerItemDecoration(getContext()));

        mRecyclerAdapter = new CustomAdapter(getContext(), mCategoryName);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        // 行のクリックイベント
        mRecyclerAdapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, TextView textContents, int position) {
                Toast.makeText(getContext(), "\"" + textContents.getText() + "\"" + " is Cliped", Toast.LENGTH_SHORT).show();
                copyClip(textContents);
            }
        });

        // イメージのクリックイベント
        mRecyclerAdapter.setOnImageItemClickListener(new CustomAdapter.OnImageItemClickListener() {
            @Override
            public void onClick(final View view, final TextView textRowId, final TextView textContents, final int position) {
                popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                // ポップアップメニューのメニュー項目のクリック処理
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        onOptionsPopUpItemSelected(item, textRowId, textContents, position);
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        // ロングクリックイベント
        mRecyclerAdapter.setOnItemLongClickListener(new CustomAdapter.OnItemLongClickListener() {
            @Override
            public boolean onLongClick(final View view, final int position) {
                // タイトルを消す
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

                if (mActionModeCallback != null) {
                    return false;
                }

                // チェックボックス表示用のフラグをセット
                RecyclerView recyclerView = getCurrentRecyclerView();
                for (int i = 0; i < mContentsListMap.get(mCategoryName).size(); i++) {
                    mContentsListMap.get(mCategoryName).get(i).setCheckBoxVisibleFlg(CHECK_VISIBLE_FLG_ON);
                    recyclerView.getAdapter().notifyItemChanged(i);
                }

                mContentsListMap.get(mCategoryName).get(position).setCheckedFlg(true);

                recyclerView.getAdapter().notifyItemChanged(position);


                // アクションモードコールバック呼び出し。
                mActionModeCallback = new CustomActionModeCallback(view, getFragmentManager()) {
                    // Called when the user exits the action mode
                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        // チェックボックスを非表示にするフラグをセット
                        for (ContentsBean contents : mContentsListMap.get(mCategoryName)) {
                            contents.setCheckBoxVisibleFlg(CHECK_VISIBLE_FLG_OFF);
                            contents.setCheckedFlg(false);
                        }
                        getCurrentRecyclerView().getAdapter().notifyDataSetChanged();
                        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                        mActionModeCallback = null;
                    }
                };

                // アクションモードスタート
                getActivity().startActionMode(mActionModeCallback);

                // ダイアログのボタン押下
                mActionModeCallback.setOnButtonClickListener(new CustomActionModeCallback.OnButtonClickListener() {
                    @Override
                    public void onButtonClick(boolean bool, ActionMode mode) {
                        if (bool) {

                            // 削除対象のリストを準備
                            ArrayList<ContentsBean> removeContentsList = new ArrayList<>();
                            for (int i = 0; i < mContentsListMap.get(mCategoryName).size(); i++) {

                                // チェック状態でなければ処理しない。
                                if (!mContentsListMap.get(mCategoryName).get(i).getCheckedFlg()) {
                                    continue;
                                }

                                // データ削除
                                int contentsId = mContentsListMap.get(mCategoryName).get(i).getId();
                                contentsDelete(String.valueOf(contentsId));

                                // 削除対象のリストに追加
                                removeContentsList.add(mContentsListMap.get(mCategoryName).get(i));
                            }

                            // 削除対象をまとめて変数から削除
                            for (ContentsBean removeContents : removeContentsList) {
                                // 削除対象の変数の位置を取得、見つからなければ次の行へ
                                int contentsPosition = mContentsListMap.get(mCategoryName).indexOf(removeContents);
                                if (contentsPosition < 0) {
                                    continue;
                                }

                                // 変数を削除する。
                                mContentsListMap.get(mCategoryName).remove(removeContents);

                                // リサイクルビューに通知
                                mRecyclerAdapter.notifyItemRemoved(contentsPosition);
                                mRecyclerAdapter.notifyItemRangeChanged(contentsPosition, mContentsListMap.get(mCategoryName).size());
                            }
							
							// 削除した分補充する
                            // 再取得する行数
                            int rowSize= mContentsListMap.get(mCategoryName).size();
                            int lack = mMaxRowSize - rowSize;

                            // 取得する行のid
                            int id = mContentsListMap.get(mCategoryName).get(rowSize - 1).getId();

                            // コンテンツ取得
                            LinkedHashMap<String, ArrayList<ContentsBean>> contentsMap = getContents(mCategoryName, String.valueOf(id), String.valueOf(lack));

                            // 変数に格納
                            for (ContentsBean contents : contentsMap.get(mCategoryName)) {
                                mContentsListMap.get(mCategoryName).add(contents);
                                mRecyclerAdapter.notifyItemInserted(mContentsListMap.get(mCategoryName).size());
                            }
							
                        }
                        mode.finish();
                    }
                });

                view.setSelected(true);
                return true;
            }
        });

        Log.d(TAG, "onCreateView End");

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "onActivityResult Start");

        // Fragment#startActivityForResult() で呼んだ
        // IntentをFragmentActivityのonActivityResult()で処理する場合には、
        // 渡されたrequestCodeの下位16ビットだけを比較対象にする必要がある。
        // (requestCode & 0xffff)

        // コンテンツ編集画面からの戻り
        if (requestCode == REQUEST_CODE_EDIT_CONTENTS) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Intent取得
                ContentsBean contents = (ContentsBean) intent.getSerializableExtra("contents");

                // 変数を更新
                int page = mViewPager.getCurrentItem();
                String categoryName = mCategoryList.get(page).getCategory_name();

                int position = getRowPositionById(contents.getId(), categoryName);
                mContentsListMap.get(categoryName).get(position).setContents(contents.getContents());

                // リサイクルビューに通知
                getCurrentRecyclerView().getAdapter().notifyItemChanged(position);

            } else if (requestCode == RESULT_CANCELED) {

            }
        }

        // 設定画面からの戻り
        if (requestCode == REQUEST_CODE_SETTING) {
            if (resultCode == RESULT_OK) {
                boolean isChenged = intent.getBooleanExtra("settingChengedFlg", false);
                settingChengedListener.onSettingChengedListener(isChenged);
            }
        }
        Log.d(TAG, "onActivityResult End");
    }

    /**
     * コンテンツの行番号を返す
     *
     * @param id int
     * @return int
     */
    private int getRowPositionById(int id, String categoryName) {
        ArrayList<ContentsBean> currentList = mContentsListMap.get(categoryName);
        int result = 0;
        for (int i = 0; i < currentList.size(); i++) {
            if (id == currentList.get(i).getId()) {
                result = i;
                break;
            }
        }
        return result;
    }

    /**
     * カテゴリー追加のダイアログイベント
     */
    private void categoryInsertEvent() {
        Log.d(TAG, "categoryInsertEvent Start");

        CustomDialogFragment newFragment = CustomDialogFragment.newInstance("Add Categry", "realy?", "CategoryName", null, "1");
        newFragment.setEditDialogListener1(new CustomDialogFragment.EditDialogListener1() {
            @Override
            public void onPositiveClick(EditText text) {
                Log.d(TAG, "categoryInsertEvent Click Positive");

                // 値が入力されていない場合は何もしない
                if (TextUtils.isEmpty(text.getText())) {
                    Log.d(TAG, "categoryInsertEvent Please enter something");
                    Snackbar.make(getActivity().findViewById(R.id.activity_main), "Please enter something", Snackbar.LENGTH_SHORT).show();
                    return;
                }


                SQLiteDatabase sqLiteDatabase = new DBOpenHelper(getContext()).getWritableDatabase();
                try {
                    // カテゴリテーブルを検索
                    Cursor cursor = new DBHelper(sqLiteDatabase).selectCategory(text.getText().toString());

                    // 同一のカテゴリが存在する場合は後続処理を行わない。
                    int cnt = cursor.getCount();
                    cursor.close();
                    if (cnt > 0) {
                        Log.d(TAG, "categoryInsertEvent Already There is same Category");
                        Snackbar.make(getActivity().findViewById(R.id.activity_main), "Already There is same Category", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    CategoryBean category = new CategoryBean();
                    category.setCategory_name(text.getText().toString());
                    int id = (int) new DBHelper(sqLiteDatabase).insertCategory(category);

                    // 新規タブ追加
                    category.setId(id);
                    mCategoryList.add(category);

                    // 新規コンテンツのリストを作成する。
                    mContentsListMap.put(category.getCategory_name(), new ArrayList<ContentsBean>());

                    // ページャーに変更を通知
                    mViewPager.getAdapter().notifyDataSetChanged();
                    mViewPager.setCurrentItem(mCategoryList.size() - 1);

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    Snackbar.make(getActivity().findViewById(R.id.activity_main), "Registration ERROR", Snackbar.LENGTH_SHORT).show();

                } finally {
                    sqLiteDatabase.close();
                }

            }

            @Override
            public void onNegativeClick(EditText text) {
                Log.d(TAG, "categoryInsertEvent Click Negative");
            }
        });
        newFragment.show(getFragmentManager(), "categoryInsertEvent");

        Log.d(TAG, "categoryInsertEvent End");
    }

    /**
     * カテゴリー削除のダイアログイベント
     */
    private void categoryDeletetEvent() {
        Log.d(TAG, "categoryDeletetEvent Start");

        CustomDialogFragment newFragment = CustomDialogFragment.newInstance("Delete Categry", "realy?", null, null, "0");
        newFragment.setConfirmDialogListener(new CustomDialogFragment.ConfirmDialogListener() {
            @Override
            public void onPositiveClick() {
                Log.d(TAG, "categoryDeletetEvent Click Positive");

                // 現在のフラグメントのpositionを取得
                int page = mViewPager.getCurrentItem();

                // デフォルトタブでなければ削除
                if (page == CLIPBOARD_TAB_POSITON) {
                    Snackbar.make(getActivity().findViewById(R.id.activity_main), "CLIPBOARD cannot Delete", Snackbar.LENGTH_SHORT).show();

                } else {
                    SQLiteDatabase sqLiteDatabase = new DBOpenHelper(getContext()).getWritableDatabase();
                    try {
                        String param = mCategoryList.get(page).getCategory_name();
                        new DBHelper(sqLiteDatabase).deletetCategory(param);

                        // 変数からカテゴリーを削除
                        mCategoryList.remove(page);

                        // ページャーに変更を通知
                        mViewPager.getAdapter().notifyDataSetChanged();


                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Snackbar.make(getActivity().findViewById(R.id.activity_main), "Delete ERROR", Snackbar.LENGTH_SHORT).show();

                    } finally {
                        sqLiteDatabase.close();
                    }
                }
            }

            @Override
            public void onNegativeClick() {
                Log.d(TAG, "categoryDeletetEvent Click Negative");
            }
        });
        newFragment.show(getFragmentManager(), "categoryDeletetEvent");

        Log.d(TAG, "categoryDeletetEvent End");
    }

    /**
     * コンテンツ追加のダイアログイベント
     */
    private void contentsInsertEvent() {
        Log.d(TAG, "contentsInsertEvent Start");
        CustomDialogFragment newFragment = CustomDialogFragment.newInstance("Add Contents", "realy", "ContentsTitle", "Contents", "2");
        newFragment.setEditDialogListener2(new CustomDialogFragment.EditDialogListener2() {
            @Override
            public void onPositiveClick(EditText contentsTitle, EditText contentsText) {
                Log.d(TAG, "contentsInsertEvent Click Positive");

                // 値が入力されていない場合は何もしない
                if (TextUtils.isEmpty(contentsTitle.getText())) {
                    Snackbar.make(getActivity().findViewById(R.id.activity_main), "Please enter something", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // 現在のフラグメントのpositionを取得
                int page = mViewPager.getCurrentItem();

                // コンテンツ登録
                SQLiteDatabase sqLiteDatabase = new DBOpenHelper(getContext()).getWritableDatabase();
                try {
                    ContentsBean contents = new ContentsBean();
                    contents.setCategory_name(mCategoryList.get(page).getCategory_name());
                    contents.setContents_title(contentsTitle.getText().toString());
                    contents.setContents(contentsText.getText().toString());
                    int id = (int) new DBHelper(sqLiteDatabase).insertContents(contents);

                    // 1行目に追加する
                    contents.setId(id);
                    mContentsListMap.get(mCategoryList.get(page).getCategory_name()).add(0, contents);

                    // リサイクルビューに通知
                    getCurrentRecyclerView().getAdapter().notifyItemInserted(0);
                    getCurrentRecyclerView().scrollToPosition(0);


                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());

                } finally {
                    sqLiteDatabase.close();
                }

            }

            @Override
            public void onNegativeClick(EditText contentsTitle, EditText contentsText) {
                Log.d(TAG, "contentsInsertEvent Click Negative");
                Snackbar.make(getActivity().findViewById(R.id.activity_main), "Registration ERROR", Snackbar.LENGTH_SHORT).show();

            }
        });

        newFragment.show(getFragmentManager(), "contentsInsertEvent");

        Log.d(TAG, "contentsInsertEvent End");
    }

    /**
     * コンテンツ削除
     */
    private void contentsDelete(String mRowId) {
        Log.d(TAG, "contentsDelete Start");

        SQLiteDatabase sqLiteDatabase = new DBOpenHelper(getContext()).getWritableDatabase();
        try {
            new DBHelper(sqLiteDatabase).deletetContents(mRowId);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            Snackbar.make(getActivity().findViewById(R.id.activity_main), "Delete ERROR", Snackbar.LENGTH_SHORT).show();

        } finally {
            sqLiteDatabase.close();
        }

        Log.d(TAG, "contentsDelete End");

    }

    /**
     * 全削除のダイアログイベント
     */
    private void deletetAllEvent() {
        Log.d(TAG, "deleteAllEvent Start");

        CustomDialogFragment newFragment = CustomDialogFragment.newInstance("Delete All", "realy?", null, null, "0");
        newFragment.setConfirmDialogListener(new CustomDialogFragment.ConfirmDialogListener() {
            @Override
            public void onPositiveClick() {
                Log.d(TAG, "deleteAllEvent Click Positive");

                // 全データ削除
                SQLiteDatabase sqLiteDatabase = new DBOpenHelper(getContext()).getWritableDatabase();
                try {
                    new DBHelper(sqLiteDatabase).deletetAll();
                    Snackbar.make(getActivity().findViewById(R.id.activity_main), "Delete All data Success", Snackbar.LENGTH_SHORT).show();

                    // データをクリア
                    mCategoryList = new ArrayList<>();
                    mContentsListMap = new LinkedHashMap<>();

                    // デフォルトカテゴリを取得
                    ArrayList<CategoryBean> categoryList = new ArrayList<>();
                    Cursor cursor = new DBHelper(sqLiteDatabase).selectCategory(CLIPBOARD_TAB_NAME);
                    boolean isEof = cursor.moveToFirst();
                    while (isEof) {
                        CategoryBean category = new CategoryBean();
                        category.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                        category.setCategory_name(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
                        categoryList.add(category);
                        isEof = cursor.moveToNext();
                    }

                    // デフォルトカテゴリを設定
                    mCategoryList = categoryList;
                    cursor.close();

                    // コンテンツを新規作成
                    for (CategoryBean category : mCategoryList) {
                        mContentsListMap.put(category.getCategory_name(), new ArrayList<ContentsBean>());
                    }

                    // データの変更を通知
                    mViewPager.getAdapter().notifyDataSetChanged();

                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                    Snackbar.make(getActivity().findViewById(R.id.activity_main), "Delete All data ERROR", Snackbar.LENGTH_SHORT).show();

                } finally {
                    sqLiteDatabase.close();
                }

            }

            @Override
            public void onNegativeClick() {
                Log.d(TAG, "deleteAllEvent Click Negative");
            }
        });
        newFragment.show(getFragmentManager(), "categoryInsertEvent");

        Log.d(TAG, "deleteAllEvent End");
    }

    /**
     * 現在表示されているリサイクルビューを返す
     *
     * @return
     */
    private RecyclerView getCurrentRecyclerView() {
        FragmentPagerAdapter sectionPagerAdapter = (FragmentPagerAdapter) mViewPager.getAdapter();
        MainActivityFragment fragment = (MainActivityFragment) sectionPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
        return (RecyclerView) fragment.getView().findViewById(R.id.recyclerView);
    }

    /**
     * クリップボードコピー
     *
     * @param textView TextView
     */
    private void copyClip(TextView textView) {
        ClipData.Item item = new ClipData.Item(textView.getText());
        ClipData clipData = new ClipData(new ClipDescription("text_data", new String[]{ClipDescription.MIMETYPE_TEXT_URILIST}), item);
        ClipboardManager clipboardManager = (ClipboardManager) textView.getContext().getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(clipData);
    }

    /**
     * フローティングアクションボタンのクリックイベントを定義
     */
    private void setFabEvent() {
        Log.d(TAG, "setFabEvent Start");

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentsInsertEvent();
            }
        });

        mRecyclerAdapter.notifyDataSetChanged();
        Log.d(TAG, "setFabEvent End");
    }

    /**
     * ナビゲーションドロワーリスト設定
     */
    private void setDrawerList() {
        // ナビゲーションドロワーに設定するリストを作成
        ListView mDrawerList = (ListView) getActivity().findViewById(R.id.left_drawer);

        ArrayList<String> drawerList = new ArrayList<>();
        for (CategoryBean category : mCategoryList) {
            drawerList.add(category.getCategory_name());
        }
        mDrawerAdapter = new ArrayAdapter<>(getActivity(), R.layout.drawer_list_item, drawerList);
        mDrawerList.setAdapter(mDrawerAdapter);

        //リスト項目が選択された時のイベントを追加
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mViewPager = (ViewPager) getActivity().findViewById(R.id.pager);
                mViewPager.setCurrentItem(position);

                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
                drawer.closeDrawers();
            }
        });
    }

    /**
     * ポップアップメニューから編集ボタン押下
     *
     * @param position int
     */
    private void popupEditEvent(int position) {
        // 設定押下
        int page = mViewPager.getCurrentItem();
        ContentsBean selectedContents =
                mContentsListMap.get(mCategoryList.get(page).getCategory_name()).get(position);
        String contentsId = String.valueOf(selectedContents.getId());
        String contentsTitle = selectedContents.getContents_title();
        String contents = selectedContents.getContents();
        Intent intent = new Intent(getContext(), EditContentsActivity.class);

        // 情報受け渡し
        intent.putExtra("contentsId", contentsId);
        intent.putExtra("contentsTitle", contentsTitle);
        intent.putExtra("contents", contents);

        // 編集画面起動
        startActivityForResult(intent, REQUEST_CODE_EDIT_CONTENTS);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach Start");

        // リスナーは必ずここでセットする。
        if (context instanceof OnSettingChengedListener) {
            settingChengedListener = (OnSettingChengedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        Log.d(TAG, "onAttach End");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach Start");

        settingChengedListener = null;

        Log.d(TAG, "onDetach End");
    }


    interface OnSettingChengedListener {
        void onSettingChengedListener(boolean isChenged);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        // フローティングアクションボタンを設定
        setFabEvent();
    }

    /**
     * コンテキストメニュー押下イベント
     *
     * @param item MenuItem
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected getItemPosition Start");

        // 押下されたメニューで分岐
        switch (item.getItemId()) {
            case R.id.add_category:
                // カテゴリー追加押下
                Log.d(TAG, "add_category selected");
                categoryInsertEvent();
                return true;

            case R.id.delete_category:
                // カテゴリー削除押下
                Log.d(TAG, "delete_category selected");
                categoryDeletetEvent();
                return true;

            case R.id.all_delete:
                // 全削除押下
                Log.d(TAG, "all_delete selected");
                deletetAllEvent();
                return true;

            case R.id.action_settings:
                // 設定押下
                Log.d(TAG, "action_settings selected");
                Intent intent = new android.content.Intent(getContext(), SettingsActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SETTING);
                return true;

        }
        Log.d(TAG, "onOptionsItemSelected getItemPosition End");
        return super.onOptionsItemSelected(item);
    }

    /**
     * ポップアップメニュー押下イベント
     *
     * @param item     MenuItem
     * @param position int
     * @return boolean
     */
    public boolean onOptionsPopUpItemSelected(MenuItem item, TextView textRowId, TextView textContents, int position) {
        Log.d(TAG, "onOptionsPopUpItemSelected getItemPosition Start");

        // 押下されたメニューで分岐
        switch (item.getItemId()) {

            case R.id.menu_edit:
                Log.d(TAG, "dmenu_edit selected");

                // 編集ボタン押下時イベント
                popupEditEvent(position);

                return true;

            case R.id.menu_share:
                Log.d(TAG, "menu_share selected");

                Intent intent = new Intent();
                String sendString = textContents.getText().toString();

                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, sendString);
                startActivity(intent);

                return true;

            case R.id.menu_delete:
                Log.d(TAG, "delete_category selected");

                // 削除ボタン押下イベント
                contentsDelete(textRowId.getText().toString());

                // 対象のカテゴリ取得
                int page = mViewPager.getCurrentItem();
                String categoryName = mCategoryList.get(page).getCategory_name();

                // 変数から削除
                ContentsBean removeContents = mContentsListMap.get(categoryName).get(position);
                mContentsListMap.get(categoryName).remove(removeContents);

                // リサイクルビューに通知
                mRecyclerAdapter.notifyItemRemoved(position);
                mRecyclerAdapter.notifyItemRangeChanged(position, mContentsListMap.get(categoryName).size());
				
				// 再取得する行数
				int rowSize= mContentsListMap.get(categoryName).size();
				int lack = mMaxRowSize - rowSize;
				
				// 取得する行のid
				int id = mContentsListMap.get(categoryName).get(rowSize - 1).getId();
				
				// コンテンツ取得
                LinkedHashMap<String, ArrayList<ContentsBean>> contentsMap = getContents(categoryName, String.valueOf(id), String.valueOf(lack));

                // 変数に格納
				for (ContentsBean contents : contentsMap.get(categoryName)) {
					mContentsListMap.get(categoryName).add(contents);
					mRecyclerAdapter.notifyItemInserted(mContentsListMap.get(categoryName).size());
				}

                return true;
        }
        Log.d(TAG, "onOptionsPopUpItemSelected getItemPosition End");
        return super.onOptionsItemSelected(item);
    }
	
	// 指定した件数分、コンテンツを取得する
	LinkedHashMap<String, ArrayList<ContentsBean>> getContents(String categoryName, String from, String limit) {
        Log.d(TAG, "getContents Start");

        // DBからカテゴリー名を取得する
        LinkedHashMap<String, ArrayList<ContentsBean>> result = new LinkedHashMap<>();
        SQLiteDatabase sqLiteDatabase = new DBOpenHelper(this.getContext()).getWritableDatabase();

        try {
			Cursor cursor = new DBHelper(sqLiteDatabase).selectContentsWhereCategoryNameId(new String[]{categoryName, from, limit});
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
			result.put(categoryName, contentsList);
			cursor.close();

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());

        } finally {
            sqLiteDatabase.close();
        }

        Log.d(TAG, "getContents End");
        return result;
    }
	
	
}
