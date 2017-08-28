package com.pgyt.myapp_2;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;
import java.util.*;

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<String[]> mDataset;
	
	private List<String> mRowIdset;

	private String title;

    private static final String TAG = "CustomAdapter";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        private TextView mContentsTitle;
        private TextView mContents;
		private TextView mRowId;

        private ViewHolder(View v) {
            super(v);

            // reciclerViewのクリックイベント
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "ID:" + getRowId().getText() + " TITLE:" + getContentsTitle().getText() + " ELEMENT:" + getContents().getText() + " clicked.");

                    // クリップボードにコピー
                    ClipData.Item item = new ClipData.Item(getContents().getText());
                    ClipData clipData = new ClipData(new ClipDescription("text_data", new String[]{ClipDescription.MIMETYPE_TEXT_URILIST}), item);
                    ClipboardManager clipboardManager = (ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE);
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(v.getContext(), "\"" + getContents().getText() + "\"" + " is on cliped", Toast.LENGTH_SHORT).show();

                }
            });

            // reciclerViewのロングクリックイベント
            v.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(view.getContext(), "call onLongClick", Toast.LENGTH_SHORT).show();

                    return true;
                }
            });
            mContentsTitle = (TextView) v.findViewById(R.id.text_contents_title);
            mContents = (TextView) v.findViewById(R.id.text_contents);
			mRowId = (TextView) v.findViewById(R.id.row_id);
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

    // Provide a suitable constructor (depends on the kind of dataset)
    CustomAdapter(String title, LinkedHashMap<String, String[]> item) {
		this.title = title;
		this.mRowIdset = new ArrayList<>(item.keySet());
		this.mDataset = new ArrayList<>(item.values());
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
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
            Resources res = Resources.getSystem();
			holder.mContents.setTextColor(res.getColor(R.color.colorTextDefault));
		}
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
