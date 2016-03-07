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

    // This list is used by the ImageFragment to display the photos.
    public static List<Image> itemList;

    private final static String username = ParseUser.getCurrentUser().getUsername();

    public MostRecentAdapter(List<Image> itemList) {
        MostRecentAdapter.itemList = itemList;
    }

    // This is called when a ViewHolder has been created.
    // Note that a ViewHolder can be recycled to hold different items. This means
    // that there may be more photos than ViewHolders.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(HomeActivity.activity.getApplicationContext()).inflate(R.layout.fragment_home_most_recent_item, parent, false);
        return new MostRecentItemViewHolder(view);
    }

    // This is called when a ViewHolder has been "associated" with a view.
    // It is used to set the data of the view according to an element of the itemList.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        MostRecentItemViewHolder holder = (MostRecentItemViewHolder) viewHolder;

        Image image = itemList.get(position);

        holder.setItemImage(image.getFile());

        List likeUsers = image.getLike();

        if (likeUsers != null && likeUsers.contains(username)) {
            holder.setItemHeartImage(true);
        } else {
            holder.setItemHeartImage(false);
        }

    }

    // Returns the number of items in the RecyclerView
    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    // This class is used to "hold a view".
    // An object of this class contains references to the relevant subviews that
    // may be needed later. For example setting the username after a Parse query.
    class MostRecentItemViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgPhoto;
        private ImageView imgHeart;

        public MostRecentItemViewHolder(final View parent) {
            super(parent);

            imgPhoto = (ImageView) parent.findViewById(R.id.fragment_home_most_recent_item_image);
            imgHeart = (ImageView) parent.findViewById(R.id.fragment_home_most_recent_item_heart);

//            Log.d("MostRecentAdapter", "Count: " + count);

            // Setting some OnClickListeners
            setPhotoOnClickListener();
            setHeartImageOnClickListener();
        }

        // Use this method to set the photo to the given File
        public void setItemImage(File file) {
//            Bitmap imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//            imgPhoto.setImageBitmap(imageBitmap);

            Glide.with(HomeActivity.context)
                    .load(file)
                    .centerCrop()
                    .into(imgPhoto);
        }

        // Use this method to set the heart image color.
        // Red = true
        // White = false
        // TODO: setImageResource needs to be replaced. A quick RAM usage inspection showed this method uses a lot of memory if called multiple times.
        public void setItemHeartImage(boolean red) {
            if (red) imgHeart.setImageResource(R.mipmap.cuore_pressed);
            else imgHeart.setImageResource(R.mipmap.cuore);
        }

        // Redirect user to ImageFragment (gallery) if he/she clicks on the photo
        private void setPhotoOnClickListener() {
            imgPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.context, ImageFragment.class);
                    intent.putExtra("classe", "MostRecentPhotos");
                    intent.putExtra("position", MostRecentItemViewHolder.this.getAdapterPosition());
                    HomeActivity.activity.startActivity(intent);
                }
            });
        }

        // Add/Remove a like to the current photo both locally and on Parse database.
        private void setHeartImageOnClickListener() {
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

                    // Update like list on Parse database.
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

    }

}