package com.barikoi.barikoidemo.Model;

import java.io.Serializable;


public class Type implements Serializable {
    private String name;
    private String subtype;
    private String displayname;
    private int img;

    public Type(String name, String subtype, String displayname, int img) {
        this.name = name;
        this.img = img;
        this.displayname=displayname;
        this.subtype=subtype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return displayname;
    }

    public boolean istype(){

        return !name.equals("");
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }
}
