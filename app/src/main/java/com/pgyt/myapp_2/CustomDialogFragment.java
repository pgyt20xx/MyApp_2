package com.pgyt.myapp_2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.EventListener;

import static com.pgyt.myapp_2.CommonConstants.ARG_EDIT_TITLE_1;
import static com.pgyt.myapp_2.CommonConstants.ARG_EDIT_TITLE_2;
import static com.pgyt.myapp_2.CommonConstants.ARG_MESSAGE;
import static com.pgyt.myapp_2.CommonConstants.ARG_PATTERN;
import static com.pgyt.myapp_2.CommonConstants.ARG_TITLE;
import static com.pgyt.myapp_2.CommonConstants.PETTERN_DIALOG_CONFIRM;
import static com.pgyt.myapp_2.CommonConstants.PETTERN_DIALOG_EDIT_1;
import static com.pgyt.myapp_2.CommonConstants.PETTERN_DIALOG_EDIT_2;


public class CustomDialogFragment extends DialogFragment {
    private static CustomDialogFragment frag = new CustomDialogFragment();

    private ConfirmDialogListener confirmDialogListener = null;
    private EditDialogListener1 editDialogListener1 = null;
    private EditDialogListener2 editDialogListener2 = null;

    /**
     * ダイアログフラグメント作成
     *
     * @param title      String
     * @param message    String
     * @param editTitle1 String
     * @param editTitle2 String
     * @param pattern    String
     * @return フラグメント
     */
    public static CustomDialogFragment newInstance(String title, String message, String editTitle1, String editTitle2, String pattern) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TITLE, title);
        bundle.putString(ARG_MESSAGE, message);
        bundle.putString(ARG_EDIT_TITLE_1, editTitle1);
        bundle.putString(ARG_EDIT_TITLE_2, editTitle2);
        bundle.putString(ARG_PATTERN, pattern);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String title = getArguments().getString(ARG_TITLE);
        String message = getArguments().getString(ARG_MESSAGE);
        String editTitle1 = getArguments().getString(ARG_EDIT_TITLE_1);
        String editTitle2 = getArguments().getString(ARG_EDIT_TITLE_2);
        String pattern = getArguments().getString(ARG_PATTERN);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (pattern == null) {
            return builder.create();
        }

        final LinearLayout layout = new LinearLayout(getContext());
        final EditText editView1 = new EditText(getContext());
        final EditText editView2 = new EditText(getContext());
        final TextView title1 = new TextView(getContext());
        final TextView title2 = new TextView(getContext());

        layout.setOrientation(LinearLayout.VERTICAL);
        title1.setText(editTitle1);
        title2.setText(editTitle2);

        switch (pattern) {
            case PETTERN_DIALOG_CONFIRM:
                // ダイアログ作成
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle(title);
                builder.setMessage(message);

                // クリックイベント設定
                setEvent(builder);
                break;

            case PETTERN_DIALOG_EDIT_1:
                // ダイアログ作成
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle(title);
                builder.setMessage(message);
                layout.addView(title1);
                layout.addView(editView1);
                builder.setView(layout);

                // クリックイベント設定
                setEvent(builder, editView1);
                break;

            case PETTERN_DIALOG_EDIT_2:
                // ダイアログ作成
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle(title);
                builder.setMessage(message);
                layout.addView(title1);
                layout.addView(editView1);
                layout.addView(title2);
                layout.addView(editView2);
                builder.setView(layout);

                // クリックイベント設定
                setEvent(builder, editView1, editView2);

                break;

            default:
                break;

        }

        return builder.create();
    }

    /**
     * 確認ダイアログのイベントを設定する。
     *
     * @param builder Builder
     */
    private void setEvent(AlertDialog.Builder builder) {
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        confirmDialogListener.onPositiveClick();
                        dismiss();
                    }
                }
        )
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                confirmDialogListener.onNegativeClick();
                                dismiss();
                            }
                        }
                );
    }

    /**
     * 登録・編集用のダイアログイベントを設定する。
     *
     * @param builder Builder
     * @param text    EditText
     */
    private void setEvent(AlertDialog.Builder builder, final EditText text) {
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        editDialogListener1.onPositiveClick(text);
                        dismiss();
                    }
                }
        )
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                editDialogListener1.onNegativeClick(text);
                                dismiss();
                            }
                        }
                );
    }

    private void setEvent(AlertDialog.Builder builder, final EditText text1, final EditText text2) {
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        editDialogListener2.onPositiveClick(text1, text2);
                        dismiss();
                    }
                }
        )
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                editDialogListener2.onNegativeClick(text1, text2);
                                dismiss();
                            }
                        }
                );
    }


    /**
     * リスナーを追加する(確認ダイアログ)
     *
     * @param listener
     */
    public void setConfirmDialogListener(ConfirmDialogListener listener) {
        this.confirmDialogListener = listener;
    }

    /**
     * リスナーを追加する(登録・編集ダイアログ)
     *
     * @param listener
     */
    public void setEditDialogListener1(EditDialogListener1 listener) {
        this.editDialogListener1 = listener;
    }

    /**
     * リスナーを追加する(登録・編集ダイアログ)
     *
     * @param listener
     */
    public void setEditDialogListener2(EditDialogListener2 listener) {
        this.editDialogListener2 = listener;
    }

    /**
     * リスナーを削除する
     */
    public void removeDialogListener() {

    }

    /**
     * 確認ダイアログリスナー
     */
    public interface ConfirmDialogListener extends EventListener {

        /**
         * okボタンが押されたイベントを通知する
         */
        void onPositiveClick();

        /**
         * cancelボタンが押されたイベントを通知する
         */
        void onNegativeClick();
    }

    /**
     * 登録・編集ダイアログリスナー
     */
    public interface EditDialogListener1 extends EventListener {

        /**
         * okボタンが押されたイベントを通知する
         */
        void onPositiveClick(EditText text);

        /**
         * cancelボタンが押されたイベントを通知する
         */
        void onNegativeClick(EditText text);
    }

    /**
     * 登録・編集ダイアログリスナー
     */
    public interface EditDialogListener2 extends EventListener {

        /**
         * okボタンが押されたイベントを通知する
         */
        void onPositiveClick(EditText text1, EditText text2);

        /**
         * cancelボタンが押されたイベントを通知する
         */
        void onNegativeClick(EditText text1, EditText text2);
    }

}
