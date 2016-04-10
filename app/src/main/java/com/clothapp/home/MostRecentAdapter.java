package com.clothapp.home;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.ImageFragment;
import com.clothapp.R;
import com.clothapp.profile.UserProfileActivity;
import com.clothapp.profile_shop.ShopProfileActivity;
import com.clothapp.resources.Image;
import com.clothapp.parse.notifications.LikeRes;
import com.parse.ParseUser;

import java.io.File;
import java.util.List;

public class MostRecentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // This list is used by the ImageFragment to display the photos.
    public static List<Image> itemList;

    private final static String username = ParseUser.getCurrentUser().getUsername();

    public int lastPosition = -1;
    private String flag;

    public MostRecentAdapter(List<Image> itemList) {
        MostRecentAdapter.itemList = itemList;
    }

    // This is called when a ViewHolder has been created.
    // Note that a ViewHolder can be recycled to hold different items. This means
    // that there may be more photos than ViewHolders.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(HomeActivity.activity.getApplicationContext()).inflate(R.layout.fragment_home_most_recent_item, parent, false);
        return new MostRecentItemViewHolder(view);
    }

    // This is called when a ViewHolder has been "associated" with a view.
    // It is used to set the data of the view according to an element of the itemList.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        MostRecentItemViewHolder holder = (MostRecentItemViewHolder) viewHolder;

        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
        //layoutParams.height=holder.imgPhoto.getHeight();
       // if(position%2!=0) MostRecentFragment.totHeight[0]+=holder.itemView.getHeight();
       // else MostRecentFragment.totHeight[1]+=holder.itemView.getHeight();
       // if((position+1)%11==0) holder.itemView.setMinimumHeight(MostRecentFragment.totHeight[0]-MostRecentFragment.totHeight[1]);
        if(position%11==0) layoutParams.setFullSpan(true);
        else layoutParams.setFullSpan(false);
/*
        android.view.ViewGroup.LayoutParams layoutParamsImageView = holder.imgPhoto.getLayoutParams();
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, HomeActivity.context.getResources().getDisplayMetrics());
        int extra = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, HomeActivity.context.getResources().getDisplayMetrics());
        if(position%11!=0 && (position%6==0 || position%7==0) )layoutParamsImageView.height=(height+extra);
        else layoutParamsImageView.height=(height);

*/

        Image image = itemList.get(position);

        holder.setItemImage(image.getFile());

        flag=image.getFlag();
        holder.setShop(flag);

        List likeUsers = image.getLike();

        holder.setUser(image.getUser());

        if (likeUsers != null && likeUsers.contains(username)) {
            holder.setItemHeartImage(true);
        } else {
            holder.setItemHeartImage(false);
        }

        setAnimation(holder.getAnimationView(), position);
    }

    // Returns the number of items in the RecyclerView
    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    @Override
    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder) {
        ((MostRecentItemViewHolder) holder).clearAnimation();
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition && position > 5) {
            Animation animation = AnimationUtils.loadAnimation(HomeActivity.context, R.anim.slide_up);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    // This class is used to "hold a view".
    // An object of this class contains references to the relevant subviews that
    // may be needed later. For example setting the username after a Parse query.
    class MostRecentItemViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private final ImageView imgPhoto;
        private ImageView imgHeart;
        private ImageView imgShop;
        private TextView user;

        public MostRecentItemViewHolder(final View parent) {
            super(parent);

            cardView = (CardView) parent.findViewById(R.id.most_recent_item);
            imgPhoto = (ImageView) parent.findViewById(R.id.fragment_home_most_recent_item_image);
            imgHeart = (ImageView) parent.findViewById(R.id.fragment_home_most_recent_item_heart);
            imgShop  = (ImageView) parent.findViewById(R.id.fragment_home_most_recent_item_shop);
            user=(TextView) parent.findViewById(R.id.fragment_home_most_recent_item_user);

//            Log.d("MostRecentAdapter", "Count: " + count);

            // Setting some OnClickListeners
            setPhotoOnClickListener();
            setHeartImageOnClickListener();
            //imgPhoto.setMaxHeight(500);
            setShopOnClickListener();
            setUserOnClickListener();
        }

        public CardView getAnimationView() {
            return cardView;
        }

        public void clearAnimation() {
            cardView.clearAnimation();
        }

        // Use this method to set the photo to the given File
        public void setItemImage(File file) {
//            Bitmap imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//            imgPhoto.setImageBitmap(imageBitmap);

            if (file!=null) {
               // imgPhoto.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
//                imgPhoto.setMinimumWidth(800);


                Glide.with(HomeActivity.context)
                        .load(file)
                        .centerCrop()
                        .placeholder(R.mipmap.gallery_icon)
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
        public void setItemHeartImage(boolean red) {
            if (red) imgHeart.setColorFilter(Color.rgb(210, 36, 36));
            else imgHeart.setColorFilter(Color.rgb(219 , 134 , 134));
        }

        public void setUser(String username) {
            user.setText(username);
        }

        private void setUserOnClickListener() {
            user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (HomeActivity.menuMultipleActions.isExpanded()) {
                        HomeActivity.menuMultipleActions.collapse();
                    } else {
                        if(flag!=null && flag.equals("negozio")) {
                            Intent intent = new Intent(HomeActivity.context, ShopProfileActivity.class);
                            intent.putExtra("user", user.getText());
                            HomeActivity.activity.startActivity(intent);
                        }
                        else{
                            Intent intent = new Intent(HomeActivity.context, UserProfileActivity.class);
                            intent.putExtra("user", user.getText());
                            HomeActivity.activity.startActivity(intent);
                        }
                    }

                }
            });
        }

        private void setShopOnClickListener() {
            imgShop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (HomeActivity.menuMultipleActions.isExpanded()) {
                        HomeActivity.menuMultipleActions.collapse();
                    } else {
                        Intent intent = new Intent(HomeActivity.context, ShopProfileActivity.class);
                        intent.putExtra("user", user.getText());
                        HomeActivity.activity.startActivity(intent);
                    }

                }
            });
        }

        // Redirect user to ImageFragment (gallery) if he/she clicks on the photo
        private void setPhotoOnClickListener() {
            imgPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (HomeActivity.menuMultipleActions.isExpanded()) {
                        HomeActivity.menuMultipleActions.collapse();
                    } else {
                        Intent intent = new Intent(HomeActivity.context, ImageFragment.class);
                        intent.putExtra("classe", "MostRecentPhotos");
                        intent.putExtra("position", MostRecentItemViewHolder.this.getAdapterPosition());
                        HomeActivity.activity.startActivity(intent);
                    }
                }
            });
        }

        // Add/Remove a like to the current photo both locally and on Parse database.
        private void setHeartImageOnClickListener() {
            imgHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Log.d("MostRecentAdapter", "Clicked on photo with position: " + MostRecentItemViewHolder.this.getAdapterPosition());

                    if (HomeActivity.menuMultipleActions.isExpanded()) {
                        HomeActivity.menuMultipleActions.collapse();
                    } else {
                        Image image = MostRecentAdapter.itemList.get(MostRecentItemViewHolder.this.getAdapterPosition());

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

        public void setShop(String flag) {
            if(flag!=null && flag.equals("Negozio")) imgShop.setVisibility(View.VISIBLE);
            else imgShop.setVisibility(View.INVISIBLE);
        }
    }

}