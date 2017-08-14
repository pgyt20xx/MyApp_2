package com.pgyt.myapp_2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

//    private static final String ARG_PARAM = "page";
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

    public static void contentsArgSet(ArrayList<String> contents){
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_CONTENTS, contents);
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
        int page = getArguments().getInt(ARG_SECTION_NUMBER, 0);
        String title = getArguments().getString(ARG_TITLE_NAME);
        ArrayList<String> contentsList = CONTENTS_MAP.get(title);

        View view = inflater.inflate(R.layout.content_main, container, false);

        // TODO Mapから表示内容を取得
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        viewItem = contentsList;

        // 表示内容があるときだけ設定
        if(viewItem != null){
            mAdapter = new CustomAdapter(viewItem);
            mRecyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
