package com.pgyt.myapp_2;

import android.content.*;
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

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.pgyt.myapp_2.MainActivity.mCategoryList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_TITLE_NAME = "title_name";
    private static final String BUTTOM_POSITIVE = "OK";
    private static final String BUTTOM_NEGATIVE = "CANCEL";
    private static final String DIALOG_STRING_TITLE = "Title";
    private static final String DIALOG_STRING_CONTENTS = "Contents";
    private static final String TAG = "MainActivityFragment";
    private ViewPager mViewPager;
    private CustomAdapter mAdapter;
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

        if (MainActivity.mContentsListMap.get(mCategoryName) == null) {
            Log.d(TAG, "onCreateView End contentsList == null");
            return view;
        }

        // ナビゲーションドロワーリスト設定
        setDrawerList();

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new RecilerItemDecoration(getContext()));

        // 表示内容があるときだけ設定
        mAdapter = new CustomAdapter(getContext(), mCategoryName, MainActivity.mContentsListMap.get(mCategoryName));
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
	
	private void categoryInsertEvent() {
		CustomDialogFragment newFragment = CustomDialogFragment.newInstance("Add Categry", "realy?", null, null, "1");
		newFragment.setEditDialogListener(new CustomDialogFragment.EditDialogListener() {
				@Override
				public void onPositiveClick(EditText text) {
					//bottomClickListener.onBottomClick(true, mode);   Log.d(TAG, "contentsInsertEvent Click OK");

					// 値が入力されていない場合は何もしない
					if (TextUtils.isEmpty(text.getText())) {
						Snackbar.make(getActivity().findViewById(R.id.activity_main), "Please enter something", Snackbar.LENGTH_SHORT).show();
						return;
					}

					SQLiteDatabase sqLiteDatabase = new DBOpenHelper(getContext()).getWritableDatabase();
					try {
						CategoryBean category = new CategoryBean();
						category.setCategory_name(text.getText().toString());
						int id = (int) new DBHelper(sqLiteDatabase).insertCategory(category);

						// 新規タブ追加
						category.setId(id);
						mCategoryList.add(category);

						mViewPager = (ViewPager) getActivity().findViewById(R.id.pager);
						mViewPager.getAdapter().notifyDataSetChanged();
						mDrawerAdapter.addAll();

						mDrawerAdapter.notifyDataSetChanged();
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
					//bottomClickListener.onBottomClick(false, mode);
				}
			});
		newFragment.show(getFragmentManager(), "categoryInsertEvent");

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
                    contents.setCategory_name(mCategoryList.get(position).getCategory_name());
                    contents.setContents_title(contentsTitleEditView.getText().toString());
                    contents.setContents(contentsEditView.getText().toString());
                    int id = (int) new DBHelper(sqLiteDatabase).insertContents(contents);

                    // 1行目に追加する
                    contents.setId(id);
                    MainActivity.mContentsListMap.get(mCategoryList.get(position).getCategory_name()).add(0,  contents);

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

    /**
     * ナビゲーションドロワーリスト設定
     */
    private void setDrawerList(){
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

}
