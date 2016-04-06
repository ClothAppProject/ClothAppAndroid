package com.clothapp.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.ImageFragment;
import com.clothapp.R;
import com.clothapp.profile.utils.ProfileUtils;
import com.clothapp.resources.CircleTransform;
import com.clothapp.resources.Image;
import com.clothapp.parse.notifications.LikeRes;
import com.parse.GetCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.List;

public class TopRatedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // This list is used by the ImageFragment to display the photos.
    public static List<Image> itemList;

    private final static String username = ParseUser.getCurrentUser().getUsername();

    public int lastPosition = -1;

    public TopRatedAdapter(List<Image> itemList) {
        TopRatedAdapter.itemList = itemList;
    }

    // This is called when a ViewHolder has been created.
    // Note that a ViewHolder can be recycled to hold different items. This means
    // that there may be more photos than ViewHolders.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_home_top_rated_item, parent, false);
        return new TopRatedItemViewHolder(view);
    }

    // This is called when a ViewHolder has been "associated" with a view.
    // It is used to set the data of the view according to an element of the itemList.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        final TopRatedItemViewHolder holder = (TopRatedItemViewHolder) viewHolder;

        Image image = itemList.get(position);

        holder.setUsername(image.getUser());
        holder.setItemName(image.getTypeVestiti());
        holder.setHashtags(image.getHashtag());
        holder.setLikeCount(image.getNumLike());
        holder.setPhoto(image.getFile());

        List likeUsers = image.getLike();

        if (likeUsers.contains(username)) {
            holder.setHeartImage(true);
        } else {
            holder.setHeartImage(false);
        }

        setupUserProfilePhoto(holder, image.getUser());

        setAnimation(holder.getAnimationView(), position);
    }

    // Returns the number of items in the RecyclerView
    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    @Override
    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder) {
        ((TopRatedItemViewHolder) holder).clearAnimation();
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition && position > 2) {
            Animation animation = AnimationUtils.loadAnimation(HomeActivity.context, R.anim.slide_up);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    // Download and display the user profile photo. If the user has no profile photo, display
    // a placeholder.
    private void setupUserProfilePhoto(final TopRatedItemViewHolder holder, String username) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserPhoto");
        query.whereEqualTo("username", username);

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject photo, ParseException e) {
                if (e == null) {
                    ParseFile thumbnail = photo.getParseFile("thumbnail");
                    thumbnail.getFileInBackground(new GetFileCallback() {
                        @Override
                        public void done(File file, ParseException e) {
                            if (e == null) {
                                holder.setProfilePhoto(file);
                            } else {
                                Log.d("TopRatedAdapter", "Error while downloading thumbnail: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    // Log.d("TopRatedAdapter", "Error: " + e.getMessage());
                    holder.setProfilePhoto(null);
                }
            }
        });
    }

    // This class is used to "hold a view".
    // An object of this class contains references to the relevant subviews that
    // may be needed later. For example setting the username after a Parse query.
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

        private CardView cardView;

        private LinearLayout linearLayoutProfile;

        public TopRatedItemViewHolder(final View parent) {
            super(parent);

            // Initialize some TextViews
            txtUsername = (TextView) parent.findViewById(R.id.fragment_home_top_rated_item_username);
            txtItemName = (TextView) parent.findViewById(R.id.fragment_home_top_rated_item_item_name);
            txtHashtags = (TextView) parent.findViewById(R.id.fragment_home_top_rated_item_hashtags);
            txtLikeCount = (TextView) parent.findViewById(R.id.fragment_home_top_rated_item_like_count);

            // Initialize some ImageViews
            imgPhoto = (ImageView) parent.findViewById(R.id.fragment_home_top_rated_item_image);
            imgHeart = (ImageView) parent.findViewById(R.id.fragment_home_top_rated_item_like);
            imgProfilePhoto = (ImageView) parent.findViewById(R.id.fragment_home_top_rated_item_profile_image);
            imgProfileIcon = (ImageView) parent.findViewById(R.id.fragment_home_top_rated_item_profile);

            // Initialize some LinearLayouts
            linearLayoutProfile = (LinearLayout) parent.findViewById(R.id.fragment_home_top_rated_item_profile_linear_layout);

            cardView = (CardView) parent.findViewById(R.id.top_rated_item);

            // Setup some OnClickListeners
            setupPhotoOnClickListener();
            setupHeartImageOnClickListener();
            setupProfileIconOnClickListener();
            setupProfileLinearLayoutOnClickListener();
        }

        public CardView getAnimationView() {
            return cardView;
        }

        public void clearAnimation() {
            cardView.clearAnimation();
        }

        // Set the username for the current view. Example: Simone
        public void setUsername(String username) {
            txtUsername.setText(username);
        }

        // Set the item name(s) for the current view. Example: Suit & Tie
        public void setItemName(List<String> clothesList) {

            if (clothesList == null) return;

            StringBuilder sb = new StringBuilder();

            for (String clothing : clothesList) {
                sb.append(clothing.substring(0, 1).toUpperCase())
                        .append(clothing.substring(1))
                        .append(" & ");
            }

            String result = sb.toString();
            if (result.length() > 0) result = result.substring(0, result.length() - 2);

            txtItemName.setText(result);
        }

        // Set the hashtags for the current view. Example: #Hashtag1, #Hashtag2
        public void setHashtags(List<String> hashtagList) {

            if (hashtagList == null) return;

            StringBuilder sb = new StringBuilder();

            for (String hashtag : hashtagList) {
                if (!hashtag.isEmpty()) {
                    sb.append(hashtag.substring(0, 1))
                            .append(hashtag.substring(1, 2).toUpperCase())
                            .append(hashtag.substring(2))
                            .append(" ");
                }
            }

            txtHashtags.setText(sb.toString());
        }

        // Set the photo for the Photo ImageView of the current view with the given File
        public void setPhoto(File file) {
            if (file!=null) {
                Glide.with(HomeActivity.context)
                        .load(file)
                        .centerCrop()
                        .into(imgPhoto);
            }else{
                Glide.with(HomeActivity.context)
                        .load(R.drawable.loading)
                        .asGif()
                        .centerCrop()
                        .into(imgPhoto);
            }
        }

        // Set the profile photo for the ProfilePhoto ImageView of the current view with the given File
        public void setProfilePhoto(File file) {

            if (file != null) {
                Glide.with(HomeActivity.context)
                        .load(file)
                        .placeholder(R.drawable.com_facebook_profile_picture_blank_circle)
                        .centerCrop()
                        .transform(new CircleTransform(HomeActivity.context))
                        .into(imgProfilePhoto);
            } else {
                Glide.with(HomeActivity.context)
                        .load(R.drawable.com_facebook_profile_picture_blank_circle)
                        .centerCrop()
                        .transform(new CircleTransform(HomeActivity.context))
                        .into(imgProfilePhoto);
            }
        }

        // Use this method to set the heart image color.
        // Red = true
        // White = false
        public void setHeartImage(boolean red) {
            if (red) imgHeart.setColorFilter(Color.rgb(210, 36, 36));
            else imgHeart.setColorFilter(Color.rgb(205, 205, 205));
        }

        // Set the like count for the current photo
        public void setLikeCount(int value) {
            txtLikeCount.setText(value + "");
        }

        // Redirect user to ImageFragment (gallery) if he/she clicks on the photo
        private void setupPhotoOnClickListener() {
            imgPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (HomeActivity.menuMultipleActions.isExpanded()) {
                        HomeActivity.menuMultipleActions.collapse();
                    } else {
                        Intent intent = new Intent(HomeActivity.context, ImageFragment.class);
                        intent.putExtra("classe", "TopRatedPhotos");
                        intent.putExtra("position", TopRatedItemViewHolder.this.getAdapterPosition());
                        HomeActivity.activity.startActivity(intent);
                    }
                }
            });
        }

        // Add/Remove a like to the current photo both locally and on Parse database.
        private void setupHeartImageOnClickListener() {
            imgHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (HomeActivity.menuMultipleActions.isExpanded()) {
                        HomeActivity.menuMultipleActions.collapse();
                    } else {
                        Image image = TopRatedAdapter.itemList.get(TopRatedItemViewHolder.this.getAdapterPosition());
                        final String imageUsername = image.getUser();

                        final boolean add = image.getLike().contains(username);
                        if (add) {
                            // Log.d("MostRecentAdapter", "Removing...");
                            LikeRes.deleteLike(image.getObjectId(), image, username);

                        } else {
                            // Log.d("MostRecentAdapter", "Adding...");
                            LikeRes.addLike(image.getObjectId(), image, username);
                        }

                        notifyDataSetChanged();

                    }
                }
            });
        }

        // Redirect user to the profile page of the user/shop of the current photo.
        private void setupProfileIconOnClickListener() {
            imgProfileIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (HomeActivity.menuMultipleActions.isExpanded()) {
                        HomeActivity.menuMultipleActions.collapse();
                    } else {
                        Image image = TopRatedAdapter.itemList.get(TopRatedItemViewHolder.this.getAdapterPosition());
                        Intent intent = ProfileUtils.goToProfile(HomeActivity.activity.getApplicationContext(), image.getUser());
                        intent.putExtra("user", image.getUser());
                        HomeActivity.activity.startActivity(intent);
                    }
                }
            });
        }

        // Redirect user to the profile page of the user/shop of the current photo.
        private void setupProfileLinearLayoutOnClickListener() {
            linearLayoutProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (HomeActivity.menuMultipleActions.isExpanded()) {
                        HomeActivity.menuMultipleActions.collapse();
                    } else {
                        Image image = TopRatedAdapter.itemList.get(TopRatedItemViewHolder.this.getAdapterPosition());
                        Intent intent = ProfileUtils.goToProfile(HomeActivity.activity.getApplicationContext(), image.getUser());
                        intent.putExtra("user", image.getUser());
                        HomeActivity.activity.startActivity(intent);
                    }
                }
            });
        }

    }
}
