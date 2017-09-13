package com.pgyt.myapp_2;


import android.support.v4.app.FragmentManager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class CustomActionModeCallback implements ActionMode.Callback {

    private final String TAG = "ActionModeCallback";
    private FragmentManager mFragmentManager;

    private OnButtonClickListener buttonClickListener;

    CustomActionModeCallback(View view, FragmentManager fragmentManager) {
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

            case R.id.menu_delete:
                // 削除ボタン押下
                CustomDialogFragment newFragment = CustomDialogFragment.newInstance("Delete", "realy?", null, null, "0");
                newFragment.setConfirmDialogListener(new CustomDialogFragment.ConfirmDialogListener() {
                    @Override
                    public void onPositiveClick() {
                        buttonClickListener.onButtonClick(true, mode);
                    }

                    @Override
                    public void onNegativeClick() {
                        buttonClickListener.onButtonClick(false, mode);
                    }
                });

                newFragment.show(mFragmentManager, "CommonDialogFragment");
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
     * ダイアログボタン押下のインターフェース
     */
    interface OnButtonClickListener {
        void onButtonClick(boolean bool, ActionMode mode);
    }

    /**
     * ダイアログボタン押下イベントのリスナーセット
     *
     * @param listener OnImageItemClickListener
     */
    void setOnButtonClickListener(OnButtonClickListener listener) {
        this.buttonClickListener = listener;
    }
}
