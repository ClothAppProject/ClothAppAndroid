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
    private List<String> like;
    private int nLike;
    private List<String> hashtag;
    private List<String> vestiti;


    public Image(File f, String Id, String user,List likes)   {
        this.user=user;
        this.objectId = Id;
        this.file=f;
        if (likes==null) {
            like= new ArrayList();
        }else {
            this.like = likes;
        }
        this.nLike=like.size();
    }

    public Image(ParseObject o)  {
        try {
            this.file=o.getParseFile("thumbnail").getFile();
        } catch (ParseException e) {}
        this.objectId=o.getObjectId();
        this.user=o.getString("user");
        this.nLike=o.getInt("nLike");
        this.like=(ArrayList)o.get("like");
        this.vestiti=(ArrayList)o.get("tipo");
        this.hashtag=(ArrayList)o.get("hashtag");

    }

    public List<String> getHashtag() {
        return hashtag;
    }

    public List<String> getVestiti() {
        return vestiti;
    }

    public String getHashtagToString() {
        String res="";
        if(hashtag!=(null)) {
            for (int i = 0; i < hashtag.size(); i++) {
                res += (" " + hashtag.get(i));
            }
        }
        return res;
    }

    public String getVestitiToString() {
        String res="";
        if(vestiti!=(null)){
            for(int i=0;i<vestiti.size();i++){
                res+=(" "+vestiti.get(i));
            }
        }
        return res;
    }

    public String getUser() {
        return user;
    }
    public List getLike() {return like;}
    public int getNumLike() {return nLike;}
    public void addLike(String o) {like.add(o); nLike++;}
    public void remLike(String o) {like.remove(o); nLike--;}
    public void setLike(List like) {this.like = like;}

    public File getFile(){
        return this.file;
    }
    public String getObjectId() {
        return this.objectId;
    }

    //metodi equals e hashcode che controllano se un oggetto è uguale, basta controllare sull'objectId
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return objectId.equals(image.objectId);
    }
    @Override
    public int hashCode() {
        return objectId.hashCode();
    }

    //funzioni per dell'interfaccia parcelable, per poter passare un ArrayList<Image> da una classe all'imageFragment
    protected Image(Parcel in) {
        objectId = in.readString();
        user = in.readString();
        if (in.readByte() == 0x01) {
            like = new ArrayList<String>();
            in.readList(like, String.class.getClassLoader());
        } else {
            like = null;
        }
        nLike = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(objectId);
        dest.writeString(user);
        if (like == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(like);
        }
        dest.writeInt(nLike);
    }

    @SuppressWarnings("unused")
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
}
