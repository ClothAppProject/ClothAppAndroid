package com.clothapp.profile.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clothapp.R;
import com.clothapp.profile.UserProfileActivity;
import com.clothapp.profile.utils.ProfileInfoListItem;
import com.clothapp.profile.utils.ProfileUploadedPhotosListItem;

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
        String username = UserProfileActivity.username;
        photoViewHolder.txtTitle.setText(username);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle;

        PhotoViewHolder(View itemView) {
            super(itemView);

            txtTitle = (TextView) itemView.findViewById(R.id.profile_uploaded_photos_list_item_title);
        }
    }

}
