package com.pgyt.myapp_2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.pgyt.myapp_2.CommonConstants.BLANK_STRING;
import static com.pgyt.myapp_2.CommonConstants.CLIPBOARD_TAB_NAME;
import static com.pgyt.myapp_2.MainActivity.mContentsListMap;

/**
 * CustomAdapter
 */
class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";
    private String mTitle;
    private LayoutInflater layoutInflater;
    private OnItemClickListener itemClickListener;
    private OnImageItemClickListener imageItemClickListener;
    private OnItemLongClickListener itemLongClickListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    CustomAdapter(Context context, String title) {
        this.mTitle = title;
        this.layoutInflater = LayoutInflater.from(context);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        private CheckBox mCheckBox;
        private TextView mContentsTitle;
        private TextView mContents;
        private TextView mRowId;
        private ImageView mRowSetting;
        private LinearLayout mRow;


        private ViewHolder(View v) {
            super(v);
            mCheckBox = (CheckBox) v.findViewById(R.id.checkbox);
            mContentsTitle = (TextView) v.findViewById(R.id.text_contents_title);
            mContents = (TextView) v.findViewById(R.id.text_contents);
            mRowId = (TextView) v.findViewById(R.id.row_id);
            mRowSetting = (ImageView) v.findViewById(R.id.image_clip_setting);
            mRow = (LinearLayout) v.findViewById(R.id.row);

        }

        public TextView getmContents() {
            return mContents;
        }

        public TextView getmContentsTitle() {
            return mContentsTitle;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder Start");

        // create a new view
        View view = layoutInflater
                .inflate(R.layout.recycler_view, parent, false);

        Log.d(TAG, "onCreateViewHolder End");

        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder Start");

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mContentsTitle.setText(mContentsListMap.get(mTitle).get(position).getContents_title());
        if (mContentsListMap.get(mTitle).get(position) == null) {
            mContentsListMap.get(mTitle).get(position).setContents(BLANK_STRING);
        }
        holder.mContents.setText(mContentsListMap.get(mTitle).get(position).getContents());
        holder.mRowId.setText(String.valueOf(mContentsListMap.get(mTitle).get(position).getId()));

        // クリップボートタブとそれ以外の描画を分ける
        if (CLIPBOARD_TAB_NAME.equals(this.mTitle)) {
            // クリップボードタブの表示内容
            holder.mContentsTitle.setVisibility(View.GONE);
            holder.mContents.setVisibility(View.VISIBLE);
            holder.mRowSetting.setVisibility(View.VISIBLE);

        } else {
            // クリップボードタブ以外の表示内容
            holder.mCheckBox.setChecked(mContentsListMap.get(mTitle).get(position).getCheckedFlg());
            holder.mContentsTitle.setVisibility(View.VISIBLE);
            holder.mContents.setVisibility(View.GONE);
            holder.mRowSetting.setVisibility(View.VISIBLE);
        }
        // チェックフラグを判定
        if (mContentsListMap.get(mTitle).get(position).getCheckBoxVisibleFlg()) {
            // チェックボックスを表示する。
            holder.mCheckBox.setVisibility(View.VISIBLE);

        } else {
            // チェックボックスを非表示にする。
            holder.mCheckBox.setVisibility(View.GONE);

        }

        // 行のクリックイベント
        holder.mRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, holder.mContents, holder.getAdapterPosition());
            }
        });

        // イメージのクリックイベント
        holder.mRowSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageItemClickListener.onClick(v, holder.mRowId, holder.mContents, holder.getAdapterPosition());
            }
        });

        // recyclerViewのロングクリックイベント
        holder.mRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemLongClickListener.onLongClick(v, holder.getAdapterPosition());
                return true;
            }
        });

        // チェックボックスのチェックボックス変更イベント
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // チェックボックスのチェンジイベントはここで処理しないと、
                // スクロールしたときにチェックが全て外れてしまう。
                mContentsListMap.get(mTitle).get(holder.getAdapterPosition()).setCheckedFlg(isChecked);
            }
        });

        // 行の選択状態を設定する。
        String currentId = holder.mRowId.getText().toString();
        if (currentId.equals(MainActivityFragment.selectedContents)) {
            Log.d(TAG, currentId + "is selected");
            holder.mContents.setTypeface(Typeface.DEFAULT_BOLD);
            holder.mContentsTitle.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            Log.d(TAG, currentId + "is not selected");
            holder.mContents.setTypeface(Typeface.DEFAULT);
            holder.mContentsTitle.setTypeface(Typeface.DEFAULT);
        }

        Log.d(TAG, "onBindViewHolder End");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public int getItemCount() {
        return mContentsListMap.get(mTitle).size();
    }

    /**
     * 行クリックのインターフェース
     */
    interface OnItemClickListener {
        void onClick(View v, TextView textView, int position);
    }

    /**
     * イメージクリックのインターフェース
     */
    interface OnImageItemClickListener {
        void onClick(View v, TextView rowId, TextView contents, int position);
    }

    /**
     * ロングクリックのインターフェース
     */
    interface OnItemLongClickListener {
        boolean onLongClick(View v, int position);
    }

    /**
     * 行のクリックイベントのリスナーセット
     *
     * @param listener OnItemClickListener
     */
    void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    /**
     * イメージのクリックイベントのリスナーセット
     *
     * @param listener OnImageItemClickListener
     */
    void setOnImageItemClickListener(OnImageItemClickListener listener) {
        this.imageItemClickListener = listener;
    }

    /**
     * ロングクリックイベントのリスナーセット
     *
     * @param listener OnItemLongClickListener
     */
    void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }
}
