package com.pgyt.myapp_2;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;
import android.widget.*;

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<String[]> mDataset;
	
	private List<String> mRowIdset;

	private String title;
	
    private Context context;

    private LayoutInflater layoutInflater;
	
	private View.OnClickListener clickListener;
	
	private View.OnLongClickListener longClickListener;

    private static final String TAG = "CustomAdapter";
	
	// Provide a suitable constructor (depends on the kind of dataset)
    CustomAdapter(Context context, String title, LinkedHashMap<String, String[]> item) {
        // TODO; これは。。。
        this.context = context;
		this.title = title;
		this.mRowIdset = new ArrayList<>(item.keySet());
		this.mDataset = new ArrayList<>(item.values());
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
		private CheckBox mCheckBox;
		private ImageView mRowSetting;
		private View mRow;


        private ViewHolder(View v) {
            super(v);
            mContentsTitle = (TextView) v.findViewById(R.id.text_contents_title);
            mContents = (TextView) v.findViewById(R.id.text_contents);
			mRowId = (TextView) v.findViewById(R.id.row_id);
			mCheckBox = (CheckBox) v.findViewById(R.id.checkbox);
			mRowSetting = (ImageView) v.findViewById(R.id.image_clip_setting);
			mRow = (LinearLayout) v.findViewById(R.id.row);
        }
		
        /**
         * クリックされたコンテンツを返す。
         * @return
         */
        private TextView getContentsTitle() {
            return mContentsTitle;
        }

        /**
         * クリックされたコンテンツを返す。
         * @return
         */
        private TextView getContents() {
            return mContents;
        }

        /**
         * クリックされたコンテンツのIDを返す。
         * @return
         */
		private TextView getRowId() {
            return mRowId;
        }
		
		
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = layoutInflater
                .inflate(R.layout.text_view, parent, false);

        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mContentsTitle.setText(mDataset.get(position)[0]);
        holder.mContents.setText(mDataset.get(position)[1]);
		holder.mRowId.setText(mRowIdset.get(position));
		
		if (MainActivity.CLIPBOARD_TAB_NAME.equals(this.title)) {
			holder.mContentsTitle.setVisibility(View.GONE);
			holder.mContents.setVisibility(View.VISIBLE);
			holder.mRowSetting.setVisibility(View.GONE);
			
		} else {
			holder.mContentsTitle.setVisibility(View.VISIBLE);
			holder.mContents.setVisibility(View.GONE);
			holder.mRowSetting.setVisibility(View.VISIBLE);	
		}
		
		holder.mRow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					clickListener.onClick(v);
					return;
				}
			});
	
		holder.mRowSetting.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					clickListener.onClick(v);
					return;
				}
			});
			
		// reciclerViewのロングクリックイベント
		holder.mRow.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View v) {
                    longClickListener.onLongClick(v);
                    return true;
                }
            });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
	
	public void setOnItemClickListener(View.OnClickListener listener) {
		this.clickListener = listener;
	}
	
	public void setOnItemLongClickListener(View.OnLongClickListener listener) {
		this.longClickListener = listener;
	}

}
