package com.clothapp.profile_shop.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clothapp.R;
import com.clothapp.profile.utils.ProfileInfoListItem;
import com.clothapp.profile_shop.ShopProfileActivity;

import java.util.List;

public class ProfileShopInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<ProfileInfoListItem> items;

    private final static int ITEM_TYPE_HEADER = 0;
    private final static int ITEM_TYPE_INFO = 1;

    public ProfileShopInfoAdapter(List<ProfileInfoListItem> items) {
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {

            case (ITEM_TYPE_HEADER): {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_profile_shop_info_list_header, parent, false);
                HeaderViewHolder headerViewHolder = new HeaderViewHolder(v);
                return headerViewHolder;
            }

            case (ITEM_TYPE_INFO): {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_profile_shop_info_list_item, parent, false);
                InfoShopViewHolder infoViewHolder = new InfoShopViewHolder(v);
                return infoViewHolder;
            }

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {

            case ITEM_TYPE_HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                String username = ShopProfileActivity.username.substring(0, 1).toUpperCase() + ShopProfileActivity.username.substring(1);
                headerViewHolder.txtUsername.setText(username);

                break;

            case ITEM_TYPE_INFO:
                InfoShopViewHolder infoViewHolder = (InfoShopViewHolder) holder;
                infoViewHolder.txtTitle.setText(items.get(position).getTitle());
                infoViewHolder.txtContent.setText(items.get(position).getContent());

                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == ITEM_TYPE_HEADER) ? ITEM_TYPE_HEADER : ITEM_TYPE_INFO;
    }

    public static class InfoShopViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle;
        TextView txtContent;

        InfoShopViewHolder(View itemView) {
            super(itemView);

            txtTitle = (TextView) itemView.findViewById(R.id.profile_info_list_item_title);
            txtContent = (TextView) itemView.findViewById(R.id.profile_info_list_item_content);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView txtUsername;

        HeaderViewHolder(View itemView) {
            super(itemView);

            txtUsername = (TextView) itemView.findViewById(R.id.profile_info_list_header_title);
        }
    }

}
