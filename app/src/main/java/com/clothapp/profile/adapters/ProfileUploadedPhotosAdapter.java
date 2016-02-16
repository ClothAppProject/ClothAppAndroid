package com.clothapp.profile.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clothapp.R;
import com.clothapp.profile.utils.ProfileUploadedPhotosListItem;

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
        photoViewHolder.txtUsername.setText(items.get(position).getObjectId());

        File imageFile = items.get(position).getImageFile();
        Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

        photoViewHolder.photo.setImageBitmap(imageBitmap);

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

        List<String> clothesList = items.get(position).clothes;

        if (clothesList != null) {

            StringBuilder sb = new StringBuilder();

            for (String clothing : clothesList) {
                sb.append(clothing).append(" & ");
            }

            String result = sb.toString();
            result = result.substring(0, result.length() - 2);

            photoViewHolder.txtItems.setText(result);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        TextView txtUsername;
        TextView txtItems;
        TextView txtHashtags;
        ImageView photo;

        PhotoViewHolder(View itemView) {
            super(itemView);

            txtUsername = (TextView) itemView.findViewById(R.id.profile_uploaded_photos_list_item_title);
            txtItems = (TextView) itemView.findViewById(R.id.profile_uplaoded_photos_card_item_name);
            txtHashtags = (TextView) itemView.findViewById(R.id.profile_uploaded_photos_card_hashtags);
            photo = (ImageView) itemView.findViewById(R.id.profile_uploaded_photos_card_image);
        }
    }

}
