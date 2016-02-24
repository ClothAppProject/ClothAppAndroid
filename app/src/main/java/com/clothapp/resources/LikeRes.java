package com.clothapp.resources;

import com.parse.ParseObject;

/**
 * Created by giacomoceribelli on 24/02/16.
 */
public class LikeRes {
    public static void deleteLike(ParseObject point, Image image, String username)  {
        image.remLike(username);
        point.put("like", image.getLike());
        point.put("nLike", image.getNumLike());
        point.saveInBackground();
    }
    public static void addLike(ParseObject point, Image image, String username)    {
        image.addLike(username);
        point.add("like", username);
        point.put("nLike", image.getNumLike());
        point.saveInBackground();
    }
}
