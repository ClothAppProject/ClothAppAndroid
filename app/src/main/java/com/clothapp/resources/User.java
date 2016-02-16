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
public class User{

    private String username;
    private String name;
    private String city;
    public User(String username, String name, String city) {
        this.username = username;
        this.name = name;
        this.city = city;
    }

    public User(ParseObject o) {

        this.username=o.getString("username");
        this.name=o.getString("name");
        this.city=o.getString("city");
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }
    }
