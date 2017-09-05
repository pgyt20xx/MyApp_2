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
    private Context context;
    private FragmentManager mFragmentManager;
	
	private OnBottomClickListener bottomClickListener;

    CustomActionModeCallback(View view, FragmentManager fragmentManager) {
        this.context = view.getContext();
        this.mFragmentManager = fragmentManager;
        
    }

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
    public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_edit:

                mode.finish();

                return true;
				
            case R.id.menu_delete:
                CustomDialogFragment newFragment = CustomDialogFragment.newInstance("title", "this is message");
                newFragment.setDialogListener(new CustomDialogFragment.DialogListener() {
                    @Override
                    public void onPositiveClick() {
						bottomClickListener.onBottomClick(true, mode);
                    }
                    @Override
                    public void onNegativeClick() {
						bottomClickListener.onBottomClick(false, mode);
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
		
    }
	
	/**
     * イメージクリックのインターフェース
     */
    interface OnBottomClickListener {
        void onBottomClick(boolean bool, ActionMode mode);
    }
	
	/**
     * イメージのクリックイベントのリスナーセット
     * @param listener OnImageItemClickListener
     */
    void setOnBottomClickListener(OnBottomClickListener listener) {
        this.bottomClickListener = listener;
    }
}
