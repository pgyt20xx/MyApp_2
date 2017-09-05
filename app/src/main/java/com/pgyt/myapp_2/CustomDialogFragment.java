package com.pgyt.myapp_2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.EventListener;


public class CustomDialogFragment extends DialogFragment {
    private DialogListener listener = null;
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";

    private static CustomDialogFragment frag = new CustomDialogFragment();

    public static CustomDialogFragment newInstance(String title, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TITLE, title);
        bundle.putString(ARG_MESSAGE, message);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String title = getArguments().getString(ARG_TITLE);
        String message = getArguments().getString(ARG_MESSAGE);

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                listener.onPositiveClick();
                                dismiss();
                            }
                        }
                )
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                listener.onNegativeClick();
                                dismiss();
                            }
                        }
                )
                .create();
    }

    /**
     * リスナーを追加する
     *
     * @param listener
     */
    public void setDialogListener(DialogListener listener) {
        this.listener = listener;
    }

    /**
     * リスナーを削除する
     */
    public void removeDialogListener() {

    }

    public interface DialogListener extends EventListener {

        /**
         * okボタンが押されたイベントを通知する
         */
        void onPositiveClick();

        /**
         * cancelボタンが押されたイベントを通知する
         */
        void onNegativeClick();
    }

}
