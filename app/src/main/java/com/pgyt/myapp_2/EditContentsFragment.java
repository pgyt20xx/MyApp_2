package com.pgyt.myapp_2;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class EditContentsFragment extends Fragment {

    private static String TAG = "EditContentsFragment";
    private String contentsId;
    private String contentsTitle;
    private String contents;

    public EditContentsFragment() {
    }

    public EditContentsFragment(String contentsId, String contentsTitle, String contents){
        this.contentsId = contentsId;
        this.contentsTitle = contentsTitle;
        this.contents = contents;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.contents_edit_view, container, false);
        TextView title = (TextView) view.findViewById(R.id.edit_contents_title);
        title.setText(contentsTitle);

        return view;
    }
}
