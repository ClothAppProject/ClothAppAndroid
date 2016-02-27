package com.clothapp.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.clothapp.home_gallery.*;
import com.clothapp.profile.UserProfileActivity;
import com.clothapp.profile_shop.ShopProfileActivity;
import com.clothapp.resources.Image;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MostRecentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static List<Image> itemList;

    private static int imgCount;

    private final static String username = ParseUser.getCurrentUser().getUsername();

    public MostRecentAdapter(List<Image> itemList) {
        MostRecentAdapter.itemList = itemList;
        imgCount = 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_home_most_recent_item, parent, false);
        return new MostRecentItemViewHolder(view, imgCount++);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        MostRecentItemViewHolder holder = (MostRecentItemViewHolder) viewHolder;

        Image image = itemList.get(position);

        holder.setItemText(image.getUser());
        holder.setItemImage(image.getFile());

        List likeUsers = image.getLike();

        if (likeUsers != null && likeUsers.contains(username)) {
            holder.setItemHeartImage(true);
        } else {
            holder.setItemHeartImage(false);
        }

    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    class MostRecentItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtTitle;
        private final ImageView imgPhoto;
        private ImageView imgHeart;
        private final int position;

        public MostRecentItemViewHolder(final View parent, int count) {
            super(parent);

            txtTitle = (TextView) parent.findViewById(R.id.fragment_home_most_recent_item_title);
            imgPhoto = (ImageView) parent.findViewById(R.id.fragment_home_most_recent_item_image);
            imgHeart = (ImageView) parent.findViewById(R.id.fragment_home_most_recent_item_heart);

            this.position = count;

//            Log.d("MostRecentAdapter", "Count: " + count);

            imgPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.context, ImageFragment.class);
                    intent.putExtra("classe", "MostRecentPhotos");
                    intent.putExtra("position", position);
                    HomeActivity.activity.startActivity(intent);
                }
            });

            imgHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Log.d("MostRecentAdapter", "Clicked on photo with position: " + MostRecentItemViewHolder.this.getAdapterPosition());

                    Image image = MostRecentAdapter.itemList.get(MostRecentItemViewHolder.this.getAdapterPosition());

                    final boolean add = !image.getLike().contains(username);
                    if (add) {
                        // Log.d("MostRecentAdapter", "Adding...");
                        image.addLike(username);
                    } else {
                        // Log.d("MostRecentAdapter", "Removing...");
                        image.remLike(username);
                    }

                    notifyDataSetChanged();

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");

                    query.getInBackground(image.getObjectId(), new GetCallback<ParseObject>() {
                        public void done(ParseObject photo, ParseException e) {
                            if (e == null) {
                                if (add) {
                                    photo.addUnique("like", username);
                                    photo.put("nLike", photo.getInt("nLike") + 1);
                                    photo.saveInBackground();
                                } else {
                                    photo.removeAll("like", Collections.singletonList(username));
                                    photo.put("nLike", photo.getInt("nLike") - 1);
                                    photo.saveInBackground();
                                }
                            } else {
                                Log.d("MostRecentAdapter", "Error: " + e.getMessage());
                            }
                        }
                    });
                }
            });
        }

        public void setItemText(CharSequence text) {
            if (txtTitle != null) {
                txtTitle.setText(text);
            }
        }

        public void setItemImage(File file) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgPhoto.setImageBitmap(imageBitmap);

//            Glide.with(HomeActivity.context)
//                    .load(file)
//                    .centerCrop()
//                    .into(imgPhoto);
        }

        public void setItemHeartImage(boolean red) {
            if (red) imgHeart.setImageResource(R.mipmap.cuore_pressed);
            else imgHeart.setImageResource(R.mipmap.cuore);
        }

    }

}