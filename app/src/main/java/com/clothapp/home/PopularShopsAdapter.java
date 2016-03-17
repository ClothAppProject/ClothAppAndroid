package com.clothapp.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clothapp.R;
import com.clothapp.resources.Image;

import java.util.List;

/**
 * Created by SimoneConia on 17/03/16.
 */
public class PopularShopsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static List<PopularShop> itemList;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_popular_shop_item, parent, false);
        return new PopularShopsItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        final PopularShopsItemViewHolder holder = (PopularShopsItemViewHolder) viewHolder;

        PopularShop popularShop = itemList.get(position);

        holder.name.setText(popularShop.name);
        holder.address.setText(popularShop.address);
        holder.likeCount.setText(popularShop.likeCount + "");
        holder.imageView.setImageResource(popularShop.image_id);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class PopularShopsItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView address;
        private final TextView likeCount;

        private final ImageView imageView;

        public PopularShopsItemViewHolder(final View parent) {
            super(parent);

            name = (TextView) parent.findViewById(R.id.popular_shop_name);
            address = (TextView) parent.findViewById(R.id.popular_shop_address);
            likeCount = (TextView) parent.findViewById(R.id.popular_shop_like_count);

            imageView = (ImageView) parent.findViewById(R.id.popular_shop_image);
        }

    }
}

class PopularShop {
    public String name;
    public String address;
    public int likeCount;
    public int image_id;

    public PopularShop(String name, String address, int likeCount, int image_id) {
        this.name = name;
        this.address = address;
        this.likeCount = likeCount;
        this.image_id = image_id;
    }
}

