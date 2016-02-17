package com.clothapp.profile.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileUploadedPhotosListItem {

    private String objectId;
    private File imgFile;
    private String username;

    public List<String> hashtags;
    public List<String> clothes;
    public List<String> users;

    private int likeCount;

    // Constructor
    public ProfileUploadedPhotosListItem(String objectId, File file, String username, int likeCount) {

        this.objectId = objectId;
        this.imgFile = file;
        this.username = username;
        this.likeCount = likeCount;

        hashtags = new ArrayList<>();
        clothes = new ArrayList<>();
        users = new ArrayList<>();
    }

    // Get ParseObject objectId
    public String getObjectId() {
        return objectId;
    }

    // Get the File object of the uploaded photo
    // This file is the thumbnail image
    public File getImageFile() {
        return imgFile;
    }

    // Get the username of the user who uploaded the photo
    public String getUsername() {
        return username;
    }

    // Get the number of likes for this photo
    public int getLikeCount() {
        return likeCount;
    }
}
