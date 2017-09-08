package com.pgyt.myapp_2;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.widget.*;
import java.util.*;

import android.support.v4.app.DialogFragment;


public class CustomDialogFragment extends DialogFragment {
    private ConfirmDialogListener confirmDialogListener = null;
	private EditDialogListener editDialogListener = null;
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
	private static final String ARG_EDIT_TITLE_1 = "editTextTitle1";
	private static final String ARG_EDIT_TITLE_2 = "editTextTitle2";
	private static final String ARG_PATTERN = "pattern";//0:確認、1:テキストボックス1つ、2:テキストボックス2つ

	private static final String PETTERN_DIALOG_CONFIRM = "0";
	private static final String PETTERN_DIALOG_EDIT_1 = "1";
	private static final String PETTERN_DIALOG_EDIT_2 = "2";
	
	
	

    private static CustomDialogFragment frag = new CustomDialogFragment();

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
		
		switch (pattern) {
            case PETTERN_DIALOG_CONFIRM:
				//builder = setConfimDialog(new AlertDialog.Builder(getActivity()));
            	builder.setIcon(R.mipmap.ic_launcher);
				builder.setTitle(title);
				builder.setMessage(message);
				setEvent(builder);
				break;
				
            case PETTERN_DIALOG_EDIT_1:
				//builder = setEditDialog1(new AlertDialog.Builder(getActivity()));
				builder.setIcon(R.mipmap.ic_launcher);
				builder.setTitle(title);
				builder.setMessage(message);
				builder.setTitle(editTitle1);
				final EditText editView = new EditText(getContext());
				builder.setView(editView);
				setEvent(builder, editView);
				break;
				
			case PETTERN_DIALOG_EDIT_2:
				//builder = setEditDialog2(new AlertDialog.Builder(getActivity()));
				break;
				
            default:
				break;
			
        }
		
		

        return builder.create();
    }
	
	private void setEvent(AlertDialog.Builder builder){
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
	
	private void setEvent(AlertDialog.Builder builder, final EditText text){
		builder.setPositiveButton(android.R.string.ok,
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					editDialogListener.onPositiveClick(text);
					dismiss();
				}
			}
		)
			.setNegativeButton(android.R.string.cancel,
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					editDialogListener.onNegativeClick(text);
					dismiss();
				}
			}
		);
	}
	

    /**
     * リスナーを追加する
     *
     * @param listener
     */
    public void setConfirmDialogListener(ConfirmDialogListener listener) {
        this.confirmDialogListener = listener;
    }
	
	/**
     * リスナーを追加する
     *
     * @param listener
     */
    public void setEditDialogListener(EditDialogListener listener) {
        this.editDialogListener = listener;
    }

    /**
     * リスナーを削除する
     */
    public void removeDialogListener() {

    }

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
	
	public interface EditDialogListener extends EventListener {

        /**
         * okボタンが押されたイベントを通知する
         */
        void onPositiveClick(EditText text);

        /**
         * cancelボタンが押されたイベントを通知する
         */
        void onNegativeClick(EditText text);
    }

}
