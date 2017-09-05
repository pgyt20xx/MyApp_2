package com.pgyt.myapp_2;

import android.content.*;
import android.database.sqlite.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.util.*;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private CustomAdapter mAdapter;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_TITLE_NAME = "title_name";

    private static final String TAG = "MainActivityFragment";

    CustomActionModeCallback mActionModeCallback;


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
        String title = getArguments().getString(ARG_TITLE_NAME);
        LinkedHashMap<String, String[]> contentsMap = MainActivity.CONTENTS.get(title);

        View view = inflater.inflate(R.layout.content_main, container, false);

        if (contentsMap == null) {
            Log.d(TAG, "onCreateView End contentsMap == null");
            return view;
        }

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new RecilerItemDecoration(getContext()));

        // 表示内容があるときだけ設定
        mAdapter = new CustomAdapter(getContext(), title, contentsMap);
        mRecyclerView.setAdapter(mAdapter);

        // 行のクリックイベント
        mAdapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, TextView textView) {
                Toast.makeText(getContext(), textView.getText(), Toast.LENGTH_SHORT).show();
                copyClip(textView);
            }
        });

        // イメージのクリックイベント
        mAdapter.setOnImageItemClickListener(new CustomAdapter.OnImageItemClickListener() {
            @Override
            public void onClick(View view, TextView textView) {
                Toast.makeText(getContext(), textView.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        // ロングクリックイベント
        mAdapter.setOnItemLongClickListener(new CustomAdapter.OnItemLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
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


    private void contentsDelete(TextView mRowId) {
        Log.d(TAG, "contentsDelete Start");

        SQLiteDatabase sqLiteDatabase = new DBOpenHelper(getContext()).getWritableDatabase();
        try {
            //new DBHelper(sqLiteDatabase).deletetContents(mRowId.getText().toString());
            // 変数からコンテンツを削除
//            mViewPager = (ViewPager) view.findViewById(R.id.pager);
//            int position = mViewPager.getCurrentItem();
            //MainActivity.CONTENTS.remove(MainActivity.CONTENTS.get(MainActivity.TITLE_NAME.get(1)).get(mRowId));
            //mAdapter.updateData(MainActivity.CONTENTS.get("g"));
			

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());

        } finally {
            sqLiteDatabase.close();
        }

        Log.d(TAG, "contentsDelete End");

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
        void onFragmentInteraction(Uri uri);
		void onContentsChanged(View v)
    }

}
