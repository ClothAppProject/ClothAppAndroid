package com.clothapp.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.ImageFragment;
import com.clothapp.R;
import com.clothapp.resources.Image;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class TopRatedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static List<Image> itemList;

    private final static String username = ParseUser.getCurrentUser().getUsername();

    public TopRatedAdapter(List<Image> itemList) {
        TopRatedAdapter.itemList = itemList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_home_top_rated_item, parent, false);
        return new TopRatedItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        TopRatedItemViewHolder holder = (TopRatedItemViewHolder) viewHolder;

        Image image = itemList.get(position);

        holder.setUsername(image.getUser());
        holder.setItemName(image.getVestitiToString());
        holder.setHashtags(image.getHashtagToString());
        holder.setLikeCount(image.getNumLike());
        holder.setPhoto(image.getFile());

        List likeUsers = image.getLike();

        if (likeUsers != null && likeUsers.contains(username)) {
            holder.setHeartImage(true);
        } else {
            holder.setHeartImage(false);
        }

    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    class TopRatedItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtUsername;
        private final TextView txtItemName;
        private final TextView txtHashtags;
        private final TextView txtLikeCount;

        private final ImageView imgPhoto;
        private ImageView imgProfilePhoto;
        private ImageView imgHeart;
        private ImageView imgShare;
        private ImageView imgProfileIcon;

        public TopRatedItemViewHolder(final View parent) {
            super(parent);

            txtUsername = (TextView) parent.findViewById(R.id.fragment_home_top_rated_item_username);
            txtItemName = (TextView) parent.findViewById(R.id.fragment_home_top_rated_item_item_name);
            txtHashtags = (TextView) parent.findViewById(R.id.fragment_home_top_rated_item_hashtags);
            txtLikeCount = (TextView) parent.findViewById(R.id.fragment_home_top_rated_item_like_count);

            imgPhoto = (ImageView) parent.findViewById(R.id.fragment_home_top_rated_item_image);
            imgHeart = (ImageView) parent.findViewById(R.id.fragment_home_top_rated_item_like);
            imgProfilePhoto = (ImageView) parent.findViewById(R.id.fragment_home_top_rated_item_profile_image);
            imgProfileIcon = (ImageView) parent.findViewById(R.id.fragment_home_top_rated_item_profile);

        }

        public void setUsername(String username) {
            txtUsername.setText(username);
        }

        public void setItemName(String itemName) {
            txtItemName.setText(itemName);
        }

        public void setHashtags(String hashtags) {
            txtHashtags.setText(hashtags);
        }

        public void setPhoto(File file) {
            Glide.with(HomeActivity.context)
                    .load(file)
                    .centerCrop()
                    .into(imgPhoto);
        }

        public void setProfilePhoto(File file) {
            Glide.with(HomeActivity.context)
                    .load(file)
                    .centerCrop()
                    .into(imgProfilePhoto);
        }

        public void setHeartImage(boolean red) {
            if (red) imgHeart.setColorFilter(Color.rgb(181, 47, 41));
            else imgHeart.setColorFilter(Color.rgb(239, 239, 239));
        }

        public void setLikeCount(int value) {
            txtLikeCount.setText(value + "");
        }
    }
}
