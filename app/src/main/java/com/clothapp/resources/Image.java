package com.clothapp.resources;

import java.io.File;

/**
 * Created by giacomoceribelli on 28/01/16.
 */

//classe che prende sia il tipo l'id di una foto che il file stesso
public class Image {
    private File file;
    private String objectId;
    public Image(File f, String Id)   {
        this.objectId = Id;
        this.file=f;
    }
    public File getFile(){
        return this.file;
    }
    public String getObjectId() {
        return this.objectId;
    }
}
