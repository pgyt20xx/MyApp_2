package com.pgyt.myapp_2;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pgyt.myapp_2.model.ContentsBean;

import static com.pgyt.myapp_2.CommonConstants.BLANK_STRING;


/**
 * EditContentsFragment
 */
public class EditContentsFragment extends Fragment {
    private static final String TAG = "EditContentsFragment";

    private OnFragmentInteractionListener mListener;
    private String contentsId;
    private String contentsTitle;
    private String contents;

    public EditContentsFragment() {
    }

    @SuppressLint("ValidFragment")
    public EditContentsFragment(String contentsId, String contentsTitle, String contents) {
        this.contentsId = contentsId;
        this.contentsTitle = contentsTitle;
        this.contents = contents;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreateView Start");

        View view = inflater.inflate(R.layout.contents_edit_view, container, false);

        // コンテンツタイトルを設定
        TextView titleView = (TextView) view.findViewById(R.id.edit_contents_title);
        titleView.setText(contentsTitle);

        // コンテンツを設定
        final EditText contentsView = (EditText) view.findViewById(R.id.edit_contents);
        contentsView.setText(contents);

        // ボタンのクリックイベントを設定
        Button registrationButton = (Button) view.findViewById(R.id.button_registration);
        Button backButton = (Button) view.findViewById(R.id.button_back);

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onCreateView registrationButton Click");

                // nullの場合は空文字セット
                if (contentsView.getText() == null) {
                    contentsView.setText(BLANK_STRING);
                }

                // 編集内容を登録
                ContentsBean param = new ContentsBean();
                param.setId(Integer.parseInt(contentsId));
                param.setContents_title(contentsTitle);
                param.setContents(contentsView.getText().toString());

                // 更新
                updateContents(param);

                // Activityへ通知
                mListener.onFragmentInteractionListener(view, param);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onCreateView backButton Click");
                getActivity().finish();
            }
        });

        Log.d(TAG, "onCreateView End");
        return view;
    }

    /**
     * コンテンツの更新
     *
     * @param param ContentsBean
     */
    private void updateContents(ContentsBean param) {
        Log.d(TAG, "updateContents Start");

        SQLiteDatabase sqLiteDatabase = new DBOpenHelper(getActivity()).getWritableDatabase();
        try {
            // 編集内容で更新
            new DBHelper(sqLiteDatabase).updateContents(param);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());

        } finally {
            sqLiteDatabase.close();
        }
        Log.d(TAG, "updateContents End");
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

    interface OnFragmentInteractionListener {
        void onFragmentInteractionListener(View v, ContentsBean contens);
    }

}
