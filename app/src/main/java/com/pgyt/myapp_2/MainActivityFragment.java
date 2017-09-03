package com.pgyt.myapp_2;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedHashMap;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment{

    private OnFragmentInteractionListener mListener;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_TITLE_NAME = "title_name";

    private static final String TAG = "MainActivityFragment";


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
                public boolean onLongClick(View view) {
                    getActivity().startActionMode(new CustomActionModeCallback(view, getFragmentManager()));
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

    private void copyClip(TextView textView) {
        // クリップボードにコピー
        ClipData.Item item = new ClipData.Item(textView.getText());
        ClipData clipData = new ClipData(new ClipDescription("text_data", new String[]{ClipDescription.MIMETYPE_TEXT_URILIST}), item);
        ClipboardManager clipboardManager = (ClipboardManager) textView.getContext().getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(clipData);
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
    }

}
