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

    private int nLikes;

    public ProfileUploadedPhotosListItem (String objectId, File file, String username, int nLikes) {
        this.objectId = objectId;
        this.imgFile = file;
        this.username = username;
        this.nLikes = nLikes;

        hashtags = new ArrayList<>();
        clothes = new ArrayList<>();
        users = new ArrayList<>();
    }

    public String getObjectId() {
        return objectId;
    }

    public File getImageFile() {
        return imgFile;
    }

    public void setObjectId(String title) {
        this.objectId = title;
    }

    public void setImgFile(File imgFile) {
        this.imgFile = imgFile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getnLikes() {
        return nLikes;
    }

    public void setnLikes(int nLikes) {
        this.nLikes = nLikes;
    }
}
