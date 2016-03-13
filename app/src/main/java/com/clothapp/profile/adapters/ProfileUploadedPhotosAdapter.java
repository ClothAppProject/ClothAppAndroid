package com.clothapp.profile.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.ImageFragment;
import com.clothapp.R;
import com.clothapp.parse.notifications.NotificationsUtils;
import com.clothapp.profile.UserProfileActivity;
import com.clothapp.profile_shop.ShopProfileActivity;
import com.clothapp.resources.Image;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileUploadedPhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String profilo;
    public static List<Image> photos;

    private final static String username = ParseUser.getCurrentUser().getUsername();

    public ProfileUploadedPhotosAdapter(List<Image> items, String profilo) {
        this.profilo = profilo;
        ProfileUploadedPhotosAdapter.photos = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_profile_uploaded_photos_list_item, parent, false);
        return new PhotoViewHolder(v, profilo);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;

        File imageFile = photos.get(position).getFile();

        if (profilo.equals("persona")) {
            Glide.with(UserProfileActivity.context)
                    .load(imageFile)
                    .into(photoViewHolder.photo);
        } else {
            Glide.with(ShopProfileActivity.context)
                    .load(imageFile)
                    .into(photoViewHolder.photo);
        }

        // Set item names
        photoViewHolder.txtItemNames.setText(photos.get(position).getTypeVestitiToString());

        // Set hashtags
        photoViewHolder.txtHashtags.setText(photos.get(position).getHashtagToString());

        String username = ParseUser.getCurrentUser().getUsername();

        List likeUsers = photos.get(position).getLike();

        if (likeUsers != null && likeUsers.contains(username)) {
            photoViewHolder.likeImage.setColorFilter(Color.rgb(210, 36, 36));
        } else {
            photoViewHolder.likeImage.setColorFilter(Color.rgb(205, 205, 205));
        }

        photoViewHolder.txtLikeCount.setText(photos.get(position).getNumLike() + "");
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {

        TextView txtItemNames;
        TextView txtHashtags;
        ImageView photo;
        ImageView share;
        TextView txtLikeCount;
        ImageView likeImage;

        PhotoViewHolder(View itemView, final String profilo) {
            super(itemView);

            txtItemNames = (TextView) itemView.findViewById(R.id.profile_uplaoded_photos_card_item_name);
            txtHashtags = (TextView) itemView.findViewById(R.id.profile_uploaded_photos_card_hashtags);
            photo = (ImageView) itemView.findViewById(R.id.profile_uploaded_photos_card_image);
            txtLikeCount = (TextView) itemView.findViewById(R.id.profile_uploaded_photos_card_like_count);
            likeImage = (ImageView) itemView.findViewById(R.id.profile_uploaded_photos_card_like_image);
            share = (ImageView) itemView.findViewById(R.id.profile_uploaded_photos_card_share);

            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (profilo.equals("persona")) {
                        Intent intent = new Intent(UserProfileActivity.context, ImageFragment.class);
                        intent.putExtra("classe", "profilo");
                        intent.putExtra("position", PhotoViewHolder.this.getAdapterPosition());
                        UserProfileActivity.activity.startActivity(intent);
                    } else {
                        Intent intent = new Intent(ShopProfileActivity.context, ImageFragment.class);
                        intent.putExtra("classe", "profilo");
                        intent.putExtra("position", PhotoViewHolder.this.getAdapterPosition());
                        ShopProfileActivity.activity.startActivity(intent);
                    }
                }
            });

            likeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Image image = ProfileUploadedPhotosAdapter.photos.get(PhotoViewHolder.this.getAdapterPosition());
                    final String imageUsername = image.getUser();

                    final boolean add = !image.getLike().contains(username);
                    if (add) {
                        // Log.d("ProfileUploadedPhotos", "Adding...");
                        image.addLike(username);
                    } else {
                        // Log.d("ProfileUploadedPhotos", "Removing...");
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

                                    // Send "Like" notification to the user who posted the image
                                    NotificationsUtils.sendNotification(imageUsername, ParseUser.getCurrentUser().getUsername() + " ha messo \"Mi Piace\" a una tua foto!");
                                }
                            } else {
                                Log.d("ProfileUploadedPhotos", "Error: " + e.getMessage());
                            }
                        }
                    });
                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap icon = BitmapFactory.decodeFile(photos.get(PhotoViewHolder.this.getAdapterPosition()).getFile().getPath());
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
                    try {
                        f.createNewFile();
                        FileOutputStream fo = new FileOutputStream(f);
                        fo.write(bytes.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
                    if (profilo.equals("persona"))
                        UserProfileActivity.context.startActivity(Intent.createChooser(share, "Share Image"));
                    else
                        ShopProfileActivity.context.startActivity(Intent.createChooser(share, "Share Image"));
                }
            });
        }
    }

}
