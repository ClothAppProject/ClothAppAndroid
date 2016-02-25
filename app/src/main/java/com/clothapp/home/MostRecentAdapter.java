package com.clothapp.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clothapp.R;

import java.util.List;

class MostRecentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> mItemList;

    public MostRecentAdapter(List<String> itemList) {
        mItemList = itemList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_home_most_recent_item, parent, false);
        return RecyclerItemViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;
        String itemText = mItemList.get(position);
        holder.setItemText(itemText);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    static class RecyclerItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView mItemTextView;

        public RecyclerItemViewHolder(final View parent, TextView itemTextView) {
            super(parent);
            mItemTextView = itemTextView;
        }

        public static RecyclerItemViewHolder newInstance(View parent) {
            TextView itemTextView = (TextView) parent.findViewById(R.id.fragment_home_most_recent_item_title);
            return new RecyclerItemViewHolder(parent, itemTextView);
        }

        public void setItemText(CharSequence text) {
            if (mItemTextView != null) {
                mItemTextView.setText(text);
            }
        }

    }

}