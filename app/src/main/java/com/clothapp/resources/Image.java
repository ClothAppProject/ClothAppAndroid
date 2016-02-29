package com.clothapp.resources;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
    private int nLike;
    private List<String> like;
    private List<String> hashtag;
    private List<String> idClothes;
    private List<String> typeClothes;

    public Image(File f, String Id, String user,List likes, int nLike, List hashtags, List idClothes, List typeClothes)   {
        this.user=user;
        this.objectId = Id;
        this.file=f;
        if (likes==null) like= new ArrayList();
        else this.like = likes;
        if (hashtags==null) hashtag= new ArrayList();
        else this.hashtag = hashtags;
        if (idClothes==null) this.idClothes= new ArrayList();
        else this.idClothes = idClothes;
        if (typeClothes==null) this.typeClothes= new ArrayList();
        else this.typeClothes = idClothes;
        this.nLike = nLike;
    }

    public Image(ParseObject o)  {
        try {
            this.file=o.getParseFile("thumbnail").getFile();
        } catch (ParseException e) {}
        this.objectId=o.getObjectId();
        this.user=o.getString("user");
        this.nLike=o.getInt("nLike");
        if (o.getList("like")==null) like = new ArrayList<>();
        else this.like=o.getList("like");
        if (o.getList("vestiti")==null) idClothes = new ArrayList<>();
        else this.idClothes=o.getList("vestiti");
        if (o.getList("tipo")==null) typeClothes = new ArrayList<>();
        else this.typeClothes=o.getList("tipo");
        if (o.getList("hashtag")==null) hashtag = new ArrayList<>();
        else this.hashtag=o.getList("hashtag");

    }

    public List<String> getHashtag() {
        return hashtag;
    }

    public List<String> getIdVestiti() {
        return idClothes;
    }
    public List<String> getTypeVestiti() {
        return typeClothes;
    }
    public String getTypeVestitiToString() {
        String result="";
        if (!typeClothes.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String clothing : typeClothes) {
                sb.append(clothing).append(" & ");
            }
            result = sb.toString();
            result = result.substring(0, result.length() - 2);
        }
        return result;
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
