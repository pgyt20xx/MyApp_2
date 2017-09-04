package com.pgyt.myapp_2;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.*;
import android.support.v7.widget.*;
import android.view.Display.*;

public class CustomActionModeCallback implements ActionMode.Callback {

    private final String TAG = "ActionModeCallback";

    private View view;
    private Context context;
    private ImageView mSelectedImage;
    private TextView mRowId;
    private FragmentManager mFragmentManager;
	
	private OnBottomClickListener bottomClickListener;
	private ActionMode mode;

    CustomActionModeCallback(View view, FragmentManager fragmentManager) {
        this.view = view;
        this.context = view.getContext();
        this.mFragmentManager = fragmentManager;
        this.mSelectedImage = (ImageView) view.findViewById(R.id.image_clip_edit);
        this.mRowId = (TextView) view.findViewById(R.id.row_id);
        this.mSelectedImage.setVisibility(View.VISIBLE);
    }

    // Called when the action mode is created; startActionMode() was called
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate a menu resource providing context menu items
		this.mode = mode;
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
                CustomDialogFragment newFragment = CustomDialogFragment.newInstance("title", "this is message");
                newFragment.setDialogListener(new CustomDialogFragment.DialogListener() {
                    @Override
                    public void onPositiveClick() {
						bottomClickListener.onBottomClick(true);
                    }
                    @Override
                    public void onNegativeClick() {
						bottomClickListener.onBottomClick(false);
                    }
                });
				
                newFragment.show(mFragmentManager, "CommonDialogFragment");
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
        this.mSelectedImage.setVisibility(View.GONE);
		
    }

    private void contentsDelete() {
        Log.d(TAG, "contentsDelete Start");
		
		SQLiteDatabase sqLiteDatabase = new DBOpenHelper(context).getWritableDatabase();
		try {
			new DBHelper(sqLiteDatabase).deletetContents(mRowId.getText().toString());

			// 変数からコンテンツを削除
			//mViewPager = (ViewPager) view.findViewById(R.id.pager);
			//int position = mViewPager.getCurrentItem();
			//MainActivity.CONTENTS.remove(MainActivity.CONTENTS.get(MainActivity.TITLE_NAME.get(position)).get(mRowId));

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());

		} finally {
			sqLiteDatabase.close();
		}

        Log.d(TAG, "contentsDelete End");

    }
	
	/**
     * イメージクリックのインターフェース
     */
    interface OnBottomClickListener {
        void onBottomClick(boolean bool);
    }
	
	/**
     * イメージのクリックイベントのリスナーセット
     * @param listener OnImageItemClickListener
     */
    void setOnBottomClickListener(OnBottomClickListener listener) {
        this.bottomClickListener = listener;
    }
	
	ActionMode getMode() {
		return this.mode;
	}
}
