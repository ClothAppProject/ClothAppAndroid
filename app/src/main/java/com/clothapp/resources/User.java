package com.clothapp.resources;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nc94 on 2/15/16.
 */
public class User{
    
    private File profilo;
    private String username;
    private String name;
    private String city;
    public User(String username, String name, String city) {
        this.username = username;
        this.name = name;
        this.city = city;
    }

    public User(ParseUser o) {

        this.username=o.getString("username");
        this.name=o.getString("name");
        this.city=o.getString("city");
    }

    public User() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return !(username != null ? !username.equals(user.username) : user.username != null);

    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }

    public void setUser(ParseUser u){
        this.username=u.getString("username");
        this.name=u.getString("name");
        this.city=u.getString("city");
    }

    public void setProfilo(File profilo) {
        this.profilo = profilo;
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

    public File getProfilo() {
        return profilo;
    }
}
