package com.clothapp.resources;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nc94 on 2/15/16.
 */
public class Users {

    private String username;
    private String surname;
    private String city;
    public Users(String username, String surname, String city) {
        this.username = username;
        this.surname = surname;
        this.city = city;
    }

    public Users(ParseObject o) {

        this.username=o.getString("username");
        this.surname=o.getString("surname");
        this.city=o.getString("city");
    }

    public String getUsername() {
        return username;
    }

    public String getSurname() {
        return surname;
    }

    public String getCity() {
        return city;
    }
}
