package com.clothapp.profile.utils;

import java.io.File;

public class ProfileUploadedPhotosListItem {

    private String title;
    private File imgFile;

    public ProfileUploadedPhotosListItem (String title, File file) {
        this.title = title;
        this.imgFile = file;
    }

    public String getTitle() {
        return title;
    }

    public File getImageFile() {
        return imgFile;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImgFile(File imgFile) {
        this.imgFile = imgFile;
    }
}
