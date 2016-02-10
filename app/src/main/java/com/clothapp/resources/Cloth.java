package com.clothapp.resources;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jack1 on 09/02/2016.
 */
public class Cloth implements Parcelable {
    private String cloth;
    private String address;
    private String shop;
    private String brand;
    private String price;

    public Cloth(String cloth, String address, String price, String shop,String brand) {
        this.cloth = cloth;
        this.address = address;
        this.price = price;
        this.shop = shop;
        this.brand=brand;
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

    public String getPrice() {
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

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setPrize(String price) {
        this.price = price;
    }

    protected Cloth(Parcel in) {
        cloth = in.readString();
        address = in.readString();
        shop = in.readString();
        price = in.readString();
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
        dest.writeString(price);
    }
}
