package com.clothapp.resources;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.clothapp.HomepageActivity;
import com.parse.ParseObject;
import java.util.List;

/**
 * Created by Roberto on 20/01/16.
 */

public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    private View view;

    List<ParseObject> pics;


    public ImageAdapter(Context c, List<ParseObject> list, View v) {
        mContext = c;
        view = v;
        pics = list;
    }

    public int getCount() {
        return pics.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(600, 600));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        //Bitmap bm = decodeSampledBitmap(pics.get(position), 600, 600);
        //imageView.setImageBitmap(bm);

        HomepageActivity.loadBitmap(pics.get(position),imageView, mContext.getResources());

        return imageView;
    }
}
