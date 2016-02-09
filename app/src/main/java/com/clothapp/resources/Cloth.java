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
    private double prize;

    public Cloth(String cloth, String address, double prize, String shop) {
        this.cloth = cloth;
        this.address = address;
        this.prize = prize;
        this.shop = shop;
    }

    public String getCloth() {
        return cloth;
    }

    public String getAddress() {
        return address;
    }

    public String getShop() {
        return shop;
    }

    public double getPrize() {
        return prize;
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

    public void setPrize(double prize) {
        this.prize = prize;
    }

    protected Cloth(Parcel in) {
        cloth = in.readString();
        address = in.readString();
        shop = in.readString();
        prize = in.readDouble();
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
        dest.writeDouble(prize);
    }
}
