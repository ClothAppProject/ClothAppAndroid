package com.clothapp.home_gallery;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.clothapp.R;

/**
 * Created by jack1 on 07/02/2016.
 */
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.clothapp.ImageFragment;
import com.clothapp.R;
import com.clothapp.profile.UserProfileActivity;
import com.clothapp.profile.utils.ProfileUtils;
import com.clothapp.resources.Image;
import com.clothapp.resources.LikeRes;
import com.clothapp.resources.SquaredImageView;
import com.clothapp.settings.SettingsActivity;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class MyListAdapter extends BaseAdapter {
    private final Context context;
    private List<Image> files=new ArrayList<>();

    public MyListAdapter(Context context, List<Image> photos) {
        this.context = context;
        files = photos;
    }
    public void addToGridView(Image foto)   {files.add(foto);}

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View row = convertView;
        if (row==null) {
            //se la convertView di quest'immagine è nulla la inizializzo
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.fragment_cardlist, parent, false);
        }
        final Image image = getItem(position);
        //prendo i vari oggetti del gridview_layout
        ImageView view = (ImageView) row.findViewById(R.id.topfoto);
        TextView user=(TextView)row.findViewById(R.id.user);
        user.setText((CharSequence) files.get(position).getUser());


        // Get the image URL for the current position.
        final File file = image.getFile();
        // Trigger the download of the URL asynchronously into the image view.

        Glide.with(context)
                .load(file)
                .centerCrop()
                .placeholder(R.mipmap.gallery_icon)
                .into(view);

        //listener su more TODO:per ora apre settings in futuro "segnala"
        ImageView more=(ImageView)row.findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SettingsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });


        ImageView person=(ImageView)row.findViewById(R.id.person);
        //setto il listener sull'icona persona
        person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = ProfileUtils.goToProfile(context, getItem(position).getUser());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }
        });


        ImageView share=(ImageView)row.findViewById(R.id.share);
        //TODO: problemi di permesso di lettura
        //setto il listener sull'icona share
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap icon = BitmapFactory.decodeFile(file.getPath());
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                context.startActivity(Intent.createChooser(share, "Share Image").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        //setto gli hashtag
        TextView hashtag=(TextView)row.findViewById(R.id.hashtag);
        hashtag.setText(image.getHashtagToString());

        //mostro il numero di like
        int numLikes = files.get(position).getNumLike();
        //  se ho zero likes scrivo like sennò likes
        String singPlur = numLikes == 0 || numLikes == 1? "like" : "likes";

        TextView like=(TextView)row.findViewById(R.id.like);
        like.setText(Integer.toString(numLikes) + " " + singPlur);

        ImageView cuore=(ImageView)row.findViewById(R.id.cuore);
        final String username = ParseUser.getCurrentUser().getUsername();
        //controllo se ho messo like sull'attuale foto
        if (image.getLike().contains(username))    {
            cuore.setImageResource(R.mipmap.ic_favorite_white_48dp);
        }else{
            cuore.setImageResource(R.mipmap.ic_favorite_border_white_48dp);
        }
        //listener sul like della foto
        cuore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject point = ParseObject.createWithoutData("Photo", image.getObjectId());
                if(HomeActivity.menuMultipleActions.isExpanded()) HomeActivity.menuMultipleActions.collapse();
                else {
                    if ((image.getLike().contains(username))) {
                        //possibile problema di concorrenza sull'oggetto in caso più persone stiano mettendo like contemporaneamente
                        //rimuovo il like e cambio la lista

                        LikeRes.deleteLike(point,image,username);
                        notifyDataSetChanged();
                    } else {
                        //aggiungo like e aggiorno anche in parse
                        LikeRes.addLike(point,image,username);
                        notifyDataSetChanged();
                    }
                }
            }
        });

        return row;
    }

    @Override public int getCount() {
        return files.size();
    }

    @Override public Image getItem(int position) {
        return (files.get(position));
    }

    @Override public long getItemId(int position) {
        return position;
    }
}