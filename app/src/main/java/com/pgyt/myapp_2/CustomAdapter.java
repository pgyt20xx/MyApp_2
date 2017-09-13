package com.pgyt.myapp_2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pgyt.myapp_2.model.ContentsBean;

import java.util.ArrayList;

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private static final String TAG = "CustomAdapter";
    private static final String BLANK_STRING = "";
    private String mTitle;
    private ArrayList<ContentsBean> mItemList;
    private LayoutInflater layoutInflater;
    private OnItemClickListener itemClickListener;
    private OnImageItemClickListener imageItemClickListener;
    private OnItemLongClickListener itemLongClickListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    CustomAdapter(Context context, String title, ArrayList<ContentsBean> itemList) {
        this.mTitle = title;
        this.mItemList = itemList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        private TextView mContentsTitle;
        private TextView mContents;
        private TextView mRowId;
        private ImageView mRowSetting;
        private LinearLayout mRow;


        private ViewHolder(View v) {
            super(v);
            mContentsTitle = (TextView) v.findViewById(R.id.text_contents_title);
            mContents = (TextView) v.findViewById(R.id.text_contents);
            mRowId = (TextView) v.findViewById(R.id.row_id);
            mRowSetting = (ImageView) v.findViewById(R.id.image_clip_setting);
            mRow = (LinearLayout) v.findViewById(R.id.row);
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
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder Start");

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mContentsTitle.setText(mItemList.get(position).getContents_title());
        if (mItemList.get(position) == null) {
            mItemList.get(position).setContents(BLANK_STRING);
        }
        holder.mContents.setText(mItemList.get(position).getContents());
        holder.mRowId.setText(String.valueOf(mItemList.get(position).getId()));

        // クリップボートタブとそれ以外の描画を分ける
        if (MainActivity.CLIPBOARD_TAB_NAME.equals(this.mTitle)) {
            holder.mContentsTitle.setVisibility(View.GONE);
            holder.mContents.setVisibility(View.VISIBLE);
            holder.mRowSetting.setVisibility(View.GONE);

        } else {
            holder.mContentsTitle.setVisibility(View.VISIBLE);
            holder.mContents.setVisibility(View.GONE);
            holder.mRowSetting.setVisibility(View.VISIBLE);
        }

        // 行のクリックイベント
        holder.mRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, holder.mContents, position);
            }
        });

        // イメージのクリックイベント
        holder.mRowSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageItemClickListener.onClick(v, holder.mRowId, holder.mContents, position);
            }
        });

        // reciclerViewのロングクリックイベント
        holder.mRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemLongClickListener.onLongClick(v, position);
                return true;
            }
        });

        Log.d(TAG, "onBindViewHolder End");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mItemList.size();
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
