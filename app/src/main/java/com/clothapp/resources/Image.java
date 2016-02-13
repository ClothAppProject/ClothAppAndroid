package com.clothapp.resources;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by giacomoceribelli on 28/01/16.
 */

//classe che prende sia il tipo l'id di una foto che il file stesso
public class Image implements Parcelable{
    private File file;
    private String objectId;
    private String user;
    private List like;
    private int nLike;


    public Image(File f, String Id, String user,List likes)   {
        this.user=user;
        this.objectId = Id;
        this.file=f;
        if (likes==null) {
            like= new ArrayList();
        }else {
            this.like = likes;
        }
    }

    public Image(ParseObject o)  {
        try {
            this.file=o.getParseFile("photo").getFile();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.objectId=o.getObjectId();
        this.user=o.getString("user");
        this.nLike=(int)o.get("nLike");
        this.like=(ArrayList)o.get("like");
    }

    public String getUser() {
        return user;
    }

    public List getLike() {return like;}
    public int getNumLike() {return like.size();}
    public void addLike(Object o) {like.add(o);}
    public void remLike(Object o) {like.remove(o);}
    public void setLike(List like) {this.like = like;}

    public File getFile(){
        return this.file;
    }
    public String getObjectId() {
        return this.objectId;
    }

    //funzioni per dell'interfaccia parcelable, per poter passare un ArrayList<Image> da una classe all'imageFragment
    protected Image(Parcel in) {
        objectId = in.readString();
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(objectId);
    }
}
