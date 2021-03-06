package com.clothapp.profile.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.image_detail.ImageActivity;
import com.clothapp.R;
import com.clothapp.resources.Image;
import com.clothapp.parse.notifications.LikeRes;
import com.parse.ParseUser;

import java.io.File;
import java.util.List;

public class ProfileUploadedPhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Parcelable {

    private String profilo;
    public List<Image> photos;
    private Context context;
    private ProfileUploadedPhotosAdapter oggetto;

    private final String username = ParseUser.getCurrentUser().getUsername();

    public ProfileUploadedPhotosAdapter(List<Image> items, String profilo, Context context) {
        this.profilo = profilo;
        this.photos = items;
        this.context = context;
        this.oggetto = this;
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
        if (imageFile!=null) {
            Glide.clear(photoViewHolder.photo);
            Glide.with(context)
                    .load(imageFile)
                    .placeholder(R.mipmap.gallery_icon)
                    .into(photoViewHolder.photo);
        }else{
            Glide.with(context)
                    .load(R.drawable.loading)
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
                    Intent intent = new Intent(context, ImageActivity.class);
                    intent.putExtra("classe", "profilo");
                    intent.putExtra("photo", oggetto);
                    intent.putExtra("position", PhotoViewHolder.this.getAdapterPosition());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

            likeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Image image = photos.get(PhotoViewHolder.this.getAdapterPosition());
                    final String imageUsername = image.getUser();

                    final boolean add = image.getLike().contains(username);
                    if (add) {
                        // Log.d("ProfileUploadedPhotos", "Removing...");
                        LikeRes.deleteLike(image.getObjectId(), image, username);
                    } else {
                        // Log.d("ProfileUploadedPhotos", "Adding...");
                        LikeRes.addLike(image.getObjectId(), image, username);
                    }

                    notifyDataSetChanged();
                }
            });
            /*
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
                    share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //TODO risolvere problema perchè crasha
                    if (profilo.equals("persona")) {
                        context.startActivity(Intent.createChooser(share, "Share Image"));
                    }else {
                        context.startActivity(Intent.createChooser(share, "Share Image"));
                    }
                }
            });*/
        }
    }


    //implementato Parcelable per poter passare l'oggetto
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(photos);
    }

    protected ProfileUploadedPhotosAdapter(Parcel in) {
        this.photos = in.createTypedArrayList(Image.CREATOR);
    }

    public static final Parcelable.Creator<ProfileUploadedPhotosAdapter> CREATOR = new Parcelable.Creator<ProfileUploadedPhotosAdapter>() {
        @Override
        public ProfileUploadedPhotosAdapter createFromParcel(Parcel source) {
            return new ProfileUploadedPhotosAdapter(source);
        }

        @Override
        public ProfileUploadedPhotosAdapter[] newArray(int size) {
            return new ProfileUploadedPhotosAdapter[size];
        }
    };
}
