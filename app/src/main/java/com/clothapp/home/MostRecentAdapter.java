package com.clothapp.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clothapp.R;
import com.clothapp.resources.Image;

import java.io.File;
import java.util.List;

class MostRecentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<Image> itemList;

    public MostRecentAdapter(List<Image> itemList) {
        this.itemList = itemList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_home_most_recent_item, parent, false);
        return new MostRecentItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        MostRecentItemViewHolder holder = (MostRecentItemViewHolder) viewHolder;
        Image image = itemList.get(position);
        holder.setItemText(image.getUser());
        holder.setItemImage(image.getFile());
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    static class MostRecentItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtTitle;
        private final ImageView imgPhoto;

        public MostRecentItemViewHolder(final View parent) {
            super(parent);
            txtTitle = (TextView) parent.findViewById(R.id.fragment_home_most_recent_item_title);
            imgPhoto = (ImageView) parent.findViewById(R.id.fragment_home_most_recent_item_image);
        }

        public void setItemText(CharSequence text) {
            if (txtTitle != null) {
                txtTitle.setText(text);
            }
        }

        public void setItemImage(File file) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgPhoto.setImageBitmap(imageBitmap);
        }

    }

}