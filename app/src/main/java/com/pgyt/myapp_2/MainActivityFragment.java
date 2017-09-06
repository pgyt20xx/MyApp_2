package com.pgyt.myapp_2;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pgyt.myapp_2.model.CategoryBean;
import com.pgyt.myapp_2.model.ContentsBean;

import java.util.ArrayList;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_TITLE_NAME = "title_name";
    private static final String ARG_CONTENTS_LIST = "contents_list";
    private static final String BUTTOM_POSITIVE = "OK";
    private static final String BUTTOM_NEGATIVE = "CANCEL";
    private  static final String DIALOG_STRING_TITLE = "Title";
    private  static final String DIALOG_STRING_CONTENTS = "Contents";
    private static final String TAG = "MainActivityFragment";

    private ViewPager mViewPager;

    CustomAdapter mAdapter;

    CustomActionModeCallback mActionModeCallback;


    // コンストラクタ
    public MainActivityFragment() {
    }

    static MainActivityFragment newInstance(int page, String title, ArrayList<ContentsBean> contentsList) {
        Log.d(TAG, "newInstance Start");

        MainActivityFragment fragment = new MainActivityFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, page);
        args.putString(ARG_TITLE_NAME, title);
        args.putSerializable(ARG_CONTENTS_LIST, contentsList);
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
        String title = getArguments().getString(ARG_TITLE_NAME);

        ArrayList<ContentsBean> contentsList = (ArrayList<ContentsBean>) getArguments().getSerializable(ARG_CONTENTS_LIST);

        View view = inflater.inflate(R.layout.content_main, container, false);

        if (contentsList == null) {
            Log.d(TAG, "onCreateView End contentsList == null");
            return view;
        }

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new RecilerItemDecoration(getContext()));

        // 表示内容があるときだけ設定
        mAdapter = new CustomAdapter(getContext(), title, contentsList);
        mRecyclerView.setAdapter(mAdapter);

        // 行のクリックイベント
        mAdapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, TextView textView, int position) {
                Toast.makeText(getContext(), textView.getText(), Toast.LENGTH_SHORT).show();
                copyClip(textView);
            }
        });

        // イメージのクリックイベント
        mAdapter.setOnImageItemClickListener(new CustomAdapter.OnImageItemClickListener() {
            @Override
            public void onClick(View view, TextView textView, int position) {
                Toast.makeText(getContext(), textView.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        // ロングクリックイベント
        mAdapter.setOnItemLongClickListener(new CustomAdapter.OnItemLongClickListener() {
            @Override
            public boolean onLongClick(final View view, final int position) {
                if (mActionModeCallback != null) {
                    return false;
                }
				
				final ImageView mSelectedImage = (ImageView) view.findViewById(R.id.image_clip_edit);
				
                mActionModeCallback = new CustomActionModeCallback(view, getFragmentManager()) {
                    // Called when the user exits the action mode
                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
						mSelectedImage.setVisibility(View.GONE);
                        mActionModeCallback = null;
                    }
                };
				
				
				mSelectedImage.setVisibility(View.VISIBLE);
				
                getActivity().startActionMode(mActionModeCallback);
                mActionModeCallback.setOnBottomClickListener(new CustomActionModeCallback.OnBottomClickListener() {
                    @Override
                    public void onBottomClick(boolean bool, ActionMode mode) {
                        if (bool) {
                            //contentsDelete(mRowId);
							mListener.onContentsChanged(view);
							mAdapter.notifyItemRemoved(position);

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

    private void copyClip(TextView textView) {
        // クリップボードにコピー
        ClipData.Item item = new ClipData.Item(textView.getText());
        ClipData clipData = new ClipData(new ClipDescription("text_data", new String[]{ClipDescription.MIMETYPE_TEXT_URILIST}), item);
        ClipboardManager clipboardManager = (ClipboardManager) textView.getContext().getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(clipData);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach Start");

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
		void onContentsChanged(View v);
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
                Log.d(TAG, "add_category selected");
                categoryInsertEvent();
                return true;

            case R.id.delete_category:
                Log.d(TAG, "delete_category selected");
//                categoryDeletetEvent();
                return true;

            case R.id.all_delete:
                Log.d(TAG, "all_delete selected");
//                deletetAllEvent();
                return true;

            case R.id.action_settings:
                Log.d(TAG, "action_settings selected");
                Intent intent = new android.content.Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
                return true;

        }
        Log.d(TAG, "onOptionsItemSelected getItemPosition End");
        return super.onOptionsItemSelected(item);
    }

    /**
     * カテゴリ追加のダイアログイベント
     */
    private void categoryInsertEvent() {
        Log.d(TAG, "categoryInsertEvent Start");

        final EditText editView = new EditText(getContext());

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
        dialog.setTitle(R.string.add_category);
        dialog.setView(editView);

        // OKボタン押下時
        dialog.setPositiveButton(BUTTOM_POSITIVE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Log.d(TAG, "categoryInsertEvent Click OK");

                // 値が入力されていない場合は何もしない
                if (TextUtils.isEmpty(editView.getText())) {
                    Snackbar.make(getActivity().findViewById(R.id.activity_main), "Please enter something", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                SQLiteDatabase sqLiteDatabase = new DBOpenHelper(getContext()).getWritableDatabase();
                try {
                    CategoryBean category = new CategoryBean();
                    category.setCategory_name(editView.getText().toString());
                    int id = (int) new DBHelper(sqLiteDatabase).insertCategory(category);

                    // 新規タブ追加
                    category.setId(id);
                    MainActivity.mCategoryList.add(category);

                    Snackbar.make(getActivity().findViewById(R.id.activity_main), "Registration Success", Snackbar.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());

                } finally {
                    sqLiteDatabase.close();
                }

                mAdapter.notifyDataSetChanged();

//                // ナビゲーションドロワーの更新
//                adapter.notifyDataSetChanged();
//
//                // フラグメントの初期化
//                initFragmentView();
//
//                // 追加したページを開く
//                mViewPager = (ViewPager) findViewById(R.id.pager);
//                mViewPager.setCurrentItem(mCategoryList.size() - 1);
            }
        });
        // Cancelボタン押下時
        dialog.setNegativeButton(BUTTOM_NEGATIVE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                Log.d(TAG, "categoryInsertEvent Click cancel");

            }
        });

        dialog.show();
        Log.d(TAG, "categoryInsertEvent End");
    }

    /**
     * コンテンツ追加のダイアログイベント
     */
    private void contentsInsertEvent() {
        Log.d(TAG, "contentsInsertEvent Start");

        // レイアウトセット
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView textTitle = new TextView(getContext());
        final TextView textContents = new TextView(getContext());
        textTitle.setText(DIALOG_STRING_TITLE);
        textContents.setText(DIALOG_STRING_CONTENTS);

        final EditText contentsTitleEditView = new EditText(getContext());
        final EditText contentsEditView = new EditText(getContext());
        layout.addView(textTitle, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(contentsTitleEditView);
        layout.addView(textContents, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(contentsEditView);

        // ダイアログセット
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
        dialog.setTitle(R.string.fab_title);
        dialog.setView(layout);

        // OKボタン押下時
        dialog.setPositiveButton(BUTTOM_POSITIVE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "contentsInsertEvent Click OK");

                // 値が入力されていない場合は何もしない
                if (TextUtils.isEmpty(contentsTitleEditView.getText())) {
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
                    contents.setCategory_name(MainActivity.mCategoryList.get(position).getCategory_name());
                    contents.setContents_title(contentsTitleEditView.getText().toString());
                    contents.setContents(contentsEditView.getText().toString());
                    int id = (int) new DBHelper(sqLiteDatabase).insertContents(contents);

                    // 1行目に追加する
                    contents.setId(id);
                    MainActivity.mContentsList.add(0,  contents);

                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());

                } finally {
                    sqLiteDatabase.close();
                }

                Snackbar.make(getActivity().findViewById(R.id.activity_main), "Registration Success", Snackbar.LENGTH_SHORT).show();

                // フラグメントの初期化
//                initFragmentView();

                // 元のページを開く
//                mViewPager.setCurrentItem(position);

            }
        });
        // Cancelボタン押下時
        dialog.setNegativeButton(BUTTOM_NEGATIVE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                Log.d(TAG, "contentsInsertEvent Click Cancel");

            }
        });

        dialog.show();
        Log.d(TAG, "contentsInsertEvent End");
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

    	mAdapter.notifyDataSetChanged();
        Log.d(TAG, "setFabEvent End");
    }

}
