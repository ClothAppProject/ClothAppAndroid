package com.clothapp.profile.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.R;
import com.clothapp.profile.UserProfileActivity;
import com.clothapp.profile.utils.ProfileUtils;
import com.clothapp.profile_shop.ShopProfileActivity;
import com.clothapp.resources.CircleTransform;
import com.clothapp.resources.User;

import java.io.File;
import java.util.List;

/**
 * Created by giacomoceribelli on 25/02/16.
 */
public class PeopleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<User> users;
    private String profilo;

    public PeopleListAdapter(List<User> utenti, String profilo) {
        this.profilo = profilo;
        this.users = utenti;
        if (profilo.equals("persona"))
            context = UserProfileActivity.activity.getApplicationContext();
        else context = ShopProfileActivity.activity.getApplicationContext();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item_user, parent, false);
        return new PersonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        PersonViewHolder profileViewHolder = (PersonViewHolder) holder;
        if (users.get(position).getProfilo() != null) {
            // Create a bitmap from a file
            File imageFile = users.get(position).getProfilo();
            Glide.with(context)
                    .load(imageFile)
                    .placeholder(R.drawable.com_facebook_profile_picture_blank_circle)
                    .transform(new CircleTransform(context))
                    .into(profileViewHolder.photo);
        } else {
            Glide.with(context)
                    .load(R.drawable.com_facebook_profile_picture_blank_circle)
                    .transform(new CircleTransform(context))
                    .into(profileViewHolder.photo);
        }
        profileViewHolder.user.setText(users.get(position).getUsername());
        //commentato perchè tanto il nome è null, bisognerebbe fare una query per prendere anche il nome
        //profileViewHolder.name.setText(users.get(position).getName());

        profileViewHolder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = ProfileUtils.goToProfile(context, users.get(position).getUsername());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });
    }

    public void add(User usr) {
        if (users.contains(usr)) users.add(usr);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class PersonViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView user;
        TextView name;
        LinearLayout ll;

        PersonViewHolder(View itemView) {
            super(itemView);
            ll = (LinearLayout) itemView.findViewById(R.id.striscia);
            photo = (ImageView) itemView.findViewById(R.id.foto);
            user = (TextView) itemView.findViewById(R.id.user);
            name = (TextView) itemView.findViewById(R.id.name);
        }
    }
}
