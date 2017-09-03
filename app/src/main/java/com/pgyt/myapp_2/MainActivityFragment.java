package com.pgyt.myapp_2;

import android.content.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.util.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_TITLE_NAME = "title_name";

    private static final String TAG = "MainActivityFragment";
	
	private ActionMode mActionMode;


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
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new RecilerItemDecoration(view.getContext()));

        // 表示内容があるときだけ設定
        if (contentsMap != null) {
            CustomAdapter mAdapter = new CustomAdapter(getContext(), title, contentsMap);
            mRecyclerView.setAdapter(mAdapter);
			mAdapter.setOnItemClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Toast.makeText(getContext(), "test", Toast.LENGTH_SHORT).show();
					}
				});
				
			mAdapter.setOnItemLongClickListener(new View.OnLongClickListener() {
					@Override
                	public boolean onLongClick(View view) {
                    	if (mActionMode != null) {
                        	return false;
                    	}

                    	mActionMode = getActivity().startActionMode(mActionModeCallback);
                    	view.setSelected(true);
                    	return true;
              		}
				});

        }
        Log.d(TAG, "onCreateView End");

		
        return view;
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
	
	ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {

				case R.id.menu_edit:

					mode.finish();

					return true;
				case R.id.menu_delete:
					mode.finish();

					return true;
				case R.id.menu_share:
					mode.finish();

					return true;
				default:
					return false;

            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach Start");

        mListener = null;
        Log.d(TAG, "onDetach End");
    }

    interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
	
}
