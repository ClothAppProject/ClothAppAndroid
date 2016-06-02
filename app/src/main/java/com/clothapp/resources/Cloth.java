package com.clothapp.resources;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

/**
 * Created by jack1 on 09/02/2016.
 */
public class Cloth implements Parcelable {
    private String cloth = "";
    private String address = "";
    private String shop = "";
    private String shopUsername = "";
    private String brand = "";
    private Float price;
    private int id;
    private String objectId;

    public Cloth(String cloth, String address, Float price, String shop, String shopUsername, String brand) {
        this.cloth = cloth;
        this.address = address;
        this.price = price;
        this.shop = shop;
        this.shopUsername = shopUsername;
        this.brand=brand;
    }

    public Cloth() {

    }

    public Cloth(ParseObject o){
        if( o.getString("tipo")!=null) this.cloth = o.getString("tipo");
        if( o.getString("luogoAcquisto")!=null) this.address = o.getString("luogoAcquisto");
        if( o.get("prezzo")!=null) this.price = Float.parseFloat(o.get("prezzo").toString());
        if( o.getString("shop")!=null) this.shop = o.getString("shop");
        if( o.getString("brand")!=null) this.brand=o.getString("brand");
    }

    public String getCloth() {
        return cloth;
    }

    public String getAddress() {
        return address;
    }

    public String getBrand() {
        return brand;
    }

    public String getShop() {
        return shop;
    }

    public String getShopUsername() {
        return shopUsername;
    }

    public Float getPrice() {
        return price;
    }

    public void setCloth(String cloth) {
        this.cloth = cloth;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public void setShopUsernam(String shopUsername) {
        this.shopUsername = shopUsername;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setPrize(Float price) {
        this.price = price;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    protected Cloth(Parcel in) {
        cloth = in.readString();
        address = in.readString();
        shop = in.readString();
        shopUsername = in.readString();
        price = in.readFloat();
    }

    public static final Creator<Cloth> CREATOR = new Creator<Cloth>() {
        @Override
        public Cloth createFromParcel(Parcel in) {
            return new Cloth(in);
        }

        @Override
        public Cloth[] newArray(int size) {
            return new Cloth[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cloth);
        dest.writeString(address);
        dest.writeString(shop);
        dest.writeString(shopUsername);
        dest.writeFloat(price);
    }

    @Override
    public String toString() {
        return "Cloth{" +
                "cloth='" + cloth + '\'' +
                ", address='" + address + '\'' +
                ", shop='" + shop + '\'' +
                ", shopUsername='" + shopUsername + '\'' +
                ", brand='" + brand + '\'' +
                ", price=" + price +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cloth cloth = (Cloth) o;

        return id == cloth.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    public int getID() {
        return id;
    }

    public boolean isEmpty(){
        if(cloth=="" && shop=="" && address=="" && brand=="" && price==null) return true;
        else return false;

    }

    private boolean set=false;
    public void setSet(boolean set){
        this.set=set;
    }
    public boolean isSet() {
        return set;
    }
}
