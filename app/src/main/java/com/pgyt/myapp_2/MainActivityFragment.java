package com.pgyt.myapp_2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_TITLE_NAME = "title_name";
    public static final String ARG_CONTENTS = "contents";
    private static  HashMap<String, ArrayList<String>> CONTENTS_MAP;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<String> viewItem;

    // コンストラクタ
    public MainActivityFragment() {
    }

    public static MainActivityFragment newInstance(int page, String title, HashMap<String, ArrayList<String>> contentsMap) {
        MainActivityFragment fragment = new MainActivityFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, page);
        args.putString(ARG_TITLE_NAME, title);
        CONTENTS_MAP = contentsMap;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // param取得
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // パラメータ取得
        String title = getArguments().getString(ARG_TITLE_NAME);
        ArrayList<String> contentsList = CONTENTS_MAP.get(title);

        View view = inflater.inflate(R.layout.content_main, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new RecilerItemDecoration(view.getContext()));
        viewItem = contentsList;

        // 表示内容があるときだけ設定
        if(viewItem != null){
            mAdapter = new CustomAdapter(viewItem);
            mRecyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
