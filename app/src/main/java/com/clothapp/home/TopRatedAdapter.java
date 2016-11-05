package com.clothapp.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
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
import com.clothapp.http.Get;
import com.clothapp.image_detail.ImageActivity;
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

public class TopRatedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Parcelable {

    // This list is used by the ImageActivity to display the photos.
    public List<Image> itemList;
    private TopRatedAdapter oggetto;
    private final static String username = ParseUser.getCurrentUser().getUsername();

    public int lastPosition = -1;

    public TopRatedAdapter(List<Image> itemList) {
        this.itemList = itemList;
        this.oggetto = this;
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
        holder.setHashtags(image);
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
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e==null)    {
                    if (object.getParseFile("thumbnail") != null && object.getParseFile("thumbnail").getUrl() != null)  {
                        Glide.clear(holder.imgProfilePhoto);
                        Glide.with(HomeActivity.context)
                                .load(object.getParseFile("thumbnail").getUrl())
                                .placeholder(R.drawable.com_facebook_profile_picture_blank_circle)
                                .centerCrop()
                                .transform(new CircleTransform(HomeActivity.context))
                                .into(holder.imgProfilePhoto);
                    }else{
                        //chiamata get per salvare il thumbnail
                        String url = "http://clothapp.westeurope.cloudapp.azure.com/createprofilethumbnail/"+object.getObjectId();
                        Get g = new Get();
                        g.execute(url);
                    }
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
        public void setHashtags(Image img) {

            txtHashtags.setText(img.getHashtagToString());
        }

        // Set the photo for the Photo ImageView of the current view with the given File
        public void setPhoto(File file) {
            if (file!=null) {
                Glide.clear(imgPhoto);
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

        // Redirect user to ImageActivity (gallery) if he/she clicks on the photo
        private void setupPhotoOnClickListener() {
            imgPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (HomeActivity.menuMultipleActions.isExpanded()) {
                        HomeActivity.menuMultipleActions.collapse();
                    } else {
                        Intent intent = new Intent(HomeActivity.context, ImageActivity.class);
                        intent.putExtra("classe", "TopRatedPhotos");
                        intent.putExtra("position", TopRatedItemViewHolder.this.getAdapterPosition());
                        intent.putExtra("photo", oggetto);
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
                        Image image = itemList.get(TopRatedItemViewHolder.this.getAdapterPosition());
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
                        Image image = itemList.get(TopRatedItemViewHolder.this.getAdapterPosition());
                        ProfileUtils.goToProfile(HomeActivity.activity.getApplicationContext(), image.getUser());
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
                        Image image = itemList.get(TopRatedItemViewHolder.this.getAdapterPosition());
                        ProfileUtils.goToProfile(HomeActivity.activity.getApplicationContext(), image.getUser());
                    }
                }
            });
        }

    }

    //implementato Parcelable per poter passare l'oggetto
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(itemList);
    }

    protected TopRatedAdapter(Parcel in) {
        this.itemList = in.createTypedArrayList(Image.CREATOR);
    }

    public static final Parcelable.Creator<TopRatedAdapter> CREATOR = new Parcelable.Creator<TopRatedAdapter>() {
        @Override
        public TopRatedAdapter createFromParcel(Parcel source) {
            return new TopRatedAdapter(source);
        }

        @Override
        public TopRatedAdapter[] newArray(int size) {
            return new TopRatedAdapter[size];
        }
    };
}
