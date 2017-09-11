package com.pgyt.myapp_2;

import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import com.pgyt.myapp_2.model.*;

public class EditContentsActivity extends AppCompatActivity implements EditContentsFragment.OnFragmentInteractionListener{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 情報受取
        Intent intent = getIntent();
        String contentsId = intent.getStringExtra("contentsId");
        String contentsTitle = intent.getStringExtra("contentsTitle");
        String contents = intent.getStringExtra("contents");

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new EditContentsFragment(contentsId, contentsTitle, contents)).commit();

    }
	
	
	
	public void onFragmentInteractionListener(View view, ContentsBean contents) {
		
	}
}
