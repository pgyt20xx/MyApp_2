package com.pgyt.myapp_2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pgyt.myapp_2.model.ContentsBean;

/**
 * EditContentsActivity
 */
public class EditContentsActivity extends AppCompatActivity
        implements EditContentsFragment.OnFragmentInteractionListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 情報受取
        Intent intent = getIntent();
        String contentsId = intent.getStringExtra("contentsId");
        String contentsTitle = intent.getStringExtra("contentsTitle");
        String contents = intent.getStringExtra("contents");

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content,
                        new EditContentsFragment(contentsId, contentsTitle, contents)).commit();

    }


    /**
     * @param view     View
     * @param contents ContentsBean
     */
    public void onFragmentInteractionListener(View view, ContentsBean contents) {
        Intent data = new Intent();
        data.putExtra("contents", contents);
        setResult(RESULT_OK, data);
        finish();
    }
}
