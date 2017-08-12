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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

//    private static final String ARG_PARAM = "page";
    private String mParam;
    private OnFragmentInteractionListener mListener;
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_TITLE_NAME = "title_name";

    // TODO 仮
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<String> viewItem;
    private static final String[] names = {
            "test",
            "test",
            "test",
            "test",
            "test",
            "test",
            "test",
            "test",
            "test",
            "test"
    };



    DBHelper dBhelper = null;

    // コンストラクタ
    public MainActivityFragment() {
    }

    public static MainActivityFragment newInstance(int page, String title) {
        MainActivityFragment fragment = new MainActivityFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, page);
        args.putString(ARG_TITLE_NAME, title);
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
        int page = getArguments().getInt(ARG_SECTION_NUMBER, 0);
        String title = getArguments().getString(ARG_TITLE_NAME);

        View view = inflater.inflate(R.layout.content_main, container, false);

        // TODO Mapから表示内容を取得
        ArrayList itemList = new ArrayList<String>();
        itemList.add("aaa");
        itemList.add("bbb");
        itemList.add("ccc");
        itemList.add("ddd");

        Map<String, List<String>> itemMap = new LinkedHashMap();
        itemMap.put("MY_CLIP", itemList);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        viewItem = itemMap.get(title);

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
