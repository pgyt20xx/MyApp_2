package com.pgyt.myapp_2;


import android.content.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;

public class CustomActionModeCallback implements ActionMode.Callback {

    private final String TAG = "ActionModeCallback";
    private Context context;
    private FragmentManager mFragmentManager;
    private View view;

    private OnButtonClickListener buttonClickListener;
    private OnEditClickListener editClickListener;

    CustomActionModeCallback(View view, FragmentManager fragmentManager) {
        this.view = view;
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
                // 情報取得
                TextView contentsId = (TextView) view.findViewById(R.id.row_id);
                TextView contentsTitle = (TextView) view.findViewById(R.id.text_contents_title);
                TextView contents = (TextView) view.findViewById(R.id.text_contents);
                Intent intent = new Intent(context, EditContentsActivity.class);

                // 情報受け渡し
                intent.putExtra("contentsId", contentsId.getText().toString());
                intent.putExtra("contentsTitle", contentsTitle.getText().toString().toString());
                intent.putExtra("contents", contents.getText().toString());

                // 編集画面起動

//				context.startActivity(intent);

                editClickListener.editClick(intent);

                mode.finish();

                return true;

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

            case R.id.menu_share:
                // 共有ボタン押下
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
    interface OnButtonClickListener {
        void onButtonClick(boolean bool, ActionMode mode);
    }

    /**
     * 編集クリック
     */
    interface OnEditClickListener {
        void editClick(Intent intent);
    }

    /**
     * イメージのクリックイベントのリスナーセット
     *
     * @param listener OnImageItemClickListener
     */
    void setOnButtonClickListener(OnButtonClickListener listener) {
        this.buttonClickListener = listener;
    }

    /**
     * 編集クリックイベントのリスナーセット
     * @param listener OnEditClickListener
     */
    void setOnEditClickListener(OnEditClickListener listener) {
        this.editClickListener = listener;
    }
}
