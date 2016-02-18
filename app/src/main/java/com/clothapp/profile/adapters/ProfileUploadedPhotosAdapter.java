package com.clothapp.profile.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clothapp.R;
import com.clothapp.profile.utils.ProfileUploadedPhotosListItem;
import com.parse.ParseUser;

import java.io.File;
import java.util.List;

public class ProfileUploadedPhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public List<ProfileUploadedPhotosListItem> items;

    public ProfileUploadedPhotosAdapter(List<ProfileUploadedPhotosListItem> items) {
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_profile_uploaded_photos_list_item, parent, false);
        return new PhotoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;

        // Create a bitmap from a file
        File imageFile = items.get(position).getImageFile();
        Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

        photoViewHolder.photo.setImageBitmap(imageBitmap);

        // Display hashtags, if any
        List<String> hashtagList = items.get(position).hashtags;

        if (hashtagList != null) {

            StringBuilder sb = new StringBuilder();

            for (String hashtag : hashtagList) {
                sb.append(hashtag).append(" ");
            }

            photoViewHolder.txtHashtags.setText(sb.toString());

        } else {
            photoViewHolder.txtHashtags.setText("");
        }

        // Display item (clothing) name, if any
        List<String> clothesList = items.get(position).clothes;

        if (clothesList != null) {

            StringBuilder sb = new StringBuilder();

            for (String clothing : clothesList) {
                sb.append(clothing).append(" & ");
            }

            String result = sb.toString();
            result = result.substring(0, result.length() - 2);

            photoViewHolder.txtItemNames.setText(result);
        }

        String username = ParseUser.getCurrentUser().getUsername();
        if (items.get(position).users != null && items.get(position).users.contains(username)) {
            Log.d("PUPAdapter", "Item " + position + ": already clicked like.");

            photoViewHolder.likeImage.setColorFilter(Color.argb(255, 181, 47, 41));
        }

        photoViewHolder.txtLikeCount.setText(items.get(position).getLikeCount() + "");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        TextView txtItemNames;
        TextView txtHashtags;
        ImageView photo;
        TextView txtLikeCount;
        ImageView likeImage;

        PhotoViewHolder(View itemView) {
            super(itemView);

            txtItemNames = (TextView) itemView.findViewById(R.id.profile_uplaoded_photos_card_item_name);
            txtHashtags = (TextView) itemView.findViewById(R.id.profile_uploaded_photos_card_hashtags);
            photo = (ImageView) itemView.findViewById(R.id.profile_uploaded_photos_card_image);
            txtLikeCount = (TextView) itemView.findViewById(R.id.profile_uploaded_photos_card_like_count);
            likeImage = (ImageView) itemView.findViewById(R.id.profile_uploaded_photos_card_like_image);
        }
    }

}
