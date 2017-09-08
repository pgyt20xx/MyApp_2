package com.pgyt.myapp_2;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pgyt.myapp_2.model.CategoryBean;
import com.pgyt.myapp_2.model.ContentsBean;

import java.util.ArrayList;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.pgyt.myapp_2.MainActivity.mCategoryList;

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
    private OnFragmentInteractionListener mListener;
    private ViewPager mViewPager;
    private CustomAdapter mRecyclerAdapter;
    private CustomActionModeCallback mActionModeCallback;
    private ArrayAdapter<String> mDrawerAdapter;


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
            // param取得
            Log.d(TAG, "OnCreate");

        }
        Log.d(TAG, "onCreate End");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView Start");

        // パラメータ取得
        String mCategoryName = getArguments().getString(ARG_TITLE_NAME);

        View view = inflater.inflate(R.layout.content_main, container, false);

        // ページャー取得
        this.mViewPager = (ViewPager) getActivity().findViewById(R.id.pager);

        if (MainActivity.mContentsListMap.get(mCategoryName) == null) {
            Log.d(TAG, "onCreateView End contentsList == null");
            return view;
        }

        // ナビゲーションドロワーリスト設定
        setDrawerList();

        // リサイクルビューの設定
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new RecilerItemDecoration(getContext()));

        // リサイクルビューのアダプター設定
        mRecyclerAdapter = new CustomAdapter(getContext(), mCategoryName, MainActivity.mContentsListMap.get(mCategoryName));
        mRecyclerView.setAdapter(mRecyclerAdapter);

        // 行のクリックイベント
        mRecyclerAdapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, TextView textView, int position) {
                Toast.makeText(getContext(), textView.getText(), Toast.LENGTH_SHORT).show();
                copyClip(textView);
            }
        });

        // イメージのクリックイベント
        mRecyclerAdapter.setOnImageItemClickListener(new CustomAdapter.OnImageItemClickListener() {
            @Override
            public void onClick(View view, TextView textView, int position) {
                Toast.makeText(getContext(), textView.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        // ロングクリックイベント
        mRecyclerAdapter.setOnItemLongClickListener(new CustomAdapter.OnItemLongClickListener() {
            @Override
            public boolean onLongClick(final View view, final int position) {
                if (mActionModeCallback != null) {
                    return false;
                }

                // 選択状態アイコンを取得
                final ImageView mSelectedImage = (ImageView) view.findViewById(R.id.image_clip_edit);

                // アクションモードコールバック呼び出し。
                mActionModeCallback = new CustomActionModeCallback(view, getFragmentManager()) {
                    // Called when the user exits the action mode
                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        // アクションモードが破棄された時の処理
                        mSelectedImage.setVisibility(View.GONE);
                        mActionModeCallback = null;
                    }
                };

                // 選択状態アイコンをアクティブに設定
                mSelectedImage.setVisibility(View.VISIBLE);

                // ダイアログのボタン押下
                getActivity().startActionMode(mActionModeCallback);
                mActionModeCallback.setOnBottomClickListener(new CustomActionModeCallback.OnBottomClickListener() {
                    @Override
                    public void onBottomClick(boolean bool, ActionMode mode) {
                        if (bool) {
                            // データ削除
                            contentsDelete((TextView) getActivity().findViewById(R.id.row_id));

                            // 対象のカテゴリ取得
                            int page = mViewPager.getCurrentItem();
                            String categoryName = MainActivity.mCategoryList.get(page).getCategory_name();

                            // 変数から削除
                            ContentsBean removeContents = MainActivity.mContentsListMap.get(categoryName).get(position);
                            MainActivity.mContentsListMap.get(categoryName).remove(removeContents);

                            // リサイクルビューに通知
                            mRecyclerAdapter.notifyItemRemoved(position);
                            mRecyclerAdapter.notifyItemRangeChanged(position, MainActivity.mContentsListMap.get(categoryName).size());

                            Snackbar.make(getActivity().findViewById(R.id.activity_main), "Delete Success", Snackbar.LENGTH_SHORT).show();

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

                    // ページャーに変更を通知
                    mViewPager.getAdapter().notifyDataSetChanged();
                    mViewPager.setCurrentItem(mCategoryList.size() - 1);

                    Snackbar.make(getActivity().findViewById(R.id.activity_main), "Registration Success", Snackbar.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());

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
                int position = mViewPager.getCurrentItem();

                // デフォルトタブでなければ削除
                if (position == MainActivity.CLIPBOARD_TAB_POSITON) {
                    Snackbar.make(getActivity().findViewById(R.id.activity_main), "CLIPBOARD cannot Delete", Snackbar.LENGTH_SHORT).show();

                } else {
                    SQLiteDatabase sqLiteDatabase = new DBOpenHelper(getContext()).getWritableDatabase();
                    try {
                        String param = mCategoryList.get(position).getCategory_name();
                        new DBHelper(sqLiteDatabase).deletetCategory(param);

                        // 変数からカテゴリーを削除
                        mCategoryList.remove(position);

                        // ページャーに変更を通知
                        mViewPager.getAdapter().notifyDataSetChanged();

                        Snackbar.make(getActivity().findViewById(R.id.activity_main), "Delete Success", Snackbar.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());

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
                mViewPager = (ViewPager) getActivity().findViewById(R.id.pager);
                int position = mViewPager.getCurrentItem();

                // コンテンツ登録
                SQLiteDatabase sqLiteDatabase = new DBOpenHelper(getContext()).getWritableDatabase();
                try {
                    ContentsBean contents = new ContentsBean();
                    contents.setCategory_name(mCategoryList.get(position).getCategory_name());
                    contents.setContents_title(contentsTitle.getText().toString());
                    contents.setContents(contentsText.getText().toString());
                    int id = (int) new DBHelper(sqLiteDatabase).insertContents(contents);

                    // 1行目に追加する
                    contents.setId(id);
                    MainActivity.mContentsListMap.get(mCategoryList.get(position).getCategory_name()).add(0, contents);

                    // リサイクルビューに通知
                    mRecyclerAdapter.notifyItemInserted(0);

                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());

                } finally {
                    sqLiteDatabase.close();
                }

                Snackbar.make(getActivity().findViewById(R.id.activity_main), "Registration Success", Snackbar.LENGTH_SHORT).show();

            }

            @Override
            public void onNegativeClick(EditText contentsTitle, EditText contentsText) {
                Log.d(TAG, "contentsInsertEvent Click Negative");
            }
        });

        newFragment.show(getFragmentManager(), "contentsInsertEvent");

        Log.d(TAG, "contentsInsertEvent End");
    }

    /**
     * コンテンツ削除
     */
    private void contentsDelete(TextView mRowId) {
        Log.d(TAG, "contentsDelete Start");

        SQLiteDatabase sqLiteDatabase = new DBOpenHelper(getContext()).getWritableDatabase();
        try {
            new DBHelper(sqLiteDatabase).deletetContents(mRowId.getText().toString());

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());

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

        CustomDialogFragment newFragment = CustomDialogFragment.newInstance("Add Categry", "realy?", null, null, "");
        newFragment.setEditDialogListener1(new CustomDialogFragment.EditDialogListener1() {
            @Override
            public void onPositiveClick(EditText text) {
                Log.d(TAG, "deleteAllEvent Click Positive");

                // 全データ削除
                SQLiteDatabase sqLiteDatabase = new DBOpenHelper(getContext()).getWritableDatabase();
                try {
                    new DBHelper(sqLiteDatabase).deletetAll();
                    Snackbar.make(getActivity().findViewById(R.id.activity_main), "Delete All data Success", Snackbar.LENGTH_SHORT).show();

                    // データをクリア
                    MainActivity.mCategoryList.clear();
                    MainActivity.mContentsListMap.clear();

                    // デフォルトカテゴリを取得
                    ArrayList<CategoryBean> categoryList = new ArrayList<>();
                    Cursor cursor = new DBHelper(sqLiteDatabase).selectCategory(text.getText().toString());
                    boolean isEof = cursor.moveToFirst();
                    while (isEof) {
                        CategoryBean category = new CategoryBean();
                        category.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                        category.setCategory_name(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
                        categoryList.add(category);
                        isEof = cursor.moveToNext();
                    }

                    // デフォルトカテゴリを設定
                    MainActivity.mCategoryList = categoryList;
                    cursor.close();

                    // データが削除されたことを通知
                    mViewPager.getAdapter().notifyDataSetChanged();
                    mRecyclerAdapter.notifyDataSetChanged();


                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());

                } finally {
                    sqLiteDatabase.close();
                }

            }

            @Override
            public void onNegativeClick(EditText text) {
                Log.d(TAG, "deleteAllEvent Click Negative");
            }
        });
        newFragment.show(getFragmentManager(), "categoryInsertEvent");

        Log.d(TAG, "deleteAllEvent End");
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach Start");

        // リスナーは必ずここでセットする。
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

        mListener = null;

        Log.d(TAG, "onDetach End");
    }


    interface OnFragmentInteractionListener {
        void onFragmentInteractionListener(View v);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        // フローティングアクションボタンを設定
        setFabEvent();
    }

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
                startActivity(intent);
                return true;

        }
        Log.d(TAG, "onOptionsItemSelected getItemPosition End");
        return super.onOptionsItemSelected(item);
    }
}
